package screen;

public class AchievementScreen extends Screen {
    public AchievementScreen(final int width, final int height, final int fps) {
        super(width, height, fps);

        this.returnCode = 3;
    }

    public final int run() {
        super.run();

        return this.returnCode;
    }

    protected final void update() {
        super.update();
        draw();

        if (inputManager.isKeyDown(java.awt.event.KeyEvent.VK_SPACE) && this.inputDelay.checkFinished()) {
            this.returnCode = 1;
            this.isRunning = false;
        }
    }

    private void draw() {
        drawManager.initDrawing(this);
        drawManager.drawAchievementMenu(this);
        drawManager.completeDrawing(this);
    }
}
