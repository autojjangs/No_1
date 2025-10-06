package engine;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import entity.Ship;
import screen.*;

/**
 * Implements core game logic.
 *
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 *
 */
public final class Core {

    private static final int WIDTH = 448;
    private static final int HEIGHT = 520;
    private static final int FPS = 60;

    /** Lives per player (used to compute team pool in shared mode). */
    private static final int MAX_LIVES = 3;
    private static final int EXTRA_LIFE_FRECUENCY = 3;
    private static final int NUM_LEVELS = 7;

    private static final GameSettings SETTINGS_LEVEL_1 = new GameSettings(5, 4, 60, 2000);
    private static final GameSettings SETTINGS_LEVEL_2 = new GameSettings(5, 5, 50, 2500);
    private static final GameSettings SETTINGS_LEVEL_3 = new GameSettings(6, 5, 40, 1500);
    private static final GameSettings SETTINGS_LEVEL_4 = new GameSettings(6, 6, 30, 1500);
    private static final GameSettings SETTINGS_LEVEL_5 = new GameSettings(7, 6, 20, 1000);
    private static final GameSettings SETTINGS_LEVEL_6 = new GameSettings(7, 7, 10, 1000);
    private static final GameSettings SETTINGS_LEVEL_7 = new GameSettings(8, 7, 2, 500);

    /** Frame to draw the screen on. */
    private static Frame frame;
    private static Screen currentScreen;
    private static List<GameSettings> gameSettings;
    private static final Logger LOGGER = Logger.getLogger(Core.class.getSimpleName());
    private static Handler fileHandler;
    private static ConsoleHandler consoleHandler;

