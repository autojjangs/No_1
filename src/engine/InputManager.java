package engine;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Manages keyboard input for the provided screen.
 *
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 *
 */
public final class InputManager implements KeyListener {

	/** Number of recognised keys. */
	private static final int NUM_KEYS = 256;
	/** Array with the keys marked as pressed or not. */
	private static boolean[] keys;
	/** Singleton instance of the class. */
	private static InputManager instance;
	/** Last character typed. */
	private static char lastCharTyped;
	/** Flag to check if a character was typed. */
	private static boolean charTyped;

	/**
	 * Private constructor.
	 */
	private InputManager() {
		keys = new boolean[NUM_KEYS];
		lastCharTyped = '\0';
		charTyped = false;
	}

	/**
	 * Returns shared instance of InputManager.
	 *
	 * @return Shared instance of InputManager.
	 */
	protected static InputManager getInstance() {
		if (instance == null)
			instance = new InputManager();
		return instance;
	}

	/**
	 * Returns the last character typed and resets the flag.
	 *
	 * @return Last character typed, or '\0' if none.
	 */
	public char getLastCharTyped() {
		if (charTyped) {
			charTyped = false;
			return lastCharTyped;
		}
		return '\0';
	}

	/**
	 * Returns true if the provided key is currently pressed.
	 *
	 * @param keyCode
	 *            Key number to check.
	 * @return Key state.
	 */
	public boolean isKeyDown(final int keyCode) {
		return keys[keyCode];
	}

    // === PLAYER 1 CONTROLS (Existing functionality) ===
    // Player 1 uses WASD + Spacebar configuration

    /**
     * Checks if Player 1's move left key (A) is pressed.
     *
     * @return True if Player 1 is moving left
     */

    public boolean isP1LeftPressed() {
        return isKeyDown(KeyEvent.VK_A);
    }

    /**
     * Checks if Player 1's move right key (D) is pressed.
     *
     * @return True if Player 1 is moving right
     */

    public boolean isP1RightPressed() {
        return isKeyDown(KeyEvent.VK_D);
    }

    /**
     * Checks if Player 1's shoot key (Spacebar) is pressed.
     *
     * @return True if Player 1 is shooting
     */

    public boolean isP1ShootPressed() {
        return isKeyDown(KeyEvent.VK_SPACE);
    }


    // ==================== PLAYER 2 CONTROLS ====================
    // Player 2 uses Arrow Keys + Enter configuration
    // Added for two-player mode implementation

    /**
     * Checks if Player 2's move left key (Left Arrow) is pressed.
     *
     * @return True if Player 2 is moving left
     */

    public boolean isP2LeftPressed() {
        return isKeyDown(KeyEvent.VK_LEFT);
    }

    /**
     * Checks if Player 2's move right key (Right Arrow) is pressed.
     *
     * @return True if Player 2 is moving right
     */

    public boolean isP2RightPressed() {
        return isKeyDown(KeyEvent.VK_RIGHT);
    }

    /**
     * Checks if Player 2's shoot key (Enter) is pressed.
     *
     * @return True if Player 2 is shooting
     */
    public boolean isP2ShootPressed() {
        return isKeyDown(KeyEvent.VK_ENTER);
    }

	/**
	 * Changes the state of the key to pressed.
	 *
	 * @param key
	 *            Key pressed.
	 */
	@Override
	public void keyPressed(final KeyEvent key) {
		if (key.getKeyCode() >= 0 && key.getKeyCode() < NUM_KEYS)
			keys[key.getKeyCode()] = true;
	}

	/**
	 * Changes the state of the key to not pressed.
	 *
	 * @param key
	 *            Key released.
	 */
	@Override
	public void keyReleased(final KeyEvent key) {
		if (key.getKeyCode() >= 0 && key.getKeyCode() < NUM_KEYS)
			keys[key.getKeyCode()] = false;
	}

	/**
	 * Does nothing.
	 *
	 * @param key
	 *            Key typed.
	 */
	@Override
	public void keyTyped(final KeyEvent key) {
		lastCharTyped = key.getKeyChar();
		charTyped = true;
	}

	/**
	 * Clears any pending key or character input.
	 * (Prevents unintended key carry-over between screens)
	 */
	public void clearLastKey() {
		lastCharTyped = '\0' ;
		charTyped = false ;
	}
}