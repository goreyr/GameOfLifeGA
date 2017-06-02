/*
EvolutionaryAgent class applies an evolutionary algorithm to Conway's Game of
Life to evolve interesting patterns. This class manages the population,
selection, and mutation.

Last updated: 6/2/17

Author: Ryan Gorey
 */

import java.util.Random;
import java.lang.Math;

public class EvolutionaryAgent {
    private Configuration[] population;
    private int gridHeight = 12;
    private int gridWidth = 12;
    private int popSize = 50;
    private int numGens = 50;
    private Random randGen;

    public EvolutionaryAgent() {
        generateStartingPopulation();
    }

    public EvolutionaryAgent(int numGens) {
        this.numGens = numGens;
        population = new boolean[50][gridHeight][gridWidth];
    }

    public EvolutionaryAgent(int numGens, int popSize) {
        this.numGens = numGens;
        this.popSize = popSize;
        population = new boolean[50][gridHeight][gridWidth];
    }


    public EvolutionaryAgent(int gridHeight, int gridWidth, int numGens, int popSize) {
        this.gridHeight = gridHeight;
        this.gridWidth = gridWidth;
        this.numGens = numGens;
        this.popSize = popSize;
        population = new boolean[50][gridHeight][gridWidth];
    }

    private void evolvePatterns() {
        generateStartingPopulation();
        initializePopulation();
        cellGrid myGrid = new cellGrid(gridHeight, gridWidth);

        for (int gen = 0; gen < numGens; gen++) {
            for (Configuration config: population) {
                myGrid.clear
            }
        }
    }

    private void generateStartingPopulation() {
        for (int i = 0; i < popSize; i++) {
            population[i] = new Configuration(gridHeight, gridWidth);
        }
    }

    private void initializePopulation() {
        for (int i = 0; i < popSize; i++) {
            population[i].setRandomConfiguration(30);
        }

    }

    private void crossover(int startingInd, int crossoverChance) {
        shufflePopulation(startingInd);

        for (int i = startingInd; i < (popSize - startingInd)/2; i++) {
            int randChance = randGen.nextInt(100);
            if (randChance < crossoverChance) {
                int rowInd1 = randGen.nextInt(gridHeight);
                int rowInd2 = randGen.nextInt(gridHeight);
                int colInd1 = randGen.nextInt(gridWidth);
                int colInd2 = randGen.nextInt(gridWidth);

                int height = max(rowInd1, rowInd2) - min(rowInd1, rowInd2);
                int width = max(colInd1, colInd2) - min(colInd1, colInd2);
                rowInd1 = min(rowInd1, rowInd2);
                colInd1 = min(colInd1, colInd2);

                boolean[][] region1 = population[i].getCellRegion(rowInd1, colInd1, height, width);
                boolean[][] region2 = population[i + (popSize - startingInd)/ 2].getCellRegion(rowInd1, colInd1, height, width);
                population[i].setCellRegion(rowInd1, colInd1, height, width, region2);
                population[i + (popSize - startingInd)/ 2].setCellRegion(rowInd1, colInd1, height, width, region1);
            }
        }
    }

    private void shufflePopulation(int startingInd) {
        for (int i = startingInd; i < popSize; i++) {
            int newIndex = randGen.nextInt(popSize-startingInd) + startingInd;
            Configuration temp = population[newIndex];
            population[newIndex] = population[i];
            population[i] = temp;
        }
    }
}