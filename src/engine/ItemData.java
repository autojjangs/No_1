package engine;

public class ItemData {
    private String type;
    private String spriteType;
    private String dropTier;  // 문자열로 저장 (COMMON, UNCOMMON 등)
    private int effectValue;
    private int effectDuration;

    public ItemData(String type, String spriteType, String dropTier, int effectValue, int effectDuration) {
        this.type = type;
        this.spriteType = spriteType;
        this.dropTier = dropTier;
        this.effectValue = effectValue;
        this.effectDuration = effectDuration;
    }

    public String getType() { return type; }
    public String getSpriteType() { return spriteType; }
    public String getDropTier() { return dropTier; }
    public int getEffectValue() { return effectValue; }
    public int getEffectDuration() { return effectDuration; }
}
