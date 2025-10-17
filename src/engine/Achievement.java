package engine;

/**
 * Represents a single achievement with name, description, and unlock state.
 */
public class Achievement {
    private String name;
    private String description;
    private boolean unlocked;

    public Achievement(String name, String description) {
        this.name = name;
        this.description = description;
        this.unlocked = false;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public boolean isUnlocked() { return unlocked; }

    /** Unlocks this achievement. */
    public void unlock() { this.unlocked = true; }

    @Override
    public String toString() {
        return name + " - " + description + " [" + (unlocked ? "Unlocked" : "Locked") + "]";
    }
}