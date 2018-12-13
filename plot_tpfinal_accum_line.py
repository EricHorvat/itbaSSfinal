import matplotlib.pyplot as plt
import numpy as np
import json


def parse_filee(filename):
    with open(filename,"r") as file:
        return json.loads(file.readline())


def averaging_not_same_size(array_of_array):
    maxx = len(array_of_array[0])
    minn = maxx
    for arr in array_of_array:
        maxx = maxx if maxx > len(arr) else len(arr)
        minn = minn if minn > len(arr) else len(arr)

    aux = []
    for i in range(0,maxx):
        aux.append([])
        for arr in array_of_array:
            if len(arr) > i:
                aux[-1].append(arr[i])

    avgans = []
    stdans = []
    for arr in aux:
        avgans.append(np.average(np.asarray(arr)))
        stdans.append(np.std(np.asarray(arr)))

    return np.asarray(avgans),np.asarray(stdans)


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
    colors=['r','b','y','g','c']
    for index, winner in enumerate(winners):
        ax.errorbar(range(len(winner)), winner, fmt=colors[index]+"o")
        ax.plot(np.arange(0,len(winner)),winner, colors[index])
    plt.xlabel("Particulas eliminadas")
    plt.ylabel("Tiempo [s]")
    plt.savefig('winner.png')
    fig = plt.figure()
    ax = plt.gca()
    for index, loser in enumerate(losers):
        ax.errorbar(range(len(loser)), loser, fmt=colors[index]+"o")
        ax.plot(np.arange(0,n),loser,colors[index])
    plt.xlabel("Particulas eliminadas")
    plt.ylabel("Tiempo [s]")
    plt.savefig('loser.png')
    fig = plt.figure()
    ax = plt.gca()
    for index, teams in enumerate(teams_s):
        ax.errorbar(range(len(teams)), teams, fmt=colors[index]+"o")
        ax.plot(np.arange(0,len(teams)),teams, colors[index])
    plt.xlabel("Particulas eliminadas")
    plt.ylabel("Tiempo [s]")
    plt.savefig('teams.png')

    losers_arr = np.array(np.asarray(losers))

    winners_arr_avg ,winners_arr_err = averaging_not_same_size(winners)
    losers_arr_avg = np.average(losers_arr, axis=0)
    losers_arr_err = np.std(losers_arr, axis=0)
    teams_arr_avg ,teams_arr_err = averaging_not_same_size(teams_s)
    #teams_arr_avg = np.average(teams_s, axis=0)
    #teams_arr_err = np.std(teams_s, axis=0)

    fig = plt.figure()
    ax = plt.gca()
    ax.errorbar(range(len(winners_arr_avg)),winners_arr_avg,yerr = winners_arr_err,fmt="o")
    plt.xlabel("Particulas eliminadas")
    plt.ylabel("Tiempo [s]")
    plt.savefig('winner_err.png')

    fig = plt.figure()
    ax = plt.gca()
    #ax.plot(losers_arr_avg)
    ax.errorbar(range(len(losers_arr_avg)),losers_arr_avg,yerr = losers_arr_err,fmt="o")
    #ax.plot(losers_arr_avg + losers_arr_err, linestyle='--', color="r")
    #ax.plot(losers_arr_avg - losers_arr_err, linestyle='--', color="r")
    plt.xlabel("Particulas eliminadas")
    plt.ylabel("Tiempo [s]")
    plt.savefig('loser_err.png')

    fig = plt.figure()
    ax = plt.gca()
    #ax.plot(teams_arr_avg)
    ax.errorbar(range(len(teams_arr_avg)), teams_arr_avg, yerr=teams_arr_err, fmt="o")
    #ax.plot(teams_arr_avg + teams_arr_err, linestyle='--', color="r")
    #ax.plot(teams_arr_avg - teams_arr_err, linestyle='--', color="r")
    plt.xlabel("Particulas eliminadas")
    plt.ylabel("Tiempo [s]")
    plt.savefig('teams_err.png')


def mains():
    runs = 5
    n = 10


    team_outputs_by_run = []
    for run in range(1, runs + 1):

        print("r" + str(run))
        team_outputs = []
        for team in range(0, 3):
            print(team)
            team_outputs.append(parse_filee("baseRuns/" +
                                            str(run) + "/" +
                                            "outputTeam" + str("%d" % team) +
                                            ".txt"))
        team_outputs_by_run.append(team_outputs)
    plot_surfacee(team_outputs_by_run, runs, n)


if __name__ == '__main__':
    mains()