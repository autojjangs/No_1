package engine;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import Animations.BasicGameSpace;
import Animations.MenuSpace;
import screen.Screen;
import entity.Entity;
import entity.Ship;
import entity.Bullet;

/**
 * Manages screen drawing.
 *
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 *
 */
public final class DrawManager {

	/** Singleton instance of the class. */
	private static DrawManager instance;
	/** Current frame. */
	private static Frame frame;
	/** FileManager instance. */
	private static FileManager fileManager;
	/** Application logger. */
	private static Logger logger;
	/** Graphics context. */
	private static Graphics graphics;
	/** Buffer Graphics. */
	private static Graphics backBufferGraphics;
	/** Buffer image. */
	private static BufferedImage backBuffer;
	/** Normal sized font. */
	private static Font fontRegular;
	/** Normal sized font properties. */
	private static FontMetrics fontRegularMetrics;
	/** Big sized font. */
	private static Font fontBig;
	/** Big sized font properties. */
	private static FontMetrics fontBigMetrics;

	/** Sprite types mapped to their images. */
	private static Map<SpriteType, boolean[][]> spriteMap;

    /**
     * Stars background animations for both game and main menu
     * Star density specified as argument.
     * */
    BasicGameSpace basicGameSpace = new BasicGameSpace(100);
    MenuSpace menuSpace = new MenuSpace(50);

	/** Sprite types. */
	public static enum SpriteType {
		/** Player ship. */
		Ship,
		/** Destroyed player ship. */
		ShipDestroyed,
		/** Player bullet. */
		Bullet,
		/** Enemy bullet. */
		EnemyBullet,
		/** First enemy ship - first form. */
		EnemyShipA1,
		/** First enemy ship - second form. */
		EnemyShipA2,
		/** Second enemy ship - first form. */
		EnemyShipB1,
		/** Second enemy ship - second form. */
		EnemyShipB2,
		/** Third enemy ship - first form. */
		EnemyShipC1,
		/** Third enemy ship - second form. */
		EnemyShipC2,
		/** Bonus ship. */
		EnemyShipSpecial,
		/** Destroyed enemy ship. */
		Explosion,

        /** Item Graphics Temp */
        ItemScore,
        ItemCoin,
        ItemHeal
	};

	/**
	 * Private constructor.
	 */
	private DrawManager() {
		fileManager = Core.getFileManager();
		logger = Core.getLogger();
		logger.info("Started loading resources.");

		try {
			spriteMap = new LinkedHashMap<SpriteType, boolean[][]>();

			spriteMap.put(SpriteType.Ship, new boolean[13][8]);
			spriteMap.put(SpriteType.ShipDestroyed, new boolean[13][8]);
			spriteMap.put(SpriteType.Bullet, new boolean[3][5]);
			spriteMap.put(SpriteType.EnemyBullet, new boolean[3][5]);
			spriteMap.put(SpriteType.EnemyShipA1, new boolean[12][8]);
			spriteMap.put(SpriteType.EnemyShipA2, new boolean[12][8]);
			spriteMap.put(SpriteType.EnemyShipB1, new boolean[12][8]);
			spriteMap.put(SpriteType.EnemyShipB2, new boolean[12][8]);
			spriteMap.put(SpriteType.EnemyShipC1, new boolean[12][8]);
			spriteMap.put(SpriteType.EnemyShipC2, new boolean[12][8]);
			spriteMap.put(SpriteType.EnemyShipSpecial, new boolean[16][7]);
			spriteMap.put(SpriteType.Explosion, new boolean[13][7]);

            // Item sprite placeholder
            spriteMap.put(SpriteType.ItemScore, new boolean[5][5]);
            spriteMap.put(SpriteType.ItemCoin, new boolean[5][5]);
            spriteMap.put(SpriteType.ItemHeal, new boolean[5][5]);

			fileManager.loadSprite(spriteMap);
			logger.info("Finished loading the sprites.");

			// Font loading.
			fontRegular = fileManager.loadFont(14f);
			fontBig = fileManager.loadFont(24f);
			logger.info("Finished loading the fonts.");

		} catch (IOException e) {
			logger.warning("Loading failed.");
		} catch (FontFormatException e) {
			logger.warning("Font formating failed.");
		}
	}

