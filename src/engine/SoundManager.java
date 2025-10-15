package engine;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.logging.Logger;

/**
 * Minimal sound manager for short SFX.
 */
public final class  SoundManager {

    private static final Logger logger = Core.getLogger();
    private static Clip loopClip;

    private SoundManager() {
    }

    /**
     * Plays a short WAV from resources folder. Example path: "sound/shoot.wav".
     * Uses a new Clip per invocation for simplicity; suitable for very short SFX.
     */
    public static void playOnce(String resourcePath) {
        AudioInputStream audioStream = null;
        Clip clip = null;
        try {
            audioStream = openAudioStream(resourcePath);
            if (audioStream == null) return;
            audioStream = toPcmSigned(audioStream);
            DataLine.Info info = new DataLine.Info(Clip.class, audioStream.getFormat());
            clip = (Clip) AudioSystem.getLine(info);
            clip.open(audioStream);

            // Reduce volume slightly to avoid clipping
            if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                FloatControl gain = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                float attenuationDb = -6.0f; // ~ half perceived loudness
                gain.setValue(Math.max(gain.getMinimum(), Math.min(gain.getMaximum(), attenuationDb)));
            }

            clip.start();
            logger.info("Started one-shot sound: " + resourcePath);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            logger.info("Unable to play sound '" + resourcePath + "': " + e.getMessage());
        } finally {
            // We can't close 'in' immediately because AudioSystem may stream; rely on clip close
            if (clip != null) {
                final Clip c = clip;
                c.addLineListener(event -> {
                    LineEvent.Type type = event.getType();
                    if (type == LineEvent.Type.STOP || type == LineEvent.Type.CLOSE) {
                        try {
                            c.close();
                        } catch (Exception ignored) {}
                    }
                });
            }
        }
    }
    // Background music clip - static to persist across method calls
    private static Clip backgroundMusicClip = null;
    private static boolean isMusicPlaying = false;
    private static float musicVolumeDb = -10.0f; // Default music volume

    /**
     * starts playing background music that loops during gameplay
     */
    public static void startBackgroundMusic(String musicResourcePath) {
        // stop any currently playing music
        stopBackgroundMusic();

        InputStream in = null;
        AudioInputStream audioStream = null;

        try {
            in = SoundManager.class.getClassLoader().getResourceAsStream(musicResourcePath);
            if (in == null) {
                logger.fine("Music resource not found: " + musicResourcePath);
                return;
            }

            audioStream = AudioSystem.getAudioInputStream(in);
            DataLine.Info info = new DataLine.Info(Clip.class, audioStream.getFormat());
            backgroundMusicClip = (Clip) AudioSystem.getLine(info);
            backgroundMusicClip.open(audioStream);

            // set looping
            backgroundMusicClip.loop(Clip.LOOP_CONTINUOUSLY);

            // set music volume
            if (backgroundMusicClip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                FloatControl gain = (FloatControl) backgroundMusicClip.getControl(FloatControl.Type.MASTER_GAIN);
                gain.setValue(Math.max(gain.getMinimum(), Math.min(gain.getMaximum(), musicVolumeDb)));
            }

            backgroundMusicClip.start();
            isMusicPlaying = true;
            logger.fine("Background music started: " + musicResourcePath);

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            logger.fine("Unable to play background music '" + musicResourcePath + "': " + e.getMessage());
            cleanupMusicResources();
        }
    }

    /**
     * stops the background music and releases resources
     */
    public static void stopBackgroundMusic() {
        if (backgroundMusicClip != null) {
            try {
                backgroundMusicClip.stop();
                backgroundMusicClip.close();
            } catch (Exception e) {
                logger.fine("Error stopping background music: " + e.getMessage());
            } finally {
                cleanupMusicResources();
            }
        }
    }

    private static void cleanupMusicResources() {
        backgroundMusicClip = null;
        isMusicPlaying = false;
    }
}


