package entities.items;

import entities.abs.GameEntity;
import entities.GachaPullable;

public abstract class Item extends GameEntity implements GachaPullable {
    protected int value;
    protected int weight;
    protected boolean stackable;

    // 添加新属性：物品类型
    protected String itemType;

    public Item() {
        super();
        this.value = 10;
        this.weight = 1;
        this.stackable = true;
        this.itemType = "普通物品";
    }

    public Item(String name, int value) {
        super(name, nextId);
        this.value = value;
        this.weight = 1;
        this.stackable = true;
        this.itemType = "普通物品";
    }

    public Item(String name, int id, int value, int weight, boolean stackable, String itemType) {
        super(name, id);
        this.value = value;
        this.weight = weight;
        this.stackable = stackable;
        this.itemType = itemType;
    }

    public abstract void use();

    @Override
    public Item pull() {
        System.out.println("抽取物品: " + name);
        return this;
    }

    @Override
    public double getDropRate() {
        // 基础掉落率，可以根据价值调整
        return 1.0 / (value / 10.0);
    }

    @Override
    public void displayInfo() {
        System.out.println("=== 物品信息 ===");
        System.out.println("名称: " + name);
        System.out.println("类型: " + itemType);
        System.out.println("价值: " + value);
        System.out.println("重量: " + weight);
        System.out.println("可堆叠: " + (stackable ? "是" : "否"));
        System.out.println("描述: " + description);
    }

    @Override
    public String toCSVFormat() {
        return super.toCSVFormat() + String.format(",%d,%d,%b,%s",
                value, weight, stackable, itemType);
    }

    // Getter 和 Setter
    public int getValue() { return value; }
    public void setValue(int value) { this.value = value; }
    public int getWeight() { return weight; }
    public void setWeight(int weight) { this.weight = weight; }
    public boolean isStackable() { return stackable; }
    public void setStackable(boolean stackable) { this.stackable = stackable; }
    public String getItemType() { return itemType; }
    public void setItemType(String itemType) { this.itemType = itemType; }
}