	/**
	 * Returns shared instance of DrawManager.
	 *
	 * @return Shared instance of DrawManager.
	 */
	protected static DrawManager getInstance() {
		if (instance == null)
			instance = new DrawManager();
		return instance;
	}

	/**
	 * Sets the frame to draw the image on.
	 *
	 * @param currentFrame
	 *                     Frame to draw on.
	 */
	public void setFrame(final Frame currentFrame) {
		frame = currentFrame;
	}

	/**
	 * First part of the drawing process. Initialises buffers, draws the
	 * background and prepares the images.
	 *
	 * @param screen
	 *               Screen to draw in.
	 */
	public void initDrawing(final Screen screen) {
		backBuffer = new BufferedImage(screen.getWidth(), screen.getHeight(),
				BufferedImage.TYPE_INT_RGB);

		graphics = frame.getGraphics();
		backBufferGraphics = backBuffer.getGraphics();

		backBufferGraphics.setColor(Color.BLACK);
		backBufferGraphics
				.fillRect(0, 0, screen.getWidth(), screen.getHeight());

		fontRegularMetrics = backBufferGraphics.getFontMetrics(fontRegular);
		fontBigMetrics = backBufferGraphics.getFontMetrics(fontBig);

		// drawBorders(screen);
		// drawGrid(screen);
	}

	/**
	 * Draws the completed drawing on screen.
	 *
	 * @param screen
	 *               Screen to draw on.
	 */
	public void completeDrawing(final Screen screen) {
		graphics.drawImage(backBuffer, frame.getInsets().left,
				frame.getInsets().top, frame);
	}

	/**
	 * Draws an entity, using the appropriate image.
	 *
	 * @param entity
	 *                  Entity to be drawn.
	 * @param positionX
	 *                  Coordinates for the left side of the image.
	 * @param positionY
	 *                  Coordinates for the upper side of the image.
	 */
	public void drawEntity(final Entity entity, final int positionX,
			final int positionY) {
		boolean[][] image = spriteMap.get(entity.getSpriteType());

		// 2P mode: start with the entity's own color
		Color color = entity.getColor();

		// Color-code by player when applicable
		if (entity instanceof Ship) {
			Ship ship = (Ship) entity;
			int pid = ship.getPlayerId(); // requires Ship.getPlayerId()
			if (pid == 1)
				color = Color.BLUE; // P1 ship
			else if (pid == 2)
				color = Color.RED; // P2 ship

			// else leave default (e.g., green) for legacy/unknown
		} else if (entity instanceof Bullet) {
			Bullet bullet = (Bullet) entity;
			int pid = bullet.getPlayerId(); // requires Bullet.getPlayerId()
			if (pid == 1)
				color = Color.CYAN; // P1 bullet
			else if (pid == 2)
				color = Color.MAGENTA; // P2 bullet
			// enemy bullets will keep their default color from the entity
		}

		backBufferGraphics.setColor(color);
		for (int i = 0; i < image.length; i++)
			for (int j = 0; j < image[i].length; j++)
				if (image[i][j])
					backBufferGraphics.drawRect(positionX + i * 2, positionY
							+ j * 2, 1, 1);
	}


    /**
     * Draws the main menu stars background animation
     */
    public void updateMenuSpace(){
        menuSpace.updateStars();

        Graphics2D g2d = (Graphics2D) backBufferGraphics;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        backBufferGraphics.setColor(Color.WHITE);
        int[][] positions = menuSpace.getStarLocations();

        for(int i = 0; i < menuSpace.getNumStars(); i++){

            int size = 1;
            int radius = size * 2;

            float[] dist = {0.0f, 1.0f};
            Color[] colors = {
                    new Color(255, 255, 200, 255),
                    new Color(255, 255, 200, 0)
            };

            RadialGradientPaint paint = new RadialGradientPaint(
                    new Point(positions[i][0], positions[i][1]),
                    radius,
                    dist,
                    colors
            );
            g2d.setPaint(paint);
            g2d.fillOval(positions[i][0] - radius / 2, positions[i][1] - radius / 2, radius, radius);


            backBufferGraphics.fillOval(positions[i][0], positions[i][1], size, size);
        }
    }

