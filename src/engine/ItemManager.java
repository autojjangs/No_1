package engine;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.logging.Logger;

import entity.EnemyShip;
import entity.Item;
import entity.Item.ItemType;
import entity.ItemPool;
import engine.DrawManager.SpriteType;

/**
 * Responsible for item drop decisions and applying item effects.
 * Implemented as a singleton for easy access.
 */
public final class ItemManager {

    private static ItemManager instance;

    /** Base drop chance for normal enemies (0..1). Default 1.0 => always drop. */
    private double baseDropChance = 1.0;

    private final Random itemRoll = new Random();

    private Logger logger;

    private ItemManager() { logger = Core.getLogger(); }

    public static ItemManager getInstance() {
        if (instance == null)
            instance = new ItemManager();
        return instance;
    }

    /**
     * Decide whether an enemy drops an item and, if so, prepare and return it.
     * Returns null if no drop should occur.
     */
    public Item obtainDrop(final EnemyShip enemy) {
        if (enemy == null) return null;

        double dropRoll = itemRoll.nextDouble();
        this.logger.info(String.format("[ItemManager]: dropRoll/baseDropChance %.1f/%.1f", dropRoll, this.baseDropChance));
        if (dropRoll >= this.baseDropChance) {
            return null;
        }

        int centerX = enemy.getPositionX() + enemy.getWidth() / 2;
        int centerY = enemy.getPositionY() + enemy.getHeight() / 2;
        // create and initialize item
        // TODO: Should perform different actions depending on the ITEM TYPE.
        Item drop = new Item(centerX, centerY, 2, 1, SpriteType.Ship, ItemType.ITEM_1, 1, 1, 0.5);
        this.logger.info("[ItemManager]: created item " + drop.getType() + " at (" + centerX + ", " + centerY + ")");
        return drop;
    }

    /**
     * Apply the item effect. ItemManager performs the effect by calling back to
     * the provided gameScreen. This keeps game-screen responsibilities limited
     * to world state and rendering while letting ItemManager centralize item logic.
     */
    public void handlePickup(final Set<Item> items, final screen.GameScreen gameScreen) {
        if (items == null || gameScreen == null) return;
        for (Item item : items) {
            item.applyEffect();
            this.logger.info(item.getType() + " picked up");
        }
    }
}
