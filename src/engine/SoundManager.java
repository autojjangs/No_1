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

    /**
     * Plays a WAV resource in a continuous loop until stopped.
     * Suitable for background music like the main menu.
     */
    public static void playLoop(String resourcePath) {
        stop();
        AudioInputStream audioStream = null;
        try {
            audioStream = openAudioStream(resourcePath);
            if (audioStream == null) return;
            audioStream = toPcmSigned(audioStream);
            DataLine.Info info = new DataLine.Info(Clip.class, audioStream.getFormat());
            loopClip = (Clip) AudioSystem.getLine(info);
            loopClip.open(audioStream);

            // Lower volume a bit to avoid overpowering SFX
            if (loopClip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                FloatControl gain = (FloatControl) loopClip.getControl(FloatControl.Type.MASTER_GAIN);
                float attenuationDb = -8.0f;
                gain.setValue(Math.max(gain.getMinimum(), Math.min(gain.getMaximum(), attenuationDb)));
            }

            loopClip.loop(Clip.LOOP_CONTINUOUSLY);
            loopClip.start();
            logger.info("Started looped sound: " + resourcePath);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            logger.info("Unable to play looped sound '" + resourcePath + "': " + e.getMessage());
            if (loopClip != null) {
                try { loopClip.close(); } catch (Exception ignored) {}
            }
            loopClip = null;
        } finally {
            if (audioStream != null) {
                try { audioStream.close(); } catch (IOException ignored) {}
            }
        }
    }

    /**
     * Stops any currently looping sound started by playLoop.
     */
    public static void stop() {
        if (loopClip != null) {
            try {
                loopClip.stop();
            } catch (Exception ignored) {}
            try {
                loopClip.close();
            } catch (Exception ignored) {}
            loopClip = null;
        }
    }

    /**
     * Tries to open an AudioInputStream from classpath, then from filesystem.
     * Accepted filesystem paths: given path as-is, or prefixed with "res/".
     */
    private static AudioInputStream openAudioStream(String resourcePath)
            throws IOException, UnsupportedAudioFileException {
        // Try classpath first
        InputStream in = SoundManager.class.getClassLoader().getResourceAsStream(resourcePath);
        if (in != null) {
            return AudioSystem.getAudioInputStream(in);
        }
        // Try filesystem as-is
        try {
            return AudioSystem.getAudioInputStream(new FileInputStream(resourcePath));
        } catch (FileNotFoundException ignored) {
            // Try under res/
            try {
                return AudioSystem.getAudioInputStream(new FileInputStream("res/" + resourcePath));
            } catch (FileNotFoundException ignoredToo) {
                logger.info("Sound not found in classpath or filesystem: " + resourcePath);
                return null;
            }
        }
    }

    /**
     * Ensures the audio stream is PCM_SIGNED so it can be opened by Clip across platforms.
     */
    private static AudioInputStream toPcmSigned(AudioInputStream source) {
        final javax.sound.sampled.AudioFormat base = source.getFormat();
        if (base.getEncoding() == javax.sound.sampled.AudioFormat.Encoding.PCM_SIGNED) {
            return source;
        }
        final javax.sound.sampled.AudioFormat target = new javax.sound.sampled.AudioFormat(
                javax.sound.sampled.AudioFormat.Encoding.PCM_SIGNED,
                base.getSampleRate(),
                16,
                base.getChannels(),
                base.getChannels() * 2,
                base.getSampleRate(),
                false);
        return AudioSystem.getAudioInputStream(target, source);
    }
}


