/**
 * cellGrid class Implementation
 *
 * Author: Ryan Gorey
 * Last Updated: 5/29/2017
 *
 * This class implements a cellGrid object, which simulates Conway's Game of
 * Life. It includes a very basic graphical interface which can print the
 * current state of the board to the terminal. It is included to aid with
 * debugging, and may not be useful for displaying full runs.
 *
 *
 * TO - DO:
 *
 * This project currently uses a lookup table for three by three neighborhoods of cells.
 * This is currently implemented to use a mutable object boolean[][] as the key. I have
 * since learned that the keys must not be mutable objects. Therefore, I should rewrite
 * code to make the keys immutable objects. I will change the row implementation to instead
 * code 0s and 1s instead of copying arrays directly. The neighborhood will be represented
 * as a string of 0s and 1s instead of a boolean[][] which should let me properly use
 * neighborhoods as keys.
 *
 * Test: if the lookup table works properly - try lots of values of neighborhoods.
 * Test: if next gen works properly.
 * Test: if makeNeighborhood properly adjusts.
 * Test: simple oscillating pattern.
 *
 *
 */


import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import java.io.*;
import java.util.Scanner;


public class cellGrid {
    // Instance Variables
    private int gridHeight = 14;
    private int gridWidth = 14;
    private boolean[][] cellMatrix;
    private HashMap<String, Boolean> liveDieTable;
    private HashMap<String, Integer> scoreMap;
    private int genCount = 0;

    /*
    Returns a cellGrid object.

    @return the cellGrid object
     */
    public cellGrid() {
        cellMatrix = new boolean[gridHeight][gridWidth];
        liveDieTable = new HashMap<String, Boolean>();
        scoreMap = new HashMap<String, Integer>();
        String tempNeighborhood = "";
        initializeLiveDieTable(0, tempNeighborhood);
    }

    /*
    Returns a cellGrid object. Note that the user dimensions are increased by
    two - this is specific to the implementation and should not affect the
    user's experience.

    @param gridHeight    the int number of rows in the grid.
    @param gridWidth     the int number of columns in the grid.
    @return the cellGrid object
     */
    public cellGrid(int gridHeight, int gridWidth) {
        this.gridHeight = gridHeight + 2;
        this.gridWidth = gridWidth + 2;

        cellMatrix = new boolean[this.gridHeight][this.gridWidth];
        liveDieTable = new HashMap<String, Boolean>();
        scoreMap = new HashMap<String, Integer>();
        String tempNeighborhood = "";
        initializeLiveDieTable(0, tempNeighborhood);
    }

    /*
    Main function for program - can be used for testing or regular use.
     */
    public static void main(String args[]) {
        cellGrid myGrid = new cellGrid(12, 12);

        Configuration startingConfig = myGrid.loadStartingConfig();
        myGrid.setStartingConfiguration(startingConfig);
        //myGrid.runGame(5);

        //boolean[][] startingConfig = new boolean[12][12];
        //myGrid.setStartingConfiguration(startingConfig);

        double score = myGrid.runGame(30, true);
        System.out.println("Final Score: " + String.valueOf(score));
    }

    /*

    Citation: https://stackoverflow.com/questions/18551251/how-to-open-a-text-file

     */
    private Configuration loadStartingConfig() {
        String fileName = "startingConfig.txt";
        String line = null;
        Configuration startingConfig = new Configuration(gridHeight, gridWidth);
        int lineCounter = 0;

        try {
            // FileReader reads text files in the default encoding.
            FileReader fileReader = new FileReader(fileName);

            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            while((line = bufferedReader.readLine()) != null) {
                for (int i = 0; i < line.length(); i++) {
                    if (line.charAt(i) == '1') {
                        startingConfig.setCell(lineCounter, i, true);
                    }
                }
                lineCounter++;
            }

            // Always close files.
            bufferedReader.close();
        }
        catch(FileNotFoundException ex) {
            System.out.println("Unable to open file '" + fileName + "'");
        }
        catch(IOException ex) {
            System.out.println("Error reading file '" + fileName + "'");
        }
        return startingConfig;
    }

    /*
    Sets the initial configuration of living and dead cells on the cellMatrix.
    Assumes that the input is of the proper dimensions.

    @param initialConfig    2-D array that holds boolean values, with true
                            denoting living cells, false denoting dead cells.
     */
    public void setStartingConfiguration(Configuration initialConfig) {
        for (int row = 1; row < gridHeight - 1; row++) {
            for (int col = 1; col < gridWidth - 1; col++) {
                cellMatrix[row][col] = initialConfig.getCell(row-1, col-1);
            }
        }
    }

