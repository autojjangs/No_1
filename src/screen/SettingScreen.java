package screen;

import engine.Cooldown;
import engine.Core;
import engine.SoundManager;
import java.awt.event.KeyEvent;

public class SettingScreen extends Screen {
    private static final int volumeMenu = 0;
    private static final int firstplayerMenu = 1;
    private static final int secondplayerMenu= 2;
    private static final int back = -1;
    private final String[] menuItem = {"Volume", "1P Keyset", "2P Keyset"};
    private int selectMenuItem;
    private Cooldown inputCooldown;
    private int volumelevel;
    private boolean draggingVolume = false;
    private int selectedSection = 0;
    private int selectedKeyIndex = 0;
    private String[] keyItems = {"MOVE LEFT", "MOVE RIGHT", "ATTACK"};
    private boolean[] keySelected = {false, false, false};
    private boolean waitingForNewKey = false;
    private int[] player1Keys;
    private int[] player2Keys;

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
        // Import key arrangement and save it to field
        this.player1Keys = Core.getInputManager().getPlayer1Keys();
        this.player2Keys = Core.getInputManager().getPlayer2Keys();
        
        // Start menu music loop when the settings screen is created
        SoundManager.playLoop("sound/menu_sound.wav");
    }

    private void setVolumeFromX(java.awt.Rectangle barBox, int mouseX) {
        double ratio = (double)(mouseX - barBox.x) / (double)barBox.width;
        ratio = Math.max(0.0, Math.min(1.0, ratio));
        this.volumelevel = (int)Math.round(ratio * 100.0);
        Core.setVolumeLevel(this.volumelevel);
        // Update volume of currently playing sounds
        SoundManager.updateVolume();
    }

    /**
     * Starts the action.
     *
     * @return Next screen code.
     */
    public final void initialize(){
        super.initialize();
        this.inputCooldown = Core.getCooldown(200);
        this.inputCooldown.reset();
        this.selectMenuItem = volumeMenu;
        this.volumelevel = Core.getVolumeLevel();
    }
    public final int run(){
        super.run();
        // Stop menu music when leaving the settings screen
        SoundManager.stop();

        return this.returnCode;
    }

    /**
     * Updates the elements on screen and checks for events.
     */
    protected final void update() {
        super.update();

        if(inputManager.isKeyDown(KeyEvent.VK_UP)&&this.inputCooldown.checkFinished() && this.selectedSection == 0) {
            if(this.selectMenuItem == back) {
                this.selectMenuItem = menuItem.length - 1;
            } else if(this.selectMenuItem == 0){
                this.selectMenuItem = back;
            } else {
                this.selectMenuItem --;
            }
            this.inputCooldown.reset();
        }

        if(inputManager.isKeyDown(KeyEvent.VK_DOWN)&&this.inputCooldown.checkFinished() && this.selectedSection == 0) {
            if(this.selectMenuItem == back){
                this.selectMenuItem = 0;
            }else if (this.selectMenuItem == menuItem.length - 1) {
                this.selectMenuItem = back;
            }else {
                this.selectMenuItem ++;
            }
            this.inputCooldown.reset();
        }

        if(this.selectMenuItem == volumeMenu) {
             if(this.inputCooldown.checkFinished()) {
                 if (inputManager.isKeyDown(KeyEvent.VK_LEFT) && volumelevel > 0) {
                     this.volumelevel--;
                     Core.setVolumeLevel(this.volumelevel);
                     SoundManager.updateVolume();
                     this.inputCooldown.reset();
                 }
                 if (inputManager.isKeyDown(KeyEvent.VK_RIGHT) && volumelevel < 100) {
                     this.volumelevel++;
                     Core.setVolumeLevel(this.volumelevel);
                     SoundManager.updateVolume();
                     this.inputCooldown.reset();
                 }
             }
        }
        /**
         * Change key settings
         */
         else if (this.selectMenuItem == firstplayerMenu || this.selectMenuItem == secondplayerMenu) {
             if (inputManager.isKeyDown(KeyEvent.VK_RIGHT) && this.inputCooldown.checkFinished() && waitingForNewKey == false) {
                 this.selectedSection= 1;
                 this.selectedKeyIndex = 0;
                 this.inputCooldown.reset();
             }
             if (this.selectedSection == 1 && inputManager.isKeyDown(KeyEvent.VK_LEFT) && this.inputCooldown.checkFinished() && waitingForNewKey == false) {
                 selectedSection = 0;
                 this.inputCooldown.reset();
             }
             if (this.selectedSection == 1 & inputManager.isKeyDown(KeyEvent.VK_UP) && this.inputCooldown.checkFinished() && selectedKeyIndex > 0 && waitingForNewKey == false) {
                 selectedKeyIndex--;
                 this.inputCooldown.reset();
             }
             if (this.selectedSection == 1 && inputManager.isKeyDown(KeyEvent.VK_DOWN) && this.inputCooldown.checkFinished() && selectedKeyIndex < keyItems.length - 1 && waitingForNewKey == false) {
                 selectedKeyIndex++;
                 this.inputCooldown.reset();
             }
             // Start waiting for new keystrokes
            if (this.selectedSection == 1 && inputManager.isKeyDown(KeyEvent.VK_SPACE) && this.inputCooldown.checkFinished() && waitingForNewKey == false) {
                keySelected[selectedKeyIndex] = !keySelected[selectedKeyIndex];

                if (keySelected[selectedKeyIndex]) {
                    waitingForNewKey = true;
                } else {
                    waitingForNewKey = false;
                }

                this.inputCooldown.reset();
            }
            /**
             * check duplicate and exception when new key is pressed, and save as new key if valid
             */
            if (waitingForNewKey) {
                int newKey = inputManager.getLastPressedKey();
                if (newKey != -1 && this.inputCooldown.checkFinished()) {
                    // exception of esc key and backspace key
                    if (newKey == KeyEvent.VK_ESCAPE || newKey == KeyEvent.VK_BACK_SPACE) {
                        System.out.println("Key setting change cancelled : " + KeyEvent.getKeyText(newKey) + " input");
                        keySelected[selectedKeyIndex] = false;
                        waitingForNewKey = false;
                        this.inputCooldown.reset();
                        return;
                    }
                    // Check duplicate keys
                    int[] targetKeys = (this.selectMenuItem == firstplayerMenu)
                            ? player1Keys : player2Keys;
                    int[] otherKeys = (this.selectMenuItem == firstplayerMenu)
                            ? player2Keys : player1Keys;

                    boolean duplicate = false;

                    for (int i = 0; i < targetKeys.length; i++) {
                        if (i != selectedKeyIndex && targetKeys[i] == newKey) {
                            duplicate = true;
                            System.out.println("Key already in use:" + KeyEvent.getKeyText(newKey));
                            break;
                        }

                        if (otherKeys[i] == newKey) {
                            duplicate = true;
                            System.out.println("Key already in use:" + KeyEvent.getKeyText(newKey));
                            break;
                        }
                    }

                    if (duplicate) {
                        keySelected[selectedKeyIndex] = false;
                        waitingForNewKey = false;
                        this.inputCooldown.reset();
                        return;
                    }
                    // key assignment entered and save to keyconfig
                    if (this.selectMenuItem == firstplayerMenu) {
                        player1Keys[selectedKeyIndex] = newKey;
                        Core.getInputManager().setPlayer1Keys(player1Keys);
                    } else {
                        player2Keys[selectedKeyIndex] = newKey;
                        Core.getInputManager().setPlayer2Keys(player2Keys);
                    }

                    keySelected[selectedKeyIndex] = false;
                    waitingForNewKey = false;
                    Core.getInputManager().saveKeyConfig();
                    System.out.println("New key saved → " + KeyEvent.getKeyText(newKey));
                    this.inputCooldown.reset();
                }
            }
         }

         // change space to escape
         if (inputManager.isKeyDown(KeyEvent.VK_ESCAPE) && this.inputCooldown.checkFinished()) {
            this.isRunning = false;
            this.inputCooldown.reset();
        }

        // make mouse work on volume bar
        int mx = inputManager.getMouseX();
        int my = inputManager.getMouseY();
        boolean pressed = inputManager.isMousePressed();
        boolean clicked = inputManager.isMouseClicked();

        java.awt.Rectangle backBox = drawManager.getBackButtonHitbox(this);
        java.awt.Rectangle barBox  = drawManager.getVolumeBarHitbox(this);

        if (clicked && backBox.contains(mx, my)) {
            this.returnCode = 1;
            this.isRunning = false;
            return;
        }

        if (!draggingVolume && pressed && barBox.contains(mx, my)) {
            draggingVolume = true;
            setVolumeFromX(barBox, mx);
        }

        if (draggingVolume && pressed) {
            setVolumeFromX(barBox, mx);
        }

        if (!pressed) {
            draggingVolume = false;
        }

        if (inputManager.isKeyDown(KeyEvent.VK_SPACE) && this.inputCooldown.checkFinished()) {
            if (this.selectMenuItem == back) {
                this.returnCode = 1;
                this.isRunning = false;
                return;
            }
            this.inputCooldown.reset();
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
                drawManager.drawVolumeBar(this,this.volumelevel, this.draggingVolume);
                break;
            case firstplayerMenu:
                drawManager.drawKeysettings(this, 1, this.selectedSection, this.selectedKeyIndex, this.keySelected,this.player1Keys);
                break;
            case secondplayerMenu:
                drawManager.drawKeysettings(this, 2,  this.selectedSection, this.selectedKeyIndex, this.keySelected, this.player2Keys);
                break;
        }

        // hover highlight
        int mx = inputManager.getMouseX();
        int my = inputManager.getMouseY();
        java.awt.Rectangle backBox = drawManager.getBackButtonHitbox(this);

        boolean backHover = backBox.contains(mx, my);
        boolean backSelected = (this. selectMenuItem == back);
        drawManager.drawBackButton(this, backHover || backSelected);

        drawManager.completeDrawing(this);
    }

}
