import matplotlib.pyplot as plt
import numpy as np

def plot_surface(ds,name):
    fig = plt.figure()
    ax = plt.gca()
    legends = ['d = 0.15 m','d = 0.20 m','d = 0.25 m','d = 0.30 m']
    i = 1
    for d in ds:
        if name == 'Q':
            x = d["tarr"][DT-1:]
            y = d["Qarr"] * i
        else:
            x = d["tarr"]
            y = d["Earr"] * i

        ax.errorbar(x, y, fmt='o')
        i +=1
    ax.set_yscale('log')

    box = ax.get_position()
    ax.set_position([box.x0, box.y0, box.width * 0.8, box.height])

    # Put a legend to the right of the current axis
    ax.legend(legends, loc='center left', bbox_to_anchor=(1, 0.5))

    #plt.show()
    plt.xlabel("Tiempo [s]")
    plt.ylabel("Caudal [part/s]")
    plt.savefig(name + '.png')
    plt.close()

DT = 10

def parse_file(filename):
    Qc = []
    Q = []
    E = []
    t = []
    with open(filename,"r") as file:
        for line in file.readlines():
            parts = line.split("\t")
            e = float(parts[2])
            if e == 0:
                continue
            t.append(float(parts[0]))
            Qc.append(float(parts[1])*60/DT)
            E.append(e)

    Earr = np.array(E)
    for i in xrange(DT,Earr.size+1):
       Q.append(sum(Qc[i-DT:i]))
    Qarr = np.array(Q)
    print(filename)
    print(" Q avg ")
    print(np.average(Qarr))
    print(" Q std ")
    print(np.std(Qarr))
    tarr = np.array(t)/60.0
    return {"tarr": tarr, "Qarr": Qarr, "Earr": Earr}


import json


def parse_filee(filename):
    with open(filename,"r") as file:
        return json.loads(file.readline())

def plot_surfacee(ds,name):
    fig = plt.figure()
    ax = plt.gca()

    x = np.arange(0, len(ds[0]))
    for d in ds:
        ax.plot(x,d)

    plt.xlabel("Particulas egresadas")
    plt.ylabel("Tiempo [s]")
    plt.savefig('1_all.png')
    # plt.show()
    plt.close()

    #ax.set_yscale('log')

    #box = ax.get_position()
    #ax.set_position([box.x0, box.y0, box.width * 0.8, box.height])

    # Put a legend to the right of the current axis
    #ax.legend(legends, loc='center left', bbox_to_anchor=(1, 0.5))

    #plt.show()
    #plt.xlabel("Tiempo [s]")
    #plt.ylabel("Caudal [part/s]")

    #plt.savefig(name + '.png')
    #plt.close()


    fig = plt.figure()
    ax = plt.gca()

    dd = np.array(ds)
    davg= np.average(dd, axis=0)
    dstd= np.std(dd,axis=0)

    #ax.errorbar(x,davg,yerr=dstd)
    #ax.errorbar(x,davg, fmt='.')

    ax.plot(x,davg)
    ax.plot(x,davg+dstd,'r--')
    ax.plot(x,davg-dstd,'r--')

    plt.xlabel("Particulas egresadas")
    plt.ylabel("Tiempo [s]")
    plt.savefig('1_a.png')
    #plt.show()
    plt.close()


def mains():
    d = []
    dvel = 2.1

    for i in range(0,5):
            d.append(parse_filee("people-" + str("%0.2f" % dvel) + "dVel-"+ str(i) + "time.txt"))
    plot_surfacee(d, "Q")

if __name__ == '__main__':
    mains()