    public void clearCellMatrix() {
        for (int row = 0; row < gridHeight; row++) {
            for (int col = 0; col < gridWidth; col++) {
                cellMatrix[row][col] = false;
            }
        }
    }

    public double runGame(int numGenerations, boolean printToTerminal) {
        int totalScore = 0;

        if (printToTerminal) {
            System.out.println("Welcome to the Game of Life. Here is your starting configuration:");
            printGrid();
        }

        for (int gen = 0; gen < numGenerations; gen++) {
            scoreMap = new HashMap<String, Integer>();
            totalScore += nextGen();
            if (printToTerminal) {
                System.out.println("\nGeneration " + String.valueOf(gen + 1) + ":");
                printGrid();
            }
        }

        if (printToTerminal) {
            System.out.println("Thanks for playing.\n");
        }

        return totalScore;
    }

    /*
    Updates which cells are alive and which cells are dead in the next
    generation. The cells on the outer edge do not count as live cells and
    may not come to life. Returns the score for each generation.
     */
    public double nextGen() {
        boolean[][] cellMatrixNew = new boolean[this.gridHeight][this.gridWidth];
        String rowChunkA = "";
        String rowChunkB = "";
        String rowChunkC = "";
        String neighborhood;
        int rowARelInd;
        double score = 0;
        int numLiveCells = 0;

        for (int curCol = 1; curCol < gridWidth -1; curCol++) {
            rowChunkA = copyChunk(0, curCol-1, curCol +2);
            rowChunkB = copyChunk(1, curCol-1, curCol +2);
            rowARelInd = 0;

            for (int curRow = 1; curRow < gridHeight -1; curRow++) {
                switch (rowARelInd) {
                    case 0:
                        rowChunkC = copyChunk(curRow+1, curCol-1, curCol+2);
                        break;
                    case 1:
                        rowChunkB = copyChunk(curRow+1, curCol-1, curCol+2);
                        break;
                    case 2:
                        rowChunkA = copyChunk(curRow+1, curCol-1, curCol+2);
                        break;
                    default:
                        System.out.println("It's dead, Jim.");
                }
                neighborhood = makeNeighborhood(rowARelInd, rowChunkA, rowChunkB, rowChunkC);
                cellMatrixNew[curRow][curCol] = isAliveNextGen(neighborhood);
                if (isAliveNextGen(neighborhood)) {
                    score += 1;
                }
                score += scoreNeighborhood(neighborhood, curRow, curCol);
                if (rowARelInd == 0) {
                    rowARelInd = 2;
                } else { rowARelInd--; }
            }
        }
        updateCellMatrix(cellMatrixNew);
        return score;
    }

    private String makeNeighborhood(int rowARelInd, String rowChunkA, String rowChunkB, String rowChunkC) {
        String neighborhood = "";
        switch (rowARelInd) {
            case 0:
                neighborhood = rowChunkA + rowChunkB + rowChunkC;
                break;
            case 1:
                neighborhood = rowChunkC + rowChunkA + rowChunkB;
                break;
            case 2:
                neighborhood = rowChunkB + rowChunkC + rowChunkA;
                break;
            default:
                System.out.println("It's dead in the neighborhood, Jim.");
        }
        return neighborhood;
    }

    private boolean isAliveNextGen(String neighborhood) {
        boolean status = liveDieTable.get(neighborhood);
        return status;
    }

    private double scoreNeighborhood(String neighborhood, int curRow, int curCol) {
        if (neighborhood.equals("000000000")) {
            return 0;
        }
        String neighborhoodAndLocation = neighborhood + "r" + String.valueOf(curRow) + "c" + String.valueOf(curCol);

        if (scoreMap.containsKey(neighborhood)) {
            if (scoreMap.containsKey(neighborhoodAndLocation)) {
                return 250;
            } else {
                scoreMap.put(neighborhoodAndLocation, 1);
                return 500;
            }
        } else {
            scoreMap.put(neighborhood, 1);
            scoreMap.put(neighborhoodAndLocation, 1);
            return 0;
        }
    }

    private String copyChunk(int rowIndex, int leftColBound, int rightColBound) {
        String chunk = "";
        for (int i = leftColBound; i < rightColBound; i++) {
            if (cellMatrix[rowIndex][i]) {
                chunk += "1";
            } else {
                chunk += "0";
            }
        }
        return chunk;
    }

    private void updateCellMatrix(boolean[][] cellMatrixNew) {
        for(int i=0; i < gridHeight; i++)
            for(int j=0; j < gridWidth; j++)
                cellMatrix[i][j]=cellMatrixNew[i][j];
    }

