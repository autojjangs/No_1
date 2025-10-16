package screen;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import engine.Core;
import engine.Score;

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

        this.returnCode = 1;

        try {
            this.highScores1P = Core.getFileManager().loadHighScores("1P");
            this.highScores2P = Core.getFileManager().loadHighScores("2P");
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

        return this.returnCode;
    }

    /**
     * Updates the elements on screen and checks for events.
     */
    protected final void update() {
        super.update();

        draw();
        if (inputManager.isKeyDown(KeyEvent.VK_SPACE)
                && this.inputDelay.checkFinished())
            this.isRunning = false;
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

        drawManager.completeDrawing(this);
    }
}