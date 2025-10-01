package application;

import java.util.List;
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
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class Main extends Application {

	public static final int WINDOW_WIDTH = 700;
	public static final int WINDOW_HEIGHT = 700;

	// attributes for blinking animation
	private long lastBlinkTime = 0;
	private boolean showPrompt = true;
	private static final long BLINK_INTERVAL = 500_000_000;

	// pause
	private boolean paused = false;
	
	private GameBoard gameBoard;
	
    // --- GAME STATE fields ---
    private final int[] score = {0};
    private final int[] linesCleared = {0};
    private final int[] level = {1};
    private final boolean[] gameOver = {false};
    private final boolean[] holdUsedPiece = {false};
    private final boolean[] highScoreSaved = {false};
    private final boolean[] showCongrats = {false};

    //pieces
    private final Tetromino[] currentPiece = {null};
    private final Tetromino[] nextPiece = {null};
    private final Tetromino[] holdPiece = {null};
    
    //managers
    private final SoundManager soundManager = new SoundManager();
    private final HighScoreManager highScoreManager = new HighScoreManager();
    
	private final Random random = new Random();

	
	// --- HARD DROP method (nested method) ---
    private void hardDrop(Tetromino piece) {
        // move piece down until blocked
        while (gameBoard.isValidPosition(piece, piece.getRow() + 1, piece.getCol())) {
            piece.moveDown();
        }

        // lock the piece immediately
        gameBoard.lockPiece(piece);
        
        //play harddrop sound
        soundManager.playHardDropSound();

        // clear lines
        int cleared = gameBoard.clearFullLines();
        if (cleared > 0) {
            switch (cleared) {
                case 1 -> score[0] += 100;
                case 2 -> score[0] += 300;
                case 3 -> score[0] += 500;
                case 4 -> score[0] += 800;
            }

            linesCleared[0] += cleared;
            soundManager.playLineClearSound();

            // level up after 10 lines
            if (linesCleared[0] >= level[0] * 10) {
                level[0]++;
                soundManager.playLevelUpSound();
            }
        }

        // reset hold usage
        holdUsedPiece[0] = false;

        // spawn next piece
        currentPiece[0] = nextPiece[0];
        nextPiece[0] = new Tetromino(random.nextInt(Tetromino.SHAPES.length));

        // check game over
        if (!gameBoard.isValidPosition(currentPiece[0], currentPiece[0].getRow(), currentPiece[0].getCol())) {
            gameOver[0] = true;

            if (!highScoreSaved[0]) {
                int currentHighest = highScoreManager.getTopScores().isEmpty() ? 0
                        : highScoreManager.getTopScores().get(0);

                if (score[0] > currentHighest) {
                    showCongrats[0] = true;
                    soundManager.playHighScoreSound();
                } else {
                    soundManager.playGameOverSound();
                }

                soundManager.stopMusic();
                highScoreManager.addScores(score[0]);
                highScoreSaved[0] = true;
            }
        }
    }

	@SuppressWarnings("incomplete-switch")
	@Override
	public void start(Stage primaryStage) {
		Pane root = new Pane();
		root.setStyle("-fx-background-color: #111827"); // dark gray-blue
		Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);


		Canvas canvas = new Canvas(GameBoard.COLS * GameBoard.BLOCK_SIZE + 180, GameBoard.ROWS * GameBoard.BLOCK_SIZE);
		// create graphics
		GraphicsContext gContext = canvas.getGraphicsContext2D();
		root.getChildren().add(canvas);

		// Game loop
		final long[] lastUpdate = { 0 };
		
		//initialization
		gameBoard = new GameBoard();
		currentPiece[0] = new Tetromino(random.nextInt(Tetromino.SHAPES.length));
		nextPiece[0] = new Tetromino(random.nextInt(Tetromino.SHAPES.length));
		holdPiece[0] = null;


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

				if (!gameOver[0] && !paused) { // if paused, block piece updates
					// dynamic drop interval based on level
					long currentInterval = Math.max(100_000_000, 500_000_000 - (level[0] - 1) * 50_000_000);

					// play background music
					soundManager.playMusic();

					// auto fall bricks
					if (now - lastUpdate[0] >= currentInterval) {
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

								// track total lines
								linesCleared[0] += cleared;
								// play sound when a line is cleared
								soundManager.playLineClearSound();

								// level up every 10 lines
								if (linesCleared[0] >= level[0] * 10) {
									level[0]++;
									lastUpdate[0] = now - currentInterval; // new speed kicks in immediately

									// play level up sound
									soundManager.playLevelUpSound();
								}
							}

							// spawn new Tetromino logik
							holdUsedPiece[0] = false; // allow hold again after new piece spawns
							currentPiece[0] = nextPiece[0];
							nextPiece[0] = new Tetromino(random.nextInt(Tetromino.SHAPES.length));

							// checkpoint
							if (!gameBoard.isValidPosition(currentPiece[0], currentPiece[0].getRow(),
									currentPiece[0].getCol())) {
								gameOver[0] = true;

								// do highscore
								if (!highScoreSaved[0]) {
									int currentHighest = highScoreManager.getTopScores().isEmpty() ? 0
											: highScoreManager.getTopScores().get(0);
									if (score[0] > currentHighest) {
										// draw the message
										showCongrats[0] = true;

										// play high score music
										soundManager.playHighScoreSound();
									} else {
										// play only game over sound
										soundManager.playGameOverSound();
									}

									// stop background music
									soundManager.stopMusic();

									// add score to the list
									highScoreManager.addScores(score[0]);
									highScoreSaved[0] = true;
								}
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
				Color[][] board = gameBoard.getGrid();
				for (int r = 0; r < GameBoard.ROWS; r++) {
					for (int c = 0; c < GameBoard.COLS; c++) {
						Color v = board[r][c];
						if (v != null) {
							gContext.setFill(v);
							gContext.fillRect(c * GameBoard.BLOCK_SIZE, r * GameBoard.BLOCK_SIZE, GameBoard.BLOCK_SIZE,
									GameBoard.BLOCK_SIZE);
						}
					}
				}

				// draw the SCORE, LEVEL & LINES CLEARED text
				gContext.setFill(Color.YELLOW);
				gContext.setFont(new Font("Impact", 18));
				gContext.fillText("Score: " + score[0], GameBoard.COLS * GameBoard.BLOCK_SIZE + 20, 40);
				gContext.fillText("Level: " + level[0], GameBoard.COLS * GameBoard.BLOCK_SIZE + 20, 70);
				gContext.fillText("Lines Cleared: " + linesCleared[0], GameBoard.COLS * GameBoard.BLOCK_SIZE + 20, 100);

				// draw the HIGHSCORE list
				gContext.setFill(Color.ANTIQUEWHITE);
				gContext.setFont(new Font("Impact", 18));
				gContext.fillText("Top 5 Scores: ", GameBoard.COLS * GameBoard.BLOCK_SIZE + 20, 400);
				
				gContext.setFill(Color.GOLDENROD);
				List<Integer> highScores = highScoreManager.getTopScores();
				for (int i = 0; i < 5; i++) {
					int value = (i < highScores.size() ? highScores.get(i) : 0);
					gContext.fillText((i + 1) + ". " + value, GameBoard.COLS * GameBoard.BLOCK_SIZE + 20, 430 + i * 20);
				}
				
				// draw the INSTRUCTION list
				gContext.setFill(Color.ANTIQUEWHITE);
				gContext.setFont(new Font("Impact", 18));
				gContext.fillText("How To Play: ", GameBoard.COLS * GameBoard.BLOCK_SIZE + 20, 550);
				gContext.setFill(Color.GOLDENROD);
				gContext.fillText("SPACE to Pause.", GameBoard.COLS * GameBoard.BLOCK_SIZE + 20, 570);
				gContext.fillText("X for hard drop.", GameBoard.COLS * GameBoard.BLOCK_SIZE + 20, 590);
				gContext.fillText("'UP' change piece pos.", GameBoard.COLS * GameBoard.BLOCK_SIZE + 20, 610);
				gContext.fillText("'M' to Mute sound.", GameBoard.COLS * GameBoard.BLOCK_SIZE + 20, 630);


				// NEXT PIECE PREVIEW
				gContext.setFill(Color.ANTIQUEWHITE);
				gContext.setFont(new Font("Impact", 18));
				gContext.fillText("Next Tetromino: ", GameBoard.COLS * GameBoard.BLOCK_SIZE + 20, 150);

				// Draw the next piece centered in 4x4 grid
				int[][] nextTetrominoShape = nextPiece[0].getShape();
				Color nextColor = nextPiece[0].getColor();

				double previewX = GameBoard.COLS * GameBoard.BLOCK_SIZE + 40;
				double previewY = 180;

				gContext.setFill(nextColor);

				for (int r = 0; r < nextTetrominoShape.length; r++) {
					for (int c = 0; c < nextTetrominoShape[r].length; c++) {
						if (nextTetrominoShape[r][c] != 0) {
							gContext.fillRect(previewX + c * GameBoard.BLOCK_SIZE * 0.8, // scaled down to 80%
									previewY + r * GameBoard.BLOCK_SIZE * 0.8, GameBoard.BLOCK_SIZE * 0.8,
									GameBoard.BLOCK_SIZE * 0.8);
						}
					}
				}

				// HOLD PIECE - to allow switching to a different piece
				gContext.setFill(Color.ANTIQUEWHITE);
				gContext.setFont(new Font("Impact", 20));
				gContext.fillText("C: swap Tetrominos!", GameBoard.COLS * GameBoard.BLOCK_SIZE + 20, 260);

				if (holdPiece[0] != null) {
					int[][] holdShape = holdPiece[0].getShape();
					Color holdColor = holdPiece[0].getColor();

					double holdX = GameBoard.COLS * GameBoard.BLOCK_SIZE + 40;
					double holdY = 280;

					gContext.setFill(holdColor);

					for (int r = 0; r < holdShape.length; r++) {
						for (int c = 0; c < holdShape[r].length; c++) {
							if (holdShape[r][c] != 0) {
								gContext.fillRect(holdX + c * GameBoard.BLOCK_SIZE * 0.8,
										holdY + r * GameBoard.BLOCK_SIZE * 0.8, GameBoard.BLOCK_SIZE * 0.8,
										GameBoard.BLOCK_SIZE * 0.8);
							}
						}
					}
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

				// toggle prompt flag every BLINK_INTERVAL
				if (now - lastBlinkTime > BLINK_INTERVAL) {
					showPrompt = !showPrompt;
					lastBlinkTime = now;
				}

				// PAUSE overlay
				if (paused && !gameOver[0]) {
					gContext.setFill(Color.ORANGE);
					gContext.setFont(Font.font("Arial Black", FontWeight.BOLD, 42));

					String pauseMsgString = "GAME PAUSED";
					Text textPauseText = new Text(pauseMsgString);
					double textWidth = textPauseText.getLayoutBounds().getWidth();
					double canvasWidth = GameBoard.COLS * GameBoard.BLOCK_SIZE;
					double canvasHeight = GameBoard.ROWS * GameBoard.BLOCK_SIZE;
					double xP = (canvasWidth - textWidth) / 2 - 100;
					double yP = (canvasHeight / 2);

					gContext.fillText(pauseMsgString, xP, yP);

					// resume text, blinking
					if (showPrompt) {
						gContext.setFill(Color.DARKMAGENTA);
						gContext.setFont(Font.font("Courier New", FontWeight.BOLD, 30));

						String resumePString = "SPACE to Resume Game";
						Text resumePText = new Text(resumePString);

						resumePText.setFont(gContext.getFont());

						double promptWidth = resumePText.getLayoutBounds().getWidth();
						gContext.fillText(resumePString, (canvasWidth - promptWidth) / 2, yP + 40);
					}
				}

				// draw the GAME OVER text
				if (gameOver[0]) {
					// game over
					gContext.setFill(Color.DARKMAGENTA);
					gContext.setFont(Font.font("Arial Black", FontWeight.BOLD, 42));

					String msgString = "GAME OVER!";
					Text gameOverText = new Text(msgString);
					double textWidth = gameOverText.getLayoutBounds().getWidth();
					double canvasWidth = GameBoard.COLS * GameBoard.BLOCK_SIZE;
					double canvasHeight = GameBoard.ROWS * GameBoard.BLOCK_SIZE;
					double x = (canvasWidth - textWidth) / 2 - 100;
					double y = canvasHeight / 2;

					gContext.fillText(msgString, x, y);

					// blinking prompt
					if (showPrompt) {
						gContext.setFill(Color.YELLOW);
						gContext.setFont(Font.font("Courier New", FontWeight.BOLD, 30));

						String prompt = "Press R to Restart";
						Text promptText = new Text(prompt);

						promptText.setFont(gContext.getFont());

						double promptWidth = promptText.getLayoutBounds().getWidth();
						gContext.fillText(prompt, (canvasWidth - promptWidth) / 2, y + 100);

						// show congrats message
						if (showCongrats[0]) {
							gContext.setFill(Color.SNOW);
							gContext.setFont(Font.font("Impact", FontWeight.BOLD, 28));

							String congratsMsg = "GAME MASTER! - NEW HIGH!";
							Text congratsText = new Text(congratsMsg);
							congratsText.setFont(gContext.getFont());

							double msgWidth = congratsText.getLayoutBounds().getWidth();
							gContext.fillText(congratsMsg, (canvasWidth - msgWidth) / 2, canvasHeight / 2 - 100);

						}
					}

				}
			}
		};

		gameLoop.start();

		// controls
		scene.setOnKeyPressed(event -> {
			switch (event.getCode()) {
			case LEFT, A -> {
				if (!gameOver[0]) {
					int newCol = currentPiece[0].getCol() - 1;
					if (gameBoard.isValidPosition(currentPiece[0], currentPiece[0].getRow(), newCol)) {
						currentPiece[0].moveLeft();
					}
				}
			}

			// move/shift right
			case RIGHT, D -> {
				if (!gameOver[0]) {
					int newCol = currentPiece[0].getCol() + 1;
					if (gameBoard.isValidPosition(currentPiece[0], currentPiece[0].getRow(), newCol)) {
						currentPiece[0].moveRight();
					}
				}
			}

			// move down
			case DOWN, S -> {
				if (!gameOver[0]) {
					int newRow = currentPiece[0].getRow() + 1;
					if (gameBoard.isValidPosition(currentPiece[0], newRow, currentPiece[0].getCol())) {
						currentPiece[0].moveDown();
					}
				}
			}

			// rotate piece when valid move available
			case UP, W -> {
				if (!gameOver[0]) {
					currentPiece[0].rotate();
					if (!gameBoard.isValidPosition(currentPiece[0], currentPiece[0].getRow(),
							currentPiece[0].getCol())) {
						// undo by rotating 3 times
						currentPiece[0].rotate();
						currentPiece[0].rotate();
						currentPiece[0].rotate();
					}
				}
			}

			// restart game
			case R -> {
				if (gameOver[0]) {
					// clear board & gameplay state
					gameBoard.clearGame();
					score[0] = 0;
					linesCleared[0] = 0;
					level[0] = 1;

					// reset game pieces
					currentPiece[0] = new Tetromino(random.nextInt(Tetromino.SHAPES.length));
					nextPiece[0] = new Tetromino(random.nextInt(Tetromino.SHAPES.length));

					// timing + flags
					lastUpdate[0] = 0;
					gameOver[0] = false;
					paused = false;

					// HOLD for switching pieces needs reset
					holdPiece[0] = null;
					holdUsedPiece[0] = false;

					// highscore & related message
					highScoreSaved[0] = false; // allow saving on next run
					showCongrats[0] = false;

					// reset blink timer
					lastBlinkTime = 0;
					showPrompt = true;

					// stop sound
					soundManager.stopHighScoreSound();
					soundManager.stopGameOverSound();

				}
			}

			// pause resume game
			case P, SPACE -> {
				if (!gameOver[0]) {
					paused = !paused; // toggle pause/resume

					// pause the game music
					soundManager.pauseMusic();
				}
			}

			// switch the next Tetromino
			case C -> {
				if (!gameOver[0] && !paused && !holdUsedPiece[0]) {
					if (holdPiece[0] == null) {
						// first hold: move current to hold, bring in next
						holdPiece[0] = currentPiece[0];
						currentPiece[0] = nextPiece[0];
						nextPiece[0] = new Tetromino(random.nextInt(Tetromino.SHAPES.length));
					} else {
						// swap the current piece with that in hold
						Tetromino tempTetromino = currentPiece[0];
						currentPiece[0] = holdPiece[0];
						holdPiece[0] = tempTetromino;
					}
				}

				// reset position so that swapped-in piece drops from the top
				currentPiece[0].resetPosition();

				// prevent multiple holds for this turn
				holdUsedPiece[0] = true;
			}

			// hard drop Tetrominos
			case X -> {
				if (!gameOver[0] && !paused) {
					hardDrop(currentPiece[0]);
				}
			}
			
			// mute/unmute all sounds
			case M -> {
				soundManager.toggleMute();
			}

			} // end for the switch
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
