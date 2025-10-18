package screen;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import engine.Core;
import engine.Score;
import engine.SoundManager;

/**
 * Implements the high scores screen, it shows player records.
 *
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 *
 */
public class HighScoreScreen extends Screen {

    /** List of past high scores. */
    private List<Score> highScores1P, highScores2P;

    /**
     * Constructor, establishes the properties of the screen.
     *
     * @param width
     *            Screen width.
     * @param height
     *            Screen height.
     * @param fps
     *            Frames per second, frame rate at which the game is run.
     */
    public HighScoreScreen(final int width, final int height, final int fps) {
        super(width, height, fps);
        SoundManager.playLoop("sound/menu_sound.wav");

        this.returnCode = 1;

        try {
            this.highScores1P = Core.getFileManager().loadHighScores("1P");
            this.highScores2P = Core.getFileManager().loadHighScores("2P");
            //상위 7명만 남기기
            highScores1P.sort((a, b) -> b.getScore() - a.getScore());
            if (highScores1P.size() > 7) highScores1P = highScores1P.subList(0, 7);

            highScores2P.sort((a, b) -> b.getScore() - a.getScore());
            if (highScores2P.size() > 7) highScores2P = highScores2P.subList(0, 7);

        } catch (NumberFormatException | IOException e) {
            logger.warning("Couldn't load high scores!");
        }
    }

    /**
     * Starts the action.
     *
     * @return Next screen code.
     */
    public final int run() {
        super.run();
        SoundManager.playOnce("sound/select.wav");

        return this.returnCode;
    }

    /**
     * Updates the elements on screen and checks for events.
     */
    protected final void update() {
        super.update();

        draw();
        if (inputManager.isKeyDown(KeyEvent.VK_ESCAPE)
                && this.inputDelay.checkFinished())
            this.isRunning = false;

        // back button click event
        if (inputManager.isMouseClicked()) {
            int mx = inputManager.getMouseX();
            int my = inputManager.getMouseY();
            java.awt.Rectangle backBox = drawManager.getBackButtonHitbox(this);

            if (backBox.contains(mx, my)) {
                this.returnCode = 1;
                this.isRunning = false;
            }
        }
    }
    private List<Score> getPlayerScores(String mode) {
        return mode.equals("1P") ? highScores1P : highScores2P;
    }
    /**
     * Draws the elements associated with the screen.
     */
    private void draw() {
        drawManager.initDrawing(this);

        drawManager.drawHighScoreMenu(this);
        drawManager.drawHighScores(this, getPlayerScores("1P"), "1P"); // Left column
        drawManager.drawHighScores(this, getPlayerScores("2P"), "2P"); // Right column

        // hover highlight
        int mx = inputManager.getMouseX();
        int my = inputManager.getMouseY();
        java.awt.Rectangle backBox = drawManager.getBackButtonHitbox(this);

        if (backBox.contains(mx, my)) {
            drawManager.drawBackButton(this, true);
        }

        drawManager.completeDrawing(this);
    }
}