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

    // Debug logger init

    private Logger logger;
    private ItemManager() { logger = Core.getLogger(); }

    public static ItemManager getInstance() {
        if (instance == null)
            instance = new ItemManager();
        return instance;
    }

    // Random Roll for item
    private final Random itemRoll = new Random();
    // Pity Counter
    private int pityCounter = 0;

    // Item Database
    private final ItemDB itemDB = new ItemDB();

    /** -------------------------- ITEM DATA -------------------------- **/

    /** ITEM WEIGHT **/
    public static enum DropTier {
        NONE     (60.0),
        COMMON   (25.0),
        UNCOMMON (15.0),
        RARE     (5.0);

        public final double tierWeight;
        DropTier(double tierWeight) { this.tierWeight = Math.max(0.0, tierWeight); }
    }

    /** ITEM TYPE (name only, data is loaded from CSV) **/
    public static enum ItemType {

        SCORE,
        COIN,
        HEAL
    }

    /** -------------------------- INIT -------------------------- **/

    private static final double ITEM_WEIGHT;

    static {
        double sum = 0.0;
        for (DropTier t : DropTier.values()) {
            if (t != DropTier.NONE) sum += t.tierWeight;
        }
        ITEM_WEIGHT = sum;
    }

    /** -------------------------- MAIN -------------------------- **/

    public Item obtainDrop(final EnemyShip enemy) {
        if (enemy == null) return null;

        // Pity Boost
        double pityBoost = Math.min(pityCounter * 0.05, 0.5);
        double boostedNoneWeight = DropTier.NONE.tierWeight * (1.0 - pityBoost);

        // Roll Item
        double dropRoll = itemRoll.nextDouble() * (ITEM_WEIGHT + boostedNoneWeight);
        this.logger.info(String.format("[ItemManager]: DropRoll %.1f", dropRoll));

        DropTier chosenTier = DropTier.NONE;
        double acc = 0.0;

        for (DropTier tier : DropTier.values()) {
            double weight = tier.tierWeight;

            if (tier == DropTier.NONE) {
                weight = boostedNoneWeight;
            }

            acc += weight;

            if (dropRoll < acc) {
                chosenTier = tier;
                break;
            }
        }

        // Calculate Pity
        if (chosenTier == DropTier.NONE) {
            pityCounter++;
            logger.info(String.format("[ItemManager]: Tier=NONE (pity=%d)", pityCounter));
            return null;
        }

        pityCounter = 0;

        // Load item list from CSV by DropTier
        java.util.List<ItemData> candidates = new java.util.ArrayList<>();
        for (ItemData data : itemDB.getAllItems()) {
            if (data.getDropTier().equalsIgnoreCase(chosenTier.name()))
                candidates.add(data);
        }

        if (candidates.isEmpty()) {
            logger.warning("[ItemManager]: No items defined for tier " + chosenTier);
            return null;
        }

        ItemData chosenData = candidates.get(itemRoll.nextInt(candidates.size()));

        // Convert String from CSV to enum ItemType
        ItemType chosenItem;
        try {
            chosenItem = ItemType.valueOf(chosenData.getType().toUpperCase());
        } catch (IllegalArgumentException e) {
            logger.warning("[ItemManager]: Unknown item type in CSV: " + chosenData.getType());
            return null;
        }

        // get spawn position / enemy death position
        int centerX = enemy.getPositionX() + enemy.getWidth() / 2;
        int centerY = enemy.getPositionY() + enemy.getHeight() / 2;

        Item drop = ItemPool.getItem(chosenItem, centerX, centerY, 2);
        this.logger.info("[ItemManager]: created item " + drop.getType() + " at (" + centerX + ", " + centerY + ")");

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
//            item.applyEffect(gameScreen.getGameState());
            this.logger.info(item.getType() + " picked up");
        }
    }
}
