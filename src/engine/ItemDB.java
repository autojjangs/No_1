package engine;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;


public class ItemDB {
    private static final String FILE_PATH = "res/item_db.csv";
    private final Map<String, ItemData> itemMap = new HashMap<>();

    public ItemDB() {
        loadItemDB();
    }

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
            e.printStackTrace();
        }
    }

    public ItemData getItemData(String type) {
        return itemMap.get(type);
    }

    public Collection<ItemData> getAllItems() {
        return itemMap.values();
    }
}