    /**
     * Draws the stars background animation during the game
     */
    public void updateGameSpace(){
        basicGameSpace.update();

        Graphics2D g2d = (Graphics2D) backBufferGraphics;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        backBufferGraphics.setColor(Color.WHITE);
        int[][] positions = basicGameSpace.getStarLocations();
        for(int i = 0; i < basicGameSpace.getNumStars(); i++){

            int size = (positions[i][2] < 2) ? 2 : 1;
            int radius = size * 2;

            float[] dist = {0.0f, 1.0f};
            Color[] colors = {
                    new Color(255, 255, 200, 50),
                    new Color(255, 255, 200, 0)
            };

            RadialGradientPaint paint = new RadialGradientPaint(
                    new Point(positions[i][0] + size / 2, positions[i][1] + size / 2),
                    radius,
                    dist,
                    colors
            );
            g2d.setPaint(paint);
            g2d.fillOval(positions[i][0] - radius / 2, positions[i][1] - radius / 2, radius, radius);


            backBufferGraphics.fillOval(positions[i][0], positions[i][1], size, size);
        }
    }

	/**
	 * For debugging purposes, draws the canvas borders.
	 *
	 * @param screen
	 *               Screen to draw in.
	 */
	@SuppressWarnings("unused")
	private void drawBorders(final Screen screen) {
		backBufferGraphics.setColor(Color.GREEN);
		backBufferGraphics.drawLine(0, 0, screen.getWidth() - 1, 0);
		backBufferGraphics.drawLine(0, 0, 0, screen.getHeight() - 1);
		backBufferGraphics.drawLine(screen.getWidth() - 1, 0,
				screen.getWidth() - 1, screen.getHeight() - 1);
		backBufferGraphics.drawLine(0, screen.getHeight() - 1,
				screen.getWidth() - 1, screen.getHeight() - 1);
	}

	/**
	 * For debugging purposes, draws a grid over the canvas.
	 *
	 * @param screen
	 *               Screen to draw in.
	 */
	@SuppressWarnings("unused")
	private void drawGrid(final Screen screen) {
		backBufferGraphics.setColor(Color.DARK_GRAY);
		for (int i = 0; i < screen.getHeight() - 1; i += 2)
			backBufferGraphics.drawLine(0, i, screen.getWidth() - 1, i);
		for (int j = 0; j < screen.getWidth() - 1; j += 2)
			backBufferGraphics.drawLine(j, 0, j, screen.getHeight() - 1);
	}

	/**
	 * Draws current score on screen.
	 *
	 * @param screen
	 *               Screen to draw on.
	 * @param score
	 *               Current score.
	 */
	public void drawScore(final Screen screen, final int score) {
		backBufferGraphics.setFont(fontRegular);
		backBufferGraphics.setColor(Color.WHITE);
		String scoreString = String.format("%04d", score);
		backBufferGraphics.drawString(scoreString, screen.getWidth() - 60, 25);
	}

	/**
	 * Draws number of remaining lives on screen.
	 *
	 * @param screen
	 *               Screen to draw on.
	 * @param lives
	 *               Current lives.
	 */
	public void drawLives(final Screen screen, final int lives) {
		backBufferGraphics.setFont(fontRegular);
		backBufferGraphics.setColor(Color.WHITE);
		backBufferGraphics.drawString(Integer.toString(lives), 20, 25);
		Ship dummyShip = new Ship(0, 0);
		for (int i = 0; i < lives; i++)
			drawEntity(dummyShip, 40 + 35 * i, 10);
	}

	/**
	 * Draws current coin count on screen.
	 *
	 * @param screen
	 *               Screen to draw on.
	 * @param coins
	 *               Current coin count.
	 */ // ADD THIS METHOD
	public void drawCoins(final Screen screen, final int coins) { // ADD THIS METHOD
		backBufferGraphics.setFont(fontRegular); // ADD THIS METHOD
		backBufferGraphics.setColor(Color.YELLOW); // ADD THIS METHOD
		String coinString = String.format("Coins: %04d", coins); // ADD THIS METHOD
		backBufferGraphics.drawString(coinString, screen.getWidth() - 180, 25); // ADD THIS METHOD
	} // ADD THIS METHOD

    // 2P mode: drawCoins method but for both players, but separate coin counts
    public void drawCoinsP1P2(final Screen screen, final int coinsP1, final int coinsP2) {
        backBufferGraphics.setFont(fontRegular);
        backBufferGraphics.setColor(Color.YELLOW);

        backBufferGraphics.drawString("P1: " + String.format("%04d", coinsP1), screen.getWidth() - 200, 25);
        backBufferGraphics.drawString("P2: " + String.format("%04d", coinsP2), screen.getWidth() - 100, 25);
    }

