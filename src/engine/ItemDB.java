package engine;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;


    /**
     * Handles loading and managing item data from the CSV file. (item_db.csv)
     */
public class ItemDB {
    // Path to the item database CSV file.
    private static final String FILE_PATH = "res/item_db.csv";
    // Map of item type name to its corresponding ItemData.
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
     * type, spriteType, dropTier, effectValue, effectDuration
     *
     */
    private void loadItemDB() {
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            boolean header = true;

            while ((line = br.readLine()) != null) {
                if (header) { header = false; continue; }
                String[] tokens = line.split(",");
                if (tokens.length != 5) continue;

                String type = tokens[0].trim();
                String spriteType = tokens[1].trim();
                String dropTier = tokens[2].trim();
                int effectValue = Integer.parseInt(tokens[3].trim());
                int effectDuration = Integer.parseInt(tokens[4].trim());

                ItemData data = new ItemData(type, spriteType, dropTier, effectValue, effectDuration);
                itemMap.put(type, data);
            }
        } catch (IOException e) {
            Logger logger = Core.getLogger();
            logger.severe("Failed to load item database from " + FILE_PATH + ": " + e.getMessage());
        }
    }
    // Store as ItemData object
    public ItemData getItemData(String type) {
        return itemMap.get(type);
    }

    public Collection<ItemData> getAllItems() {
        return itemMap.values();
    }
}
