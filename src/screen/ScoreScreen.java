package screen;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import engine.*;

/**
 * Implements the score screen.
 *
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 *
 */
public class ScoreScreen extends Screen {

	/** Milliseconds between changes in user selection. */
	private static final int SELECTION_TIME = 200;
	/** Maximum number of high scores. */
	private static final int MAX_HIGH_SCORE_NUM = 7;
	/** Code of first mayus character. */
	private static final int FIRST_CHAR = 65;
	/** Code of last mayus character. */
	private static final int LAST_CHAR = 90;
	/** Maximum name length. */
	private static final int MAX_NAME_LENGTH = 5;

	// Added for persist per-player breakdown
	private final GameState gameState;

	/** Current score. */
	private int score;
	/** Current coins. */
	private int coins;
	/** Total bullets shot by the player. */
	private int bulletsShot;
	/** Total ships destroyed by the player. */
	private int shipsDestroyed;
	/** List of past high scores. */
	private List<Score> highScores;
	/** Checks if current score is a new high score. */
	private boolean isNewRecord;
	/** Player name for record input. */
	private StringBuilder name;
	/** Character of players name selected for change. */
	private int nameCharSelected;
	/** Make sure the name is less than 3 characters. */
	private boolean showNameError = false;
	/** Time between changes in user selection. */
	private Cooldown selectionCooldown;
	/** manages achievements.*/
	private AchievementManager achievementManager;
	/** Total coins earned in the game. */ // ADD THIS LINE
	private int totalCoins; // ADD THIS LINE

	/**
	 * Constructor, establishes the properties of the screen.
	 *
	 * @param width
	 *                  Screen width.
	 * @param height
	 *                  Screen height.
	 * @param fps
	 *                  Frames per second, frame rate at which the game is run.
	 * @param gameState
	 *                  Current game state.
	 * @param achievementManager
	 * 			            Achievement manager instance used to track and save player achievements.
	 * 			  2025-10-03  add generator parameter and comment
	 */
	public ScoreScreen(final int width, final int height, final int fps,
			final GameState gameState, final AchievementManager achievementManager) {
		super(width, height, fps);
		this.gameState = gameState; // Added

		this.score = gameState.getScore();
		this.coins = gameState.getCoins();
		this.bulletsShot = gameState.getBulletsShot();
		this.shipsDestroyed = gameState.getShipsDestroyed();
		this.totalCoins = gameState.getCoins(); // ADD THIS LINE
		this.isNewRecord = false;
		this.name = new StringBuilder();
		this.nameCharSelected = 0;
		this.selectionCooldown = Core.getCooldown(SELECTION_TIME);
		this.selectionCooldown.reset();
		this.achievementManager = achievementManager;

		try {
			this.highScores = Core.getFileManager().loadHighScores();
			if (highScores.size() < MAX_HIGH_SCORE_NUM
					|| highScores.get(highScores.size() - 1).getScore() < this.score)
				this.isNewRecord = true;

		} catch (IOException e) {
			logger.warning("Couldn't load high scores!");
		}

		// clear last key
		inputManager.clearLastKey();
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
		if (this.inputDelay.checkFinished()) {
			if (inputManager.isKeyDown(KeyEvent.VK_ESCAPE)) {
				// Return to main menu.
				this.returnCode = 1;
				this.isRunning = false;
				if (this.isNewRecord) {
					saveScore();
					saveAchievement(); //2025-10-03 call method for save achievement released
				}
			} else if (inputManager.isKeyDown(KeyEvent.VK_SPACE)) {
				// name too short -> return
				if (this.isNewRecord && this.name.length() < 3) return;

				// Play again.
				this.returnCode = 2;
				this.isRunning = false;
				if (this.isNewRecord) {
					saveScore();
					saveAchievement(); // 2025-10-03 call method for save achievement released
				}
			}

			// Handle name input if new record
			if (this.isNewRecord) {
				// Handle backspace
				if (inputManager.isKeyDown(KeyEvent.VK_BACK_SPACE)
						&& this.selectionCooldown.checkFinished()) {
					if (this.name.length() > 0) {
						this.name.deleteCharAt(this.name.length() - 1);
						this.selectionCooldown.reset();
					}
				}

				// Handle character input
				char typedChar = inputManager.getLastCharTyped();
				if (typedChar != '\0') {
					// Checks the name is not short when you press the space bar
					if (typedChar == ' ') {
						if (this.name.length() < 3) {
//                     System.out.println("too short!!");
							this.showNameError = true;
						}
					}

					// Check if it's a valid character (alphanumeric only)

					else if ((Character.isLetterOrDigit(typedChar))
							&& this.name.length() < MAX_NAME_LENGTH) {
						this.name.append(Character.toUpperCase(typedChar));
					}
				}
			}
		}
	}

