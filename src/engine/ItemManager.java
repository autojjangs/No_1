package engine;

import java.util.Random;

import entity.EnemyShip;
import entity.Item;
import entity.Item.ItemType;
import entity.ItemPool;

/**
 * Responsible for item drop decisions and applying item effects.
 * Implemented as a singleton for easy access.
 */
public final class ItemManager {

    private static final ItemManager INSTANCE = new ItemManager();

    /** Base drop chance for normal enemies (0..1). Default 1.0 => always drop. */
    private double baseDropChance = 1.0;

    private final Random itemRoll = new Random();

    private ItemManager() { }

    public static ItemManager getInstance() {
        return INSTANCE;
    }

    /**
     * Decide whether an enemy drops an item and, if so, prepare and return it.
     * Returns null if no drop should occur.
     */
    public Item obtainDrop(final EnemyShip enemy) {
        if (enemy == null) return null;

        double dropRoll = itemRoll.nextDouble();
        Core.getLogger().info("Calculating item drop chance: roll=" + dropRoll
                + " threshold=" + this.baseDropChance);

        if (dropRoll >= this.baseDropChance) {
            return null;
        }



        int centerX = enemy.getPositionX() + enemy.getWidth() / 2;
        int centerY = enemy.getPositionY() + enemy.getHeight() / 2;
        // create and initialize item
//        Item drop = ItemPool.getItem(centerX, centerY, 2);
        Item drop = new Item(centerX, centerY, 2);

        ItemType type = ItemType.ITEM_1;
//        double typeRoll = itemRoll.nextDouble();
//        if (typeRoll < 0.15)
//            type = ItemType.ITEM_1;
//        else if (typeRoll < 0.65)
//            type = ItemType.ITEM_2;
//        else
//            type = ItemType.ITEM_3;

        drop.init(centerX, centerY, type, 2, DrawManager.SpriteType.ItemDefault);
        Core.getLogger().info("ItemManager: created item " + type + " at " + centerX + "," + centerY);
        return drop;
    }

    /**
     * Apply the item effect. ItemManager performs the effect by calling back to
     * the provided gameScreen. This keeps game-screen responsibilities limited
     * to world state and rendering while letting ItemManager centralize item logic.
     */
    public void handlePickup(final Item item, final screen.GameScreen gameScreen) {
        if (item == null || gameScreen == null) return;

        switch (item.getType()) {
            case ITEM_1:
                Core.getLogger().info("ItemManager: ITEM_1");
                break;
            case ITEM_2:
                Core.getLogger().info("ItemManager: ITEM_2");
                break;
            case ITEM_3:
                Core.getLogger().info("ItemManager: ITEM_3");
                break;
            default:
                Core.getLogger().info("ItemManager: DEFAULT_ITEM");
                break;
        }
    }
}
