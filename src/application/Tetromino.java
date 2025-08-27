package application;

import javafx.scene.paint.Color;

public class Tetromino {
	/*
	 * here for the falling peaces; 2D shape ~ array of integers
	 * 
	 * all 7 tetromino shapes as 2D arrays 1 = filled cell, 0 = empty cell
	 */

	public static final int[][][] SHAPES = {
			// I shape
			{ { 1, 1, 1, 1 } },
			// O shape
			{ { 1, 1 }, { 1, 1 } },
			// T shape
			{ { 0, 1, 0 }, { 1, 1, 1 } },
			// S shape
			{ { 0, 1, 1 }, { 1, 1, 0 } },
			// Z shape
			{ { 1, 1, 0 }, { 0, 1, 1 } },
			// J shape
			{ { 1, 0, 0 }, { 1, 1, 1 } },
			// L shape
			{ { 0, 0, 1 }, { 1, 1, 1 } } };

	// matching colors for each shape
	public static final Color[] COLORS = { Color.CYAN, Color.YELLOW, Color.PURPLE, Color.GREEN, Color.RED, Color.BLUE,
			Color.ORANGE };

	private int[][] shape; // current shape matrix
	private final int typeIndex; // which of the 7 shapes
	private Color color; // color of current piece
	private int row; // top-left row position
	private int col; // top-left column position

	// constructor
	public Tetromino(int shapeIndex) {
		// copy shape from SHAPES (to avoid changing the original)
		this.shape = copyShape(SHAPES[shapeIndex]);
		this.typeIndex = shapeIndex;
		this.color = COLORS[shapeIndex];
		this.row = 0; // spawn at top
		this.col = GameBoard.COLS / 2 - shape[0].length / 2; // center horizontally
	}

	// copy shape
	private int[][] copyShape(int[][] source) {
		int[][] copy = new int[source.length][source[0].length];
		for (int r = 0; r < source.length; r++) {
			System.arraycopy(source[r], 0, copy[r], 0, source[r].length);
		}
		return copy;
	}

	// getters
	public int[][] getShape() { return shape; }
	public Color getColor() { return color; }
	public int getRow() { return row; }
	public int getCol() { return col; }
	public int getTypeIndex() { return typeIndex; }
	
//	movements 
	public void moveDown() { row++; }
	public void moveLeft() { col--; }
	public void moveRight() { col++; }
	
	/**
	 * Rotates the shape 90 degrees clockwise.
	 * 
	 * this works by: 
	 * - Creating a new matrix with swapped dimensions (col count becomes row count).
	 * - Copying each cell from old position [r][c] to new poaition [c][rows-1-r]
	 */
	
	public void rotate() {
		int rows = shape.length;
		int cols = shape[0].length;
		int[][] rotated = new int[cols][rows];
		
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < cols; c++) {
				rotated[c][rows - 1 - r] = shape[r][c];
			}
		}
		shape = rotated;
	}
}
