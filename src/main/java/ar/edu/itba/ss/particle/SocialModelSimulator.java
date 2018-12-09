package ar.edu.itba.ss.particle;

import ar.edu.itba.ss.cell.Grid;
import ar.edu.itba.ss.cell.ParticleGrid;
import ar.edu.itba.ss.cli.CommandLineOptions;

import java.util.*;

public class SocialModelSimulator {

	private List<Player> particles;
	private double dt;
	Grid<Player> grid;
	private LinkedList<Player> toRemove;
	private Ball ball;
	private final CommandLineOptions options;

	public SocialModelSimulator(CommandLineOptions options, List<Player> particles, double dt, Ball ball) {
		final double L = Math.max(options.getLenght(), options.getWidth());

		this.options = options;
		this.particles = particles;
		this.dt = dt;
		estimateInitialLastPosition();
		toRemove = new LinkedList<>();
		this.ball = ball;
		double actionRadius = 5;//2.3;
		grid = new ParticleGrid<>((int) Math.ceil(L / (actionRadius * 2)), L, actionRadius, false);
	}

	private void estimateInitialLastPosition() {
		particles.forEach( p -> p.updateLastPosition(p.getOwnForce(), dt));
	}

	public void loop() {
		grid.set(particles);

		particles.forEach(p -> p.updateTarget(null));

		particles.forEach(p -> {
			Pair force = p.getOwnForce();
			particles.stream().filter(q -> !p.equals(q)).forEach(q -> {
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

		particles.removeAll(toRemove);
		toRemove.clear();
	}

	static double time = 0;

	private Pair wallForce(Player p) {
		final double W = options.getWidth();
		final double L = options.getLenght();

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

	private void updatePosition(Player p, double dt) {
		double rx = p.position.x + p.velocity.x * dt + 2.0/3 * p.acceleration.x * Math.pow(dt, 2) - 1.0 / 6 * p.lastAceleration.x * Math.pow(dt,2);
		double ry = p.position.y + p.velocity.y * dt + 2.0/3 * p.acceleration.y * Math.pow(dt, 2) - 1.0 / 6 * p.lastAceleration.y * Math.pow(dt,2);

		p.updatePosition(rx, ry);
		if(ry<0){
			toRemove.add(p);
		}
	}

	private void updateVelocity(Player p, double dt) {
		double vx = p.velocity.x + 2.0/3 * p.acceleration.x * dt - 1.0 / 6 * p.lastAceleration.x * dt;
		double vy = p.velocity.y + 2.0/3 * p.acceleration.y * dt - 1.0 / 6 * p.lastAceleration.y * dt;
		p.updateVelocity(vx, vy);
	}

	public int escapingParticles() {
		return particles.size();
	}
}
