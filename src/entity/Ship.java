package entity;

import java.awt.Color;
import java.util.Set;

import engine.Cooldown;
import engine.Core;
import engine.GameState;
import engine.DrawManager.SpriteType;

import static engine.ItemEffect.ItemEffectType.*;

/**
 * Implements a ship, to be controlled by the player.
 *
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 *
 */
public class Ship extends Entity {

    /** Bullet Variables **/
    //default bullet variables
    private static final int BASE_BULLET_SPEED = -6;
    private static final int BASE_SHOOTING_INTERVAL = 750;
    private static final int BASE_BULLET_WIDTH = 6;  // 3 * 2
    private static final int BASE_BULLET_HEIGHT = 10;
    // special bullet variables
    private static final int DOUBLE_SHOT_OFFSET = 6;

    /** Ship Variables **/
    private static final int BASE_SPEED = 2;
    private static final int SHIP_WIDTH = 26;  // 13 * 2
    private static final int SHIP_HEIGHT = 16;
    private static final int DESTRUCTION_COOLDOWN = 1000;

    /** Types of ships. */
    public enum ShipType {
        NORMAL,         // Bullet size is normal, and moving speed is normal.
        BIG_SHOT,       // Bullet size is big, but moving speed is slow.
        DOUBLE_SHOT,    // Double shot, but moving speed is slow.
        MOVE_FAST       // Moving speed is fast, but fire rate is slow.
    }

    /** Game state and Ship type **/
    private GameState gameState;
    private ShipType type;

    // Ship properties (vary by type)
    private int moveSpeed = BASE_SPEED;
    private int bulletSpeed = BASE_BULLET_SPEED;
    private int shootingInterval = BASE_SHOOTING_INTERVAL;
    private int bulletWidth = BASE_BULLET_WIDTH;
    private int bulletHeight = BASE_BULLET_HEIGHT;

    /** Cooldowns */
    private Cooldown shootingCooldown;
    private Cooldown destructionCooldown;

    // Identify player in index: 0 = P1, 1 = P2
    private int playerIndex = 0;

    private int Y;
    private int hits;

    /**
     * Constructor, establishes the ship's properties.
     *
     * @param positionX
     *                  Initial position of the ship in the X axis.
     * @param positionY
     *                  Initial position of the ship in the Y axis.
     * @param team
     *                  Player team (null defaults to PLAYER1)
     * @param type
     *                  Ship type (null defaults to NORMAL)
     * @param gameState
     *                  Game state reference (can be null)
     */
    public Ship(final int positionX, final int positionY, final Team team,
                final ShipType type, final GameState gameState) {
        super(positionX, positionY, SHIP_WIDTH, SHIP_HEIGHT, Color.GREEN);

        this.gameState = gameState;
        this.type = (type != null) ? type : ShipType.NORMAL;
        this.spriteType = SpriteType.Ship1;

        initializeShipProperties(this.type);

        this.shootingCooldown = Core.getCooldown(this.shootingInterval);
        this.destructionCooldown = Core.getCooldown(DESTRUCTION_COOLDOWN);

        // apply entity
        Team playerID = (team != null) ? team : Team.PLAYER1;
        this.setTeam(playerID);
        this.playerIndex = (playerID == Team.PLAYER1) ? 0 : (playerID == Team.PLAYER2) ? 1 : 0;

        this.Y = positionY;
        this.hits = 0;
    }

    /**
     * Initializes ship properties based on ship type.
     *
     * @param type Ship type to configure
     */
    private void initializeShipProperties(final ShipType type) {
        this.bulletSpeed = BASE_BULLET_SPEED;

        switch (type) {
            case BIG_SHOT:
                this.moveSpeed -= 1;
                this.bulletWidth = 9;  // 3 * 3
                this.bulletHeight = 15; // 5 * 3
                this.spriteType = SpriteType.Ship2;
                break;
            case DOUBLE_SHOT:
                this.moveSpeed -= 1;
                this.spriteType = SpriteType.Ship3;
                break;
            case MOVE_FAST:
                this.moveSpeed += 1;
                this.shootingInterval += 150;
                this.spriteType = SpriteType.Ship4;
                break;
            case NORMAL:
            default:
                break;
        }
    }

    /**
     * Moves the ship speed uni ts right, or until the right screen border is reached.
     */
    public final void moveRight() { this.positionX += this.moveSpeed; }

    /**
     * Moves the ship speed units left, or until the left screen border is reached.
     */
    public final void moveLeft() {
        this.positionX -= this.moveSpeed;
    }

