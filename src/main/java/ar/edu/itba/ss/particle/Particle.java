package ar.edu.itba.ss.particle;

import java.util.Objects;

public class Particle {
	
	private int id;
	Pair position;
	Pair velocity;
	Pair acceleration;
	Pair lastAceleration;
	private double radius;
	private double mass;
	private int team;

	Particle(int id, double x, double y, double vx, double vy, double m, double r, int team) {
		this.id = id;
		this.position = new Pair(x, y);
		this.velocity = new Pair(vx, vy);
		this.acceleration = new Pair(0, 0);
		this.lastAceleration = new Pair(0, 0);
		this.mass = m;
		this.radius = r;
		this.team = team;
	}

	public int getId() {
		return id;
	}

	public double getX() {
		return position.x;
	}
	
	public double getY(){
		return position.y;
	}
	
	public Pair getPosition() {
		return position;
	}
	
	public Pair getVelocity(){
		return velocity;
	}
	
	public double getMass(){
		return mass;
	}
	
	public double getRadius() {
		return radius;
	}
	
	public int getTeam() {
		return team;
	}
	
	public void updatePosition(double x, double y) {
		this.position = new Pair(x, y);
	}
	
	public void updateVelocity(double x, double y) {
		this.velocity = new Pair(x, y);

	}
	
	public void updateAceleration(Pair force) {
		this.lastAceleration = acceleration;
		this.acceleration = new Pair(force.x/mass, force.y/mass);
	}

	
	public static <T extends Particle> boolean areOverlapped(T p, T q){
		return Pair.dist2(p.position, q.position) <= Math.pow(p.getRadius()+q.getRadius(),2);
	}

	public double dist2(Particle particle) {
		return Pair.dist2(this.position, particle.position);
	}

	public double getSpeed() {
		return velocity.abs();
	}

	@Override
	public int hashCode() {
		return Objects.hash(getId());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Particle other = (Particle) obj;
		if (id != other.id)
			return false;
		return true;
	}

	public String getInfo() {
		return getId() + " " + getX() + " " + getY() + " " + getRadius();
	}

	public void collision(){}
}
