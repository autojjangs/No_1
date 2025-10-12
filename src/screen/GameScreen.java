// screen/GameScreen.java
package screen;

import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;

import engine.*;
import entity.Bullet;
import entity.BulletPool;
import entity.EnemyShip;
import entity.EnemyShipFormation;
import entity.Entity;
import entity.Ship;

/**
 * Implements the game screen, where the action happens.(supports co-op with
 * shared team lives)
 *
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 *
 */
public class GameScreen extends Screen {

	private static final int INPUT_DELAY = 6000;
	private static final int LIFE_SCORE = 100;
	private static final int BONUS_SHIP_INTERVAL = 20000;
	private static final int BONUS_SHIP_VARIANCE = 10000;
	private static final int BONUS_SHIP_EXPLOSION = 500;
	private static final int SCREEN_CHANGE_INTERVAL = 1500;
	private static final int SEPARATION_LINE_HEIGHT = 40;
	/** For Check Achievement
	 * 2015-10-02 add new */
	private AchievementManager achievementManager;
	/** Current game difficulty settings. */
	private GameSettings gameSettings;
	private EnemyShipFormation enemyShipFormation;
	private Ship[] ships = new Ship[GameState.NUM_PLAYERS];
	private EnemyShip enemyShipSpecial;
	private Cooldown enemyShipSpecialCooldown;
	private Cooldown enemyShipSpecialExplosionCooldown;
	private Cooldown screenFinishedCooldown;
	private Set<Bullet> bullets;
	private long gameStartTime;
	private boolean levelFinished;
	private boolean bonusLife;

	private int level;
	private int score;
	private int lives;
	private int bulletsShot;
	private int shipsDestroyed;
	private Ship ship;

	/** checks if player took damage
	 * 2025-10-02 add new variable
	 * */
	private boolean tookDamageThisLevel;

	/**
	 * Constructor, establishes the properties of the screen.
	 *
	 * @param gameState
	 *                     Current game state.
	 * @param gameSettings
	 *                     Current game settings.
	 * @param bonusLife
	 *                     Checks if a bonus life is awarded this level.
	 * @param width
	 *                     Screen width.
	 * @param height
	 *                     Screen height.
	 * @param fps
	 *                     Frames per second, frame rate at which the game is run.
	 * @param achievementManager
	 * 			               Achievement manager instance used to track and save player achievements.
	 * 			  2025-10-03 add generator parameter and comment
	 */

	private final GameState state;

	public GameScreen(final GameState gameState,
					  final GameSettings gameSettings, final boolean bonusLife,
					  final int width, final int height, final int fps, final AchievementManager achievementManager) {
		super(width, height, fps);

		this.state = gameState;
		this.gameSettings = gameSettings;
		this.bonusLife = bonusLife;
		this.level = gameState.getLevel();
		this.score = gameState.getScore();
		this.lives = gameState.getLivesRemaining();
		if (this.bonusLife)
			this.lives++;
		this.bulletsShot = gameState.getBulletsShot();
		this.shipsDestroyed = gameState.getShipsDestroyed();

		// for check Achievement 2025-10-02 add
		this.achievementManager = achievementManager;
		this.tookDamageThisLevel = false;

		// 2P: bonus life adds to team pool + singleplayer mode
		if (this.bonusLife) {
			if (state.isSharedLives()) {
				state.addTeamLife(1); // two player
			} else {
				// 1P legacy: grant to P1
				state.addLife(0, 1);  // singleplayer
			}
		}

		// [ADD] ensure achievementManager is not null for popup system
		if (this.achievementManager == null) this.achievementManager = new AchievementManager();
	}