    /**
     * Test implementation.
     *
     * @param args
     *             Program args, ignored.
     */
    public static void main(final String[] args) {
        try {
            LOGGER.setUseParentHandlers(false);
            fileHandler = new FileHandler("log");
            fileHandler.setFormatter(new MinimalFormatter());
            consoleHandler = new ConsoleHandler();
            consoleHandler.setFormatter(new MinimalFormatter());
            LOGGER.addHandler(fileHandler);
            LOGGER.addHandler(consoleHandler);
            LOGGER.setLevel(Level.ALL);
        } catch (Exception e) {
            e.printStackTrace();
        }

        frame = new Frame(WIDTH, HEIGHT);
        DrawManager.getInstance().setFrame(frame);
        int width = frame.getWidth();
        int height = frame.getHeight();

        gameSettings = new ArrayList<GameSettings>();
        gameSettings.add(SETTINGS_LEVEL_1);
        gameSettings.add(SETTINGS_LEVEL_2);
        gameSettings.add(SETTINGS_LEVEL_3);
        gameSettings.add(SETTINGS_LEVEL_4);
        gameSettings.add(SETTINGS_LEVEL_5);
        gameSettings.add(SETTINGS_LEVEL_6);
        gameSettings.add(SETTINGS_LEVEL_7);


        // 2P mode: modified to null to allow for switch between 2 modes
        GameState gameState = null;
        boolean coopSelected = false; // false = 1P, true = 2P

        int returnCode = 1;
        Ship.ShipType shipTypeP1 = Ship.ShipType.NORMAL; // P1 Ship Type
        Ship.ShipType shipTypeP2 = Ship.ShipType.NORMAL; // P2 Ship Type
        do {

            switch (returnCode) {
                case 1:
                    currentScreen = new TitleScreen(width, height, FPS);
                    LOGGER.info("Starting " + WIDTH + "x" + HEIGHT + " title screen at " + FPS + " fps.");
                    returnCode = frame.setScreen(currentScreen);
                    LOGGER.info("Closing title screen.");

                    // 2P mode: reading the mode which user chose from TitleScreen
                    if (returnCode == 2) {
                        coopSelected = ((TitleScreen) currentScreen).isCoopSelected();
                        returnCode = 4; // Go to player selection.
                    } else if (returnCode == 3) {
                        coopSelected = ((TitleScreen) currentScreen).isCoopSelected();
                    }
                    break;

                case 2:
                    // 2P mode: building gameState now using user choice
                    gameState = new GameState(1, MAX_LIVES, coopSelected);

                    do {
                        // Extra life this level? Give it if team pool is below cap.
                        int teamCap = gameState.isCoop() ? (MAX_LIVES * GameState.NUM_PLAYERS) : MAX_LIVES;
                        boolean bonusLife = gameState.getLevel() % EXTRA_LIFE_FRECUENCY == 0
                                && gameState.getLivesRemaining() < teamCap;

                        currentScreen = new GameScreen(
                                gameState,
                                gameSettings.get(gameState.getLevel() - 1),
                                bonusLife, width, height, FPS, shipTypeP1, shipTypeP2);

                        LOGGER.info("Starting " + WIDTH + "x" + HEIGHT + " game screen at " + FPS + " fps.");
                        frame.setScreen(currentScreen);
                        LOGGER.info("Closing game screen.");

                        gameState = ((GameScreen) currentScreen).getGameState();

                        if (gameState.teamAlive()) {
                            gameState.nextLevel();
                        }

                    } while (gameState.teamAlive() && gameState.getLevel() <= NUM_LEVELS);

                    LOGGER.info("Starting " + WIDTH + "x" + HEIGHT + " score screen at " + FPS + " fps, with a score of "
                            + gameState.getScore() + ", "
                            + gameState.getLivesRemaining() + " lives remaining, "
                            + gameState.getBulletsShot() + " bullets shot and "
                            + gameState.getShipsDestroyed() + " ships destroyed.");
                    currentScreen = new ScoreScreen(width, height, FPS, gameState);
                    returnCode = frame.setScreen(currentScreen);
                    LOGGER.info("Closing score screen.");
                    break;

                case 3:
                    // High scores.
                    currentScreen = new HighScoreScreen(width, height, FPS);
                    LOGGER.info("Starting " + WIDTH + "x" + HEIGHT
                            + " high score screen at " + FPS + " fps.");
                    returnCode = frame.setScreen(currentScreen);
                    LOGGER.info("Closing high score screen.");
                    break;

                case 4:
                    // Ship selection for Player 1.
                    currentScreen = new ShipSelectionScreen(width, height, FPS, 1);
                    frame.setScreen(currentScreen);
                    shipTypeP1 = ((ShipSelectionScreen) currentScreen).getSelectedShipType();

                    if (coopSelected) {
                        returnCode = 5; // Go to Player 2 selection.
                    } else {
                        returnCode = 2; // Start game.
                    }
                    break;

                case 5:
                    // Ship selection for Player 2.
                    currentScreen = new ShipSelectionScreen(width, height, FPS, 2);
                    frame.setScreen(currentScreen);
                    shipTypeP2 = ((ShipSelectionScreen) currentScreen).getSelectedShipType();
                    returnCode = 2; // Start game.
                    break;

                default:
                    break;
            }

        } while (returnCode != 0);

        fileHandler.flush();
        fileHandler.close();
        System.exit(0);
    }

    /**
     * Constructor, not called.
     */
    private Core() {

    }

    /**
     * Controls access to the logger.
     * sh
     *
     * @return Application logger.
     */
    public static Logger getLogger() {
        return LOGGER;
    }

    /**
     * Controls access to the drawing manager.
     *
     * @return Application draw manager.
     */
    public static DrawManager getDrawManager() {
        return DrawManager.getInstance();
    }

    /**
     * Controls access to the input manager.
     *
     * @return Application input manager.
     */
    public static InputManager getInputManager() {
        return InputManager.getInstance();
    }

    /**
     * Controls access to the file manager.
     *
     * @return Application file manager.
     */
    public static FileManager getFileManager() {
        return FileManager.getInstance();
    }

    /**
     * Controls creation of new cooldowns.
     *
     * @param milliseconds
     *                     Duration of the cooldown.
     * @return A new cooldown.
     */
    public static Cooldown getCooldown(final int milliseconds) {
        return new Cooldown(milliseconds);
    }

    /**
     * Controls creation of new cooldowns with variance.
     *
     * @param milliseconds
     *                     Duration of the cooldown.
     * @param variance
     *                     Variation in the cooldown duration.
     * @return A new cooldown with variance.
     */
    public static Cooldown getVariableCooldown(final int milliseconds,
                                               final int variance) {
        return new Cooldown(milliseconds, variance);
    }
}
