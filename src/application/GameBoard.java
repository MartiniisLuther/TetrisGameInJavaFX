package application;

public class GameBoard {
	// some attributes for the game
	public static final int COLS = 10; // width in blocks
	public static final int ROWS = 20; // height in blocks
	public static final int BLOCK_SIZE = 30; // pixel size of each block

	// an array- 2D
	// if 0 = empty; >0 = filled, where the different numbers can mean different
	// colors
	private int[][] grid = new int[ROWS][COLS];

	// constructor for the GameBoard class
	public GameBoard() {
		// initialize an empty grid
		for (int r = 0; r < ROWS; r++) {
			for (int c = 0; c < COLS; c++) {
				grid[r][c] = 0;
			}
		}
	}
	
//	another method that returns a grid array
	public int[][] getGrid() {
		return grid;
	}
	
//	method to set cells
	public void setCell(int row, int col, int value) {
		if (row >= 0 && row < ROWS && col >= 0 && col < COLS) {
			grid[row][col] = value;
		}
	}

}
