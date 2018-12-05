package ar.edu.itba.ss.particle;

import ar.edu.itba.ss.cell.CellIndexMethod;
import ar.edu.itba.ss.cell.Grid;
import ar.edu.itba.ss.cell.ParticleGrid;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static ar.edu.itba.ss.data.Data.L;
import static ar.edu.itba.ss.data.Data.W;
import static ar.edu.itba.ss.particle.SocialModel.getContactForce;

public class BallModelSimulator {
	private double dt;
	Grid<RoastedParticle> grid;
	private CellIndexMethod<RoastedParticle> cellIndexMethod;
	private List<RoasterParticle> ballsSack;
	private Random random;
	
	public BallModelSimulator(List<RoasterParticle> ballsSack, double dt) {
		this.dt = dt;
		//estimateInitialLastPosition();
		this.ballsSack = ballsSack;
		double actionRadius = 5;//2.3;
		grid = new ParticleGrid<>((int) Math.ceil(L / (actionRadius * 2)), L, actionRadius, false);
		random = new Random();
	}
	
	/*private void estimateInitialLastPosition() {
		ballsSack.forEach( b -> b.updateLastPosition(b.11getOwnForce(), dt));
	}*/
	
	public void loop(List<Particle> particles) {
		//grid.set(particles);
		//Map<RoastedParticle, Set<RoastedParticle>> neighbours = cellIndexMethod.getNeighboursMap();
		
		ballsSack.forEach(b -> {
			Pair force = new Pair(0,0);
			ballsSack.stream().filter(q -> !b.equals(q)).forEach(q -> {
				Pair[] forceComponents = b.getForce(q);
				force.add(Pair.sum(forceComponents[0], forceComponents[1]));
				//p.addPressure(forceComponents[0]);
			});
			particles.forEach(p -> {
				Pair[] ballForce = b.getForce(p);
				force.add(Pair.sum(ballForce[0], ballForce[1]));
			});
			b.updateAceleration(Pair.sum(force, wallForce(b)));
		});
		
		
		time += dt;
		
		/*TODO REPLACE THIS WITH PEOPLE THROWING BALLS*/
		ballsSack.forEach(b -> {
			if (b.velocity.getX() == 0 && b.velocity.getY() == 0 && random.nextDouble() < 0.1){
				b.velocity = new Pair(random.nextDouble() * 10 - 5,(random.nextDouble() * 10 - 5)*0.01);
			}
		});
		/*TODO END REPLACE*/
		
		ballsSack.parallelStream().forEach(b -> {
			updatePosition(b, dt);
			updateVelocity(b, dt);
		});
		
	}
	
	static double time = 0;
	
	private Pair wallForce(RoasterParticle p) {
		Pair sum = new Pair(0, 0);
		if (p.position.x - p.getRadius() < 0 && p.position.y > 0) {
			Pair[] force = checkWallLeft(p);
			sum.add(Pair.sum(force[0], force[1]));
			//p.addPressure(force[0]);
		}
		if (p.position.x + p.getRadius() > 2 * W  && p.position.y > 0) {
			Pair[] force = checkWallRight(p);
			sum.add(Pair.sum(force[0], force[1]));
			//p.addPressure(force[0]);
		}
		if (p.position.y - p.getRadius() < 0 && p.position.x > 0) {
			Pair[] force = checkWallBottom(p);
			sum.add(Pair.sum(force[0], force[1]));
			//p.addPressure(force[0]);
		}
		if (p.position.y + p.getRadius() > L && p.position.x > 0) {
			Pair[] force = checkWallCeil(p);
			sum.add(Pair.sum(force[0], force[1]));
			//p.addPressure(force[0]);
		}
		return sum;
	}
	
	
	public static Pair[] checkWallLeft(Particle p) {
		double eps = p.getRadius() - p.getX();
		return getContactForce(p.getVelocity(), eps, new Pair(-1, 0), new Pair(0, -1));
	}
	
	public static Pair[] checkWallRight(Particle p) {
		double eps = p.getX() - W * 2 + p.getRadius();
		return getContactForce(p.getVelocity(), eps, new Pair(1, 0), new Pair(0, 1));
	}
	
	public static Pair[] checkWallBottom(Particle p) {
		double eps = p.getRadius() - p.getY();
		return getContactForce(p.getVelocity(), eps, new Pair(0, -1), new Pair(1, 0));
	}
	
	public static Pair[] checkWallCeil(Particle p) {
		double eps = p.getY() - L + p.getRadius();
		return getContactForce(p.getVelocity(), eps, new Pair(0, 1), new Pair(-1, 0));
	}
	
	
	private void updatePosition(RoasterParticle p, double dt) {
		double rx = p.position.x + p.velocity.x * dt + 2.0/3 * p.acceleration.x * Math.pow(dt, 2) - 1.0 / 6 * p.lastAceleration.x * Math.pow(dt,2);
		double ry = p.position.y + p.velocity.y * dt + 2.0/3 * p.acceleration.y * Math.pow(dt, 2) - 1.0 / 6 * p.lastAceleration.y * Math.pow(dt,2);
		
		p.updatePosition(rx, ry);
	}
	
	private void updateVelocity(RoasterParticle p, double dt) {
		double vx = p.velocity.x + 2.0/3 * p.acceleration.x * dt - 1.0 / 6 * p.lastAceleration.x * dt;
		double vy = p.velocity.y + 2.0/3 * p.acceleration.y * dt - 1.0 / 6 * p.lastAceleration.y * dt;
		p.updateVelocity(vx, vy);
	}
}
