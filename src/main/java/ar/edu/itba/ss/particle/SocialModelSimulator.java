package ar.edu.itba.ss.particle;

import ar.edu.itba.ss.cell.Grid;
import ar.edu.itba.ss.cell.ParticleGrid;
import ar.edu.itba.ss.cli.CommandLineOptions;

import java.util.*;
import java.util.stream.Collectors;

public class SocialModelSimulator {

	private final List<List<Player>> teams;
	private double dt;
	Grid<Player> grid;
	private Ball ball;
	private final CommandLineOptions options;
	private static final Random random = new Random();
	private final List<Double> timestamps = new LinkedList<>();
	private final List<Player> roastedPlayers = new LinkedList<>();
	static double time = 0;


	private static double getRandomNumber(double min, double max){
		return random.nextDouble() * (max - min) + min;
	}


	public SocialModelSimulator(CommandLineOptions options, List<List<Player>> teams, double dt, Ball ball) {
		final double L = Math.max(options.getLength(), options.getWidth()) * 2;

		this.options = options;
		this.teams = teams;
		this.dt = dt;
		estimateInitialLastPosition();
		this.ball = ball;
		double actionRadius = 5;//2.3;
		grid = new ParticleGrid<>((int) Math.ceil(L / (actionRadius * 2)), L, actionRadius, false);
	}

	private void estimateInitialLastPosition() {
		teams.forEach(particles -> particles.forEach( p -> p.updateLastPosition(p.getOwnForce(), dt)));
	}

	public void loop() {
		List<Player> particles = teams.stream().flatMap(List::stream).collect(Collectors.toList());
		grid.set(particles);

		particles.forEach(p -> {
			Pair force = p.getOwnForce();
			teams.get(p.getTeam()).stream().filter(q -> !p.equals(q)).forEach(q -> {
				Pair[] forceComponents = p.getForce(q);
				force.add(Pair.sum(forceComponents[0], forceComponents[1]));
				p.addPressure(forceComponents[0]);
			});
			p.updateAceleration(Pair.sum(force, wallForce(p)));
		});


		time += dt;

		particles.parallelStream().forEach(p -> {
            updatePosition(p, dt);
            updateVelocity(p, dt);
        });

		updateBall();

	}

	private Pair wallForce(Player p) {
		final double W = options.getWidth();
		final double L = options.getLength();

		Pair sum = new Pair(0, 0);
		if (p.position.x - p.getRadius() - W * p.getTeam() < 0 && p.position.y > 0) {
			Pair[] force = SocialModel.checkWallLeft(p);
			sum.add(Pair.sum(force[0], force[1]));
			p.addPressure(force[0]);
		}
		if (p.position.x + p.getRadius() - W * p.getTeam() > W  && p.position.y > 0) {
			Pair[] force = SocialModel.checkWallRight(p);
			sum.add(Pair.sum(force[0], force[1]));
			p.addPressure(force[0]);
		}
		if (p.position.y - p.getRadius() < 0 && p.position.x > 0) {
			Pair[] force = SocialModel.checkWallBottom(p);
			sum.add(Pair.sum(force[0], force[1]));
			p.addPressure(force[0]);
		}
		if (p.position.y + p.getRadius() > L && p.position.x > 0) {
			Pair[] force = SocialModel.checkWallCeil(p);
			sum.add(Pair.sum(force[0], force[1]));
			p.addPressure(force[0]);
		}
		return sum;
	}

	private void updateBall() {
		ball.updatePosition(ball.getX() + ball.velocity.getX() * dt, ball.getY() + ball.velocity.getY() * dt);

		BallState state = ball.getState();
		int court = (state == BallState.StandByAtLeft || state == BallState.AttackingLeft) ? 0 : 1;
		List<Player> rivalTeam = teams.get(court == 0 ? 1 : 0);
        List<Player> team = teams.get(court);

        //TODO: check for bounds here
		Pair position = ball.getPosition();
		if (position.getY() > options.getLength() || position.getY() < 0
				|| position.getX() < 0 || position.getX() > options.getWidth() * 2) {
			ball.changeState();
			ball.updateVelocity(0, 0);
			double r = ball.getRadius();
			double W = options.getWidth();
			double L = options.getLength();
			double x = getRandomNumber(r, W - 2 * r) + court * W + r;
			double y = getRandomNumber(r, L - 2 * r) + r;
			ball.updatePosition(x, y);
            team.stream().min(Comparator.comparing(ball::dist2)).ifPresent(nearest -> {
                nearest.updateTarget(ball.getPosition());
            });
            return;
		}

        Optional<Player> catcher = team.stream().filter(p -> Particle.areOverlapped(p, ball)).findFirst();

        if (!catcher.isPresent()) {
            return;
        }

		if (state == BallState.StandByAtLeft || state == BallState.StandByAtRight) {
            catcher.get().updateTarget(null);
            ball.changeState();
            // throw ball
			Player braulio = rivalTeam.get(rivalTeam.size() > 1 ? random.nextInt(teams.size()) : 0);
			Pair target = braulio.getPosition().substract(ball.getPosition());
			target.normalize();
			ball.updateVelocity(20 * target.getX(), 20 * target.getY());

            // set response times for the rival team
			// clear my team's reaction time
		} else {
        	ball.updateVelocity(0, 0);
        	Player roasted = catcher.get();
			ball.updatePosition(roasted.getX(), roasted.getY());
			System.out.println(String.format("%d got roasted at %f", roasted.getId(), time));
        	timestamps.add(time);
        	roastedPlayers.add(roasted);
        	team.remove(roasted);
        	ball.changeState();
			team.stream().min(Comparator.comparing(ball::dist2)).ifPresent(nearest -> {
				nearest.updateTarget(ball.getPosition());
			});
			// set reaction time for the rival team
		}
	}

	private void updatePosition(Player p, double dt) {
		double rx = p.position.x + p.velocity.x * dt + 2.0/3 * p.acceleration.x * Math.pow(dt, 2) - 1.0 / 6 * p.lastAceleration.x * Math.pow(dt,2);
		double ry = p.position.y + p.velocity.y * dt + 2.0/3 * p.acceleration.y * Math.pow(dt, 2) - 1.0 / 6 * p.lastAceleration.y * Math.pow(dt,2);

		p.updatePosition(rx, ry);
	}

	private void updateVelocity(Player p, double dt) {
		double vx = p.velocity.x + 2.0/3 * p.acceleration.x * dt - 1.0 / 6 * p.lastAceleration.x * dt;
		double vy = p.velocity.y + 2.0/3 * p.acceleration.y * dt - 1.0 / 6 * p.lastAceleration.y * dt;
		p.updateVelocity(vx, vy);
	}

	public int escapingParticles() {
		return teams.stream().mapToInt(List::size).min().orElse(0);
	}

	public List<Double> getTimestamps() {
		return timestamps;
	}

	public List<Player> getRoastedPlayers() {
		return roastedPlayers;
	}
}
