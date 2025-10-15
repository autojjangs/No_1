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
    private int selectedSection = 0; // 0: 왼쪽 메뉴, 1: 오른쪽 키 설정 영역
    private int selectedKeyIndex = 0;
    private String[] keyItems = {"MOVE LEFT", "MOVE RIGHT", "ATTACK"};
    private boolean[] keySelected = {false, false, false};
    private boolean waitingForNewKey = false;      // 현재 키 변경 대기 상태인지 여부
    private int[] player1Keys; // 초기화 값 제거
    private int[] player2Keys; // 2P 키 배열 필드 추가

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
        this.volumelevel = 50;
    }
    public final int run(){
        super.run();
        return this.returnCode;
    }
    /**
     * Updates the elements on screen and checks for events.
     */
    protected final void update() {
        super.update();

        if(inputManager.isKeyDown(KeyEvent.VK_UP)&&this.inputCooldown.checkFinished()&&this.selectMenuItem>0) {
            if(this.selectedSection == 0) {
                this.selectMenuItem--;
                this.inputCooldown.reset();
            }
        }
        if(inputManager.isKeyDown(KeyEvent.VK_DOWN)&&this.inputCooldown.checkFinished()&&this.selectMenuItem<menuItem.length-1) {
            if(this.selectedSection == 0) {
                this.selectMenuItem++;
                this.inputCooldown.reset();
            }
        }

        if(this.selectMenuItem == volumeMenu) {
             if(this.inputCooldown.checkFinished()) {
                 if (inputManager.isKeyDown(KeyEvent.VK_LEFT) && volumelevel > 0) {
                     this.volumelevel--;
                     this.inputCooldown.reset();
                 }
                 if (inputManager.isKeyDown(KeyEvent.VK_RIGHT) && volumelevel < 100) {
                     this.volumelevel++;
                     this.inputCooldown.reset();
                 }
             }
        }
        /**
         * Change key settings
         */
         else if (this.selectMenuItem == firstplayerMenu || this.selectMenuItem == secondplayerMenu) {
             if (inputManager.isKeyDown(KeyEvent.VK_RIGHT) && this.inputCooldown.checkFinished()) {
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
                    // exception of esc key
                    if (newKey == KeyEvent.VK_ESCAPE) {
                        System.out.println("Key setting change cancelled: ESC input");
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
                drawManager.drawVolumeBar(this,this.volumelevel);
                break;
            case firstplayerMenu:
                drawManager.drawKeysettings(this, 1, this.selectedSection, this.selectedKeyIndex, this.keySelected,this.player1Keys);
                break;
            case secondplayerMenu:
                drawManager.drawKeysettings(this, 2,  this.selectedSection, this.selectedKeyIndex, this.keySelected, this.player2Keys);
                break;
        }
        drawManager.completeDrawing(this);
    }

}
