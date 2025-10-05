package screen;

import java.awt.event.KeyEvent;

import engine.Cooldown;
import engine.Core;

/**
 * Implements the title screen.
 *
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 *
 */
public class TitleScreen extends Screen {

    // 2P mode: user picks mode, where false = 1P, true = 2P
    private boolean coopSelected = false;
    public boolean isCoopSelected() { return coopSelected; }

	/** Milliseconds between changes in user selection. */
	private static final int SELECTION_TIME = 200;

	/** Time between changes in user selection. */
	private Cooldown selectionCooldown;

    // menu index added for user mode selection
    private int menuIndex = 0;


	/** Added variable to store which menu option is currently hovered */
	private Integer hoverOption = null;

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
	public TitleScreen(final int width, final int height, final int fps) {
		super(width, height, fps);

		// Defaults to play.
		this.returnCode = 1; // 2P mode: changed to default selection as 1P
		this.selectionCooldown = Core.getCooldown(SELECTION_TIME);
		this.selectionCooldown.reset();
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
        if (this.selectionCooldown.checkFinished() && this.inputDelay.checkFinished()) {
            if (inputManager.isKeyDown(KeyEvent.VK_UP) || inputManager.isKeyDown(KeyEvent.VK_W)) {
                previousMenuItem();
                this.selectionCooldown.reset();
                this.hoverOption = null;
            }
            if (inputManager.isKeyDown(KeyEvent.VK_DOWN) || inputManager.isKeyDown(KeyEvent.VK_S)) {
                nextMenuItem();
                this.selectionCooldown.reset();
                this.hoverOption = null;
            }

            // Play : Adjust the case so that 1p and 2p can be determined within the play.
            if (inputManager.isKeyDown(KeyEvent.VK_SPACE)) {
                switch (this.menuIndex) {
                    case 0: // "Play"
                        this.returnCode = 5; // go to PlayScreen
                        this.isRunning = false;
                        break;

                    case 1: // "High scores"
                        this.returnCode = 3;
                        this.isRunning = false;
                        break;
                    case 2: // "Settings"
                        this.returnCode = 4;
                        this.isRunning = false;
                        break;

                    case 3: // "Quit"
                        this.returnCode = 0;
                        this.isRunning = false;
                        break;

                    default:
                        break;
                }
            }
            if (inputManager.isMouseClicked()) {
                int temp_x = inputManager.getMouseX();
                int temp_y = inputManager.getMouseY();

                java.awt.Rectangle[] boxes = drawManager.getMenuHitboxes(this);
                int[] pos = {5, 3, 4, 0};

                for (int i = 0; i < boxes.length; i++) {
                    if (boxes[i].contains(temp_x, temp_y)) {
                        this.returnCode = pos[i];
                        this.isRunning = false;
                        break;
                    }
                }
            }
        }
    }

	/**
	 * Shifts the focus to the next menu item. - modified for 2P mode selection
	 */
	private void nextMenuItem() {
        this.menuIndex = (this.menuIndex + 1) % 4;
	}

	/**
	 * Shifts the focus to the previous menu item.
	 */
	private void previousMenuItem() {
        this.menuIndex = (this.menuIndex + 3) % 4; // wrap upwards
    }
	/**
	 * Draws the elements associated with the screen.
	 */

	/** Check hover based on mouse position and menu hitbox. */
	private void draw() {
		drawManager.initDrawing(this);

		int mx = inputManager.getMouseX();
		int my = inputManager.getMouseY();
		java.awt.Rectangle[] boxesForHover = drawManager.getMenuHitboxes(this);

		Integer newHover = null;
		if(boxesForHover[0].contains(mx, my))
			newHover = 0;
		if(boxesForHover[1].contains(mx, my))
			newHover = 1;
		if(boxesForHover[2].contains(mx, my))
			newHover = 2;
        if(boxesForHover[3].contains(mx, my))
            newHover = 3;

        // Modify : Update after hover calculation
        if (newHover != null) {
            // Hover Update + Promote to Select Index when mouse is raised (to keep mouse away)
            if (!newHover.equals(this.hoverOption)) { this.hoverOption = newHover; }
        } else {
            // If we had a hover and the mouse left, promote last hover to selection for persistance
            if (this.hoverOption != null) {
                this.menuIndex = this.hoverOption; // persist last hovered as selection
                this.hoverOption = null; // clear hover state
            }

        }

		//pass hoverOption for menu highlights respond to mouse hover
		drawManager.drawTitle(this);
		drawManager.drawMenu(this, this.menuIndex, hoverOption, this.menuIndex); // 2P mode: using menu index for highlighting

		drawManager.completeDrawing(this);
	}
}