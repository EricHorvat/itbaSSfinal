## TP Final Simulacion y Sistemas
### Grupo 10

#### Descripcion
El trabajo practico consiste en el desarrollo de una simulacion del clasico juego escolar "Quemado".
El mismo consiste en dos equipos de misma cantidad de jugadores, y una o mas pelotas. 
El objetivo es "quemar" a los jugadores rivales, logrando tocarlos con la pelota.
Cada equipo se encuentra en una mitad del escenerio y no puede salir del mismo.


#### Mediciones
1. Medir 'caudal' de quemados, variando cantidad de jugadores en equipos parejos.
2. Medir energia del sistema, y de cada equipo. Hacerlo para 5 tamaños de equipos distintos.
3. Repetir puntos anteriores pero para un tamaño de equipo fijo, variando la cantidad de pelotas.


#### Detalles de implementacion
* Cuando la pelota esta yendo hacia un equipo, **la misma ejerce una fuerza social?** sobre los jugadores de dicho 
equipo.
* El equipo que lanzo la pelota no es afectada por la misma hasta el momento en que la pelota es lanzada por el equipo 
opuesto.
* Cuando la pelota colisiona contra un jugador, esta deja de ejercer fuerzas hacia el equipo.
* Los jugadores quemados son elimiados y se 'colocan' alrededor de la cancha rival para tirar la pelota si no es 
agarrada.
* Conforme pasa el tiempo, la fuerza deseada se reduce, simulando 'el cansancio' de los jugadores, reduciendo sus 
posibilidades de esquivar la pelota.
* Cuando los jugadores lanzan la pelota, no realizan lanzamientos perfectos, sino que tienen probabilidad de errar, o
'lanzar cerca de su objetivo'.
* La velocidad con la cual tiran las pelotas los jugadores al igual que su chance de esquivar la pelota depende de el 
radio (a mas radio, mas fuerte lanzan, y menos esquivan)


##### Caracteristicas del sistema
* Cancha: 10 x 20 mts.Cada mitad de 10 x 10 mts
* Velocidad promedio: 80km/h
* Velocidad maxima: 100km/h
* Roce del aire: 
![equation](https://latex.codecogs.com/gif.latex?-\frac{1}{2}&space;pAC\left&space;|&space;v&space;\right&space;|^2&space;\widehat{v})

#####Links
* [Velocidad de la pelota](https://www.ominocity.com/2015/01/15/how-fast-can-you-throw-a-dodgeball-anyway/)
* [Resistencia del aire](https://www.wired.com/2015/01/air-resistance-force-make-difference/)
* [Reglas estadounidences del quemado](https://en.wikipedia.org/wiki/National_Dodgeball_League_rules)


