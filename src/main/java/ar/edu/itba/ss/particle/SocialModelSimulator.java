package ar.edu.itba.ss.particle;

import ar.edu.itba.ss.cell.CellIndexMethod;

import java.util.*;

import static ar.edu.itba.ss.data.Data.*;

public class SocialModelSimulator {

	private List<EscapingParticle> particles;
	private double dt;
	private CellIndexMethod<EscapingParticle> cellIndexMethod;
	private List<EscapingParticle> borderParticles;
	private LinkedList<EscapingParticle> toRemove;

	public SocialModelSimulator(List<EscapingParticle> particles, double dt) {
		this.particles = particles;
		this.dt = dt;
		estimateInitialLastPosition();
		borderParticles = new LinkedList<>();
		borderParticles.add(new EscapingParticle(0, (W - D) / 2.0, floorLevel, 0, 0, 0, 0));
		borderParticles.add(new EscapingParticle(0, (W + D) / 2.0, floorLevel, 0, 0, 0, 0));
		cellIndexMethod = new CellIndexMethod<>(particles, L + floorLevel, 2.2, 1);
		toRemove = new LinkedList<>();
	}

	private void estimateInitialLastPosition() {
		for (EscapingParticle p : particles) {
			p.updateLastPosition(p.getOwnForce(), dt);
		}
	}

	public void loop() {
		Map<EscapingParticle, Pair> forces = new HashMap<>();
		Map<EscapingParticle, Set<EscapingParticle>> neighbours = cellIndexMethod.getNeighboursMap();
		for (EscapingParticle p : neighbours.keySet()) {
			Pair force = p.getOwnForce();
			for (EscapingParticle q : neighbours.get(p)) {
				Pair[] forceComponents = p.getForce(q);
				force.add(Pair.sum(forceComponents[0], forceComponents[1]));
				p.addPressure(forceComponents[0]);
			}
			force = Pair.sum(force, wallForce(p));
			p.updateAceleration(force);
		}

		time += dt;
		for (EscapingParticle p : neighbours.keySet()) {
			Pair lastPosition = p.getLastPosition();
			updatePosition(p, dt);
			updateVelocity(p, dt);
		}
		while(!toRemove.isEmpty()){
			EscapingParticle p = toRemove.removeFirst();
			particles.remove(p);
		}
	}

	static double time = 0;

	private Pair wallForce(EscapingParticle p) {
		Pair sum = new Pair(0, 0);
		if (p.position.x - p.getRadius() < 0 && p.position.y > floorLevel) {
			Pair[] force = SocialModel.checkWallLeft(p);
			sum.add(Pair.sum(force[0], force[1]));
			p.addPressure(force[0]);
		}
		if (p.position.x + p.getRadius() > W && p.position.y > floorLevel) {
			Pair[] force = SocialModel.checkWallRight(p);
			sum.add(Pair.sum(force[0], force[1]));
			p.addPressure(force[0]);
		}
		if (Math.abs(p.position.y - floorLevel) < p.getRadius()) {
			if (inDoor(p)) {
				for (EscapingParticle borderParticle : borderParticles) {
					Pair[] forceComponents = p.getForce(borderParticle);
					sum.add(Pair.sum(forceComponents[0], forceComponents[1]));
					p.addPressure(forceComponents[0]);
				}
			} else {
				Pair[] force = SocialModel.checkWallBottom(p);
				sum.add(Pair.sum(force[0], force[1]));
				p.addPressure(force[0]);
			}
		}
		return sum;
	}

	private boolean inDoor(EscapingParticle Particle) {
		double x = Particle.getX();
		double midW = W / 2;
		double midD = D / 2;
		return x >= midW - midD && x <= midW + midD;
	}

	private void updatePosition(EscapingParticle p, double dt) {
		double rx = p.position.x + p.velocity.x * dt + 2.0/3 * p.aceleration.x * Math.pow(dt, 2) - 1.0 / 6 * p.lastAceleration.x * Math.pow(dt,2);
		double ry = p.position.y + p.velocity.y * dt + 2.0/3 * p.aceleration.y * Math.pow(dt, 2) - 1.0 / 6 * p.lastAceleration.y * Math.pow(dt,2);

		p.updatePosition(rx, ry);
		if(ry<0){
			toRemove.add(p);
		}
	}

	private void updateVelocity(EscapingParticle p, double dt) {
		double vx = p.velocity.x + 2.0/3 * p.aceleration.x * dt - 1.0 / 6 * p.lastAceleration.x * dt;
		double vy = p.velocity.y + 2.0/3 * p.aceleration.y * dt - 1.0 / 6 * p.lastAceleration.y * dt;
		p.updateVelocity(vx, vy);
	}
	
	public int escapingParticles() {
		return particles.size();
	}
}
