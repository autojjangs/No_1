package engine;

public class ItemData {
    private String type;
    private String spriteType;
    private String dropTier;
    private int effectValue;
    private int effectDuration;

    public ItemData(String type, String spriteType, String dropTier, int effectValue, int effectDuration) {
        // Unique identifier for the item (e.g "COIN", "HEAL", "SCORE").
        this.type = type;
        // The sprite type (e.g "ItemScore, ItemHeal", etc).
        this.spriteType = spriteType;
        // The rarity tier (e.g "COMMON", "UNCOMMON", "RARE").
        this.dropTier = dropTier;
        // The numerical value of the item's effect (e.g. heal amount, score amount).
        this.effectValue = effectValue;
        // The duration (in seconds or frames) that the effect remains active. 
        this.effectDuration = effectDuration;
    }

    public String getType() { return type; }
    public String getSpriteType() { return spriteType; }
    public String getDropTier() { return dropTier; }
    public int getEffectValue() { return effectValue; }
    public int getEffectDuration() { return effectDuration; }
}
