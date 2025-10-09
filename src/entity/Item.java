package entity;

import java.awt.Color;

import engine.DrawManager.SpriteType;
import engine.GameState;

/**
 * Implements an Item that moves vertically up or down.
 */
public class Item extends Entity {


    public enum ItemType {
        Coin,
        HealthUp
    }


    /**
     * Speed of the Item, positive or negative depending on direction -
     * positive is down.
     */
    private int itemSpeed;

    private String name;

    private ItemType type;

    protected int effectValue;

    private int effectDuration;

    private static double dropChance = 0.5;





    /**
     * Constructor, establishes the Item's properties.
     *
     * @param positionX
     *            Initial position of the Item in the X axis.
     * @param positionY
     *            Initial position of the Item in the Y axis.
     * @param speed
     *            Speed of the Item, positive or negative depending on
     *            direction - positive is down.
     */

    public Item(final int positionX, final int positionY, final int speed,
                SpriteType spriteType, ItemType itemType, int effectValue, int effectDuration, double dropChance) {
        super(positionX, positionY, 3 * 2, 5 * 2, Color.WHITE);
        this.itemSpeed = speed;
        this.type = itemType;
        this.spriteType = spriteType;
        this.effectValue = effectValue;
        this.effectDuration = effectDuration;
        this.dropChance = dropChance;
        setSprite();
    }


    public final void setSprite() {
        // keep the same sprite for now; choose based on speed if you want
        this.spriteType = SpriteType.ItemDefault;
    }

    /**
     * Updates the Item's position.
     */
    public final void update() {
        this.positionY += this.itemSpeed;
    }

    /**
     * Apply the Item's effect.
     */
    public void applyEffect(final GameState gameState, final int playerId){};

    /**
     * Setter of the speed of the Item.
     *
     * @param itemSpeed
     *            New speed of the Item.
     */
    public final void setItemSpeed(final int itemSpeed) {
        this.itemSpeed = itemSpeed;
    }

    public final int getItemSpeed() {
        return this.itemSpeed;
    }

    public final void reset(ItemType itemType) {
        this.type = itemType;
        this.itemSpeed = 0;
        this.spriteType = SpriteType.ItemDefault; // change to your enum if different
    }

    /**
     * Getter for the speed of the Item.
     *
     * @return Speed of the Item.
     */
    public final ItemType getType() {
        return this.type;
    }

    public static double getDropChance() { return dropChance; }
}
