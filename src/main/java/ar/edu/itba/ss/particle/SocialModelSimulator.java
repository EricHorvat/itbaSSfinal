package ar.edu.itba.ss.particle;

import ar.edu.itba.ss.cell.CellIndexMethod;

import java.util.*;

import static ar.edu.itba.ss.data.Data.*;

public class SocialModelSimulator {

	private List<RoastedParticle> particles;
	private double dt;
	private CellIndexMethod<RoastedParticle> cellIndexMethod;
	private LinkedList<RoastedParticle> toRemove;

	public SocialModelSimulator(List<RoastedParticle> particles, double dt) {
		this.particles = particles;
		this.dt = dt;
		estimateInitialLastPosition();
		cellIndexMethod = new CellIndexMethod<>(particles, 2*W, 2.3, 1);
		toRemove = new LinkedList<>();
	}

	private void estimateInitialLastPosition() {
		for (RoastedParticle p : particles) {
			p.updateLastPosition(p.getOwnForce(), dt);
		}
	}

	public void loop() {
		Map<RoastedParticle, Pair> forces = new HashMap<>();
		Map<RoastedParticle, Set<RoastedParticle>> neighbours = cellIndexMethod.getNeighboursMap();
		for (RoastedParticle p : neighbours.keySet()) {
			Pair force = p.getOwnForce();
			for (RoastedParticle q : neighbours.get(p)) {
				Pair[] forceComponents = p.getForce(q);
				force.add(Pair.sum(forceComponents[0], forceComponents[1]));
				p.addPressure(forceComponents[0]);
			}
			force = Pair.sum(force, wallForce(p));
			p.updateAceleration(force);
		}

		time += dt;
		for (RoastedParticle p : neighbours.keySet()) {
			Pair lastPosition = p.getLastPosition();
			updatePosition(p, dt);
			updateVelocity(p, dt);
		}
		while(!toRemove.isEmpty()){
			RoastedParticle p = toRemove.removeFirst();
			particles.remove(p);
		}
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
		double rx = p.position.x + p.velocity.x * dt + 2.0/3 * p.aceleration.x * Math.pow(dt, 2) - 1.0 / 6 * p.lastAceleration.x * Math.pow(dt,2);
		double ry = p.position.y + p.velocity.y * dt + 2.0/3 * p.aceleration.y * Math.pow(dt, 2) - 1.0 / 6 * p.lastAceleration.y * Math.pow(dt,2);

		p.updatePosition(rx, ry);
		if(ry<0){
			toRemove.add(p);
		}
	}

	private void updateVelocity(RoastedParticle p, double dt) {
		double vx = p.velocity.x + 2.0/3 * p.aceleration.x * dt - 1.0 / 6 * p.lastAceleration.x * dt;
		double vy = p.velocity.y + 2.0/3 * p.aceleration.y * dt - 1.0 / 6 * p.lastAceleration.y * dt;
		p.updateVelocity(vx, vy);
	}
	
	public int escapingParticles() {
		return particles.size();
	}
}
