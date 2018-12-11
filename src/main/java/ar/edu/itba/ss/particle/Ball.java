package ar.edu.itba.ss.particle;

public class Ball extends Particle{

    public  BallState state = BallState.AttackingLeft;

    public Ball(int id, double x, double y, double vx, double vy, double m, double r) {
        super(id, x, y, vx, vy, m, r,-1);
    }

    public void changeState() {
        switch (state) {
            case AttackingLeft:
                state = BallState.StandByAtLeft;
                break;
            case StandByAtLeft:
                state = BallState.AttackingRight;
                break;
            case AttackingRight:
                state = BallState.StandByAtRight;
                break;
            case StandByAtRight:
                state = BallState.AttackingLeft;
                break;
            default:
                throw new IllegalStateException("WTF?");
        }
    }

    public BallState getState() {
        return state;
    }
    
    @Override
    public String getInfo() {
        return super.getInfo() + " 0.0";
    }

    @Override
    public void tick(double time) {

    }
}