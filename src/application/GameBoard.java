package application;

public class GameBoard {
	// some attributes for the game
	public static final int COLS = 10; // width in blocks
	public static final int ROWS = 20; // height in blocks
	public static final int BLOCK_SIZE = 30; // pixel size of each block

	// an array- 2D
	// if 0 = empty; >0 = filled, where the different numbers can mean different
	// colors
//	private int[][] grid = new int[ROWS][COLS]; //old one
	private final int[][] grid = new int[ROWS][COLS];


	// constructor for the GameBoard class
	public GameBoard() {
		// initialize an empty grid
		for (int r = 0; r < ROWS; r++) {
			for (int c = 0; c < COLS; c++) {
				grid[r][c] = 0;
			}
		}
	}

	// another method that returns a grid array
	public int[][] getGrid() { return grid; }

	// method to set cells
	public void setCell(int row, int col, int value) {
		if (row >= 0 && row < ROWS && col >= 0 && col < COLS) {
			grid[row][col] = value;
		}
	}
	
	/**
	 * Lock current piece into the grid
	 */
	public void lockPiece(Tetromino piece) {
		int[][] shape = piece.getShape();
		int type = piece.getTypeIndex() + 1; //store as 1, 2, ... 7
		int row = piece.getRow();
		int col = piece.getCol();
		
		for (int r = 0; r < shape.length; r ++) {
			for (int c = 0; c < shape[r].length; c++) {
				if (shape[r][c] != 0) {
					int boardRow = row + r;
					int boardCol = col + c;
					if (boardRow >= 0 && boardRow < ROWS && boardCol >= 0 && boardCol < COLS) {
						grid[boardRow][boardCol] = type;
					}
				}
			}
		}
	}

	// check collisions and bounds
	public boolean isValidPosition(Tetromino piece, int newRow, int newCol) {
		int[][] shape = piece.getShape();

		for (int r = 0; r < shape.length; r++) {
			for (int c = 0; c < shape[r].length; c++) {
				if (shape[r][c] != 0) {
					int boardRow = newRow + r;
					int boardCol = newCol + c;

					// check walls
					if (boardCol < 0 || boardCol >= COLS) { return false; }

					// check floor
					if (boardRow >= ROWS) { return false; }

					// check collision with filled cells
					if (boardRow >= 0 && grid[boardRow][boardCol] != 0) { return false; }
				}
			}
		}
		return true;
	}
	
//	this method should clear Tetrominoes from the base when they've been fitted
	public int clearFullLines() {
		int cleared = 0;
		
		for (int r = ROWS -1; r >= 0; r--) {
			boolean full = true;
			for (int c = 0; c < COLS; c++) {
				if (grid[r][c] == 0) {
					full = false;
					break;
				}
			}
			
			if (full) {
				cleared++;
				
//				shift all the above rows downwards by one
				for (int rr = r; rr > 0; rr--) {
					System.arraycopy(grid[rr - 1], 0, grid[rr], 0, COLS);
				}
				
//				clear top row
				for (int c = 0; c < COLS; c++) {
					grid[0][c] = 0;
				}
				
				// re-check same row index, since they've been shifted down
				r++;
			}
		}
		return cleared;
	}

}