	public final void initialize() {
		super.initialize();

		enemyShipFormation = new EnemyShipFormation(this.gameSettings);
		enemyShipFormation.attach(this);

		// 2P mode: create both ships, tagged to their respective teams
		this.ships[0] = new Ship(this.width / 2 - 60, this.height - 30, Entity.Team.PLAYER1); // P1
		this.ships[0].setPlayerId(1);

		// only allowing second ship to spawn when 2P mode is chosen
		if (state.isCoop()) {
			this.ships[1] = new Ship(this.width / 2 + 60, this.height - 30, Entity.Team.PLAYER2); // P2
			this.ships[1].setPlayerId(2);
		} else {
			this.ships[1] = null; // ensuring there's no P2 ship in 1P mode
		}

		this.enemyShipSpecialCooldown = Core.getVariableCooldown(BONUS_SHIP_INTERVAL, BONUS_SHIP_VARIANCE);
		this.enemyShipSpecialCooldown.reset();
		this.enemyShipSpecialExplosionCooldown = Core.getCooldown(BONUS_SHIP_EXPLOSION);
		this.screenFinishedCooldown = Core.getCooldown(SCREEN_CHANGE_INTERVAL);
		this.bullets = new HashSet<Bullet>();
		// Special input delay / countdown.
		this.gameStartTime = System.currentTimeMillis();
		this.inputDelay = Core.getCooldown(INPUT_DELAY);
		this.inputDelay.reset();
	}

	/**
	 * Starts the action.
	 *
	 * @return Next screen code.
	 */
	public final int run() {
		super.run();

		// 2P mode: award bonus score for remaining TEAM lives
		state.addScore(0, LIFE_SCORE * state.getLivesRemaining());

		this.logger.info("Screen cleared with a score of " + state.getScore());
		return this.returnCode;
	}

	protected final void update() {
		super.update();

		if (this.inputDelay.checkFinished() && !this.levelFinished) {

			// Per-player input/move/shoot
			for (int p = 0; p < GameState.NUM_PLAYERS; p++) {
				Ship ship = this.ships[p];

				if (ship == null || ship.isDestroyed())
					continue;

				boolean moveRight = (p == 0)
						? (inputManager.isKeyDown(KeyEvent.VK_D))
						: (inputManager.isKeyDown(KeyEvent.VK_RIGHT));

				boolean moveLeft = (p == 0)
						? (inputManager.isKeyDown(KeyEvent.VK_A))
						: (inputManager.isKeyDown(KeyEvent.VK_LEFT));

				boolean isRightBorder = ship.getPositionX() + ship.getWidth() + ship.getSpeed() > this.width - 1;

				boolean isLeftBorder = ship.getPositionX() - ship.getSpeed() < 1;

				if (moveRight && !isRightBorder)
					ship.moveRight();
				if (moveLeft && !isLeftBorder)
					ship.moveLeft();

				boolean fire = (p == 0)
						? inputManager.isKeyDown(KeyEvent.VK_SPACE)
						: inputManager.isKeyDown(KeyEvent.VK_ENTER);

				if (fire && ship.shoot(this.bullets)) {

					state.incBulletsShot(p); // 2P mode: increments per-player bullet shots

				}
			}

			// Special ship lifecycle
			if (this.enemyShipSpecial != null) {
				if (!this.enemyShipSpecial.isDestroyed())
					this.enemyShipSpecial.move(2, 0);
				else if (this.enemyShipSpecialExplosionCooldown.checkFinished())
					this.enemyShipSpecial = null;
			}
			if (this.enemyShipSpecial == null && this.enemyShipSpecialCooldown.checkFinished()) {
				this.enemyShipSpecial = new EnemyShip();
				this.enemyShipSpecialCooldown.reset();
				this.logger.info("A special ship appears");
			}
			if (this.enemyShipSpecial != null && this.enemyShipSpecial.getPositionX() > this.width) {
				this.enemyShipSpecial = null;
				this.logger.info("The special ship has escaped");
			}

			// Update ships & enemies
			for (Ship s : this.ships)
				if (s != null)
					s.update();

			this.enemyShipFormation.update();
			this.enemyShipFormation.shoot(this.bullets);
		}

		manageCollisions();
		cleanBullets();
		draw();

		// End condition: formation cleared or TEAM lives exhausted.
		if ((this.enemyShipFormation.isEmpty() || !state.teamAlive()) && !this.levelFinished) {
			this.levelFinished = true;
			this.screenFinishedCooldown.reset();

			if(state.getBulletsShot() > 0 && state.getBulletsShot() == state.getShipsDestroyed()){
				achievementManager.unlock("Perfect Shooter");
			}
			if(!this.tookDamageThisLevel){
				achievementManager.unlock("Survivor");
			}
			if(state.getLevel() == 5){
				achievementManager.unlock("Clear");
			}
		}

		if (this.levelFinished && this.screenFinishedCooldown.checkFinished()) {
			if (!achievementManager.hasPendingToasts()) {
				this.isRunning = false;
			}
		}

		if (this.achievementManager != null) this.achievementManager.update();
	}

