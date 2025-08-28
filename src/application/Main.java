package application;

import java.awt.Toolkit;
import java.util.Random;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class Main extends Application {

	public static final int WINDOW_WIDTH = 300;
	public static final int WINDOW_HEIGHT = 600;

	@SuppressWarnings("incomplete-switch")
	@Override
	public void start(Stage primaryStage) {
		Pane root = new Pane();
		Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);

		// instance of gameboard class and pass the attributes to the Canvas class
		GameBoard gameBoard = new GameBoard();

		Canvas canvas = new Canvas(GameBoard.COLS * GameBoard.BLOCK_SIZE, GameBoard.ROWS * GameBoard.BLOCK_SIZE);
		// create graphics
		GraphicsContext gContext = canvas.getGraphicsContext2D();
		root.getChildren().add(canvas);

		Random random = new Random();
		// Tetromino currentPiece = new
		// Tetromino(random.nextInt(Tetromino.SHAPES.length)); //old
		final Tetromino[] currentPiece = { new Tetromino(random.nextInt(Tetromino.SHAPES.length)) };// updated

		// Game loop
		final long[] lastUpdate = { 0 };
		final double dropInterval = 0.5e9; // 0.5 seconds in nanoseconds

		// score counter
		final int[] score = { 0 };

		// game over flag
		final boolean[] gameOver = { false };

		/*
		 * AnimationTimer: calls handle(long new) ~ 60 fps. "now" is in nanoseconds;
		 * 
		 * GraphicsContext: the drawing API for a Canvas. setFill sets fill color,
		 * fillRect paints a solid rectangle. setStroke sets line color; strokeRect
		 * draws an outiline. clearRect erases pixels.
		 */

		AnimationTimer gameLoop = new AnimationTimer() {
			@Override
			public void handle(long now) {
				if (lastUpdate[0] == 0) {
					lastUpdate[0] = now;
					return;
				}

				if (!gameOver[0]) {
					// auto fall
					if (now - lastUpdate[0] >= dropInterval) {
						int newRow = currentPiece[0].getRow() + 1;
						if (gameBoard.isValidPosition(currentPiece[0], newRow, currentPiece[0].getCol())) {
							currentPiece[0].moveDown();
						} else {
							// can't move down -> lock and spawn next
							gameBoard.lockPiece(currentPiece[0]);
							// should count the cleared lines of the fitted pieaces from the bottom
							int cleared = gameBoard.clearFullLines();

							// add scores based on the lines cleared
							if (cleared > 0) {
								switch (cleared) {
								case 1 -> score[0] += 100;
								case 2 -> score[0] += 300;
								case 3 -> score[0] += 500;
								case 4 -> score[0] += 800;
								}
							}

							currentPiece[0] = new Tetromino(random.nextInt(Tetromino.SHAPES.length));

							// if the new piece can't be placed -> game over (& stop timer)
							if (!gameBoard.isValidPosition(currentPiece[0], currentPiece[0].getRow(),
									currentPiece[0].getCol())) {
								gameOver[0] = true;
								// System.out.println("Game Over!");

							}
						}

						lastUpdate[0] = now;
					}
				}

				// RENDER
				gContext.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

				// Drawing the grid lines - strokeRect draws only the outline
				for (int r = 0; r < GameBoard.ROWS; r++) {
					for (int c = 0; c < GameBoard.COLS; c++) {
						gContext.setStroke(Color.GREY); // or Color.GRAY
						gContext.strokeRect(c * GameBoard.BLOCK_SIZE, r * GameBoard.BLOCK_SIZE, GameBoard.BLOCK_SIZE,
								GameBoard.BLOCK_SIZE);
					}
				}

				// draw settled blocks from the board
				int[][] board = gameBoard.getGrid();
				for (int r = 0; r < GameBoard.ROWS; r++) {
					for (int c = 0; c < GameBoard.COLS; c++) {
						int v = board[r][c];
						if (v != 0) {
							gContext.setFill(Tetromino.COLORS[v - 1]);
							gContext.fillRect(c * GameBoard.BLOCK_SIZE, r * GameBoard.BLOCK_SIZE, GameBoard.BLOCK_SIZE,
									GameBoard.BLOCK_SIZE);
						}
					}
				}

				// draw the score
				gContext.setFill(Color.BLACK);
				gContext.setFont(new Font("Impact", 18));
				gContext.fillText("Score: " + score[0], 10, 20);

				// draw the game over screen
				if (gameOver[0]) {
					gContext.setFill(Color.RED);
					gContext.setFont(new Font("Arial Black", 42));							
					gContext.fillText("GAME OVER", GameBoard.COLS * GameBoard.BLOCK_SIZE / 2 - 100,
							GameBoard.ROWS * GameBoard.BLOCK_SIZE / 2);
				}

				// render/draw the current falling block
				int[][] shape = currentPiece[0].getShape();
				gContext.setFill(currentPiece[0].getColor());

				for (int r = 0; r < shape.length; r++) {
					for (int c = 0; c < shape[r].length; c++) {
						if (shape[r][c] != 0) {
							// fillRect(x, y, w, h): paints a filled rectangle at pixel coords
							gContext.fillRect((currentPiece[0].getCol() + c) * GameBoard.BLOCK_SIZE,
									(currentPiece[0].getRow() + r) * GameBoard.BLOCK_SIZE, GameBoard.BLOCK_SIZE,
									GameBoard.BLOCK_SIZE);
						}
					}
				}
			}
		};

		gameLoop.start();

		// controls
		scene.setOnKeyPressed(event -> {
			switch (event.getCode()) {
			case LEFT -> {
				int newCol = currentPiece[0].getCol() - 1;
				if (gameBoard.isValidPosition(currentPiece[0], currentPiece[0].getRow(), newCol)) {
					currentPiece[0].moveLeft();
				}
			}

			case RIGHT -> {
				int newCol = currentPiece[0].getCol() + 1;
				if (gameBoard.isValidPosition(currentPiece[0], currentPiece[0].getRow(), newCol)) {
					currentPiece[0].moveRight();
				}
			}

			case DOWN -> {
				int newRow = currentPiece[0].getRow() + 1;
				if (gameBoard.isValidPosition(currentPiece[0], newRow, currentPiece[0].getCol())) {
					currentPiece[0].moveDown();
				}
			}
			case UP -> {
				// rotate when valid
				currentPiece[0].rotate();
				if (!gameBoard.isValidPosition(currentPiece[0], currentPiece[0].getRow(), currentPiece[0].getCol())) {
					// undo by rotating 3 times
					currentPiece[0].rotate();
					currentPiece[0].rotate();
					currentPiece[0].rotate();
				}
			}
			}
		});

		primaryStage.setTitle("Tetris");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	// main method
	public static void main(String[] args) {
		launch(args);
	}
}