    /**
     * Draws a thick line from side to side of the screen.
     *
     * @param screen
     *                  Screen to draw on.
     * @param positionY
     *                  Y coordinate of the line.
     */
    public void drawHorizontalLine(final Screen screen, final int positionY) {
        backBufferGraphics.setColor(Color.GREEN);
        backBufferGraphics.drawLine(0, positionY, screen.getWidth(), positionY);
        backBufferGraphics.drawLine(0, positionY + 1, screen.getWidth(),
                positionY + 1);
    }

    /**
     * Draws game title.
     *
     * @param screen
     *               Screen to draw on.
     */
    public void drawTitle(final Screen screen) {
        String titleString = "Invaders";
        String instructionsString = "select with w+s / arrows, confirm with space";

        backBufferGraphics.setColor(Color.GRAY);
        drawCenteredRegularString(screen, instructionsString,
                screen.getHeight() / 2);

        backBufferGraphics.setColor(Color.GREEN);
        drawCenteredBigString(screen, titleString, screen.getHeight() / 3);
    }

	/**
	 * Draws main menu. - remodified for 2P mode, using string array for efficiency
	 *
	 * @param screen
	 *               Screen to draw on.
	 * @param selectedIndex
	 *               Option selected.
	 */
	public void drawMenu(final Screen screen, final int selectedIndex) {
        String[] items = {"Play", "Achievements", "Settings", "Exit"}; // High scores -> Achievements

        int baseY = screen.getHeight() / 3 * 2; // same option choice, different formatting
        for (int i = 0; i < items.length; i++) {
            backBufferGraphics.setColor(i == selectedIndex ? Color.GREEN : Color.WHITE);
            drawCenteredRegularString(screen, items[i], (int) (baseY + fontRegularMetrics.getHeight() * 1.5 * i));
        }

        /** String playString = "1-Player Mode";
         String play2String = "2-Player Mode";
         String highScoresString = "High scores";
         String exitString = "exit";
         int spacing = fontRegularMetrics.getHeight() + 10;

         if (option == 2)
         backBufferGraphics.setColor(Color.GREEN);
         else
         backBufferGraphics.setColor(Color.WHITE);
         drawCenteredRegularString(screen, playString,
         screen.getHeight() / 3 * 2);
         if (option == 1)
         backBufferGraphics.setColor(Color.GREEN);
         else
         backBufferGraphics.setColor(Color.WHITE);
         drawCenteredRegularString(screen, play2String,
         screen.getHeight() / 3 * 2 + spacing);
         if (option == 3)
         backBufferGraphics.setColor(Color.GREEN);
         else
         backBufferGraphics.setColor(Color.WHITE);
         drawCenteredRegularString(screen, highScoresString, screen.getHeight()
         / 3 * 2 + spacing * 2);
         if (option == 0)
         backBufferGraphics.setColor(Color.GREEN);
         else
         backBufferGraphics.setColor(Color.WHITE);
         drawCenteredRegularString(screen, exitString, screen.getHeight() / 3
         * 2 + spacing * 3); */
    }

    /**
     * Draws game results.
     *
     * @param screen
     *                       Screen to draw on.
     * @param score
     *                       Score obtained.
     * @param livesRemaining
     *                       Lives remaining when finished.
     * @param shipsDestroyed
     *                       Total ships destroyed.
     * @param accuracy
     *                       Total accuracy.
     * @param isNewRecord
     *                       If the score is a new high score.
     */
    public void drawResults(final Screen screen, final int score,
                            final int livesRemaining, final int shipsDestroyed,
                            final float accuracy, final boolean isNewRecord, final boolean accuracy1P) {
        String scoreString = String.format("score %04d", score);
        String livesRemainingString = "lives remaining " + livesRemaining;
        String shipsDestroyedString = "enemies destroyed " + shipsDestroyed;
        String accuracyString = String
                .format("accuracy %.2f%%", accuracy * 100);

        int height = isNewRecord ? 4 : 2;

        backBufferGraphics.setColor(Color.WHITE);
        drawCenteredRegularString(screen, scoreString, screen.getHeight()
                / height);
        drawCenteredRegularString(screen, livesRemainingString,
                screen.getHeight() / height + fontRegularMetrics.getHeight()
                        * 2);
        drawCenteredRegularString(screen, shipsDestroyedString,
                screen.getHeight() / height + fontRegularMetrics.getHeight()
                        * 4);
        // Draw accuracy for player in 1P mode
        if (accuracy1P) {
            drawCenteredRegularString(screen, accuracyString, screen.getHeight()
                    / height + fontRegularMetrics.getHeight() * 6);
        }
    }