	private void draw() {
		drawManager.initDrawing(this);

		for (Ship s : this.ships)
			if (s != null)
				drawManager.drawEntity(s, s.getPositionX(), s.getPositionY());

		if (this.enemyShipSpecial != null)
			drawManager.drawEntity(this.enemyShipSpecial,
					this.enemyShipSpecial.getPositionX(),
					this.enemyShipSpecial.getPositionY());

		enemyShipFormation.draw();

		for (Bullet bullet : this.bullets)
			drawManager.drawEntity(bullet, bullet.getPositionX(),
					bullet.getPositionY());

		// Aggregate UI (team score & team lives)
		drawManager.drawScore(this, state.getScore());
		drawManager.drawLives(this, state.getLivesRemaining());
		drawManager.drawCoins(this,  state.getCoins()); // ADD THIS LINE - 2P mode: team total

		// 2P mode: setting per-player coin count
		if (state.isCoop()) {
			// left: P1
			String p1 = String.format("P1  S:%d  K:%d  B:%d  C:%d",
					state.getScore(0), state.getShipsDestroyed(0),
					state.getBulletsShot(0), state.getCoins(0));
			// right: P2
			String p2 = String.format("P2  S:%d  K:%d  B:%d  C:%d",
					state.getScore(1), state.getShipsDestroyed(1),
					state.getBulletsShot(1), state.getCoins(1));

			// remove the unnecessary "P1 S: K: B: C:" and "P2 S: K: B: C:" lines from the game screen
		}

		drawManager.drawHorizontalLine(this, SEPARATION_LINE_HEIGHT - 1);

		if (!this.inputDelay.checkFinished()) {
			int countdown = (int) ((INPUT_DELAY - (System.currentTimeMillis() - this.gameStartTime)) / 1000);
			drawManager.drawCountDown(this, this.state.getLevel(), countdown, this.bonusLife);
			drawManager.drawHorizontalLine(this, this.height / 2 - this.height / 12);
			drawManager.drawHorizontalLine(this, this.height / 2 + this.height / 12);
		}

		// [ADD] draw achievement popups right before completing the frame
		drawManager.drawAchievementToasts(
				this,
				(this.achievementManager != null)
						? this.achievementManager.getActiveToasts()
						: java.util.Collections.emptyList()
		);

		drawManager.completeDrawing(this);
	}

	private void cleanBullets() {
		Set<Bullet> recyclable = new HashSet<Bullet>();
		for (Bullet bullet : this.bullets) {
			bullet.update();
			if (bullet.getPositionY() < SEPARATION_LINE_HEIGHT
					|| bullet.getPositionY() > this.height)
				recyclable.add(bullet);
		}
		this.bullets.removeAll(recyclable);
		BulletPool.recycle(recyclable);
	}