    /*
    Prints out the current grid of living and dead cells, with living cells
    depicted as black boxes, and dead cells depicted as white spaces. The
    grid is surrounded by small white squares - these are dead cells whose
    statuses cannot change and act as the edge of the grid.
     */
    public void printGrid() {
        System.out.println();

        System.out.println(new String(new char[gridWidth]).replace("\0", "\u25AB "));
        for (int row = 1; row < gridHeight -1; row++) {
            String curRowString = "\u25AB ";
            for (int col = 1; col < gridWidth -1; col++) {
                if (cellMatrix[row][col]) {
                    curRowString += "\u25A0 ";
                } else {
                    curRowString += "\u25A1 ";
                }
            }
            System.out.println(curRowString + "\u25AB");
        }
        System.out.println(new String(new char[gridWidth]).replace("\0", "\u25AB "));
    }

    public void viewSimulation(boolean stepThru, int numGens, Configuration config) {
        setStartingConfiguration(config);
        final String ANSI_CLS = "\u001b[2J";
        final String ANSI_HOME = "\u001b[H";

        int totalScore = 0;

        System.out.print(ANSI_CLS + ANSI_HOME);
        System.out.flush();
        System.out.println("Welcome to the Game of Life. Press the enter key to step through each frame.");
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
        System.out.print(ANSI_CLS + ANSI_HOME);
        System.out.flush();

        System.out.println("Here is your starting configuration:\n");
        printGrid();

        scanner = new Scanner(System.in);
        scanner.nextLine();

        for (int gen = 0; gen < numGens; gen++) {
            System.out.print(ANSI_CLS + ANSI_HOME);
            System.out.flush();
            scoreMap = new HashMap<String, Integer>();
            totalScore += nextGen();
            System.out.println("\nGeneration " + String.valueOf(gen + 1) + ":");
            printGrid();
            scanner = new Scanner(System.in);
            scanner.nextLine();

        }
        System.out.println("Your final score is: " + String.valueOf(totalScore));
    }

    private void initializeLiveDieTable(int curCellIndex, String neighborhood) {
        if (curCellIndex > 8) {
            addNeighborhoodToTable(neighborhood, evalNeighborhoodType(neighborhood));
        }
        else {
            String neighborhood0 = neighborhood + "0";
            String neighborhood1 = neighborhood + "1";
            initializeLiveDieTable(curCellIndex + 1, neighborhood0);
            initializeLiveDieTable(curCellIndex + 1, neighborhood1);
        }
    }

    private void addNeighborhoodToTable(String neighborhood, boolean resultingStatus) {
        this.liveDieTable.put(neighborhood, evalNeighborhoodType(neighborhood));
    }

    public boolean evalNeighborhoodType(String neighborhood) {
        int neighborCount = 0;
        boolean curAlive = false;
        char alive = '1';

        if (neighborhood.charAt(4) == alive) {
            curAlive = true;
        }

        for (int i = 0; i < 9; i++) {
            if (neighborhood.charAt(i) == alive) {
                if (i != 4) {
                    neighborCount++;
                }
            }
        }

        if (neighborCount == 3) {
            return true;
        } else {
            if (curAlive && neighborCount == 2) {
                return true;
            }
        }
        return false;
    }

    private void testLookupTable() {
        String testNeighborhood1 = "000000000";
        String testNeighborhood2 = "111111111";
        String testNeighborhood3 = "111101111";
        String testNeighborhood4 = "000010000";
        String testNeighborhood5 = "111000000";
        String testNeighborhood6 = "000111000";
        String testNeighborhood7 = "000000111";
        String testNeighborhood8 = "101000101";
        String testNeighborhood9 = "010000010";
        String testNeighborhood10 = "010010010";

        System.out.println("These should all be living");
        checkLookupTable(testNeighborhood5);
        checkLookupTable(testNeighborhood6);
        checkLookupTable(testNeighborhood7);
        checkLookupTable(testNeighborhood10);

        System.out.println("These should all be dead");
        checkLookupTable(testNeighborhood1);
        checkLookupTable(testNeighborhood2);
        checkLookupTable(testNeighborhood3);
        checkLookupTable(testNeighborhood4);
        checkLookupTable(testNeighborhood8);
        checkLookupTable(testNeighborhood9);
    }

    private void checkLookupTable(String neighborhood) {
        if (isAliveNextGen(neighborhood)) {
            System.out.println("Will Be Alive");
        } else { System.out.println("will die");}
    }

}