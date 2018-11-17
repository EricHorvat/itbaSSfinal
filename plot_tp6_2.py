import matplotlib.pyplot as plt
import numpy as np
import json


def parse_filee(filename):
    with open(filename,"r") as file:
        return json.loads(file.readline())


def plot_surfacee(dss,dvelocities):
    fig = plt.figure()
    ax = plt.gca()

    oavg = []
    ostd = []

    for index, ds in enumerate(dss):
        o = []
        x = np.arange(0,len(ds[0]))
        for d in ds:
            o.append(d[-1])
        o = np.array(o)

        dd = np.array(np.asarray(ds))
        davg= np.average(dd, axis=0)
        oavg.append(np.average(o))
        ostd.append(np.std(o))
        ax.plot(x,davg)


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
    ax.errorbar(dvelocities,oavg,yerr=ostd, fmt='o')
    ax.errorbar(dvelocities,oavg, fmt='o')
    plt.xlabel("Velocidad deseada [m/s]")
    plt.ylabel("Tiempo de salida [s]")

    plt.savefig('2_t.png')
    plt.close()


def mains():
    #desired_velocities = [1.45,2.1,2.75,3.4,4.05,4.7,5.35,6.0]
    desired_velocities = [0.8,1.45,2.1,2.75,3.4,4.05,4.7,5.35,6.0]
    #desired_velocities = [0.8,1.45,2.1,2.1 + 0.65/3,2.75 - 0.65/3,2.75,2.75 + 0.65/3,3.4 - 0.65/3,3.4,4.05,4.7,5.35,6.0]
    #desired_velocities = [2.1,2.1 + 0.65/3,2.75 - 0.65/3,2.75,2.75 + 0.65/3,3.4 - 0.65/3,3.4]

    ds = []
    for dvel in desired_velocities:
        d = []
        for i in range(0,5):
            d.append(parse_filee("people-" + str("%0.2f" % dvel) + "dVel-"+ str(i) + "time.txt"))
        ds.append(d)
    plot_surfacee(ds,desired_velocities)


if __name__ == '__main__':
    mains()

