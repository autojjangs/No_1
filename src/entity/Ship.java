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

    private GameState gameState;

    /** Time between shots. */
    private int SHOOTING_INTERVAL = 750; // 750 is base shooting interval.
    /** Speed of the bullets shot by the ship. */
    private static final int BULLET_SPEED = -6;
    /** Movement of the ship for each unit of time. */
    private int speed = 2;

    /** Types of ships. */
    public enum ShipType {
        NORMAL, // Bullet size is normal, and moving speed is normal.
        BIG_SHOT, // Bullet size is big, but moving speed is slow.
        DOUBLE_SHOT, // Double shot, but moving speed is slow.
        MOVE_FAST // Moving speed is fast, but fire rate is slow.
    }

    /** Current ship type. */
    private ShipType type;
    /** Minimum time between shots. */
    private Cooldown shootingCooldown;
    /** Time spent inactive between hits. */
    private Cooldown destructionCooldown;

    // 2P mode: id number to specifying which player this ship belongs to - 0 =
    // unknown, 1 = P1, 2 = P2
    private int playerId = 0; // 0 = unknown, 1 = P1, 2 = P2
    private int bulletWidth = 3 * 2; // 3 * 2 is base bullet width.
    private int bulletHeight = 5 * 2; // 5 * 2 is base bullet height.

    /**
     * Constructor, establishes the ship's properties.
     *
     * @param positionX
     *                  Initial position of the ship in the X axis.
     * @param positionY
     *                  Initial position of the ship in the Y axis.
     * @param gameState
     *                  Gamestate
     */
    public Ship(final int positionX, final int positionY, GameState gameState) {
        super(positionX, positionY, 13 * 2, 8 * 2, Color.GREEN);

        this.gameState = gameState;
        this.spriteType = SpriteType.Ship;
        this.shootingCooldown = Core.getCooldown(SHOOTING_INTERVAL);
        this.destructionCooldown = Core.getCooldown(1000);
    }

    // 2P mode: create and tag with a team in one shot
    public Ship(final int positionX, final int positionY, final Team team, GameState gameState) {
        this(positionX, positionY, gameState);
        this.setTeam(team); // uses Entity.setTeam
        this.playerId = (team == Team.PLAYER1 ? 1 : team == Team.PLAYER2 ? 2 : 0);
    }

    // 2P mode: create and tag with a team in one shot
    /**
     * Constructor, establishes the ship's properties.
     *
     * @param positionX
     *                  Initial position of the ship in the X axis.
     * @param positionY
     *                  Initial position of the ship in the Y axis.
     * @param team
     *                  Team of player ID(Player1 or Player2)
     * @param type
     *                  Player ship type(NORMAL, BIG_SHOT, DOUBLE_SHOT, MOVE_FAST)
     */
    public Ship(final int positionX, final int positionY, final Team team, final ShipType type) {
        super(positionX, positionY, 13 * 2, 8 * 2, Color.GREEN);

        this.spriteType = SpriteType.Ship;
        this.type = type;

        switch (this.type) {
            case BIG_SHOT: // Big bullet type
                this.speed = 1; // Move slowly
                this.SHOOTING_INTERVAL = 750; // Fire rate is normal.
                this.bulletWidth = 3 * 3; // Bullet size is big.
                this.bulletHeight = 5 * 3; // Bullet size is big.
                break;
            case DOUBLE_SHOT: // Double shot type
                this.speed = 1; // Move slowly
                this.SHOOTING_INTERVAL = 750; // Fire rate is normal.
                this.bulletWidth = 3 * 2; // Bullet size is normal.
                this.bulletHeight = 5 * 2; // Bullet size is normal.
                break;
            case MOVE_FAST: // Move fast type
                this.speed = 3; // Move Fast
                this.SHOOTING_INTERVAL = 900; // Fire rate is slow.
                this.bulletWidth = 3 * 2; // Bullet size is normal.
                this.bulletHeight = 5 * 2; // Bullet size is normal.
                break;
            case NORMAL: // Normal type
            default:
                this.speed = 2; // Move Normally
                this.SHOOTING_INTERVAL = 750; // Fire rate is normal.
                this.bulletWidth = 3 * 2; // Bullet size is normal.
                this.bulletHeight = 5 * 2; // Bullet size is normal.
                break;
        }

        this.shootingCooldown = Core.getCooldown(this.SHOOTING_INTERVAL);
        this.destructionCooldown = Core.getCooldown(1000);
        this.setTeam(team);
        this.playerId = (team == Team.PLAYER1 ? 1 : team == Team.PLAYER2 ? 2 : 0);
    }

    /**
     * Moves the ship speed uni ts right, or until the right screen border is
     * reached.
     */
    public final void moveRight() {
        this.positionX += this.speed;
    }

    /**
     * Moves the ship speed units left, or until the left screen border is
     * reached.
     */
    public final void moveLeft() {
        this.positionX -= this.speed;
    }

    /**
     * Shoots a bullet upwards.
     *
     * @param bullets
     *                List of bullets on screen, to add the new bullet.
     * @return Checks if the bullet was shot correctly.
     */
    public final boolean shoot(final Set<Bullet> bullets) {
        if (this.shootingCooldown.checkFinished()) {
            this.shootingCooldown.reset();
            Core.getLogger().info("[Ship] Shooting :" + this.type);

            int bulletX = positionX + this.width / 2;
            int bulletY = this.positionY - bulletHeight;

            // Check for TRIPLESHOT item effect first
            if (gameState != null && gameState.hasEffect(playerId - 1, TRIPLESHOT)) {
                Core.getLogger().info("[Ship] Item effect: TRIPLESHOT");
                int bulletOffset = 50;

                // Center bullet
                Bullet center = BulletPool.getBullet(bulletX, bulletY, BULLET_SPEED,
                        bulletWidth, bulletHeight, this.getTeam());
                center.setOwnerPlayerId(this.getPlayerId());
                bullets.add(center);

                // Left bullet
                Bullet left = BulletPool.getBullet(bulletX - bulletOffset, bulletY, BULLET_SPEED,
                        bulletWidth, bulletHeight, this.getTeam());
                left.setOwnerPlayerId(this.getPlayerId());
                bullets.add(left);

                // Right bullet
                Bullet right = BulletPool.getBullet(bulletX + bulletOffset, bulletY, BULLET_SPEED,
                        bulletWidth, bulletHeight, this.getTeam());
                right.setOwnerPlayerId(this.getPlayerId());
                bullets.add(right);

                return true;
            }

            // default shooting
            switch (this.type) {
                case DOUBLE_SHOT:
                    int offset = 6;
                    // left bullet fire
                    Bullet b1 = BulletPool.getBullet(bulletX - offset, bulletY, BULLET_SPEED,
                            bulletWidth, bulletHeight, this.getTeam());
                    b1.setOwnerPlayerId(this.getPlayerId());
                    bullets.add(b1);

                    // right bullet fire
                    Bullet b2 = BulletPool.getBullet(bulletX + offset, bulletY, BULLET_SPEED,
                            bulletWidth, bulletHeight, this.getTeam());
                    b2.setOwnerPlayerId(this.getPlayerId());
                    bullets.add(b2);
                    break;
                case BIG_SHOT:
                case MOVE_FAST:
                case NORMAL:
                default:
                    // normal type
                    Bullet b = BulletPool.getBullet(positionX + this.width / 2, bulletY, BULLET_SPEED, bulletWidth, bulletHeight, this.getTeam());
                    b.setOwnerPlayerId(this.getPlayerId());
                    bullets.add(b);
                    break;
            }
            return true;
        }
        return false;
    }

    /**
     * Updates status of the ship.
     */
    public final void update() {
        if (!this.destructionCooldown.checkFinished())
            this.spriteType = SpriteType.ShipDestroyed;
        else
            this.spriteType = SpriteType.Ship;
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
    public final int getSpeed() {
        return this.speed;
    }

    // 2P mode: adding playerId getter and setter
    public final int getPlayerId() {
        return this.playerId;
    }

    public void setPlayerId(int id) {
        this.playerId = id;
    }

}