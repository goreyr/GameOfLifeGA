/*
EvolutionaryAgent class applies an evolutionary algorithm to Conway's Game of
Life to evolve interesting patterns. This class manages the population,
selection, and mutation.

Last updated: 6/2/17

Author: Ryan Gorey
 */

import java.util.Random;
import java.lang.Math;
import java.io.*;
import java.text.*;
import java.util.Calendar;
import java.util.*;

public class EvolutionaryAgent {
    public Configuration[] population;
    private int gridHeight = 12;
    private int gridWidth = 12;
    private int popSize = 400;
    private int numGens = 100;
    private Random randGen;

    private int numElites = 3;

    private int mutationChance = 3;
    private int crossoverChance = 5;


    public EvolutionaryAgent() {
        generateStartingPopulation();
    }

    public EvolutionaryAgent(int numGens) {
        this.numGens = numGens;
        generateStartingPopulation();
    }

    public EvolutionaryAgent(int numGens, int popSize) {
        this.numGens = numGens;
        this.popSize = popSize;
        generateStartingPopulation();
    }


    public EvolutionaryAgent(int gridHeight, int gridWidth, int numGens, int popSize) {
        this.gridHeight = gridHeight;
        this.gridWidth = gridWidth;
        this.numGens = numGens;
        this.popSize = popSize;
        generateStartingPopulation();
    }

    public static void main(String args[]) {
        EvolutionaryAgent myAgent = new EvolutionaryAgent(12, 12, 1, 5);
        myAgent.population[0].setScore(5);
        myAgent.population[2].setScore(4);
        myAgent.population[3].setScore(9);

        myAgent.printPopScores();
        myAgent.sortPopulation();
        myAgent.printPopScores();
    }

    private void evolvePatterns() {
        generateStartingPopulation();
        initializePopulation();
        cellGrid myGrid = new cellGrid(gridHeight, gridWidth);
        int numGameGenerations = 40;

        for (int gen = 0; gen < numGens; gen++) {
            // Evaluation
            for (Configuration config: population) {
                myGrid.setStartingConfiguration(config);
                config.setScore(myGrid.runGame(numGameGenerations));
            }
            // Selection (w/ elitism)
            cloneElites();
            selectRemainingIndividuals(numElites);

            // Apply chance for mutation
            applyVariationOperators(numElites, mutationChance, crossoverChance);
        }
        Configuration bestConfig = findBestConfiguration();
        saveConfiguration(bestConfig);
    }

    private void cloneElites() {
        sortPopulation();

    }

    public void sortPopulation() {
        Collections.sort(population);
    }

    public void printPopScores() {
        for (int i = 0; i < popSize; i++) {
            System.out.println(population[i].getScore());
        }
    }

    private void selectRemainingIndividuals(int startingIndex) {}

    private Configuration findBestConfiguration() {
        Configuration bestConfig = population[0];
        for (Configuration config : population) {
            if (bestConfig.getScore() < config.getScore()) {
                bestConfig = config;
            }
        }
        return bestConfig;
    }

    /*
    https://stackoverflow.com/questions/5865453/java-writing-to-a-text-file
     */
    private void saveConfiguration(Configuration bestConfig) {
        try {
            String fileName = "";
            String timeLog = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());

            BufferedWriter out = new BufferedWriter(new FileWriter("bestConfig" + timeLog + ".txt"));
            for (int row = 0; row < gridHeight; row++) {
                for (int col = 0; col < gridWidth; col++) {
                    if (bestConfig.getCell(row,col)) {
                        out.write("1");
                    } else {
                        out.write("0");
                    }
                }
                out.newLine();
            }
            out.close();
        } catch (IOException e) {}
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

    private void applyVariationOperators(int startingInd, int mutationChance, int crossoverChance) {
        for (int i = startingInd; i < popSize; i++) {
            population[i].mutation(mutationChance);
        }
        crossover(startingInd, crossoverChance);
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

                int height = Math.max(rowInd1, rowInd2) - Math.min(rowInd1, rowInd2);
                int width = Math.max(colInd1, colInd2) - Math.min(colInd1, colInd2);
                rowInd1 = Math.min(rowInd1, rowInd2);
                colInd1 = Math.min(colInd1, colInd2);

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