	/**
	 * Enemy bullets hit players → decrement TEAM lives; player bullets hit enemies
	 * → add score.
	 */
	private void manageCollisions() {
		Set<Bullet> recyclable = new HashSet<Bullet>();
		for (Bullet bullet : this.bullets) {
			if (bullet.getSpeed() > 0) {
				// Enemy bullet vs both players
				for (int p = 0; p < GameState.NUM_PLAYERS; p++) {
					Ship ship = this.ships[p];
					if (ship != null && !ship.isDestroyed()
							&& checkCollision(bullet, ship) && !this.levelFinished) {
						recyclable.add(bullet);

						ship.destroy(); // explosion/respawn handled by Ship.update()
						state.decLife(p); // decrement shared/team lives by 1

						// [ADD] record damage for Survivor achievement check
						this.tookDamageThisLevel = true;

						this.logger.info("Hit on player " + (p + 1) + ", team lives now: " + state.getLivesRemaining());
						break;
					}
				}
			} else {
				// Player bullet vs enemies
				// map Bullet owner id (1 or 2) to per-player index (0 or 1)
				final int ownerId = bullet.getOwnerPlayerId(); // 1 or 2 (0 if unset)
				final int pIdx = (ownerId == 2) ? 1 : 0; // default to P1 when unset

				for (EnemyShip enemyShip : this.enemyShipFormation)
					if (!enemyShip.isDestroyed() && checkCollision(bullet, enemyShip)) {
						int points = enemyShip.getPointValue();
						state.addCoins(pIdx, enemyShip.getCoinValue()); // 2P mode: modified to per-player coins

						state.addScore(pIdx, points); // 2P mode: modified to add to P1 score for now
						state.incShipsDestroyed(pIdx);

						// [ADD] precise First Blood check using current counter
						if (this.achievementManager != null && state.getShipsDestroyed(pIdx) == 1) {
							this.achievementManager.unlock("First Blood");
						}

						this.enemyShipFormation.destroy(enemyShip);

						/*
						  check of 'First Blood' achievement release
						  2025.10.02 add
						  */
						if(this.shipsDestroyed ==1){
							//achievementManager.unlockFirstBlood();
							achievementManager.unlock("First Blood");
						}

						recyclable.add(bullet);
						break;
					}

				if (this.enemyShipSpecial != null
						&& !this.enemyShipSpecial.isDestroyed()
						&& checkCollision(bullet, this.enemyShipSpecial)) {
					int points = this.enemyShipSpecial.getPointValue();

					state.addCoins(pIdx, this.enemyShipSpecial.getCoinValue()); // 2P mode: modified to per-player coins

					state.addScore(pIdx, points);
					state.incShipsDestroyed(pIdx); // 2P mode: modified incrementing ships destroyed

					this.enemyShipSpecial.destroy();
					this.enemyShipSpecialExplosionCooldown.reset();
					recyclable.add(bullet);
				}
			}
		}
		this.bullets.removeAll(recyclable);
		BulletPool.recycle(recyclable);
	}

	/**
	 * Checks if two entities are colliding.
	 *
	 * @param a
	 *          First entity, the bullet.
	 * @param b
	 *          Second entity, the ship.
	 * @return Result of the collision test.
	 */
	private boolean checkCollision(final Entity a, final Entity b) {
		int centerAX = a.getPositionX() + a.getWidth() / 2;
		int centerAY = a.getPositionY() + a.getHeight() / 2;
		int centerBX = b.getPositionX() + b.getWidth() / 2;
		int centerBY = b.getPositionY() + b.getHeight() / 2;
		int maxDistanceX = a.getWidth() / 2 + b.getWidth() / 2;
		int maxDistanceY = a.getHeight() / 2 + b.getHeight() / 2;
		int distanceX = Math.abs(centerAX - centerBX);
		int distanceY = Math.abs(centerAY - centerBY);
		return distanceX < maxDistanceX && distanceY < maxDistanceY;
	}

	/**
	 * Returns a GameState object representing the status of the game.
	 *
	 * @return Current game state.
	 */
	public final GameState getGameState() {
		return this.state;
	}
}
