/*
Configuration class stores a starting configuration of living and dead cells
in a user-defined matrix. Used for evolutionary algorithm evolving starting
configurations for Conway's Game of Life.

Author: Ryan Gorey

Last updated: 6/2/17
 */

import java.util.Random;

public class Configuration {
    private boolean[][] configMatrix;
    private int matrixHeight = 12;
    private int matrixWidth = 12;
    private int score = 0;
    private Random randGen;

    public Configuration() {
        configMatrix = new boolean[matrixHeight][matrixWidth];
    }

    public Configuration(int matrixHeight, int matrixWidth) {
        this.matrixHeight = matrixHeight;
        this.matrixWidth = matrixWidth;
        configMatrix = new boolean[matrixHeight][matrixWidth];
    }

    public void setRandomConfiguration(int cellChanceToLive)  {
        if (cellChanceToLive > 100) {
            cellChanceToLive = 100;
        } else {
            if (cellChanceToLive < 0) {
                cellChanceToLive = 0;
            }
        }

        for (int row = 0; row < matrixHeight; row++) {
            for (int col = 0; col < matrixWidth; col++) {
                int randInt = randGen.nextInt(100);
                if (randInt < cellChanceToLive) {
                    configMatrix[row][col] = true;
                }
            }
        }
    }

    public void setConfiguration(boolean[][] config) {
        for (int row = 0; row < matrixHeight; row++) {
            for (int col = 0; col < matrixWidth; col++) {
                configMatrix[row][col] = config[row][col];
            }
        }
    }

    public void setCell(int row, int col, boolean status) {
        configMatrix[row][col] = status;
    }

    public boolean[][] getCellRegion(int topRowInd, int leftColInd, int height, int width) {
        boolean[][] region = new boolean[height][width];
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                region[row][col] = configMatrix[topRowInd + row][leftColInd + col];
            }
        }
        return region;
    }

    public void setCellRegion(int topRowInd, int leftColInd, int height, int width, boolean[][] region) {
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                configMatrix[topRowInd + row][leftColInd + col] = region[row][col];
            }

        }
    }

    public void mutation(int mutationChance) {
        boolean status;
        int randInt;

        for (int row = 0; row < matrixHeight; row++) {
            for (int col = 0; col < matrixWidth; col++) {
                randInt = randGen.nextInt(100);
                if (randInt < mutationChance) {
                    status = randGen.nextBoolean();
                    configMatrix[row][col] = status;
                }
            }
        }
    }

    public void setRandomConfiguration()  {
        setRandomConfiguration(30);
    }

    public void getScore() {
        return this.score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
