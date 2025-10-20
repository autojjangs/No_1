package engine;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;


/**
 * Handles loading and managing item data from the CSV file. (item_db.csv)
 *
 * Supports both legacy CSV format (5 columns) and new format with cost (6 columns):
 * type,spriteType,dropTier,effectValue,effectDuration[,cost]
 */
public class ItemDB {
    /** Path to the item database CSV file. */
    private static final String FILE_PATH = "res/item_db.csv";
    /** Map of item type name to its corresponding ItemData. */
    private final Map<String, ItemData> itemMap = new HashMap<>();

    /**
     * Constructor.
     * Automatically loads the CSV file into memory.
     */
    public ItemDB() {
        loadItemDB();
    }

    /**
     * Loads all item data from the CSV file into the itemMap.
     * The CSV format is expected as:
     * type, spriteType, dropTier, effectValue, effectDuration, cost
     *
     */
    private void loadItemDB() {
        Logger logger = Core.getLogger();

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            boolean header = true;

            while ((line = br.readLine()) != null) {
                if (header) {
                    header = false;
                    continue;
                }

                // split on comma - trim tokens
                String[] tokens = line.split(",");
                if (tokens.length < 5) {
                    logger.warning("[ItemDB] Skipping malformed line (expected >=5 cols): " + line);
                    continue;
                }

                String type = tokens[0].trim();
                String spriteType = tokens[1].trim();
                String dropTier = tokens[2].trim();

                int effectValue = 0;
                int effectDuration = 0;
                int cost = 0;

                try {
                    effectValue = Integer.parseInt(tokens[3].trim());
                } catch (NumberFormatException e) {
                    logger.warning("[ItemDB] Invalid effectValue for " + type + " -> '" + tokens[3] + "'. Using 0.");
                }

                try {
                    effectDuration = Integer.parseInt(tokens[4].trim());
                } catch (NumberFormatException e) {
                    logger.warning("[ItemDB] Invalid effectDuration for " + type + " -> '" + tokens[4] + "'. Using 0.");
                }

                // optional cost column (index 5)
                if (tokens.length > 5 && tokens[5] != null && !tokens[5].trim().isEmpty()) {
                    try {
                        cost = Integer.parseInt(tokens[5].trim());
                        if (cost < 0) {
                            logger.warning("[ItemDB] Negative cost for " + type + " -> '" + tokens[5] + "'. Using 0.");
                            cost = 0;
                        }
                    } catch (NumberFormatException e) {
                        logger.warning("[ItemDB] Invalid cost for " + type + " -> '" + tokens[5] + "'. Using 0.");
                        cost = 0;
                    }
                }

                ItemData data = new ItemData(type, spriteType, dropTier, effectValue, effectDuration, cost);
                itemMap.put(type, data);
            }
        } catch (FileNotFoundException e) {
            Logger l = Core.getLogger();
            l.severe("Item DB file not found: " + FILE_PATH + " (" + e.getMessage() + ")");
        } catch (IOException e) {
            Logger l = Core.getLogger();
            l.severe("Failed to load item database from " + FILE_PATH + ": " + e.getMessage());
        }
    }

    /**
     * Return the ItemData object for the given item type.
     *
     * @param type
     *            type of the item.
     * @return ItemData object, or null if not found.
     */
    public ItemData getItemData(String type) {
        return itemMap.get(type);
    }

    /**
     * Return a collection of all ItemData objects.
     *
     * @return Collection of all items in the database.
     */
    public Collection<ItemData> getAllItems() {
        return itemMap.values();
    }
}
