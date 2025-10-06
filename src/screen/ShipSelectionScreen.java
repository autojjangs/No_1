package screen;

import java.awt.event.KeyEvent;
import engine.Cooldown;
import engine.Core;
import entity.Entity;
import entity.Ship;

public class ShipSelectionScreen extends Screen {

    private static final int SELECTION_TIME = 200;
    private Cooldown selectionCooldown;
    private int selectedShipIndex = 0; // 0: NORMAL, 1: BIG_SHOT, 2: DOUBLE_SHOT, 3: MOVE_FAST
    private Ship[] shipExamples = new Ship[4];

    private String screenTitle;

    public ShipSelectionScreen(final int width, final int height, final int fps, final int player) {
        super(width, height, fps);
        this.screenTitle = "PLAYER " + player + " : CHOOSE YOUR SHIP";
        this.selectionCooldown = Core.getCooldown(SELECTION_TIME);
        this.selectionCooldown.reset();

        // 화면에 보여줄 예시 우주선 생성
        if (player == 1) {
            shipExamples[0] = new Ship(width / 2 - 100, height / 2, Entity.Team.PLAYER1, Ship.ShipType.NORMAL);
            shipExamples[1] = new Ship(width / 2 - 35, height / 2, Entity.Team.PLAYER1, Ship.ShipType.BIG_SHOT);
            shipExamples[2] = new Ship(width / 2 + 35, height / 2, Entity.Team.PLAYER1, Ship.ShipType.DOUBLE_SHOT);
            shipExamples[3] = new Ship(width / 2 + 100, height / 2, Entity.Team.PLAYER1, Ship.ShipType.MOVE_FAST);
        } else if (player == 2) {
            shipExamples[0] = new Ship(width / 2 - 100, height / 2, Entity.Team.PLAYER2, Ship.ShipType.NORMAL);
            shipExamples[1] = new Ship(width / 2 - 35, height / 2, Entity.Team.PLAYER2, Ship.ShipType.BIG_SHOT);
            shipExamples[2] = new Ship(width / 2 + 35, height / 2, Entity.Team.PLAYER2, Ship.ShipType.DOUBLE_SHOT);
            shipExamples[3] = new Ship(width / 2 + 100, height / 2, Entity.Team.PLAYER2, Ship.ShipType.MOVE_FAST);
        }
    }

    /**
     * Returns the selected ship type to Core.
     *
     * @return The selected ShipType enum.
     */
    public Ship.ShipType getSelectedShipType() {
        switch (this.selectedShipIndex) {
            case 1:
                return Ship.ShipType.BIG_SHOT;
            case 2:
                return Ship.ShipType.DOUBLE_SHOT;
            case 3:
                return Ship.ShipType.MOVE_FAST;
            case 4:
            default:
                return Ship.ShipType.NORMAL;
        }
    }

    public final int run() {
        super.run();
        return this.returnCode;
    }

    protected final void update() {
        super.update();
        draw();
        if (this.selectionCooldown.checkFinished() && this.inputDelay.checkFinished()) {
            if (inputManager.isKeyDown(KeyEvent.VK_LEFT) || inputManager.isKeyDown(KeyEvent.VK_A)) {
                this.selectedShipIndex = this.selectedShipIndex - 1;
                if (this.selectedShipIndex < 0) {
                    this.selectedShipIndex += 4;
                }
                this.selectedShipIndex = this.selectedShipIndex % 4;
                this.selectionCooldown.reset();
            }
            if (inputManager.isKeyDown(KeyEvent.VK_RIGHT) || inputManager.isKeyDown(KeyEvent.VK_D)) {
                this.selectedShipIndex = (this.selectedShipIndex + 1) % 4;
                this.selectionCooldown.reset();
            }
            if (inputManager.isKeyDown(KeyEvent.VK_SPACE)) {
                this.isRunning = false;
                this.returnCode = 4;
            }
        }
    }

    private void draw() {
        drawManager.initDrawing(this);

        Ship ship = shipExamples[this.selectedShipIndex];
        int centerX = ship.getPositionX();

        // Ship Type Info
        String[] shipNames = {"Normal Type", "Big Shot Type", "Double Shot Type", "Speed Type"};
        String[] shipSpeeds = {"SPEED: NORMAL", "SPEED: SLOW", "SPEED: SLOW", "SPEED: FAST"};
        String[] shipFireRates = {"FIRE RATE: NORMAL", "FIRE RATE: NORMAL", "FIRE RATE: NORMAL", "FIRE RATE: SLOW"};

        for (int i = 0; i < 4; i++) {
            // Draw Player Ship
            drawManager.drawEntity(ship, ship.getPositionX() - ship.getWidth()/2, ship.getPositionY());
        }

        // Draw Selected Player Page Title
        drawManager.drawCenteredBigString(this, this.screenTitle, this.getHeight() / 4);
        // Draw Selected Player Ship Type
        drawManager.drawCenteredRegularString(this, " > " + shipNames[this.selectedShipIndex] + " < ", this.getHeight() / 2 - 40);
        // Draw Selected Player Ship Info
        drawManager.drawCenteredRegularString(shipSpeeds[this.selectedShipIndex], centerX, this.getHeight() / 2 + 60);
        drawManager.drawCenteredRegularString(shipFireRates[this.selectedShipIndex], centerX, this.getHeight() / 2 + 80);

        drawManager.drawCenteredRegularString(this, "Press SPACE to Select", this.getHeight() - 50);

        drawManager.completeDrawing(this);
    }
}