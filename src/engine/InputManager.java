package engine;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent; // add this line
import java.awt.event.MouseListener; // add this line

/**
 * Manages keyboard input for the provided screen.
 *
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 *
 */
public final class InputManager implements KeyListener, MouseListener { // add MouseListener param

	/** Number of recognised keys. */
	private static final int NUM_KEYS = 256;
	/** Array with the jeys marked as pressed or not. */
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
}