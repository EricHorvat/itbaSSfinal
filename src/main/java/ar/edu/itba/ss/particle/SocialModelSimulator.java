package ar.edu.itba.ss.particle;

import ar.edu.itba.ss.cell.CellIndexMethod;
import ar.edu.itba.ss.cell.Grid;
import ar.edu.itba.ss.cell.ParticleGrid;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static ar.edu.itba.ss.data.Data.*;

public class SocialModelSimulator {

	private List<RoastedParticle> particles;
	private double dt;
	Grid<RoastedParticle> grid;
	private LinkedList<RoastedParticle> toRemove;
	private CellIndexMethod<RoastedParticle> cellIndexMethod;
	private List<RoasterParticle> ballsSack;

	public SocialModelSimulator(List<RoastedParticle> particles, double dt, List<RoasterParticle> ballsSack) {
		this.particles = particles;
		this.dt = dt;
		estimateInitialLastPosition();
		toRemove = new LinkedList<>();
		this.ballsSack = ballsSack;
		double actionRadius = 5;//2.3;
		grid = new ParticleGrid<>((int) Math.ceil(L / (actionRadius * 2)), L, actionRadius, false);
	}

	private void estimateInitialLastPosition() {
		particles.forEach( p -> p.updateLastPosition(p.getOwnForce(), dt));
	}

	public void loop() {
		//grid.set(particles);
		//Map<RoastedParticle, Set<RoastedParticle>> neighbours = cellIndexMethod.getNeighboursMap();
		
		particles.forEach(p -> {
			p.updateTarget(p.getPosition());
			Pair force = p.getOwnForce();
			particles.stream().filter(q -> !p.equals(q)).forEach(q -> {
				Pair[] forceComponents = p.getForce(q);
				force.add(Pair.sum(forceComponents[0], forceComponents[1]));
				ballsSack.stream().forEach(ball -> {
					Pair[] ballForce = p.getForce(ball);
					force.add(Pair.sum(ballForce[0], ballForce[1]));
				});
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

	private Pair wallForce(RoastedParticle p) {
		Pair sum = new Pair(0, 0);
		if (p.position.x - p.getRadius() + W * p.getTeam() < 0 && p.position.y > 0) {
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

	private void updatePosition(RoastedParticle p, double dt) {
		double rx = p.position.x + p.velocity.x * dt + 2.0/3 * p.acceleration.x * Math.pow(dt, 2) - 1.0 / 6 * p.lastAceleration.x * Math.pow(dt,2);
		double ry = p.position.y + p.velocity.y * dt + 2.0/3 * p.acceleration.y * Math.pow(dt, 2) - 1.0 / 6 * p.lastAceleration.y * Math.pow(dt,2);

		p.updatePosition(rx, ry);
		if(ry<0){
			toRemove.add(p);
		}
	}

	private void updateVelocity(RoastedParticle p, double dt) {
		double vx = p.velocity.x + 2.0/3 * p.acceleration.x * dt - 1.0 / 6 * p.lastAceleration.x * dt;
		double vy = p.velocity.y + 2.0/3 * p.acceleration.y * dt - 1.0 / 6 * p.lastAceleration.y * dt;
		p.updateVelocity(vx, vy);
	}

	public int escapingParticles() {
		return particles.size();
	}
}