    /**
     * Shoots a bullet based on ship type and active effects.
     *
     * @param bullets
     *                List of bullets on screen, to add the new bullet.
     * @return
     *                True if shooting was successful, false if on cooldown
     */
    public final boolean shoot(final Set<Bullet> bullets) {

        if (!this.shootingCooldown.checkFinished()) { return false; }

        this.shootingCooldown.reset();
        Core.getLogger().info("[Ship] Shooting :" + this.type);

        int bulletX = positionX + this.width / 2;
        int bulletY = this.positionY - this.bulletHeight;

        if (hasTripleShotEffect()) {
            shootTripleShot(bullets, bulletX, bulletY);
            return true;
        }

        // Default shooting based on ship type
        shootBasedOnType(bullets, bulletX, bulletY);
        return true;
    }

    /**
     * Updates status of the ship.
     */
    public final void update() {
        if (!this.destructionCooldown.checkFinished())
            switch (this.spriteType) {
                case Ship1 -> this.spriteType = SpriteType.ShipDestroyed1;
                case Ship2 -> this.spriteType = SpriteType.ShipDestroyed2;
                case Ship3 -> this.spriteType = SpriteType.ShipDestroyed3;
                case Ship4 -> this.spriteType = SpriteType.ShipDestroyed4;
            }
        else
            switch (this.spriteType) {
                case ShipDestroyed1 -> this.spriteType = SpriteType.Ship1;
                case ShipDestroyed2 -> this.spriteType = SpriteType.Ship2;
                case ShipDestroyed3 -> this.spriteType = SpriteType.Ship3;
                case ShipDestroyed4 -> this.spriteType = SpriteType.Ship4;
        }
    }

    /**
     * Switches the ship to its destroyed state.
     */
    public final void destroy() {
        this.destructionCooldown.reset();
    }

    /**
     * Checks if the ship is destroyed.
     *
     * @return True if the ship is currently destroyed.
     */
    public final boolean isDestroyed() {
        return !this.destructionCooldown.checkFinished();
    }

    /**
     * Getter for the ship's speed.
     *
     * @return Speed of the ship.
     */
    public final int getSpeed() { return this.moveSpeed; }

    // 2P mode: adding playerIndex getter and setter
    public final int getPlayerId() { return this.playerIndex + 1; }

    public void setPlayerId(int id) { this.playerIndex = id -1; }


    /**
     * Fires bullets based on ship type.
     */
    private void shootBasedOnType(final Set<Bullet> bullets, final int centerX, final int bulletY) {
        switch (this.type) {
            case DOUBLE_SHOT:
                addBullet(bullets, centerX - DOUBLE_SHOT_OFFSET, bulletY);
                addBullet(bullets, centerX + DOUBLE_SHOT_OFFSET, bulletY);
                break;
            case BIG_SHOT:
            case MOVE_FAST:
            case NORMAL:
            default:
                addBullet(bullets, centerX, bulletY);
                break;
        }
    }

    /**
     * Creates and adds a bullet to the game.
     */
    private void addBullet(final Set<Bullet> bullets, final int x, final int y) {
        int speedMultiplier = getBulletSpeedMultiplier();
        int currentBulletSpeed = this.bulletSpeed * speedMultiplier;

        Bullet bullet = BulletPool.getBullet(x, y, currentBulletSpeed,
                this.bulletWidth, this.bulletHeight, this.getTeam());
        bullet.setOwnerPlayerId(this.getPlayerId());
        bullets.add(bullet);
    }

    /** ========================= Item Effect check ========================= **/

    /**
     * Checks if player has effect active
     *
     * @return
     *                              list of active effects
     */
    private boolean hasTripleShotEffect() {
        return gameState != null && gameState.hasEffect(playerIndex, TRIPLESHOT);
    }

    private int getBulletSpeedMultiplier() {
        if (gameState == null) return 1;

        Integer effectValue = gameState.getEffectValue(playerIndex, BULLETSPEEDUP);
        if (effectValue != null) {
            Core.getLogger().info("[Ship] Item effect: Faster Bullets");
            return effectValue;
        }
        return 1;
    }

    public void addHit(){
        this.hits++;
    }

    /**
     * TRIPLESHOT effect
     */
    private void shootTripleShot(final Set<Bullet> bullets, final int centerX, final int bulletY) {
        Core.getLogger().info("[Ship] Item effect: TRIPLESHOT");
        Integer TRIPLE_SHOT_OFFSET = gameState.getEffectValue(playerIndex, TRIPLESHOT);

        addBullet(bullets, centerX, bulletY);
        addBullet(bullets, centerX - TRIPLE_SHOT_OFFSET, bulletY);
        addBullet(bullets, centerX + TRIPLE_SHOT_OFFSET, bulletY);
    }
}