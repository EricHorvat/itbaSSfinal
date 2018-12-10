package ar.edu.itba.ss.particle;

public class Player extends Particle {
	
	private Pair lastPosition;
	private Pair targetPosition;
	private double pressure;
	private double throwingVelocity;

	public Player(int id, double x, double y, double vx, double vy, double m, double r, int team) {
		super(id, x, y, vx, vy, m, r, team);
		lastPosition = new Pair(0,0);
		//targetPosition = new Pair(W / 2, 0);
		targetPosition = null;
	}
	
	public Pair getOwnForce() {
		return getDrivingForce();
	}
	
	private Pair getDrivingForce() {
		if (targetPosition == null) {
			return new Pair(0, 0);
		}

		Pair dir = Pair.less(targetPosition, position);
		if (dir.x != 0 || dir.y != 0){
			dir.normalize();
		}
		return SocialModel.getDrivingForce(getMass(), velocity, dir);
	}

	private Pair getSocialForce(Particle p) {
		Pair dir = Pair.less(position, p.position);
		double eps = dir.abs() - p.getRadius() - getRadius();
		if (eps > 1 || p instanceof Ball) {
			return new Pair(0, 0);
		}
		dir.normalize();
		return SocialModel.getSocialForce(dir, eps);
	}
	
	private Pair[] getGranularForce(Particle p) {
		Pair dir = Pair.less(p.position, position);
		double eps = p.getRadius() + getRadius() - dir.abs();
		if (eps < 0) {
			return new Pair[] { new Pair(0, 0), new Pair(0, 0) };
		}
		dir.normalize();
		return SocialModel.getContactForce(Pair.less(velocity, p.velocity), eps, dir, new Pair(-dir.y, dir.x));
	}
	
	public Pair[] getForce(Particle p) {
		Pair[] granularForce = getGranularForce(p);
		Pair socialForce = getSocialForce(p);
		Pair n = Pair.sum(granularForce[0], socialForce);
		return new Pair[] {n, granularForce[1]};
	}
	
	public void updatePosition(double x, double y) {
		this.lastPosition = position;
		position = new Pair(x, y);
	}
	
	public Pair getLastPosition() {
		return this.lastPosition;
	}
	
	public void updateLastPosition(Pair force, double dt) {
		double x = position.x + (-dt) * velocity.x + (dt * dt) * force.x / (2 * getMass());
		double y = position.y + (-dt) * velocity.y + (dt * dt) * force.y / (2 * getMass());
		lastPosition.reset(x, y);
	}
	
	public double getPressure() {
		return pressure;
	}
	
	public void addPressure(Pair normalForce) {
		this.pressure += normalForce.abs() / getArea();
	}
	
	public void resetPressure() {
		pressure = 0;
	}
	
	public double getKineticEnergy() {
		return 0.5 * getMass() * Math.pow(getSpeed(), 2);
	}
	
	private double getArea() {
		return 2 * Math.PI * getRadius();
	}

	public double getThrowingVelocity() {
		return throwingVelocity;
	}


	@Override
	public String getInfo() {
		return super.getInfo() + " " + getPressure();
	}
	
	@Override
	public void collision(){
		//updateTargetPosition();
	}
	
	private void updateTargetPosition(/**/){
		/*TODO GET */
	}
	
	public void updateTarget(Pair targetPosition){
		this.targetPosition = targetPosition;
	}
	
}
