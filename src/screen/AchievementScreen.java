package screen;

import java.awt.event.KeyEvent;
import engine.SoundManager;

public class AchievementScreen extends Screen {
    public AchievementScreen(final int width, final int height, final int fps) {
        super(width, height, fps);

        this.returnCode = 3;
        
        // Start menu music loop when the achievement screen is created
        SoundManager.playLoop("sound/menu_sound.wav");
    }

    public final int run() {
        super.run();
        // Stop menu music when leaving the achievement screen
        SoundManager.stop();

        return this.returnCode;
    }

    protected final void update() {
        super.update();
        draw();

        if (inputManager.isKeyDown(KeyEvent.VK_ESCAPE) && this.inputDelay.checkFinished()) {
            this.returnCode = 1;
            this.isRunning = false;
        }

        // back button click event
        if (inputManager.isMouseClicked()) {
            int mx = inputManager.getMouseX();
            int my = inputManager.getMouseY();
            java.awt.Rectangle backBox = drawManager.getBackButtonHitbox(this);

            if (backBox.contains(mx, my)) {
                this.returnCode = 1;
                this.isRunning = false;
            }
        }
    }

    private void draw() {
        drawManager.initDrawing(this);
        drawManager.drawAchievementMenu(this);

        // hover highlight
        int mx = inputManager.getMouseX();
        int my = inputManager.getMouseY();
        java.awt.Rectangle backBox = drawManager.getBackButtonHitbox(this);

        if (backBox.contains(mx, my)) {
            drawManager.drawBackButton(this, true);
        }

        drawManager.completeDrawing(this);
    }
}
