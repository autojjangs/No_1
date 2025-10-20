package engine;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;

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
    /** Last character typed. */
    private static char lastCharTyped;
    /** Flag to check if a character was typed. */
    private static boolean charTyped;


    // add three variable

    private static int mouseX;
    private static int mouseY;
    private static boolean mouseClicked;

	/**
     *  Declare variables to save and return input keys
     */
    private int lastPressedKey = -1;
    private static final String KEY_CONFIG_FILE = "keyconfig.txt";

    protected static int[] player1Keys;
    protected static int[] player2Keys;

    public void setPlayer1Keys(int[] newKeys) {
        player1Keys = newKeys.clone();
    }
    public int[] getPlayer1Keys() {
        return player1Keys.clone();
    }

    public void setPlayer2Keys(int[] newKeys) {
        player2Keys = newKeys.clone();
    }
    public int[] getPlayer2Keys() {
        return player2Keys.clone();
    }

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
     * Checks if Player 1's move left key (player1keys[0]) is pressed.
     *
     * @return True if Player 1 is moving left
     */

    public boolean isP1LeftPressed() {
        return isKeyDown(player1Keys[0]);
    }

    /**
     * Checks if Player 1's move right key (player1keys[1]) is pressed.
     *
     * @return True if Player 1 is moving right
     */

    public boolean isP1RightPressed() {
        return isKeyDown(player1Keys[1]);
    }

    /**
     * Checks if Player 1's shoot key (player1keys[2]) is pressed.
     *
     * @return True if Player 1 is shooting
     */

    public boolean isP1ShootPressed() {
        return isKeyDown(player1Keys[2]);
    }


    // ==================== PLAYER 2 CONTROLS ====================
    // Player 2 uses Arrow Keys + Enter configuration
    // Added for two-player mode implementation

    /**
     * Checks if Player 2's move left key (player2keys[0] is pressed.
     *
     * @return True if Player 2 is moving left
     */

    public boolean isP2LeftPressed() {
        return isKeyDown(player2Keys[0]);
    }

    /**
     * Checks if Player 2's move right key (player2keys[1]) is pressed.
     *
     * @return True if Player 2 is moving right
     */

    public boolean isP2RightPressed() {
        return isKeyDown(player2Keys[1]);
    }

    /**
     * Checks if Player 2's shoot key (player2keys[2]) is pressed.
     *
     * @return True if Player 2 is shooting
     */
    public boolean isP2ShootPressed() {
        return isKeyDown(player2Keys[2]);
    }

	/**
	 * Changes the state of the key to pressed.
	 *
	 * @param key
	 *            Key pressed.
	 */
	@Override
	public void keyPressed(final KeyEvent key) {
		if (key.getKeyCode() >= 0 && key.getKeyCode() < NUM_KEYS) {
            keys[key.getKeyCode()] = true;
            lastPressedKey = key.getKeyCode();
        }
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
    // Save and return the last pressed key
    public int getLastPressedKey() {
        int temp = lastPressedKey;
        lastPressedKey = -1;
        return temp;
    }

    /**
     * Clears any pending key or character input.
     * (Prevents unintended key carry-over between screens)
     */
    public void clearLastKey() {
        lastCharTyped = '\0' ;
        charTyped = false ;
    }
    // Create and return a project path/res/keyconfig.txt file object
    private File getKeyConfigFile() {
        String projectPath = System.getProperty("user.dir");
        return new File(projectPath + File.separator + "res" + File.separator + KEY_CONFIG_FILE);
    }
    // write a key code in a keyconfig.txt file
    public void saveKeyConfig() {
        try {
            File file = getKeyConfigFile();
            File folder = file.getParentFile();
            if (!folder.exists()) folder.mkdirs();

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write(player1Keys[0] + "," + player1Keys[1] + "," + player1Keys[2]);
                writer.newLine();
                writer.write(player2Keys[0] + "," + player2Keys[1] + "," + player2Keys[2]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // Import a file and change the saved input key code
    public void loadKeyConfig() {
        File file = getKeyConfigFile();

        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line1 = reader.readLine();
            String line2 = reader.readLine();
            if (line1 != null) {
                String[] parts = line1.split(",");
                for (int i = 0; i < 3; i++) player1Keys[i] = Integer.parseInt(parts[i]);
            }
            if (line2 != null) {
                String[] parts = line2.split(",");
                for (int i = 0; i < 3; i++) player2Keys[i] = Integer.parseInt(parts[i]);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * After setting the default, import the saved key settings from the file and cover the default values
     */
    static {
        instance = new InputManager();
        player1Keys = new int[] {KeyEvent.VK_A, KeyEvent.VK_D, KeyEvent.VK_SPACE};
        player2Keys = new int[] {KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, KeyEvent.VK_ENTER};

        instance.loadKeyConfig();
    }

    /**
     * Resets all key states to not pressed.
     */
    public static void resetKeys() {
        for (int i = 0; i < NUM_KEYS; i++) {
            keys[i] = false;
        }
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