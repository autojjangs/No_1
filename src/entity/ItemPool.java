package entity;

import engine.ItemData;
import java.util.HashSet;
import java.util.Set;

/**
 * Implements a pool of recyclable items.
 */
public final class ItemPool {

    /** Set of items. */
    private static Set<Item> pool = new HashSet<Item>();

    /**
     * Constructor, not called.
     */
    private ItemPool() {

    }

    /**
     * Returns an item from the pool if one is available, a new one if there
     * isn't.
     * Caller should call item.init(...) to set position/type/sprite after obtaining.
     *
     * @param data
     *          data of item created
     * @param positionX
     *            Requested position of the item in the X axis.
     * @param positionY
     *            Requested position of the item in the Y axis.
     * @param speed
     *            Requested speed of the item, positive or negative depending
     *            on direction - positive is down.
     * @return Requested item.
     */
    public static Item getItem(ItemData data, int positionX, int positionY, int speed) {
        String type = data.getType();
        // create new item
        Item item;
        if (!pool.isEmpty()) {
            item = pool.iterator().next();
            pool.remove(item);

            item.reset(type);
            item.setPositionX(positionX - item.getWidth() / 2);
            item.setPositionY(positionY);
            item.setItemSpeed(speed);
        } else {
            item = new Item(type, positionX - 3, positionY, speed);
        }

        return item;
    }

    /**
     * Adds one or more items to the list of available ones.
     *
     * @param items
     *            Items to recycle.
     */
    public static void recycle(final Set<Item> items) {
        if (items == null) return;
        pool.addAll(items);
    }
}