    /**
     * Draws interactive characters for name input.
     *
     * @param screen
     *                         Screen to draw on.
     * @param name
     *                         Current name selected.
     * @param nameCharSelected
     *                         Current character selected for modification.
     */
    public void drawNameInput(final Screen screen, final char[] name,
                              final int nameCharSelected) {
        String newRecordString = "New Record!";
        String introduceNameString = "Introduce name:";

        backBufferGraphics.setColor(Color.GREEN);
        drawCenteredRegularString(screen, newRecordString, screen.getHeight()
                / 4 + fontRegularMetrics.getHeight() * 10);
        backBufferGraphics.setColor(Color.WHITE);
        drawCenteredRegularString(screen, introduceNameString,
                screen.getHeight() / 4 + fontRegularMetrics.getHeight() * 12);

        // 3 letters name.
        int positionX = screen.getWidth()
                / 2
                - (fontRegularMetrics.getWidths()[name[0]]
                + fontRegularMetrics.getWidths()[name[1]]
                + fontRegularMetrics.getWidths()[name[2]]
                + fontRegularMetrics.getWidths()[' ']) / 2;

        for (int i = 0; i < 3; i++) {
            if (i == nameCharSelected)
                backBufferGraphics.setColor(Color.GREEN);
            else
                backBufferGraphics.setColor(Color.WHITE);

            positionX += fontRegularMetrics.getWidths()[name[i]] / 2;
            positionX = i == 0 ? positionX
                    : positionX
                    + (fontRegularMetrics.getWidths()[name[i - 1]]
                    + fontRegularMetrics.getWidths()[' ']) / 2;

            backBufferGraphics.drawString(Character.toString(name[i]),
                    positionX,
                    screen.getHeight() / 4 + fontRegularMetrics.getHeight()
                            * 14);
        }
    }

    /**
     * Draws basic content of game over screen.
     *
     * @param screen
     *                     Screen to draw on.
     * @param acceptsInput
     *                     If the screen accepts input.
     * @param isNewRecord
     *                     If the score is a new high score.
     */
    public void drawGameOver(final Screen screen, final boolean acceptsInput,
                             final boolean isNewRecord) {
        String gameOverString = "Game Over";
        String continueOrExitString = "Press Space to play again, Escape to exit";

        int height = isNewRecord ? 4 : 2;

        backBufferGraphics.setColor(Color.GREEN);
        drawCenteredBigString(screen, gameOverString, screen.getHeight()
                / height - fontBigMetrics.getHeight() * 2);

        if (acceptsInput)
            backBufferGraphics.setColor(Color.GREEN);
        else
            backBufferGraphics.setColor(Color.GRAY);
        drawCenteredRegularString(screen, continueOrExitString,
                screen.getHeight() / 2 + fontRegularMetrics.getHeight() * 10);
    }

    /**
     * Draws high score screen title and instructions.
     *
     * @param screen
     *               Screen to draw on.
     */
    public void drawHighScoreMenu(final Screen screen) {
        String highScoreString = "High Scores";
        String instructionsString = "Press Space to return";

        int midX = screen.getWidth() / 2;
        int startY = screen.getHeight() / 3;

        backBufferGraphics.setColor(Color.GREEN);
        drawCenteredBigString(screen, highScoreString, screen.getHeight() / 8);

        backBufferGraphics.setColor(Color.GRAY);
        drawCenteredRegularString(screen, instructionsString,
                screen.getHeight() / 5);

        backBufferGraphics.setColor(Color.GREEN);
        backBufferGraphics.drawString("1-PLAYER MODE", midX / 2 - fontBigMetrics.stringWidth("1-PLAYER MODE") / 2 + 40,
                startY);

        backBufferGraphics.drawString("2-PLAYER MODE",
                midX + midX / 2 - fontBigMetrics.stringWidth("2-PLAYER MODE") / 2 + 40, startY);
    }

