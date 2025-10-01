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

	/** Milliseconds between changes in user selection. */
	private static final int SELECTION_TIME = 200;

	/** Time between changes in user selection. */
	private Cooldown selectionCooldown;


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
		this.returnCode = 2;
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
		if (this.selectionCooldown.checkFinished()
				&& this.inputDelay.checkFinished()) {
			if (inputManager.isKeyDown(KeyEvent.VK_UP)
					|| inputManager.isKeyDown(KeyEvent.VK_W)) {
				previousMenuItem();
				this.selectionCooldown.reset();
			}
			if (inputManager.isKeyDown(KeyEvent.VK_DOWN)
					|| inputManager.isKeyDown(KeyEvent.VK_S)) {
				nextMenuItem();
				this.selectionCooldown.reset();
			}
			if (inputManager.isKeyDown(KeyEvent.VK_SPACE))
				this.isRunning = false;

            // add code 76 - 90
            // When mouse click input comes in, return code accordingly
            if (inputManager.isMouseClicked()) {
                int temp_x = inputManager.getMouseX();
                int temp_y = inputManager.getMouseY();

                java.awt.Rectangle[] boxes = drawManager.getMenuHitboxes(this);
                int[] pos = {2, 3, 0};

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
	 * Shifts the focus to the next menu item.
	 */
	private void nextMenuItem() {
		if (this.returnCode == 3)
			this.returnCode = 0;
		else if (this.returnCode == 0)
			this.returnCode = 2;
		else
			this.returnCode++;
	}

	/**
	 * Shifts the focus to the previous menu item.
	 */
	private void previousMenuItem() {
		if (this.returnCode == 0)
			this.returnCode = 3;
		else if (this.returnCode == 2)
			this.returnCode = 0;
		else
			this.returnCode--;
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
			newHover = 2;
		if(boxesForHover[1].contains(mx, my))
			newHover = 3;
		if(boxesForHover[2].contains(mx, my))
			newHover = 0;

		if (newHover != null && !newHover.equals(this.returnCode)){
			this.returnCode = newHover;
		}

		//pass hoverOption for menu highlights respond to mouse hover
		drawManager.drawTitle(this);
		drawManager.drawMenu(this, this.returnCode, hoverOption);

		drawManager.completeDrawing(this);
	}
}