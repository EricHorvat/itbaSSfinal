import matplotlib.pyplot as plt
import numpy as np
import json


def parse_filee(filename):
    with open(filename,"r") as file:
        return json.loads(file.readline())


def plot_surfacee(team_outputs_by_run_by_parameter, runs, parameter_variations):
    fig = plt.figure()
    ax = plt.gca()

    oavg = []
    ostd = []

    for parameter_index, team_outputs_by_run in enumerate(team_outputs_by_run_by_parameter):

        teams1 = []
        teams2 = []
        teams_s = []

        for run_index, team_outputs in enumerate(team_outputs_by_run):

            team1_x = np.arange(0, len(team_outputs[0]))
            team2_x = np.arange(0, len(team_outputs[1]))
            teams_x = np.arange(0, len(team_outputs[2]))


            teams1.append(team_outputs[0])
            teams2.append(team_outputs[1])
            teams_s.append(team_outputs[2])

            #dd = np.array(np.asarray(team_outputs))
            #davg= np.average(dd, axis=0)
            #oavg.append(np.average(o))
            #ostd.append(np.std(o))
            #ax.plot(x,davg)
            ax.plot(team1_x,team_outputs[0])
            ax.plot(team2_x,team_outputs[1])
            ax.plot(teams_x,team_outputs[2])
            plt.show()


    box = ax.get_position()
    ax.set_position([box.x0, box.y0, box.width * 0.8, box.height])

    legends = ["0.8 m/s","1.45 m/s","2.1 m/s","2.75 m/s","3.4 m/s","4.05 m/s","4.7 m/s","5.35 m/s","6.0 m/s"]
    # Put a legend to the right of the current axis
    ax.legend(legends, loc='center left', bbox_to_anchor=(1, 0.5))

    plt.xlabel("Particulas egresadas")
    plt.ylabel("Tiempo [s]")
    plt.savefig('2.png')

    fig = plt.figure()
    ax = plt.gca()
    ax.errorbar(runs,oavg,yerr=ostd, fmt='o')
    ax.errorbar(runs,oavg, fmt='o')
    plt.xlabel("Velocidad deseada [m/s]")
    plt.ylabel("Tiempo de salida [s]")

    plt.savefig('2_t.png')
    plt.close()


def mains():
    runs = 1
    paramater_variations = [0]

    team_outputs_by_run_by_parameter = []
    for paramater_variation in paramater_variations:
        team_outputs_by_run = []
        for run in range(0,runs):
            team_outputs = []
            for team in range(0,3):
                team_outputs.append(parse_filee("outputTeam" + str("%d" % team) +
                                     # "dVel-"+ str(i) + "time" + TODO ADD MULTIPLE TIMES
                                     # "dVel-"+ str(i) + "time" + TODO ADD VARYING PARAMETER
                                     ".txt"))
            team_outputs_by_run.append(team_outputs)
        team_outputs_by_run_by_parameter.append(team_outputs_by_run)

    plot_surfacee(team_outputs_by_run_by_parameter, runs, paramater_variations)


if __name__ == '__main__':
    mains()