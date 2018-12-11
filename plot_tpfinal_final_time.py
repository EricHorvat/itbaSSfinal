import matplotlib.pyplot as plt
import numpy as np
import json


def parse_filee(filename):
    with open(filename,"r") as file:
        return json.loads(file.readline())


def plot_surfacee(team_outputs_by_run_by_parameter, runs, parameter_values):

    oavg = []
    ostd = []

    winners = []
    losers = []
    teams_s = []
    times = [[] for x in range(runs)]

    for parameter_index, team_outputs_by_run in enumerate(team_outputs_by_run_by_parameter):
        for run_index, team_outputs in enumerate(team_outputs_by_run):
            times[parameter_index].append(team_outputs[2][-1])

    times = np.asarray(times)
    times_avg = np.average(times, axis=0)
    times_err = np.std(times, axis=0)

    fig = plt.figure()
    ax = plt.gca()
    ax.errorbar(np.array(parameter_values), times_avg, yerr=times_err, fmt='o')
    ax.errorbar(np.array(parameter_values), times_avg, fmt='o')
    plt.xlabel("PARAMETRO")
    plt.ylabel("Tiempo [s]")
    plt.savefig('finalTime.png')
    plt.show()


def mains():
    runs = 2
    varying_parameter_values = [0,1]

    team_outputs_by_run_by_parameter = []
    for varying_parameter_val in varying_parameter_values:

        team_outputs_by_run = []

        for run in range(0,runs):
            team_outputs = []
            for team in range(0,3):
                team_outputs.append(parse_filee("outputTeam" + str("%d" % team) +
                                    "-" + str(run) + "time" +
                                    "-" + str(varying_parameter_val) +
                                    ".txt"))
            team_outputs_by_run.append(team_outputs)
        team_outputs_by_run_by_parameter.append(team_outputs_by_run)

    plot_surfacee(team_outputs_by_run_by_parameter, runs, varying_parameter_values)


if __name__ == '__main__':
    mains()