    /**
     * Draws high scores.
     *
     * @param screen
     *                   Screen to draw on.
     * @param highScores
     *                   List of high scores.
     */
    public void drawHighScores(final Screen screen,
                               final List<Score> highScores, final String mode) { // add mode to parameter
        backBufferGraphics.setColor(Color.WHITE);
        int i = 0;
        String scoreString = "";

        int midX = screen.getWidth() / 2;
        int startY = screen.getHeight() / 3 + fontBigMetrics.getHeight() + 20;
        int lineHeight = fontRegularMetrics.getHeight() + 5;

        for (Score score : highScores) {
            scoreString = String.format("%s        %04d", score.getName(), score.getScore());
            int x;
            if (mode.equals("1P")) {
                // Left column(1P)
                x = midX / 2 - fontRegularMetrics.stringWidth(scoreString) / 2;
            } else {
                // Right column(2P)
                x = midX + midX / 2 - fontRegularMetrics.stringWidth(scoreString) / 2;
            }
            backBufferGraphics.drawString(scoreString, x, startY + lineHeight * i);
            i++;
        }
    }
	// Made it to check if the Achievement button works temporarily.
	public void drawAchievementMenu(final Screen screen) {
		String AchievementsString = "Achievements";
		String instructionsString = "Press Space to return";
		backBufferGraphics.setColor(Color.GREEN);
		drawCenteredBigString(screen, AchievementsString, screen.getHeight() / 8);

		backBufferGraphics.setColor(Color.GRAY);
		drawCenteredRegularString(screen, instructionsString,
				screen.getHeight() / 5);
	}

    public void drawSettingMenu(final Screen screen) {
        String settingsString = "Settings";
        String instructionsString = "Press Space to return";

        backBufferGraphics.setColor(Color.GREEN);
        drawCenteredBigString(screen, settingsString, screen.getHeight() / 8);
		backBufferGraphics.setFont(fontRegular);
        backBufferGraphics.setColor(Color.GRAY);
        drawCenteredRegularString(screen, instructionsString, screen.getHeight() / 6);
    }
	public void drawSettingLayout(final Screen screen, final String[] menuItems, final int selectedmenuItems) {
		int splitPointX = screen.getWidth() *3/10;
		backBufferGraphics.setFont(fontRegular);
		int menuY = screen.getHeight()*3/10;
		for (int i = 0; i < menuItems.length; i++) {
			if (i == selectedmenuItems) {
				backBufferGraphics.setColor(Color.GREEN);
			}
			else {
				backBufferGraphics.setColor(Color.WHITE);
			}
			backBufferGraphics.drawString(menuItems[i], 30, menuY+(i*60));
			backBufferGraphics.setColor(Color.GREEN);
		}
		backBufferGraphics.drawLine(splitPointX, screen.getHeight()/4, splitPointX,(menuY+menuItems.length*60));
	}
	public void drawVolumeBar(final Screen screen, final int volumlevel){
		int bar_startWidth = screen.getWidth() / 2;
		int bar_endWidth = screen.getWidth()-40;
		int barHeight = screen.getHeight()*3/10;

		String volumelabel = "Volume";
		backBufferGraphics.setFont(fontRegular);
		backBufferGraphics.setColor(Color.WHITE);
		backBufferGraphics.drawLine(bar_startWidth, barHeight, bar_endWidth, barHeight);

		backBufferGraphics.setColor(Color.WHITE);
		backBufferGraphics.drawString(volumelabel, bar_startWidth-80, barHeight+7);

		int indicatorX = bar_startWidth + (int)((bar_endWidth-bar_startWidth)*(volumlevel/100.0));
		int indicatorY = barHeight+7;
		backBufferGraphics.fillRect(indicatorX, indicatorY-13, 14, 14);
		backBufferGraphics.setColor(Color.WHITE);
		String volumeText = Integer.toString(volumlevel);
		backBufferGraphics.drawString(volumeText, bar_endWidth+10, indicatorY);

	}

