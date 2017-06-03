/*
Configuration class stores a starting configuration of living and dead cells
in a user-defined matrix. Used for evolutionary algorithm evolving starting
configurations for Conway's Game of Life.

Author: Ryan Gorey

Last updated: 6/2/17
 */

import java.util.Random;

public class Configuration implements Comparable<Configuration>{
    private boolean[][] configMatrix;
    private int matrixHeight = 12;
    private int matrixWidth = 12;
    private double score = 0;
    private Random randGen = new Random();

    public Configuration() {
        configMatrix = new boolean[matrixHeight][matrixWidth];
    }

    public Configuration(int matrixHeight, int matrixWidth) {
        this.matrixHeight = matrixHeight;
        this.matrixWidth = matrixWidth;
        configMatrix = new boolean[matrixHeight][matrixWidth];
    }

    public int compareTo(Configuration otherConfig) {
        if (this.score > otherConfig.getScore()) {
            return -1;
        } else {
            return 1;
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

    public void deepCopy(Configuration copyFrom) {
        for (int row = 0; row < matrixHeight; row++) {
            for (int col = 0; col < matrixWidth; col++) {
               this.setCell(row, col, copyFrom.getCell(row, col));
            }
        }
        this.setScore(copyFrom.getScore());
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

    public void setConfiguration(Configuration config) {
        for (int row = 0; row < matrixHeight; row++) {
            for (int col = 0; col < matrixWidth; col++) {
                configMatrix[row][col] = config.getCell(row, col);
            }
        }
    }

    public void setCell(int row, int col, boolean status) {
        configMatrix[row][col] = status;
    }

    public boolean getCell(int row, int col) {
        return configMatrix[row][col];
    }

    public void setCellRegion(int topRowInd, int leftColInd, int height, int width, boolean[][] region) {
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                configMatrix[topRowInd + row][leftColInd + col] = region[row][col];
            }

        }
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

    public void setRandomConfiguration()  {
        setRandomConfiguration(30);
    }

    public double getScore() {
        return this.score;
    }

    public void setScore(double score) {
        this.score = score;
    }
}
