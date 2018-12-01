package ar.edu.itba.ss.cell;

import ar.edu.itba.ss.particle.Pair;
import ar.edu.itba.ss.particle.Particle;

import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ParticleGrid<T extends Particle> implements Grid<T> {
    private final int bucketCount;
    private final double sideSize;
    private final double actionRadius;
    private final boolean includeSelf;
    private final Map<T, Collection<T>> particles = new HashMap<>();
    private boolean checkBruteForce = false;
    final Map<Cell, Collection<T>> buckets = new HashMap<>();


    public ParticleGrid(int bucketCount, double sideSize, double actionRadius, boolean includeSelf) {
        this.bucketCount = bucketCount;
        this.sideSize = sideSize;
        this.actionRadius = actionRadius;
        this.includeSelf = includeSelf;
        if (actionRadius > sideSize / bucketCount) {
            String msg = "action radius ({0}) can't be larger than half the bucket side size ({1})";
            throw new IllegalArgumentException(MessageFormat.format(msg, actionRadius, sideSize / bucketCount));
        }
    }

    private void addBothWays(T particle, T neighbor) {
        synchronized (this) {
            Collection<T> theirNeighborhood = particles.get(neighbor);
            Collection<T> myNeighborhood = particles.get(particle);

            theirNeighborhood.add(particle);
            myNeighborhood.add(neighbor);
        }
    }

    private void connectNeighborParticles(Map<Cell, Collection<T>> buckets, Cell cell) {
        Collection<T> particles = buckets.get(cell);

        semisphereNeighborhood(cell)
                .map(buckets::get)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .forEach(neighbor -> {
                    for (T particle : particles) {
                        if (Math.sqrt(particle.dist2(neighbor)) < actionRadius) {
                            addBothWays(particle, neighbor);
                        }
                    }
                });
    }

    @Override
    public Grid<T> set(Collection<T> originalParticles) {
        particles.clear();
        buckets.clear();

        originalParticles.forEach(theParticle -> {
            Pair position = theParticle.getPosition();
            int row = (int) Math.floor(position.getX() * getBucketCount() / getSideLength());
            int col = (int) Math.floor(position.getY() * getBucketCount() / getSideLength());
            if (includeSelf) {
                buckets.computeIfAbsent(new Cell(row, col), (cell) -> new LinkedList<>()).add(theParticle);
            }
            particles.put(theParticle, new LinkedList<>());
        });

        buckets.forEach((cell, bucket) -> {
            connectNeighborParticles(buckets, cell);
        });


        if (checkBruteForce) bruteForceCheck(originalParticles);

        return this;
    }

    private void bruteForceCheck(Collection<T> allParticles) {
        for (T someParticle : allParticles) {
            for (T anotherParticle : allParticles) {
                assert !(Math.sqrt(someParticle.dist2(anotherParticle)) < actionRadius) || getNeighbors(someParticle).contains(anotherParticle);
            }
        }
        allParticles.forEach(particle -> {
            getNeighbors(particle).forEach(neighbor -> {
                assert Math.sqrt(particle.dist2(neighbor)) < actionRadius;
            });
        });
        assert particles.size() == allParticles.size();
        assert actionRadius > allParticles.stream().mapToDouble(Particle::getRadius).max().orElse(0);
    }

    private Stream<Cell> semisphereNeighborhood(Cell cell) {
        int row = cell.getRow();
        int col = cell.getCol();
        Cell selfCell = new Cell(row, col);
        Cell upperCell = row < bucketCount - 1 ? new Cell(row + 1, col) : null;
        Cell upperRightCell = row < bucketCount - 1 && col < bucketCount - 1 ? new Cell(row + 1, col + 1) : null;
        Cell rightCell = col < bucketCount - 1 ? new Cell(row, col + 1) : null;
        Cell bottomRightCell = row > 0 && col < bucketCount - 1 ? new Cell(row - 1, col + 1) : null;
        return Stream.of(selfCell, upperCell, upperRightCell, rightCell, bottomRightCell).filter(Objects::nonNull);
    }

    @Override
    public Collection<T> getNeighbors(T particle) {
        return particles.getOrDefault(particle, Collections.emptyList());
    }

    @Override
    public boolean contains(T particle) {
        return particles.containsKey(particle);
    }

    @Override
    public boolean isPeriodic() {
        return false;
    }

    @Override
    public int getBucketCount() {
        return bucketCount;
    }

    @Override
    public double getSideLength() {
        return sideSize;
    }

    @Override
    public Collection<T> getParticles() {
        return particles.keySet();
    }

    @Override
    public int countParticles() {
        return particles.size();
    }

    private static final class Cell {
        private final int row;
        private final int col;

        public Cell(int row, int col) {
            this.row = row;
            this.col = col;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Cell cell = (Cell) o;
            return row == cell.row &&
                    col == cell.col;
        }

        @Override
        public int hashCode() {
            return Objects.hash(row, col);
        }

        public int getRow() {
            return row;
        }

        public int getCol() {
            return col;
        }
    }

    @Override
    public Collection<T> getWouldBeNeighbors(T theParticle) {
        Pair position = theParticle.getPosition();
        int row = (int) Math.floor(position.getX() * getBucketCount() / getSideLength());
        int col = (int) Math.floor(position.getY() * getBucketCount() / getSideLength());

        Cell leftCell = col > 1 ? new Cell(row, col - 1) : null;
        Cell selfCell = new Cell(row, col);
        Cell rightCell = col < bucketCount - 1 ? new Cell(row, col + 1) : null;

        Cell upperLeftCell = row < bucketCount - 1 && col > 1 ? new Cell(row + 1, col - 1) : null;
        Cell upperCell = row < bucketCount - 1 ? new Cell(row + 1, col) : null;
        Cell upperRightCell = row < bucketCount - 1 && col < bucketCount - 1 ? new Cell(row + 1, col + 1) : null;

        Cell bottomLeftCell = row > 0 && col > 1 ? new Cell(row - 1, col - 1) : null;
        Cell bottomCell = row > 0 ? new Cell(row - 1, col) : null;
        Cell bottomRightCell = row > 0 && col < bucketCount - 1 ? new Cell(row - 1, col + 1) : null;

        return Stream.of(
                upperLeftCell,  upperCell,  upperRightCell,
                leftCell,       selfCell,   rightCell,
                bottomLeftCell, bottomCell, bottomRightCell
        ).filter(Objects::nonNull)
                .map(buckets::get)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }
}