    public void drawKeysettings(final Screen screen, int playerNum) {
        int panelWidth = 220;
        int panelHeight = 180;
        int x = screen.getWidth() - panelWidth - 50;  // 오른쪽 여백
        int y = screen.getHeight() / 4;               // 세로 위치

        String leftKey, rightKey, attackKey;
        if (playerNum == 1) {
            leftKey = "A";
            rightKey = "D";
            attackKey = "SPACE";
        } else {
            leftKey = "LEFT";
            rightKey = "RIGHT";
            attackKey = "ENTER";
        }

        String[] labels = {"MOVE LEFT :", "MOVE RIGHT:", "ATTACK :"};
        String[] keys = {leftKey, rightKey, attackKey};

        for (int i = 0; i < labels.length; i++) {
            int textY = y + 70 + (i * 50);

            if (i < labels.length - 1) {
                backBufferGraphics.setColor(Color.DARK_GRAY);
                backBufferGraphics.drawLine(x + 20, textY + 20, x + panelWidth - 20, textY + 20);
            }

            backBufferGraphics.setColor(Color.LIGHT_GRAY);
            backBufferGraphics.drawString(labels[i], x + 30, textY);

            backBufferGraphics.setColor(Color.WHITE);
            backBufferGraphics.drawString(keys[i], x + 150, textY);
        }

    }

	/**
	 * Draws a centered string on regular font.
	 *
	 * @param screen
	 *               Screen to draw on.
	 * @param string
	 *               String to draw.
	 * @param height
	 *               Height of the drawing.
	 */
	public void drawCenteredRegularString(final Screen screen,
			final String string, final int height) {
		backBufferGraphics.setFont(fontRegular);
		backBufferGraphics.drawString(string, screen.getWidth() / 2
				- fontRegularMetrics.stringWidth(string) / 2, height);
	}

    /**
     * Draws a centered string on big font.
     *
     * @param screen
     *               Screen to draw on.
     * @param string
     *               String to draw.
     * @param height
     *               Height of the drawing.
     */
    public void drawCenteredBigString(final Screen screen, final String string,
                                      final int height) {
        backBufferGraphics.setFont(fontBig);
        backBufferGraphics.drawString(string, screen.getWidth() / 2
                - fontBigMetrics.stringWidth(string) / 2, height);
    }

    /**
     * Countdown to game start.
     *
     * @param screen
     *                  Screen to draw on.
     * @param level
     *                  Game difficulty level.
     * @param number
     *                  Countdown number.
     * @param bonusLife
     *                  Checks if a bonus life is received.
     */
    public void drawCountDown(final Screen screen, final int level,
                              final int number, final boolean bonusLife) {
        int rectWidth = screen.getWidth();
        int rectHeight = screen.getHeight() / 6;
        backBufferGraphics.setColor(Color.BLACK);
        backBufferGraphics.fillRect(0, screen.getHeight() / 2 - rectHeight / 2,
                rectWidth, rectHeight);
        backBufferGraphics.setColor(Color.GREEN);
        if (number >= 4)
            if (!bonusLife) {
                drawCenteredBigString(screen, "Level " + level,
                        screen.getHeight() / 2
                                + fontBigMetrics.getHeight() / 3);
            } else {
                drawCenteredBigString(screen, "Level " + level
                                + " - Bonus life!",
                        screen.getHeight() / 2
                                + fontBigMetrics.getHeight() / 3);
            }
        else if (number != 0)
            drawCenteredBigString(screen, Integer.toString(number),
                    screen.getHeight() / 2 + fontBigMetrics.getHeight() / 3);
        else
            drawCenteredBigString(screen, "GO!", screen.getHeight() / 2
                    + fontBigMetrics.getHeight() / 3);
    }

    /**
     * Draws the play mode selection menu (1P / 2P / Back).
     *
     * @param screen
     *                  Screen to draw on.
     * @param selectedIndex
     *                  Currently selected option (0 = 1P, 1 = 2P, 2 = Back).
     */

    public void drawPlayMenu(final Screen screen, final int selectedIndex) {
        String[] items = {"1 Player", "2 Players", "Back"};

        int baseY = screen.getHeight() / 3 * 1;
        for (int i = 0; i < items.length; i++) {
            backBufferGraphics.setColor(i == selectedIndex ? Color.GREEN : Color.WHITE);
            drawCenteredRegularString(screen, items[i],
                    baseY + fontRegularMetrics.getHeight() * 3 * i);
        }
    }
}