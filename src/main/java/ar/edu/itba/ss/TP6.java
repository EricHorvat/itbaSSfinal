package ar.edu.itba.ss;

import ar.edu.itba.ss.cli.CommandLineOptions;
import ar.edu.itba.ss.output.Output;
import ar.edu.itba.ss.output.OutputStat;
import ar.edu.itba.ss.particle.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.List.*;

public class TP6 {

	private static int id_count = 1;
	private static CommandLineOptions options;

	private static final Random random = new Random();

	private static double getRandomNumber(double min, double max){
		return random.nextDouble() * (max - min) + min;
	}

	private static Player createRandomParticle(int team_index) {
		final double RAD_MIN = options.getMinRadius();
		final double RAD_MAX = options.getMaxRadius();
		final double W = options.getWidth();
		final double L = options.getLenght();
		final double mass = options.getMass();

		double r = getRandomNumber(RAD_MIN, RAD_MAX) / 2.0;
		double x = getRandomNumber(r, W - 2 * r) + team_index * W +  r;
		double y = getRandomNumber(r, L - 2 * r) + r;
		return new Player(id_count, x, y, 0, 0, mass, r, team_index);
	}

	private static List<List<Player>> generateTeams(Ball ball) {
		final int N = options.getN();

		List<List<Player>> teams = new ArrayList<>();
		boolean overlap;
		for (int i = 0; i < 2; i++) {
			id_count = 1 + N * i;
			List<Player> team = new ArrayList<>();
			while (id_count - 1 < N * (i + 1)) {
				overlap = false;
				Player newParticle = createRandomParticle(i);
				for (Player otherParticle : team) {
					if (Particle.areOverlapped(otherParticle, newParticle)) {
						overlap = true;
						break;
					}
				}
				if (Particle.areOverlapped(ball, newParticle)) {
					overlap = true;
				}
				if (!overlap) {
					team.add(newParticle);
					id_count++;
				}
			}
			teams.add(team);
		}
		return teams;
	}

	private static void systemSimulation(int loop){
		final double dt = options.getDt();
		final double dt2 = options.getDT2();

		String desiredVelocityStr = String.format(Locale.US,"%.2f", options.getSpeed());
		Output ovitoFile = new Output(options, "ovitoFile-"+desiredVelocityStr+"dVel-"+loop+"time.txt");
		OutputStat peopleFile = new OutputStat("people-"+desiredVelocityStr+"dVel-"+loop+"time.txt");
		OutputStat largePeopleFile = new OutputStat("largePeople-"+desiredVelocityStr+"dVel-"+loop+"time.txt");
		OutputStat diffPeopleFile = new OutputStat("diffPeople-"+desiredVelocityStr+"dVel-"+loop+"time.txt");
		OutputStat maxPressureFile = new OutputStat("maxPressure-"+desiredVelocityStr+"dVel-"+loop+"time.txt");
		Ball ball = new Ball(666, 2.5, 2.5,0, 0, 1, 0.2);
		List<List<Player>> teams = generateTeams(ball);
		List<SocialModelSimulator> teamsSocialModelSimulator = teams.stream()
			.map(team -> new SocialModelSimulator(options, team, dt, ball)).collect(Collectors.toList());
		double time = 0.0;
		double lastTime = - dt2 - 1.0;
		double maxPressure = 0.0;
		int localdiff = 0;
		int totalDiff = 0;
		int print = 0;
		while (teamsSocialModelSimulator.stream().noneMatch(team -> team.escapingParticles() == 0)) {
			if (lastTime + dt2 < time) {
				List<Player> particles = teams.stream().flatMap(List::stream).collect(Collectors.toList());
				ovitoFile.printState(particles, ball);
				double mp = particles.stream().mapToDouble(Player::getPressure).max().getAsDouble();
				particles.forEach(Player::resetPressure);
				if (maxPressure < mp) {
					maxPressure = mp;
				}
				lastTime = time;
				diffPeopleFile.addStat(Integer.toString(totalDiff - localdiff));
				localdiff = totalDiff;
			}
			largePeopleFile.addStat(Integer.toString(totalDiff));
			
			List<Integer> indexes = IntStream.range(0,2).boxed().collect(Collectors.toList());
			Collections.shuffle(indexes);
			indexes.stream().map(teamsSocialModelSimulator::get).forEach(SocialModelSimulator::loop);
			
			int diff = 0;//@see getDifPeople and apply to each SocialModelSim indexes.stream().map(teamsSocialModelSimulator::get).mapToInt(SocialModelSimulator::diff).sum();
			totalDiff += diff;
			while (diff > 0){
				peopleFile.addStat(Double.toString(time));
				System.out.println("Tdiff: " + totalDiff);
				diff--;
			}
			time += dt;

            if (print++ % 1000 == 0) {
            	System.out.println("Time elapsed: " + time);
			}
		}
		System.out.println("Time elapsed: " + time);
		maxPressureFile.addStat(Double.toString(maxPressure));
		maxPressureFile.writeFile();
		peopleFile.writeFile();
		largePeopleFile.writeFile();
		diffPeopleFile.writeFile();
	}
	

	public static void main(String[] args){
		options = new CommandLineOptions(args);
		SocialModel.options = options;

		double timee = System.currentTimeMillis();
		systemSimulation(1);
		System.out.println(System.currentTimeMillis() - timee);
	}

	private static int getDifPeople(List<Player> particles) {
		int diff = 0;
		for (Player particle : particles) {
			/*if (particle.getLastPosition().y > floorLevel && particle.getPosition().y <= floorLevel) {
				diff += 1;
			}*/
		}
		return diff;
	}

}
