package engine;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioFormat;
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

            // Set volume based on user settings
            if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                FloatControl gain = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                float volumeDb = calculateVolumeDb(Core.getVolumeLevel());
                gain.setValue(Math.max(gain.getMinimum(), Math.min(gain.getMaximum(), volumeDb)));
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

    /**
     * Plays a WAV in a loop until {@link #stop()} is called.
     */
    public static void playLoop(String resourcePath) {
        stop();
        stopBackgroundMusic();

        AudioInputStream audioStream = null;
        try {
            audioStream = openAudioStream(resourcePath);
            if (audioStream == null) return;
            audioStream = toPcmSigned(audioStream);

            DataLine.Info info = new DataLine.Info(Clip.class, audioStream.getFormat());
            loopClip = (Clip) AudioSystem.getLine(info);
            loopClip.open(audioStream);

            // Set volume based on user settings for loops
            if (loopClip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                FloatControl gain = (FloatControl) loopClip.getControl(FloatControl.Type.MASTER_GAIN);
                float volumeDb = calculateVolumeDb(Core.getVolumeLevel());
                gain.setValue(Math.max(gain.getMinimum(), Math.min(gain.getMaximum(), volumeDb)));
            }

            loopClip.loop(Clip.LOOP_CONTINUOUSLY);
            loopClip.start();
            logger.fine("Started looped sound: " + resourcePath);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            logger.fine("Unable to loop sound '" + resourcePath + "': " + e.getMessage());
            if (loopClip != null) {
                try { loopClip.close(); } catch (Exception ignored) {}
                loopClip = null;
            }
        }
    }

    /**
     * Stops and releases the current looped clip, if any.
     */
    public static void stop() {
        if (loopClip != null) {
            try {
                loopClip.stop();
                loopClip.close();
            } catch (Exception e) {
                logger.fine("Error stopping looped sound: " + e.getMessage());
            } finally {
                loopClip = null;
            }
        }
    }

    /**
     * Stops all music (both looped and background music).
     * Use this when transitioning between screens to ensure no overlap.
     */
    public static void stopAllMusic() {
        stop(); // stops looped music
        stopBackgroundMusic(); // stops background music
    }
    // Background music clip - static to persist across method calls
    private static Clip backgroundMusicClip = null;
    private static boolean isMusicPlaying = false;
    private static float musicVolumeDb = -10.0f; // Default music volume

    /**
     * starts playing background music that loops during gameplay
     */
    public static void startBackgroundMusic(String musicResourcePath) {
        // stop any currently playing music (both loop and background music)
        stop();
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

            // set music volume based on user settings
            if (backgroundMusicClip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                FloatControl gain = (FloatControl) backgroundMusicClip.getControl(FloatControl.Type.MASTER_GAIN);
                float volumeDb = calculateVolumeDb(Core.getVolumeLevel());
                gain.setValue(Math.max(gain.getMinimum(), Math.min(gain.getMaximum(), volumeDb)));
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

    /** Opens an audio stream from classpath resources or absolute/relative file path. */
    private static AudioInputStream openAudioStream(String resourcePath)
            throws UnsupportedAudioFileException, IOException {
        InputStream in = SoundManager.class.getClassLoader().getResourceAsStream(resourcePath);
        if (in != null) {
            return AudioSystem.getAudioInputStream(in);
        }
        // Fallback to file system path for developer/local runs
        try (FileInputStream fis = new FileInputStream(resourcePath)) {
            return AudioSystem.getAudioInputStream(fis);
        } catch (FileNotFoundException e) {
            logger.fine("Audio resource not found: " + resourcePath);
            return null;
        }
    }

    /** Ensures the audio stream is PCM_SIGNED for Clip compatibility on all JVMs. */
    private static AudioInputStream toPcmSigned(AudioInputStream source) throws UnsupportedAudioFileException, IOException {
        AudioFormat format = source.getFormat();
        if (format.getEncoding() == AudioFormat.Encoding.PCM_SIGNED) {
            return source;
        }

        AudioFormat targetFormat = new AudioFormat(
                AudioFormat.Encoding.PCM_SIGNED,
                format.getSampleRate(),
                16,
                format.getChannels(),
                format.getChannels() * 2,
                format.getSampleRate(),
                false
        );
        return AudioSystem.getAudioInputStream(targetFormat, source);
    }

    /**
     * Updates the volume of currently playing sounds.
     * This should be called when the volume slider is changed.
     */
    public static void updateVolume() {
        float volumeDb = calculateVolumeDb(Core.getVolumeLevel());
        
        // Update looped sound volume (menu music)
        if (loopClip != null && loopClip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            FloatControl gain = (FloatControl) loopClip.getControl(FloatControl.Type.MASTER_GAIN);
            gain.setValue(Math.max(gain.getMinimum(), Math.min(gain.getMaximum(), volumeDb)));
        }
        
        // Update background music volume (game music)
        if (backgroundMusicClip != null && backgroundMusicClip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            FloatControl gain = (FloatControl) backgroundMusicClip.getControl(FloatControl.Type.MASTER_GAIN);
            gain.setValue(Math.max(gain.getMinimum(), Math.min(gain.getMaximum(), volumeDb)));
        }
    }

    /**
     * Calculates the volume in decibels based on the volume level (0-100).
     * Volume level 100 = 0dB (full volume), Volume level 0 = -80dB (silent)
     * 
     * @param volumeLevel Volume level from 0 to 100
     * @return Volume in decibels
     */
    private static float calculateVolumeDb(int volumeLevel) {
        if (volumeLevel <= 0) {
            return -80.0f; // Silent
        }
        if (volumeLevel >= 100) {
            return 0.0f; // Full volume
        }
        
        // Convert percentage to decibels
        // Using logarithmic scale: dB = 20 * log10(volumeLevel/100)
        // But we'll use a simpler linear mapping for better user experience
        float ratio = volumeLevel / 100.0f;
        return (float) (20.0 * Math.log10(ratio));
    }
}


