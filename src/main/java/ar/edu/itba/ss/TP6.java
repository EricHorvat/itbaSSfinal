package ar.edu.itba.ss;

import ar.edu.itba.ss.output.Output;
import ar.edu.itba.ss.output.OutputStat;
import ar.edu.itba.ss.particle.EscapingParticle;
import ar.edu.itba.ss.particle.Particle;
import ar.edu.itba.ss.particle.SocialModelSimulator;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import static ar.edu.itba.ss.data.Data.*;

public class TP6 {

    private static final Random random = new Random();

	private static double getRandomNumber(double min, double max){
		return random.nextDouble() * (max - min) + min;
	}

	private static EscapingParticle createRandomParticle() {
		double r = getRandomNumber(RAD_MIN, RAD_MAX) / 2.0;
		double x = getRandomNumber(0, W - 2 * r)+r;
		double y = getRandomNumber(0, L - 2 * r) + floorLevel + r;
		return new EscapingParticle(id_count, x, y, 0, 0, mass, r);
	}

	private static List<EscapingParticle> generateParticles(int N) {
		List<EscapingParticle> list = new ArrayList<>();
		boolean overlap;
		id_count = 1;
		while (id_count - 1 < N) {
			overlap = false;
			EscapingParticle newParticle = createRandomParticle();
			for (EscapingParticle otherParticle : list) {
				if (Particle.areOverlapped(otherParticle, newParticle)) {
					overlap = true;
					break;
				}
			}
			if (!overlap) {
				list.add(newParticle);
				id_count++;
			}
		}
		return list;
	}

	private static void systemSimulation(int loop){
		String desiredVelocityStr = String.format(Locale.US,"%.2f",desiredVelocity);
		Output ovitoFile = new Output("ovitoFile-"+desiredVelocityStr+"dVel-"+loop+"time.txt");
		OutputStat peopleFile = new OutputStat("people-"+desiredVelocityStr+"dVel-"+loop+"time.txt");
		OutputStat largePeopleFile = new OutputStat("largePeople-"+desiredVelocityStr+"dVel-"+loop+"time.txt");
		OutputStat diffPeopleFile = new OutputStat("diffPeople-"+desiredVelocityStr+"dVel-"+loop+"time.txt");
		OutputStat maxPressureFile = new OutputStat("maxPressure-"+desiredVelocityStr+"dVel-"+loop+"time.txt");
		List<EscapingParticle> particles = generateParticles(N);
		SocialModelSimulator socialModelSimulator = new SocialModelSimulator(particles, dt);
		
		double time = 0.0;
		double lastTime = - dt2 - 1.0;
		double maxPressure = 0.0;
		int localdiff = 0;
		int totalDiff = 0;

		while (socialModelSimulator.escapingParticles() > 0) {
			if (lastTime + dt2 < time) {
				ovitoFile.printState(particles);
				double mp = particles.stream().mapToDouble(EscapingParticle::getPressure).max().getAsDouble();
				particles.forEach(EscapingParticle::resetPressure);
				if (maxPressure < mp) {
					maxPressure = mp;
				}
				lastTime = time;
				diffPeopleFile.addStat(Integer.toString(totalDiff - localdiff));
				localdiff = totalDiff;
			}
			largePeopleFile.addStat(Integer.toString(totalDiff));
			socialModelSimulator.loop();
			int diff = getDifPeople(particles);
			totalDiff += diff;
			while (diff > 0){
				peopleFile.addStat(Double.toString(time));
				System.out.println("Tdiff: " + totalDiff);
				System.out.println("Time elapsed: " + time);
				diff--;
			}
			time += dt;
		}
		System.out.println("Time elapsed: " + time);
		maxPressureFile.addStat(Double.toString(maxPressure));
		maxPressureFile.writeFile();
		peopleFile.writeFile();
		largePeopleFile.writeFile();
		diffPeopleFile.writeFile();
	}
	

	public static void main(String[] args){
		double timee = System.currentTimeMillis();
		for (double dVelocity : desiredVelocities){
			desiredVelocity = dVelocity;
			for (int time = 0; time < times; time++){
				systemSimulation(time);
			}
		}
		System.out.println(System.currentTimeMillis() - timee);
	}

	private static int getDifPeople(List<EscapingParticle> particles) {
		int diff = 0;
		for (EscapingParticle particle : particles) {
			if (particle.getLastPosition().y > floorLevel && particle.getPosition().y <= floorLevel) {
				diff += 1;
			}
		}
		return diff;
	}

}
