import matplotlib.pyplot as plt
import numpy as np
import json


def parse_filee(filename):
    with open(filename,"r") as file:
        return json.loads(file.readline())


def plot_surfacee(team_outputs_by_run, runs, n):

    oavg = []
    ostd = []

    winners = []
    losers = []
    teams_s = []

    for run_index, team_outputs in enumerate(team_outputs_by_run):
        winner = team_outputs[1] if len(team_outputs[0]) == n else team_outputs[0]
        loser = team_outputs[0] if len(team_outputs[0]) == n else team_outputs[1]

        winners.append(np.array(winner))
        losers.append(np.array(loser))
        teams_s.append(np.array(team_outputs[2]))

    fig = plt.figure()
    ax = plt.gca()
    for winner in winners:
        ax.plot(np.arange(0,len(winner)),winner)
    plt.xlabel("Particulas eliminadas")
    plt.ylabel("Tiempo [s]")
    plt.savefig('winner.png')
    fig = plt.figure()
    ax = plt.gca()
    for loser in losers:
        ax.plot(np.arange(0,n),loser)
    plt.xlabel("Particulas eliminadas")
    plt.ylabel("Tiempo [s]")
    plt.savefig('loser.png')
    fig = plt.figure()
    ax = plt.gca()
    for teams in teams_s:
        ax.plot(np.arange(0,len(teams)),teams)
    plt.xlabel("Particulas eliminadas")
    plt.ylabel("Tiempo [s]")
    plt.savefig('teams.png')

    #TODO PLOT AVG & ERR

    winners_arr = np.array(np.asarray(winners))
    losers_arr = np.array(np.asarray(losers))
    teams_arr = np.array(np.asarray(teams_s))

    winners_arr_avg = np.average(winners_arr, axis=0)
    winners_arr_err = np.std(winners_arr, axis=0)
    losers_arr_avg = np.average(losers_arr, axis=0)
    losers_arr_err = np.std(losers_arr, axis=0)
    teams_arr_avg = np.average(teams_s, axis=0)
    teams_arr_err = np.std(teams_s, axis=0)

    fig = plt.figure()
    ax = plt.gca()
    ax.plot(winners_arr_avg)
    ax.plot(winners_arr_avg + winners_arr_err, linestyle='--', color="r")
    ax.plot(winners_arr_avg - winners_arr_err, linestyle='--', color="r")
    plt.savefig('winner_err.png')

    fig = plt.figure()
    ax = plt.gca()
    ax.plot(losers_arr_avg)
    ax.plot(losers_arr_avg + losers_arr_err, linestyle='--', color="r")
    ax.plot(losers_arr_avg - losers_arr_err, linestyle='--', color="r")
    plt.savefig('loser_err.png')

    fig = plt.figure()
    ax = plt.gca()
    ax.plot(teams_arr_avg)
    ax.plot(teams_arr_avg + teams_arr_err, linestyle='--', color="r")
    ax.plot(teams_arr_avg - teams_arr_err, linestyle='--', color="r")
    plt.savefig('teams_err.png')


def mains():
    runs = 2
    n = 40

    team_outputs_by_run = []
    for run in range(0,runs):
        team_outputs = []
        for team in range(0,3):
            team_outputs.append(parse_filee("outputTeam" + str("%d" % team) +
                                "-"+ str(run) + "time" + #TODO ADD MULTIPLE TIMES
                                ".txt"))
        team_outputs_by_run.append(team_outputs)

    plot_surfacee(team_outputs_by_run, runs, n)


if __name__ == '__main__':
    mains()