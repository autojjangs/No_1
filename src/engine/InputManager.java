package engine;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import java.awt.event.MouseEvent;//add this line
import java.awt.event.MouseListener;//add this line
import java.awt.event.MouseMotionListener;//add this line



/**
 * Manages keyboard input for the provided screen.
 *
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 *
 */

public final class InputManager implements KeyListener, MouseListener, MouseMotionListener { // add MouseListener, MouseMotionListener param


	/** Number of recognised keys. */
	private static final int NUM_KEYS = 256;
	/** Array with the keys marked as pressed or not. */
	private static boolean[] keys;

    /** Mouse pressed state. */
    private static  boolean mousePressed; // add this line

	/** Singleton instance of the class. */
	private static InputManager instance;


    // add three variable

    private static int mouseX;
    private static int mouseY;
    private static boolean mouseClicked;

	/**
	 * Private constructor.
	 */
	private InputManager() {
		keys = new boolean[NUM_KEYS];
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

	}

    public int getMouseX() { return mouseX; } // add this function

    public int getMouseY() { return mouseY; } // add this function

    public boolean isMouseClicked() { // add this function
        if (mouseClicked) {
            mouseClicked = false;
            return true;
        }
        return false;
    }

    @Override
    public void mouseClicked(final MouseEvent e) { // add this function
        // Can be left empty or used if needed
    }

    @Override
    public void mousePressed(final MouseEvent e) { // add this function
        mousePressed = true;
        mouseX = e.getX();
        mouseY = e.getY();
    }

    @Override
    public void mouseReleased(final MouseEvent e) { // add this function
        mousePressed = false;
        mouseX = e.getX();
        mouseY = e.getY();
        mouseClicked = true;
    }

    @Override
    public void mouseEntered(final MouseEvent e) { // add this function

    }

    @Override
    public void mouseExited(final MouseEvent e) { // add this function

    }

	/** Added mouse move/drag event to update mouse position right now */
	@Override
	public void mouseMoved(final MouseEvent e){
		mouseX = e.getX();
		mouseY = e.getY();
	}
	@Override
	public void mouseDragged(final MouseEvent e){
		mouseX = e.getX();
		mouseY = e.getY();
	}

	public boolean isMousePressed(){
		return mousePressed;
	}

}