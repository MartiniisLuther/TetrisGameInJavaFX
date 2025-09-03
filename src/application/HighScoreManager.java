package application;

import java.io.*;
import java.util.*;

public class HighScoreManager {
	// some attributes to use
	private static final String FILE_NAME = "highscores.txt";
	private final List<Integer> gameScores = new ArrayList<>();

	// constructor
	public HighScoreManager() {
		loadScores();
	}

	// loadScores method
	private void loadScores() {
		gameScores.clear();

		try (BufferedReader bReader = new BufferedReader(new FileReader(FILE_NAME))) {
			String lineString;
			while ((lineString = bReader.readLine()) != null) {
				lineString = lineString.trim();
				if (!lineString.isEmpty()) {
					gameScores.add(Integer.parseInt(lineString));
				}
			}
		} catch (IOException e) {
			// no scores yet
		}
		gameScores.sort(Collections.reverseOrder());
	}

	// saveScores method
	private void saveScores() {
		try (BufferedWriter bWriter = new BufferedWriter(new FileWriter(FILE_NAME))) {
			for (int score : gameScores) {
				bWriter.write(score + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// add new scores to the list
	public void addScores(int newScore) {
		gameScores.add(newScore);
		gameScores.sort(Comparator.reverseOrder());
		saveScores();
	}

	// get the Scores
	public List<Integer> getTopScores() {
		List<Integer> topScores = new ArrayList<>(gameScores);
		while (topScores.size() < 5) {
			topScores.add(0); // to give a 0th padding, while not saved in file.txt
		}

		return topScores.subList(0, Math.min(5, topScores.size()));
	}
	
	//get highscore
	public int getHighScore() {
		return gameScores.isEmpty() ? 0 : gameScores.get(0);
	}

}
