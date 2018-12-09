package ar.edu.itba.ss.particle;

public class Ball extends Particle{

    private  BallState state = BallState.AttackingLeft;

    public Ball(int id, double x, double y, double vx, double vy, double m, double r) {
        super(id, x, y, vx, vy, m, r,-1);
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
}