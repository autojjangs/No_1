package engine;

import java.util.Random;
import java.util.Set;
import java.util.logging.Logger;

import entity.EnemyShip;
import entity.Item;
import entity.ItemPool;

/**
 * Responsible for item drop decisions and applying item effects.
 * Implemented as a singleton for easy access.
 */
public final class ItemManager {

    private static ItemManager instance;

    /** Base drop chance for normal enemies (0..1). Default 1.0 => always drop. */
    private double dropThreshold = 0.0;

    private final Random itemRoll = new Random();

    private Logger logger;

    private ItemManager() { logger = Core.getLogger(); }

    public static ItemManager getInstance() {
        if (instance == null)
            instance = new ItemManager();
        return instance;
    }

    /**
     * Decide whether an enemy drops an item
     * If so, prepare and return it.
     * Else returns null.
     */
    public Item obtainDrop(final EnemyShip enemy) {
        if (enemy == null) return null;

        // TODO: Different threshold depending on the ITEM TYPE.
        double dropRoll = itemRoll.nextDouble();

        this.logger.info(String.format("[ItemManager]: dropRoll/baseDropChance*ItemDropChance %.1f/%.1f",
                dropRoll, this.dropThreshold * Item.getDropChance()));

        /** Check Item Drop threshold **/
        if (dropRoll < this.dropThreshold * Item.getDropChance()) {
            return null;
        }

        /** Get Item position. **/
        int centerX = enemy.getPositionX() + enemy.getWidth() / 2;
        int centerY = enemy.getPositionY() + enemy.getHeight() / 2;

        // create and initialize item
        Item drop = ItemPool.getItem(centerX, centerY, 2);
        this.logger.info("[ItemManager]: created item " + drop.getType()
                + " at (" + centerX + ", " + centerY + ")");

        return drop;
    }

    /**
     * Apply the item effect.
     * ItemManager performs the effect by calling back to
     * the provided gameScreen.
     */
    public void handlePickup(final Set<Item> items, final screen.GameScreen gameScreen) {

        if (items == null || gameScreen == null) return;

        for (Item item : items) {
            item.applyEffect();
            this.logger.info(item.getType() + " picked up");
        }
    }
}
