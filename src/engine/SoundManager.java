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
import java.util.logging.Logger;

/**
 * Minimal sound manager for short SFX.
 */
public final class SoundManager {

    private static final Logger logger = Core.getLogger();

    private SoundManager() {
    }

    /**
     * Plays a short WAV from resources folder. Example path: "sound/shoot.wav".
     * Uses a new Clip per invocation for simplicity; suitable for very short SFX.
     */
    public static void playOnce(String resourcePath) {
        InputStream in = null;
        AudioInputStream audioStream = null;
        Clip clip = null;
        try {
            in = SoundManager.class.getClassLoader().getResourceAsStream(resourcePath);
            if (in == null) {
                logger.fine("Sound resource not found: " + resourcePath);
                return;
            }

            audioStream = AudioSystem.getAudioInputStream(in);
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
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            logger.fine("Unable to play sound '" + resourcePath + "': " + e.getMessage());
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
}


