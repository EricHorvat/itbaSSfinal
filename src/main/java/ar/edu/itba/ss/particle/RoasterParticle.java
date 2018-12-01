package ar.edu.itba.ss.particle;

public class RoasterParticle extends Particle{

    final private static double socialForceFactor = 2;

    public RoasterParticle(int id, double x, double y, double vx, double vy, double m, double r) {
        super(id, x, y, vx, vy, m, r, socialForceFactor);
    }
}
