package entity;

import java.awt.Color;
import java.util.logging.Logger;

import engine.Core;
import engine.DrawManager.SpriteType;

import engine.GameState;
import engine.ItemDB;
import engine.ItemData;
import engine.ItemEffect;

/**
 * Implements Item that moves vertically down.
 */
public class Item extends Entity {

    /** Logger instance for logging purposes. */
    private Logger logger;

    /** Type of Item. */
    private String type;

    /** Item Movement Speed. */
    private int itemSpeed;

    /**
     * Constructor, establishes the Item's properties.
     *
     * @param itemType
     *            Type of Item being spawned
     *
     * @param positionX
     *            Initial position of the Item in the X axis.
     * @param positionY
     *            Initial position of the Item in the Y axis.
     * @param speed
     *            Speed of the Item, positive or negative depending on
     *            direction - positive is down.
     */

    public Item(String itemType, final int positionX, final int positionY, final int speed) {

        super(positionX, positionY, 3 * 2, 5 * 2, Color.WHITE);

        logger = Core.getLogger();

        this.type = itemType;
        this.itemSpeed = speed;

        setSprite();
    }

    /**
     * Setter for the sprite of the Item using data from ItemDB.
     */
    public final void setSprite() {
        ItemDB itemDB = new ItemDB();
        ItemData data = itemDB.getItemData(this.type);

        if (data != null) {
            try {
                this.spriteType = SpriteType.valueOf(data.getSpriteType());
            } catch (IllegalArgumentException e) {
                this.spriteType = SpriteType.ItemScore; // fallback
                this.logger.warning("[Item]: Unknown sprite type in ItemDB: " + data.getSpriteType() + ", using default.");
            }
        } else {
            this.spriteType = SpriteType.ItemScore;
        }
    }

    /**
     * Updates the Item's position.
     */
    public final void update() {
        this.positionY += this.itemSpeed;
    }

    /**
     * Applies the effect of the Item to the player.
     *
     * @param gameState
     *            current game state instance.
     * @param playerId
     *            ID of the player to apply the effect to.
     */
    public boolean applyEffect(final GameState gameState, final int playerId) {
        ItemDB itemDB = new ItemDB();
        ItemData data = itemDB.getItemData(this.type);

        if (data == null) return false;

        int value = data.getEffectValue();
        int duration = data.getEffectDuration();
        int cost = data.getCost();

        boolean applied = false;
        /* item data always true to apply because free
        * duration item will apply if enough coins*/
        switch (this.type) {
            case "COIN":
                ItemEffect.applyCoinItem(gameState, playerId, value);
                applied = true;
                break;
            case "HEAL":
                ItemEffect.applyHealItem(gameState, playerId, value);
                applied = true;
                break;
            case "SCORE":
                ItemEffect.applyScoreItem(gameState, playerId, value);
                applied = true;
                break;
            case "TRIPLESHOT":
                applied = ItemEffect.applyTripleShot(gameState, playerId, value, duration,cost);
                break;
            case "SCOREBOOST":
                applied = ItemEffect.applyScoreBoost(gameState, playerId, value, duration,cost);
                break;
            case "BULLETSPEEDUP":
                applied = ItemEffect.applyBulletSpeedUp(gameState, playerId, value, duration, cost);
                break;
            default:
                this.logger.warning("[Item]: No ItemEffect for type " + this.type);
                applied = false;
                break;
        }
        if (!applied) {
            // Player couldn't afford the item (or other failure).
            logger.info("[Item]: Player " + playerId + " couldn't afford " + this.type + " (cost=" + cost + ")");
        }

        return applied;
    };

    /**
     * Setter of the speed of the Item.
     *
     * @param itemSpeed
     *            New speed of the Item.
     */
    public final void setItemSpeed(final int itemSpeed) {
        this.itemSpeed = itemSpeed;
    }

    /**
     * Getter for Item Movement Speed.
     *
     * @return speed of the Item.
     */
    public final int getItemSpeed() {
        return this.itemSpeed;
    }

    /**
     * Reset the Item.
     * Set the item type and sprite to newType, and the speed to 0.
     *
     * @param newType
     *            new type of the Item.
     */
    public final void reset(String newType) {
        this.type = newType;
        this.itemSpeed = 0;
        setSprite(); // change to your enum if different
    }

    /**
     * Getter for the speed of the Item.
     *
     * @return type of the Item.
     */
    public final String getType() {
        return this.type;
    }
}