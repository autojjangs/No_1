package engine;

/**
 * Implements an object that stores the state of the game between levels.
 *
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 *
 */
public class GameState {

	// 2P mode: number of players used for shared lives in co-op
	public static final int NUM_PLAYERS = 2; // adjust later if needed

	// 2P mode: whether we’re in co-op mode (shared lives) or single-player
	private boolean coop;

	/** Current game level. */
	private int level;
	/** Current score. */
	private int score;
	/** Lives currently remaining. */
	private int livesRemaining;
	/** Bullets shot until now. */
	private int bulletsShot;
	/** Ships destroyed until now. */
	private int shipsDestroyed;
	/** Current coin count. */ // ADD THIS LINE
	private int coins; // ADD THIS LINE

	// 2P mode: Co-op aware constructor used by the updated Core loop
	public GameState(final int level, final int livesEach, final boolean coop) {
		this.level = level;
		this.coop = coop;
		int players = coop ? NUM_PLAYERS : 1;
		this.livesRemaining = Math.max(0, livesEach * players);
		this.score = 0;
		this.bulletsShot = 0;
		this.shipsDestroyed = 0;
		this.coins = 0;
	}

	// Legacy 6-arg for existing calls to work for single-player

	/**
	 * Constructor.
	 *
	 * @param level
	 *                       Current game level.
	 * @param score
	 *                       Current score.
	 * @param livesRemaining
	 *                       Lives currently remaining.
	 * @param bulletsShot
	 *                       Bullets shot until now.
	 * @param shipsDestroyed
	 *                       Ships destroyed until now.
	 * @param coins          // ADD THIS LINE
	 *                       Current coin count. // ADD THIS LINE
	 */
	public GameState(final int level, final int score,
			final int livesRemaining, final int bulletsShot,
			final int shipsDestroyed, final int coins) { // MODIFY THIS LINE
		this.level = level;
		this.score = score;
		this.livesRemaining = livesRemaining;
		this.bulletsShot = bulletsShot;
		this.shipsDestroyed = shipsDestroyed;
		this.coins = coins; // ADD THIS LINE
		this.coop = false; // 2P: single-player mode
	}

	// 2P mode: helpers for updated Core loop
	public boolean teamAlive() {
		return this.livesRemaining > 0;
	}

	public void nextLevel() {
		this.level += 1;
	}

	/**
	 * @return the level
	 */
	public final int getLevel() {
		return level;
	}

	/**
	 * @return the score
	 */
	public final int getScore() {
		return score;
	}

	/**
	 * @return the livesRemaining
	 */
	public final int getLivesRemaining() {
		return livesRemaining;
	}

	/**
	 * @return the bulletsShot
	 */
	public final int getBulletsShot() {
		return bulletsShot;
	}

	/**
	 * @return the shipsDestroyed
	 */
	public final int getShipsDestroyed() {
		return shipsDestroyed;
	}

	/**
	 * @return the coins
	 */
	public final int getCoins() { // ADD THIS METHOD
		return coins;
	}

	// 2P: callers can branch on 1P vs 2P
	public boolean isCoop() {
		return coop;
	}
}