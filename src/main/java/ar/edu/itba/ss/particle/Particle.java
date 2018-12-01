package ar.edu.itba.ss.particle;

public class Particle {
	
	private int id;
	Pair position;
	Pair velocity;
	Pair aceleration;
	Pair lastAceleration;
	private double radius;
	private double mass;

	private double socialForceFactor = 1;

	Particle(int id, double x, double y, double vx, double vy, double m, double r, double sff) {
		this.id = id;
		this.position = new Pair(x, y);
		this.velocity = new Pair(vx, vy);
		this.aceleration= new Pair(0, 0);
		this.lastAceleration = new Pair(0, 0);
		this.mass = m;
		this.radius = r;
		this.socialForceFactor = sff;
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
	
	public void updatePosition(double x, double y) {
		this.position = new Pair(x, y);
	}
	
	public void updateVelocity(double x, double y) {
		this.velocity = new Pair(x, y);

	}
	
	public void updateAceleration(Pair force) {
		this.lastAceleration = aceleration;
		this.aceleration = new Pair(force.x/mass, force.y/mass);
	}

	
	public static <T extends Particle> boolean areOverlapped(T p, T q){
		return Pair.dist2(p.position, q.position) <= Math.pow(p.getRadius()+q.getRadius(),2);
	}

	public double getSpeed() {
		return velocity.abs();
	}

	public double getSocialForceFactor() {
		return socialForceFactor;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
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
