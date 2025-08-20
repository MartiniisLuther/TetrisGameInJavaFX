package application;

import java.util.Random;

import javafx.application.Application;
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
		Random random = new Random();
		Tetromino currentPiece = new Tetromino(random.nextInt(Tetromino.SHAPES.length));
		Pane root = new Pane();
		Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);

		// instance of gameboard class and pass the attributes to the Canvas class
		GameBoard gameBoard = new GameBoard();

		Canvas canvas = new Canvas(GameBoard.COLS * GameBoard.BLOCK_SIZE, GameBoard.ROWS * GameBoard.BLOCK_SIZE);
		// create graphics
		GraphicsContext gContext = canvas.getGraphicsContext2D();

		// fill some cells to see colors
		gameBoard.setCell(5, 4, 1);
		gameBoard.setCell(5, 5, 1);
		gameBoard.setCell(10, 3, 2);
		gameBoard.setCell(15, 6, 3);

		// draw the grid
		for (int r = 0; r < GameBoard.ROWS; r++) {
			for (int c = 0; c < GameBoard.COLS; c++) {
				int cellValue = gameBoard.getGrid()[r][c];

				if (cellValue != 0) {
					// //not yet used for placed blocks
				}

				gContext.setStroke(Color.GRAY);
				gContext.strokeRect(c * GameBoard.BLOCK_SIZE, r * gameBoard.BLOCK_SIZE, GameBoard.BLOCK_SIZE,
						GameBoard.BLOCK_SIZE);

			}

		}
		
//		Draw current piece
		int[][] shape = currentPiece.getShape();
		gContext.setFill(currentPiece.getColor());
		
		for (int r = 0; r < shape.length; r++) {
			for (int c = 0; c < shape[r].length; c++) {
				if (shape[r][c] != 0) {
					gContext.fillRect((currentPiece.getCol() + c) * GameBoard.BLOCK_SIZE, 
										(currentPiece.getRow() + r) * GameBoard.BLOCK_SIZE, 
										GameBoard.BLOCK_SIZE, GameBoard.BLOCK_SIZE);
				}
			}
		}
		
		

		root.getChildren().add(canvas);

		primaryStage.setTitle("Tetris");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

//	main method 
	public static void main(String[] args) {
		launch(args);
	}
}
