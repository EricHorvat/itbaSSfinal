package ar.edu.itba.ss.output;

import ar.edu.itba.ss.particle.Particle;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static ar.edu.itba.ss.data.Data.*;

public class Output {

	private String fileName;
	private int c = 0;
	private List<String> borderParticles;

	public Output(String fileName) {
		this.fileName = "./output/"+fileName;
		borderParticles = borders();
	}

	public void printState(List<? extends Particle> particles, List<? extends Particle> balls) {
		List<String> lines = new LinkedList<>();
		lines.add(String.valueOf(particles.size() + balls.size() + borderParticles.size()));
		lines.add(""+c);
		c++;
		for (Particle p : particles) {
			lines.add(p.getInfo());
		}
		for (Particle p : balls) {
			lines.add(p.getInfo());
		}
		lines.addAll(borderParticles);
		writeFile(lines);
	}

	private List<String> borders(){
		List<String> borderLines = new ArrayList<>();
		double[] YsVaryingX = {0,L};
		double[] XsVaryingY = {0,W,2*W};
		for (double x: XsVaryingY) {
			double y = 0;
			while(y <= L){
				borderLines.add("-1 "+ x + " " + y + " 0.0 0.0 0.0 " +RAD_MAX/10.0+" 1 0 0");
				y += RAD_MAX/10.0;
			}
		}
		for (double y: YsVaryingX) {
			double x = 0;
			while(x <= 2*W){
				borderLines.add("-1 "+ x + " " + y + " 0.0 0.0 0.0 " +RAD_MAX/10.0+" 1 0 0");
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
