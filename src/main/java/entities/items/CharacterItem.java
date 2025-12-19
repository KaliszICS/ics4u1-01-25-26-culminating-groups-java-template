package entities.items;

public class CharacterItem extends Item {
    private int level;
    private String element; // element attribute
    private String path; // character pathway

    public CharacterItem(String name, int id, int rarity, boolean isUpItem, String element, String path) {
        super(name, id, rarity, isUpItem);
        this.element = element;
        this.path = path;
        this.level = 1;
        this.description = String.format("Element: %s, Path: %s", element, path);
    }

    // Getters
    public String getElement() { return element; }
    public String getPath() { return path; }
    public int getLevel() { return level; }

    public void levelUp() {
        level++;
    }

    @Override
    public double getDropRate() {
        switch(rarity) {
            case 5: return 0.006; // 0.6%
            case 4: return 0.051; // 5.1%
            default: return 0.0;
        }
    }
}