	/**
	 * Saves the score as a high score.
	 */
	private void saveScore() {
		highScores.add(new Score(new String(this.name), this.gameState));
		Collections.sort(highScores);
		if (highScores.size() > MAX_HIGH_SCORE_NUM)
			highScores.remove(highScores.size() - 1);

		try {
			Core.getFileManager().saveHighScores(highScores);
		} catch (IOException e) {
			logger.warning("Couldn't load high scores!");
		}
	}

	/**
	 * Save the achievement released.
	 * 2025-10-03
	 * add new method
	 */
	private void saveAchievement() {
		try {
			this.achievementManager.saveToFile(new String(this.name));
		} catch (IOException e) {
			logger.warning("Couldn't save achievements!");
		}
	}

	/**
	 * Draws the elements associated with the screen.
	 */
	private void draw() {
		drawManager.initDrawing(this);

		drawManager.drawGameOver(this, this.inputDelay.checkFinished(), this.isNewRecord);

		float accuracy = (this.bulletsShot > 0)
				? (float) this.shipsDestroyed / this.bulletsShot
				: 0f;

        // 2P mode: edit to include co-op + individual score/coins
        if (this.gameState != null && this.gameState.isCoop()) {
            // team summary
            drawManager.drawResults(this,
                    this.gameState.getScore(), // team score
                    this.gameState.getCoins(),
                    this.gameState.getShipsDestroyed(),
                    0f, // leaving out team accuracy
                    this.isNewRecord,
					false // Draw accuracy for 2P mode
            );

            // show per-player lines when in 2P mode

            float p1Acc = this.gameState.getBulletsShot(0) > 0 ? (float) this.gameState.getShipsDestroyed(0) / this.gameState.getBulletsShot(0) : 0f;
            float p2Acc = this.gameState.getBulletsShot(1) > 0 ? (float) this.gameState.getShipsDestroyed(1) / this.gameState.getBulletsShot(1) : 0f;

            String p1 = String.format("P1  %04d  |  acc %.2f%%", this.gameState.getScore(0), p1Acc * 100f);
            String p2 = String.format("P2  %04d  |  acc %.2f%%", this.gameState.getScore(1), p2Acc * 100f);

            int y;  // tweak these if you want
			if (this.isNewRecord) {
				y = this.getHeight() / 2 - 5; // Position if new record is True
			} else {
				y = this.getHeight() / 2 + 60; // Position if new record is False
			}
            drawManager.drawCenteredRegularString(this, p1, y);
            drawManager.drawCenteredRegularString(this, p2, y + 40); // Increase spacing

        } else {
            // 1P legacy summary with accuracy
            float acc = (this.bulletsShot > 0) ? (float) this.shipsDestroyed / this.bulletsShot : 0f;

            drawManager.drawResults(this, this.score, this.coins, this.shipsDestroyed, acc, this.isNewRecord, true); // Draw accuracy for 1P mode
        }

        if (this.isNewRecord) {
			drawManager.drawNameInput(this, this.name);
			if (showNameError)
				drawManager.drawNameInputError(this);
		}


		drawManager.completeDrawing(this);
	}

}
