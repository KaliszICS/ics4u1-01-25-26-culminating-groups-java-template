package entities.items;

import systems.gacha.interfaces.Displayable;
import systems.gacha.interfaces.GachaPullable;

public abstract class Item implements Displayable, GachaPullable {
    protected String name;
    protected int id;
    protected String description;
    protected int rarity; // 3, 4, or 5 stars
    protected boolean isUpItem; // is up character or not

    public Item(String name, int id, int rarity, boolean isUpItem) {
        this.name = name;
        this.id = id;
        this.rarity = rarity;
        this.isUpItem = isUpItem;
        this.description = "";
    }

    // Getters
    public String getName() {
        return this.name;
    }

    public int getId() {
        return this.id;
    }

    public int getRarity() {
        return this.rarity;
    }

    public boolean isUpItem() {
        return this.isUpItem;
    }

    // Displayable interface implementation
    @Override
    public void displayInfo() {
        StringBuilder sb = new StringBuilder();
        //  = "★".repeat(rarity)
        String rarityStarStr = "";
        for (int i = 0; i < getRarity(); i++) {
            sb.append("★");
        }
        rarityStarStr = sb.toString();
        String upStatus = isUpItem ? "[UP]" : "";
        System.out.printf("%-15s %-10s Rarity: %s %s%n",
                name, upStatus, rarityStarStr, description);
    }

    @Override
    public String toCSVFormat() {
        return String.format("%s,%d,%d,%b", name, id, rarity, isUpItem);
    }

    // GachaPullable interface implementation
    @Override
    public Object pull() {
        return this;
    }

    @Override
    public double getDropRate() {
        return 0; // To be overridden by subclasses
    }
}
