package entities.items;

public class LightConeItem extends Item {
    private int attack;
    private int defense;
    private int hp;
    private String skillDescription;

    public LightConeItem(String name, int id, int rarity, boolean isUpItem, int attack, int defense, int hp, String skill) {
        super(name, id, rarity, isUpItem);
        this.attack = attack;
        this.defense = defense;
        this.hp = hp;
        this.skillDescription = skill;
        this.description = String.format("ATK: %d, DEF: %d, HP: %d", attack, defense, hp);
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