package screen;

import engine.Cooldown;
import engine.Core;
import java.awt.event.KeyEvent;

public class SettingScreen extends Screen {
    private static final int volumeMenu = 0;
    private static final int firstplayerMenu = 1;
    private static final int secondplayerMenu= 2;
    private final String[] menuItem = {"Volume", "1P Keyset", "2P Keyset"};
    private int selectMenuItem;
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
    /**
     * Starts the action.
     *
     * @return Next screen code.
     */
    public final void initialize(){
        super.initialize();
        this.inputCooldown = Core.getCooldown(100);
        this.inputCooldown.reset();
        this.selectMenuItem = volumeMenu;
        this.volumelevel = 50;
    }

    /**
     * Updates the elements on screen and checks for events.
     */
    protected final void update() {
        super.update();

        if(inputManager.isKeyDown(KeyEvent.VK_UP)&&this.inputDelay.checkFinished()&&this.selectMenuItem>0) {
            this.selectMenuItem--;
            this.inputDelay.reset();
        }
        if(inputManager.isKeyDown(KeyEvent.VK_DOWN)&&this.inputDelay.checkFinished()&&this.selectMenuItem<menuItem.length-1) {
            this.selectMenuItem++;
            this.inputDelay.reset();
        }
        if(this.selectMenuItem == volumeMenu) {
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
        }
        if (inputManager.isKeyDown(java.awt.event.KeyEvent.VK_SPACE) && this.inputDelay.checkFinished()) {
            this.isRunning = false;
        }
        draw();
    }

    /**
     * Draws the elements associated with the screen.
     */
    private void draw() {
        drawManager.initDrawing(this);
        drawManager.drawSettingMenu(this);
        drawManager.drawSettingLayout(this, menuItem,this.selectMenuItem);

        switch(this.selectMenuItem) {
            case volumeMenu:
                drawManager.drawVolumeBar(this);
                break;
            case firstplayerMenu:
                //function
                break;
            case secondplayerMenu:
                //function
                break;
        }
        drawManager.completeDrawing(this);
    }

}
