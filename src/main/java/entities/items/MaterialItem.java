package entities.items;

public class MaterialItem extends Item {
    private String type; // Enhancement, Ascension, etc.
    private int quantity;

    public MaterialItem(String name, int id, int rarity, boolean isUpItem,
                        String type, int quantity) {
        super(name, id, rarity, isUpItem);
        this.type = type;
        this.quantity = quantity;
        this.description = String.format("Type: %s, Qty: %d", type, quantity);
    }

    @Override
    public double getDropRate() {
        return 0.943; // 94.3% for 3-star items
    }
}