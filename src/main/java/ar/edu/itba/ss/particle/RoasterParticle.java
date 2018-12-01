package ar.edu.itba.ss.particle;

public class RoasterParticle extends Particle{

    final private static double socialForceFactor = 20;
    private  BallState state = BallState.AttackingLeft;

    RoasterParticle(int id, double x, double y, double vx, double vy, double m, double r) {
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
}