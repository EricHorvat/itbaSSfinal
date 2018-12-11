package ar.edu.itba.ss.cli;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

public class CommandLineOptions {
    private boolean errorFree;

    @Option(name = "-n", aliases = { "--players-per-team" },
            usage = "Number of players for each team")
    private int N = 40;

    @Option(name = "-m", aliases = { "--mass" },
            usage = "input file for the particles")
    private double mass = 80;

    @Option(name = "--speed", usage = "Speed")
    private double speed = 14.06;

    @Option(name = "--ball-speed", usage = "Speed")
    private double ballSpeed = 50;

    @Option(name = "--dodge-range-team-1", usage = "Dodge range from the trajectory line for team 1")
    private double dodgeRangeTeam1 = 4.06;

    @Option(name = "--dodge-range-team-2", usage = "Dodge range from the trajectory line for team 2")
    private double dodgeRangeTeam2 = 4.06;

    @Option(name = "--min-reaction-time-team-1")
    private double minReactionTime1 = 4.06;

    @Option(name = "--min-reaction-time-team-2")
    private double minReactionTime2 = 4.06;

    @Option(name = "--max-reaction-time-team-1")
    private double maxReactionTime1 = 4.06;

    @Option(name = "--max-reaction-time-team-2")
    private double maxReactionTime2 = 4.06;

    @Option(name = "-b", usage = "Ball count")
    private double ballCount = 1;

    @Option(name = "--precision-team-1", usage = "Precision for team 1")
    private double precisionTeam1 = 0.2;

    @Option(name = "--precision-team-2", usage = "Precision for team 2")
    private double precisionTeam2 = 0.2;

    @Option(name = "-t", aliases = {"--time"}, usage = "max simulation duration")
    private double duration = 3600;

    @Option(name = "-d", aliases = {"--delta", "-dt"}, usage = "The delta")
    private double dt = 1e-4;

    @Option(name = "-f", aliases = { "--fps" }, usage = "frames per second")
    private double fps = 60;

    @Option(name = "-l", aliases = {"--length", "-L"}, usage = "The height of the container")
    private double lenght = 10;

    @Option(name = "-w", aliases = {"-W", "--width"}, usage = "The width of the container")
    private double width = 10;

    @Option(name = "-r", aliases = {"--min-radius"})
    private double minRadius = 0.5;

    @Option(name = "-R", aliases = {"--max-radius"})
    private double maxRadius = 0.58;

    @Option(name = "-K", aliases = {"--Kn"})
    private double Kn = 1.2e5;

    @Option(name = "-k", aliases = {"--Kt"})
    private double Kt = 2.4e5;

    @Option(name = "-A")
    private double A = 2000;

    @Option(name = "-B")
    private double B = 0.08;

    @Option(name = "-T")
    private double TAU = 0.5;

    @Option(name = "-S", usage = "Number of simulations to run")
    private int times = 1;


    public static int id_count = 1;


    public CommandLineOptions(String... args) {
        CmdLineParser parser = new CmdLineParser(this);
        try {
            errorFree = true;
            parser.parseArgument(args);
            if (fps <= 0) {
                errorFree = false;
            }
        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            parser.printUsage(System.err);
        }
    }


    public boolean isErrorFree() {
        return errorFree;
    }


    public int getN() {
        return N;
    }

    public double getMass() {
        return mass;
    }

    public double getDodgeRangeTeam1() {
        return dodgeRangeTeam1;
    }

    public double getDodgeRangeTeam2() {
        return dodgeRangeTeam2;
    }

    public double getBallCount() {
        return ballCount;
    }

    public double getPrecisionTeam1() {
        return precisionTeam1;
    }

    public double getPrecisionTeam2() {
        return precisionTeam2;
    }

    public double getDuration() {
        return duration;
    }

    public double getDt() {
        return dt;
    }

    public double getFps() {
        return fps;
    }

    public double getLength() {
        return lenght;
    }

    public double getWidth() {
        return width;
    }

    public double getMinRadius() {
        return minRadius;
    }

    public double getMaxRadius() {
        return maxRadius;
    }

    public double getKn() {
        return Kn;
    }

    public double getKt() {
        return Kt;
    }

    public double getA() {
        return A;
    }

    public double getB() {
        return B;
    }

    public double getTAU() {
        return TAU;
    }

    public int getTimes() {
        return times;
    }

    public static int getId_count() {
        return id_count;
    }

    public double getDT2() {
        return 1.0 / getFps();
    }

    public double getSpeed() {
        return speed;
    }

    public double getMinReactionTime1() {
        return minReactionTime1;
    }

    public double getMinReactionTime2() {
        return minReactionTime2;
    }

    public double getMaxReactionTime1() {
        return maxReactionTime1;
    }

    public double getMaxReactionTime2() {
        return maxReactionTime2;
    }

    public double getBallSpeed() {
        return ballSpeed;
    }

}
