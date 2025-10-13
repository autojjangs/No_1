package screen;

import engine.Cooldown;
import engine.Core;

import java.awt.event.KeyEvent;

public class SettingScreen extends Screen {
    private Cooldown inputCooldown;
    private int volumelevel;
    /**
     * Constructor, establishes the properties of the screen.
     *
     * @param width  Screen width.
     * @param height Screen height.
     * @param fps    Frames per second, frame rate at which the game is run.
     */
    public SettingScreen(final int width, final int height, final int fps) {
        super(width, height, fps);

        this.returnCode = 1;
    }
    public final void initialize(){
        super.initialize();
        this.inputCooldown = Core.getCooldown(100);
        this.inputCooldown.reset();
        this.volumelevel = 50;
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
        if(this.inputCooldown.checkFinished()){
            if(inputManager.isKeyDown(KeyEvent.VK_LEFT)&&volumelevel > 0){
                this.volumelevel--;
                this.inputCooldown.reset();
            }
            if(inputManager.isKeyDown(KeyEvent.VK_RIGHT)&& volumelevel < 100){
                this.volumelevel++;
                this.inputCooldown.reset();
            }
        }
        if (inputManager.isKeyDown(java.awt.event.KeyEvent.VK_SPACE)
                && this.inputDelay.checkFinished())
            this.isRunning = false;
        draw();
    }

    /**
     * Draws the elements associated with the screen.
     */
    private void draw() {
        drawManager.initDrawing(this);
        drawManager.drawVolumeBar(this,volumelevel);
        drawManager.drawSettingMenu(this);
        drawManager.completeDrawing(this);
    }

}
