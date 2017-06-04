# GameOfLifeGA
Runs a genetic algorithm on Conway's Game of Life to produce cool patterns.

To use: 

First, compile cellGrid.java (sorry cellGrid starts lower case - I didn't realize I was
breaking convention until far too late), Configuration.java, and EvolutionaryAgent.java.

Second, run EvolutionaryAgent.java. It will be set to evolve a pattern across 200 generations
and then visualize the best result. You can uncomment the testing code to see some of what
I used to conduct multiple runs, if you wish. You can also change parameters like popSize,
numElites, etc. in the local variables at the top of EvolutionaryAgent.java, though I do
not recommend changing the grid size.