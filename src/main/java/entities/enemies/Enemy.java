package entities.enemies;
import entities.abs.BattleUnit;
import entities.items.Item;
import java.util.Random;

public class Enemy extends BattleUnit {
    protected int difficultyLevel;
    protected double dropRate;
    protected Item[] possibleDrops;
    protected static final Random random = new Random();
    public Enemy() {
        super();
        this.difficultyLevel = 1;
        this.dropRate = 0.3; // 30% drop rate
        this.possibleDrops = new Item[3];
    }

    public Enemy(String name, int difficulty) {
        super(name, 50 + difficulty * 10, 5 + difficulty * 2);
        this.difficultyLevel = difficulty;
        this.dropRate = 0.1 + difficulty * 0.05; // higher the difficulty, higher the drop rate
        this.possibleDrops = new Item[3];
        // Adjust attributes using levels
        this.maxHP += difficulty * 10;
        this.currentHP = this.maxHP;
        this.attack += difficulty * 2;
        this.defense += difficulty;
    }
    public int getDifficultyLevel() { return difficultyLevel; }
    public void setDifficultyLevel(int difficultyLevel) { this.difficultyLevel = difficultyLevel; }
    public double getDropRate() { return dropRate; }
    public void setDropRate(double dropRate) { this.dropRate = dropRate; }
    public Item[] getPossibleDrops() { return possibleDrops; }

    public Item calculateDrop() {
        if (random.nextDouble() < dropRate) {
            if (possibleDrops.length > 0) {
                // Choose drops randomly
                Item drop = possibleDrops[random.nextInt(possibleDrops.length)];
                if (drop != null) {
                    System.out.println(name + " dropped: " + drop.getName());
                    return drop;
                }
            }
        }
        System.out.println(name + " didn't drop anything");
        return null;
    }

    @Override
    public void useSkill() {
        System.out.println(name + " used enemy skill");
        // Randomly choose skill
        int skillType = random.nextInt(3);
        switch (skillType) {
            case 0:
                System.out.println("Use standard skill");
                break;
            case 1:
                System.out.println("Used defense skill");
                defense += 5;
                break;
            case 2:
                System.out.println("Used healing skill");
                heal(20);
                break;
        }
    }
    public void setPossibleDrops(Item[] drops) {
        this.possibleDrops = drops;
    }
    public void addPossibleDrop(Item drop) {
        for (int i = 0; i < possibleDrops.length; i++) {
            if (possibleDrops[i] == null) {
                possibleDrops[i] = drop;
                return;
            }
        }
        System.out.println("Drop list is full");
    }
    @Override
    public void displayInfo() {
        System.out.println("=== Enemy info ===");
        System.out.println("Name: " + name);
        System.out.println("Difficulty: " + difficultyLevel);
        System.out.println("Health: " + currentHP + "/" + maxHP);
        System.out.println("Attack: " + attack);
        System.out.println("Defense: " + defense);
        System.out.println("Speed: " + speed);
        System.out.println("Drop rate: " + (dropRate * 100) + "%");
        System.out.println("Possible drops:");
        for (Item drop : possibleDrops) {
            if (drop != null) {
                System.out.println("  - " + drop.getName());
            }
        }
    }

    @Override
    public String toCSVFormat() {
        StringBuilder drops = new StringBuilder();
        for (Item drop : possibleDrops) {
            if (drop != null) {
                if (drops.length() > 0) drops.append(";");
                drops.append(drop.getName());
            }
        }
        String dropsCSV = drops.toString();
        String var = "," + difficultyLevel + "," + dropRate + ",\"" + dropsCSV.replace("\"", "\"\"") + "\"";
        return super.toCSVFormat() + var;
    }
}