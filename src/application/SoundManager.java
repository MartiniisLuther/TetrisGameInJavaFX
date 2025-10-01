package application;

import java.nio.file.Paths;

import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class SoundManager {
	private MediaPlayer bgMusicPlayer;
	private AudioClip lineClearSound;
	private AudioClip gameOverSound;
	private AudioClip highScoreSound;
	private AudioClip levelUpSound;
	private AudioClip hardDropSound; 
    private boolean muted = false;

	// constructor to manage the sounds
	public SoundManager() {
		try {
			// background music
			String bgMusicPath = "sounds/Title.mp3";
			Media bgMusic = new Media((Paths.get(bgMusicPath)).toUri().toString());
			bgMusicPlayer = new MediaPlayer(bgMusic);
			bgMusicPlayer.setCycleCount(MediaPlayer.INDEFINITE); // loop
			bgMusicPlayer.setVolume(0.6);

			// clear line sound
			String lineClearPath = "sounds/Stage_Clear.mp3";
			lineClearSound = new AudioClip(Paths.get(lineClearPath).toUri().toString());
			lineClearSound.setVolume(0.6);

			// game over
			String gameOverPath = "sounds/Game_Over.mp3";
			gameOverSound = new AudioClip(Paths.get(gameOverPath).toUri().toString());
			gameOverSound.setVolume(0.6);
			gameOverSound.setCycleCount(AudioClip.INDEFINITE);

			// high score sound
			String highScoreSoundPath = "sounds/new_high_score.mp3";
			highScoreSound = new AudioClip(Paths.get(highScoreSoundPath).toUri().toString());
			highScoreSound.setVolume(0.6);
			highScoreSound.setCycleCount(AudioClip.INDEFINITE);

			// level up sound
			String levelUpPath = "sounds/game_level-up.mp3";
			levelUpSound = new AudioClip(Paths.get(levelUpPath).toUri().toString());
			levelUpSound.setVolume(0.8);
			
			//hard drop sound
			String hardDropPath = "sounds/Hard_Drop.mp3";
			hardDropSound = new AudioClip(Paths.get(hardDropPath).toUri().toString());
			hardDropSound.setVolume(0.8);

		} catch (Exception e) {
			System.out.println("Error loading sounds: \n " + e.getMessage());
		}
	}

	// play sound
	public void playMusic() {
        if (bgMusicPlayer != null && !muted) {
			bgMusicPlayer.play();
		}
	}

	// pause
	public void pauseMusic() {
		if (bgMusicPlayer != null) {
			bgMusicPlayer.pause();
		}
	}

	// stop
	public void stopMusic() {
		if (bgMusicPlayer != null) {
			bgMusicPlayer.stop();
		}
	}

	// play clear line music
	public void playLineClearSound() {
        if (lineClearSound != null && !muted) {
			lineClearSound.play();
		}
	}

	// level up sound
	public void playLevelUpSound() {
        if (levelUpSound != null && !muted) {
			levelUpSound.play();
		}
	}

	// playgame over sound
	public void playGameOverSound() {
        if (gameOverSound != null && !muted) {
			gameOverSound.play();
		}
	}

	// stop game over sound
	public void stopGameOverSound() {
		if (gameOverSound != null) {
			gameOverSound.stop();
		}
	}

	// highscore sound
	public void playHighScoreSound() {
        if (highScoreSound != null && !muted) {
			highScoreSound.play();
		}
	}

	// stop highscore sound
	public void stopHighScoreSound() {
		if (highScoreSound != null) {
			highScoreSound.stop();
		}
	}
	
	//play hard drop sound
	public void playHardDropSound() {
        if (hardDropSound != null && !muted) {
			hardDropSound.play();
		}
	}

	
    // toggle mute state for all sounds
    public void toggleMute() {
        muted = !muted;
        if (bgMusicPlayer != null) {
            bgMusicPlayer.setMute(muted);
        }
        if (muted) {
            // ensure currently looping clips are stopped
            stopHighScoreSound();
            stopGameOverSound();
        }
    }

    public boolean isMuted() {
        return muted;
    }

}
