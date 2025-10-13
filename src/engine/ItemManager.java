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

    /** ITEM DATA **/
    public static enum ItemType {
        // itemType(dropTier, spriteType, itemEffectId)
        SCORE(DropTier.COMMON,   DrawManager.SpriteType.ItemScore,   "heal_small_effect"),
        COIN (DropTier.UNCOMMON, DrawManager.SpriteType.ItemCoin,  "score_small_effect"),
        HEAL (DropTier.RARE,     DrawManager.SpriteType.ItemHeal,"special_power_effect");

        public final DropTier dropTier;
        public final DrawManager.SpriteType spriteType;
        public final String itemEffectId;

        ItemType(DropTier dropTier, DrawManager.SpriteType spriteType, String itemEffectId) {
            this.dropTier   = dropTier;
            this.spriteType = spriteType;
            this.itemEffectId = itemEffectId;
        }
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

        // 2. find item from tier - return SCORE/COIN/POINT

        java.util.List<ItemType> candidates = new java.util.ArrayList<>();
        for (ItemType it : ItemType.values()) {
            if (it.dropTier == chosenTier)
                candidates.add(it);
        }

        if (candidates.isEmpty()) {
            logger.warning("[ItemManager]: No items defined for tier " + chosenTier);
            return null;
        }

        ItemType chosenItem = candidates.get(itemRoll.nextInt(candidates.size()));

        // 3. return item

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
