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


public class cellGrid {
    // Instance Variables
    private int gridHeight = 14;
    private int gridWidth = 14;
    private boolean[][] cellMatrix;
    private HashMap<boolean[][], Boolean> liveDieTable;
    private int genCount = 0;

    /*
    Returns a cellGrid object.

    @return the cellGrid object
     */
    public cellGrid() {
        cellMatrix = new boolean[gridHeight][gridWidth];
        liveDieTable = new HashMap<boolean[][], Boolean>();
        boolean[][] tempNeighborhood = new boolean[3][3];
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
        liveDieTable = new HashMap<boolean[][], Boolean>();
        boolean[][] tempNeighborhood = new boolean[3][3];
        initializeLiveDieTable(0, tempNeighborhood);


    }

    /*
    Main function for program - can be used for testing or regular use.
     */
    public static void main(String args[]) {
        cellGrid myGrid = new cellGrid(12, 12);

        boolean[][] startingConfig = new boolean[12][12];
        myGrid.setStartingConfiguration(startingConfig);

        boolean[][] neighTest = {{false, false, false}, {true, true, true}, {false, false, false}};

        /*if (myGrid.liveDieTable.containsKey(neighTest)) {
            System.out.println("It worked");
        } else { System.out.println("It broke."); }*/
        System.out.println(myGrid.liveDieTable.size());

        myGrid.printGrid();
    }



    /*
    Sets the initial configuration of living and dead cells on the cellMatrix.
    Assumes that the input is of the proper dimensions.

    @param initialConfig    2-D array that holds boolean values, with true
                            denoting living cells, false denoting dead cells.
     */
    public void setStartingConfiguration(boolean[][] initialConfig) {
        for (int row = 1; row < gridHeight - 1; row++) {
            for (int col = 1; col < gridWidth - 1; col++) {
                cellMatrix[row][col] = initialConfig[row-1][col-1];
            }
        }
    }

    private void updateRowChunk(int rowARelInd, int curRow, int curCol, boolean[] rowChunkA, boolean[] rowChunkB, boolean[] rowChunkC) {
        switch (rowARelInd) {
            case 0:
                rowChunkC = Arrays.copyOfRange(cellMatrix[curRow + 1], curCol-1, curCol+2);
                break;
            case 1:
                rowChunkB = Arrays.copyOfRange(cellMatrix[curRow + 1], curCol-1, curCol+2);
                break;
            case 2:
                rowChunkA = Arrays.copyOfRange(cellMatrix[curRow + 1], curCol-1, curCol+2);
                break;
            default:
                System.out.println("It's dead, Jim.");
        }
    }

    private boolean[][] makeNeighborhood(int rowARelInd, boolean[] rowChunkA, boolean[] rowChunkB, boolean[] rowChunkC) {
        boolean[][] neighborhood = new boolean[3][3];
        neighborhood[rowARelInd] = rowChunkA;
        neighborhood[(rowARelInd + 1) % 3] = rowChunkB;
        neighborhood[(rowARelInd + 2) % 2] = rowChunkC;
        return neighborhood;
    }

    private boolean isAliveNextGen(boolean[][] neighborhood) {
        boolean status = this.liveDieTable.get(neighborhood);
        return status;
    }





    /*
    Updates which cells are alive and which cells are dead in the next
    generation. The cells on the outer edge do not count as live cells and
    may not come to life.
     */
    public void nextGen() {
        boolean[][] cellMatrixNew = new boolean[this.gridHeight][this.gridWidth];
        boolean[] rowChunkA = new boolean[3];
        boolean[] rowChunkB = new boolean[3];
        boolean[] rowChunkC = new boolean[3];
        boolean[][] neighborhood = new boolean[3][3];
        int rowARelInd = 0;

        for (int curCol = 1; curCol < gridWidth -1; curCol++) {
            rowChunkA = Arrays.copyOfRange(cellMatrix[0], curCol-1, curCol+2);
            rowChunkB = Arrays.copyOfRange(cellMatrix[1], curCol-1, curCol+2);
            rowARelInd = 0;

            for (int curRow = 1; curRow < gridHeight -1; curRow++) {
                updateRowChunk(rowARelInd, curRow, curCol, rowChunkA, rowChunkB, rowChunkC);
                neighborhood = makeNeighborhood(rowARelInd, rowChunkA, rowChunkB, rowChunkC);
                cellMatrixNew[curRow][curCol] = isAliveNextGen(neighborhood);
                rowARelInd = (rowARelInd - 1) % 3;
            }
        }
        updateCellMatrix(cellMatrixNew);
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

    public boolean checkLookupTable(boolean[][] neighborhood) {
        return isAliveNextGen(neighborhood);
    }

    private void initializeLiveDieTable(int curCellIndex, boolean[][] neighborhood) {
        if (curCellIndex > 8) {
            addNeighborhoodToTable(neighborhood, evalNeighborhoodType(neighborhood));
        }
        else {
            neighborhood[curCellIndex/3][curCellIndex % 3] = true;
            initializeLiveDieTable(curCellIndex + 1, neighborhood);
            neighborhood[curCellIndex/3][curCellIndex % 3] = false;
            initializeLiveDieTable(curCellIndex + 1, neighborhood);
        }
    }

    private void addNeighborhoodToTable(boolean[][] neighborhood, boolean resultingStatus) {
        this.liveDieTable.put(neighborhood, evalNeighborhoodType(neighborhood));
    }

    private boolean evalNeighborhoodType(boolean[][] neighborhood) {
        int totalNeighbors = Arrays.deepToString(neighborhood).replaceAll("[^t]", "").length();
        if (neighborhood[1][1]) {
            if (totalNeighbors == 3 | totalNeighbors == 4) {
                return true;
            }
        }
        else {
            if (totalNeighbors == 3) {
                return true;
            }
        }
        return false;
    }

}