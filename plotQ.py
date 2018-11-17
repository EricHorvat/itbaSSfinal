import matplotlib.pyplot as plt
import numpy as np
import json

def plot_surface(ds,name):
    fig = plt.figure()
    ax = plt.gca()
    #legends = ['d = 0.15 m','d = 0.20 m','d = 0.25 m','d = 0.30 m']

    dd = np.array([np.array(d) for d in ds])
    davg= np.average(dd, axis=0)
    dstd= np.std(dd,axis=0)
    x = np.arange(0+DN,len(dstd)+DN)
    ax.errorbar(x, davg, yerr=dstd, fmt='.')
    ax.errorbar(x,davg, fmt='o')

    #ax.plot(x,davg)
    #ax.plot(x,davg+dstd,'r--')
    #ax.plot(x,davg-dstd,'r--')
    plt.xlabel("Particulas egresadas")
    plt.ylabel("Caudal [p/s]")
    plt.savefig(name + '.png')
    plt.show()

DN = 30
def parse_file(filename):
    with open(filename,"r") as file:
        people = np.array(json.loads(file.readline()))
    Q = []
    for i in xrange(DN,people.size):
        Q.append(DN/(people[i] - people[i-DN]))
        print people[i] - people[i-DN]

    Qarr = np.array(Q)
    print(filename)
    print(" Q avg ")
    print(np.average(Qarr))
    print(" Q std ")
    print(np.std(Qarr))
    return Q

def main():
    dvel = 2.1

    d = []
    for i in range(0,5):
        d.append(parse_file("people-" + str("%0.2f" % dvel) + "dVel-"+ str(i) + "time.txt"))
    plot_surface(d, "Q2")

if __name__ == '__main__':
    main()