package ar.edu.itba.ss.output;

import ar.edu.itba.ss.cli.CommandLineOptions;
import ar.edu.itba.ss.particle.Ball;
import ar.edu.itba.ss.particle.Particle;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Output {

	private String fileName;
	private int c = 0;
	private List<String> borderParticles;
	private CommandLineOptions options;

	public Output(CommandLineOptions options, String fileName) {
		this.options = options;
		this.fileName = "./output/"+fileName;
		try {
			Files.deleteIfExists(Paths.get(this.fileName));
		} catch (IOException e) {
			System.err.println("Ooops");
		}
		borderParticles = borders();
	}

	public void printState(List<? extends Particle> particles, Ball ball) {
		List<String> lines = new LinkedList<>();
		lines.add(String.valueOf(particles.size() + 1 + borderParticles.size()));
		lines.add(""+c);
		c++;
		for (Particle p : particles) {
			lines.add(p.getInfo());
		}
			lines.add(ball.getInfo());
		lines.addAll(borderParticles);
		writeFile(lines);
	}

	private List<String> borders(){
		final double L = options.getLength();
		final double W = options.getWidth();
		final double RAD_MAX = options.getMaxRadius();

		List<String> borderLines = new ArrayList<>();
		double[] YsVaryingX = {0,L};
		double[] XsVaryingY = {0,W,2*W};
		for (double x: XsVaryingY) {
			double y = 0;
			while(y <= L){
				borderLines.add("-1 "+ x + " " + y + " " +RAD_MAX/10.0+" 1 3 0");
				y += RAD_MAX/10.0;
			}
		}
		for (double y: YsVaryingX) {
			double x = 0;
			while(x <= 2*W){
				borderLines.add("-1 "+ x + " " + y + " " +RAD_MAX/10.0+" 1 3 0");
				x += RAD_MAX/10.0;
			}
		}
		
		return borderLines;
	}

    private void writeFile(List<String> lines) {
		try {
			FileWriter fw = new FileWriter(fileName, true);
			for (String line: lines) {
				fw.write(line + "\n");
			}
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
