/**
 * cellGrid class implementation
 *
 * Author: Ryan Gorey
 *
 * This class implements a cellGrid object, which simulates Conway's Game of
 * Life.
 */
public class cellGrid {
    // Instance Variables
    private int gridHeight = 12;
    private int gridWidth = 12;
    private boolean[][] cellMatrix;

    /*
    Returns a cellGrid object.

    @return the cellGrid object
     */
    public cellGrid() {
        cellMatrix = new boolean[gridHeight][gridWidth];
    }

    /*
    Returns a cellGrid object.

    @param gridHeight    the int number of rows in the grid.
    @param gridWidth     the int number of columns in the grid.
    @return the cellGrid object
     */
    public cellGrid(int gridHeight, int gridWidth) {
        this.gridHeight = gridHeight;
        this.gridWidth = gridWidth;
        cellMatrix = new boolean[gridHeight][gridWidth];
    }

    /*
    Sets the initial configuration of living and dead cells on the cellMatrix.
    Assumes that the input is of the proper dimensions.

    @param initialConfig    2-D array that holds boolean values, with true
                            denoting living cells, false denoting dead cells.
     */
    public void setStartingConfiguration(boolean[][] initialConfig) {
        for (int i = 0; i < gridHeight; i++) {
            for (int j = 0; j < gridWidth; j++) {
                cellMatrix[i][j] = initialConfig[i][j];
            }
        }
    }

    /*
    Prints out the current grid of living and dead cells, with living cells
    depicted as black boxes, and dead cells depicted as white spaces.
     */
    public void printGrid() {
        System.out.println();

        for (int i = 0; i < gridHeight; i++) {
            String curRowString = "";
            for (int j = 0; j < gridWidth; j++) {
                if (cellMatrix[i][j]) {
                    curRowString += "\u25A0 ";
                } else {
                    curRowString += "\u25A1 ";
                }
            }
            System.out.println(curRowString);
        }
    }

    public static void main(String args[]) {
        cellGrid myGrid = new cellGrid();
        myGrid.printGrid();
    }

}