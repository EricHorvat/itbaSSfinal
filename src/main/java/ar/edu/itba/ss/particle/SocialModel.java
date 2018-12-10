package ar.edu.itba.ss.particle;

import ar.edu.itba.ss.cli.CommandLineOptions;

public class SocialModel {
	public static CommandLineOptions options;

	public static Pair[] checkWallRight(Particle p) {
		final double W = options.getWidth();
		double eps = p.getX() - W * (1 + p.getTeam()) + p.getRadius();
		return getContactForce(p.getVelocity(), eps, new Pair(1, 0), new Pair(0, 1));
	}

	public static Pair[] checkWallLeft(Particle p) {
		final double W = options.getWidth();
		double eps = p.getRadius() - p.getX() + W * p.getTeam();
		return getContactForce(p.getVelocity(), eps, new Pair(-1, 0), new Pair(0, -1));
	}
	
	public static Pair[] checkWallBottom(Particle p) {
		double eps = p.getRadius() - p.getY();
		return getContactForce(p.getVelocity(), eps, new Pair(0, -1), new Pair(1, 0));
	}
	
	public static Pair[] checkWallCeil(Particle p) {
		final double L = options.getLength();
		double eps = p.getY() - L + p.getRadius();
		return getContactForce(p.getVelocity(), eps, new Pair(0, 1), new Pair(-1, 0));
	}

	public static Pair[] getContactForce(Pair relativeVelocity, double eps, Pair norm, Pair tang) {
		final double Kn = options.getKn();
		final double Kt = options.getKt();

		Pair n = norm.clone();
		n.applyFunction(cord -> (-Kn * eps * cord));
		double velTang = Pair.internalProd(relativeVelocity, tang);
		Pair t = new Pair(-Kt * eps * velTang * tang.x, -Kt * eps * velTang * tang.y);
		return new Pair[] {n, t};
	}

	public static Pair getDrivingForce(Double mass, Pair velocity, Pair normal) {
		final double TAU = options.getTAU();
		final double desiredVelocity = options.getSpeed();

		Pair n = normal.clone();
		n.multiply(desiredVelocity);
		Pair f = Pair.less(n, velocity);
		f.multiply(mass / TAU);
		return f;
	}

	public static Pair getSocialForce(Pair normal, double e) {
		final double A = options.getA();
		final double B = options.getB();
		Pair n = normal.clone();
		n.multiply(A * Math.exp(-e/B));
		return n;
	}
}
