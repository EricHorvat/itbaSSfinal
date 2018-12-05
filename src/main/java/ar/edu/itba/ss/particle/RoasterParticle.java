package ar.edu.itba.ss.particle;

public class RoasterParticle extends Particle{

    final private static double socialForceFactor = 5;
    private  BallState state = BallState.AttackingLeft;

    public RoasterParticle(int id, double x, double y, double vx, double vy, double m, double r) {
        super(id, x, y, vx, vy, m, r,-1, socialForceFactor);
    }

    public void changeState(){
        if(state == BallState.AttackingLeft || state == BallState.StandByAtLeft) {
            state = BallState.AttackingRight;
        }
        else {
            state = BallState.AttackingLeft;
        }
    }

    public BallState getState() {
        return state;
    }
    
    @Override
    public String getInfo() {
        return super.getInfo() + " 0.0";
    }
    
    
    public Pair[] getForce(Particle p) {
        return getGranularForce(p);
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
    
    @Override
    public void updateAceleration(Pair force) {
        super.updateAceleration(force);
        double signVx = Math.signum(velocity.x);
        double signVy = Math.signum(velocity.y);
        double abs2Vx = Math.pow(velocity.x,2);
        double abs2Vy = Math.pow(velocity.y,2);
        double rho = 1.2;
        double area = Math.PI * Math.pow(this.getRadius(), 2);
        double dragCoefficient = 0.47;
        this.acceleration.x -= signVx * 0.5 * rho * area * dragCoefficient * abs2Vx / this.getMass();
        this.acceleration.y -= signVy * 0.5 * rho * area * dragCoefficient * abs2Vy / this.getMass();
    }
}