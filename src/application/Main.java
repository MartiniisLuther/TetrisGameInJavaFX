package application;

import java.util.Random;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Main extends Application {

	public static final int WINDOW_WIDTH = 300;
	public static final int WINDOW_HEIGHT = 600;

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
		Tetromino currentPiece = new Tetromino(random.nextInt(Tetromino.SHAPES.length));

		// Game loop
		final long[] lastUpdate = { 0 };
		final double dropInterval = 0.5e9; // 0.5 seconds in nanoseconds

		/*
		 * AnimationTimer: calls handle(long new) ~ 60 fps. now is in nanoseconds;
		 * compare it to throttle falling.
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

				// move when valid move is available
				if (now - lastUpdate[0] >= dropInterval) {
					int newRow = currentPiece.getRow() + 1;
					if (gameBoard.isValidPosition(currentPiece, newRow, currentPiece.getCol())) {
						currentPiece.moveDown();
					}

					// else: piece should lock in position, if no valid move
					lastUpdate[0] = now;
				}

				// clear screen: clearRect(x,y,w,h): wipes the canvas so we can redraw this
				// frame
				gContext.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

				// Drawing the grid- strokeRect draws only the outline
				for (int r = 0; r < GameBoard.ROWS; r++) {
					for (int c = 0; c < GameBoard.COLS; c++) {
						gContext.setStroke(Color.GREY); // or Color.GRAY
						gContext.strokeRect(c * GameBoard.BLOCK_SIZE, r * GameBoard.BLOCK_SIZE, GameBoard.BLOCK_SIZE,
								GameBoard.BLOCK_SIZE);
					}
				}

				// render/draw the pieces
				int[][] shape = currentPiece.getShape();
				gContext.setFill(currentPiece.getColor());

				for (int r = 0; r < shape.length; r++) {
					for (int c = 0; c < shape[r].length; c++) {
						if (shape[r][c] != 0) {
							// fillRect(x, y, w, h): paints a filled rectangle at pixel coords
							gContext.fillRect((currentPiece.getCol() + c) * GameBoard.BLOCK_SIZE,
									(currentPiece.getRow() + r) * GameBoard.BLOCK_SIZE, GameBoard.BLOCK_SIZE,
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
					int newCol = currentPiece.getCol() - 1;
					if (gameBoard.isValidPosition(currentPiece, currentPiece.getRow(), newCol)) {
						currentPiece.moveLeft();
					}
				}
	
				case RIGHT -> {
					int newCol = currentPiece.getCol() + 1;
					if (gameBoard.isValidPosition(currentPiece, currentPiece.getRow(), newCol)) {
						currentPiece.moveRight();
					}
				}
	
				case DOWN -> {
					int newRow = currentPiece.getRow() + 1;
					if (gameBoard.isValidPosition(currentPiece, newRow, currentPiece.getCol())) {
						currentPiece.moveDown();
					}
				}
				case UP -> {
					// rotate when valid
					currentPiece.rotate();
					if (!gameBoard.isValidPosition(currentPiece, currentPiece.getRow(), currentPiece.getCol())) {
						//undo by rotating 3 times
						currentPiece.rotate();
						currentPiece.rotate();
						currentPiece.rotate();
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
