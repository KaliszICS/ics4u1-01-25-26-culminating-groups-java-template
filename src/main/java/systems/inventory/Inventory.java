package systems.inventory;

import entities.Searchable;
import entities.Sortable;
import entities.items.*;
import entities.characters.*;
import entities.equipment.*;
import util.Algorithms.*;
import entities.characters.Character;


import java.util.*;
import java.util.stream.Collectors;

public class Inventory implements Sortable, Searchable {
    private List<Item> items;
    private int currentWeight;
    private int maxWeight;
    private Map<Character, List<Equipment>> equippedItems;
    private SortAlgorithms sortAlgorithms;
    private SearchAlgorithms searchAlgorithms;

    public Inventory() {
        this.items = new ArrayList<>();
        this.currentWeight = 0;
        this.maxWeight = 1000; // 默认最大重量1000
        this.equippedItems = new HashMap<>();
        this.sortAlgorithms = new SortAlgorithms();
        this.searchAlgorithms = new SearchAlgorithms();
    }

    public Inventory(int capacity) {
        this();
        this.maxWeight = capacity;
    }

    /**
     * 添加物品到库存
     */
    public boolean addItem(Item item) {
        if (item == null) {
            System.out.println("不能添加空物品");
            return false;
        }

        // 检查重量
        int newWeight = currentWeight + item.getWeight();
        if (newWeight > maxWeight) {
            System.out.println("背包已满，无法添加物品: " + item.getName());
            System.out.println("当前重量: " + currentWeight + "/" + maxWeight);
            return false;
        }

        // 如果是可堆叠物品，尝试合并
        if (item.isStackable()) {
            for (Item existingItem : items) {
                if (existingItem.isStackable() &&
                        existingItem.getName().equals(item.getName()) &&
                        existingItem.getClass().equals(item.getClass())) {

                    // 合并物品
                    System.out.println("合并物品: " + item.getName());
                    items.remove(existingItem);
                    Item mergedItem = mergeItems(existingItem, item);
                    items.add(mergedItem);

                    currentWeight = calculateTotalWeight();
                    return true;
                }
            }
        }

        // 添加新物品
        items.add(item);
        currentWeight += item.getWeight();
        System.out.println("添加物品: " + item.getName() + " (重量: " + item.getWeight() + ")");
        System.out.println("当前重量: " + currentWeight + "/" + maxWeight);
        return true;
    }

    /**
     * 合并可堆叠物品
     */
    private Item mergeItems(Item item1, Item item2) {
        // 根据不同类型处理合并
        if (item1 instanceof MaterialItem && item2 instanceof MaterialItem) {
            MaterialItem mat1 = (MaterialItem) item1;
            MaterialItem mat2 = (MaterialItem) item2;

            // 创建新的合并物品
            MaterialItem merged = new MaterialItem(mat1.getName(), mat1.getMaterialType(),
                    mat1.getRarity(), mat1.getValue() + mat2.getValue());
            merged.setWeight(Math.max(mat1.getWeight(), mat2.getWeight()));
            merged.setStackable(true);

            System.out.println("合并材料: " + mat1.getName() + " + " + mat2.getName());
            return merged;
        }

        // 其他类型的堆叠处理
        item1.setValue(item1.getValue() + item2.getValue());
        return item1;
    }

    /**
     * 移除物品（通过ID）
     */
    public Item removeItem(int itemId) {
        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            if (item.getId() == itemId) {
                items.remove(i);
                currentWeight -= item.getWeight();
                System.out.println("移除物品: " + item.getName());
                System.out.println("当前重量: " + currentWeight + "/" + maxWeight);
                return item;
            }
        }

        System.out.println("未找到物品ID: " + itemId);
        return null;
    }

    /**
     * 移除物品（通过名称）
     */
    public Item removeItem(String name) {
        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            if (item.getName().equals(name)) {
                items.remove(i);
                currentWeight -= item.getWeight();
                System.out.println("移除物品: " + item.getName());
                System.out.println("当前重量: " + currentWeight + "/" + maxWeight);
                return item;
            }
        }

        System.out.println("未找到物品: " + name);
        return null;
    }

    /**
     * 按稀有度排序（使用冒泡排序）
     */
    public void sortByRarity() {
        System.out.println("按稀有度排序（冒泡排序）");

        // 创建副本进行排序
        List<Item> sortedItems = new ArrayList<>(items);

        // 使用工具类中的冒泡排序
        sortedItems = sortAlgorithms.bubbleSort(sortedItems);

        // 更新列表
        items = sortedItems;
        displayInventory();
    }

    /**
     * 按类型排序（使用选择排序）
     */
    public void sortByType() {
        System.out.println("按类型排序（选择排序）");

        // 创建副本进行排序
        List<Item> sortedItems = new ArrayList<>(items);

        // 使用工具类中的选择排序
        sortedItems = sortAlgorithms.selectionSort(sortedItems);

        // 更新列表
        items = sortedItems;
        displayInventory();
    }

    /**
     * 按名称排序（使用插入排序）
     */
    public void sortByName() {
        System.out.println("按名称排序（插入排序）");

        // 创建副本进行排序
        List<Item> sortedItems = new ArrayList<>(items);

        // 使用工具类中的插入排序
        sortedItems = sortAlgorithms.insertionSort(sortedItems);

        // 更新列表
        items = sortedItems;
        displayInventory();
    }

    /**
     * 高级排序（使用归并排序）
     */
    public void sortAdvanced() {
        System.out.println("高级排序（归并排序）");

        // 创建副本进行排序
        List<Item> sortedItems = new ArrayList<>(items);

        // 使用工具类中的归并排序
        sortedItems = sortAlgorithms.mergeSort(sortedItems);

        // 更新列表
        items = sortedItems;
        displayInventory();
    }

    /**
     * 搜索物品（通过名称）- 使用二分查找
     */
    public Item searchItem(String name) {
        System.out.println("搜索物品: " + name);

        // 首先确保列表按名称排序
        List<Item> sortedList = new ArrayList<>(items);
        sortedList = sortAlgorithms.insertionSort(sortedList);

        // 使用二分查找
        Item result = searchAlgorithms.binarySearch(sortedList, name);

        if (result != null) {
            System.out.println("找到物品: " + result.getName());
            result.displayInfo();
        } else {
            System.out.println("未找到物品: " + name);
        }

        return result;
    }

    /**
     * 搜索物品（通过类型）- 使用顺序查找
     */
    public List<Item> searchItemByType(String type) {
        System.out.println("按类型搜索: " + type);

        List<Item> result = searchAlgorithms.sequentialSearch(items, type);

        if (!result.isEmpty()) {
            System.out.println("找到 " + result.size() + " 个" + type + "类型物品:");
            for (Item item : result) {
                System.out.println("  - " + item.getName());
            }
        } else {
            System.out.println("未找到" + type + "类型物品");
        }

        return result;
    }

    /**
     * 递归搜索物品
     */
    public Item recursiveSearch(String criteria) {
        System.out.println("递归搜索: " + criteria);

        Item result = searchAlgorithms.recursiveSearch(items, 0, criteria);

        if (result != null) {
            System.out.println("递归搜索找到物品: " + result.getName());
        } else {
            System.out.println("递归搜索未找到: " + criteria);
        }

        return result;
    }

    /**
     * 为角色装备物品
     */
    public boolean equipItem(Character character, Equipment equipment) {
        if (character == null || equipment == null) {
            System.out.println("无效的角色或装备");
            return false;
        }

        // 检查角色等级是否足够
        if (character.getLevel() < equipment.getRequiredLevel()) {
            System.out.println(character.getName() + " 等级不足！需要等级 " + equipment.getRequiredLevel());
            return false;
        }

        // 检查物品是否在库存中
        if (!containsItem(equipment)) {
            System.out.println("库存中没有该装备: " + equipment.getName());
            return false;
        }

        // 获取角色的装备列表
        List<Equipment> characterEquipment = equippedItems.getOrDefault(character, new ArrayList<>());

        // 检查装备槽位是否已满
        if (characterEquipment.size() >= 4) { // 假设最多4件装备
            System.out.println(character.getName() + " 的装备槽已满");
            return false;
        }

        // 检查是否有相同槽位的装备
        for (Equipment equipped : characterEquipment) {
            if (equipped.getSlot().equals(equipment.getSlot())) {
                System.out.println(character.getName() + " 的" + equipment.getSlot() + "槽位已有装备: " + equipped.getName());
                return false;
            }
        }

        // 装备物品
        characterEquipment.add(equipment);
        equippedItems.put(character, characterEquipment);

        // 从库存中移除
        removeItem(equipment.getName());

        System.out.println(character.getName() + " 装备了 " + equipment.getName() + " 在 " + equipment.getSlot() + " 槽位");

        // 计算装备属性
        equipment.calculateStats();
        equipment.equip(character);

        return true;
    }

    /**
     * 卸下角色装备
     */
    public boolean unequipItem(Character character, Equipment equipment) {
        if (character == null || equipment == null) {
            System.out.println("无效的角色或装备");
            return false;
        }

        List<Equipment> characterEquipment = equippedItems.get(character);
        if (characterEquipment == null || !characterEquipment.contains(equipment)) {
            System.out.println(character.getName() + " 没有装备 " + equipment.getName());
            return false;
        }

        // 卸下装备
        characterEquipment.remove(equipment);
        equippedItems.put(character, characterEquipment);

        // 添加到库存
        addItem(equipment);

        System.out.println(character.getName() + " 卸下了 " + equipment.getName());
        equipment.unequip();

        return true;
    }

    /**
     * 卸下角色指定槽位的装备
     */
    public boolean unequipSlot(Character character, String slot) {
        List<Equipment> characterEquipment = equippedItems.get(character);
        if (characterEquipment == null) {
            System.out.println(character.getName() + " 没有装备任何物品");
            return false;
        }

        for (Equipment equipment : characterEquipment) {
            if (equipment.getSlot().equals(slot)) {
                return unequipItem(character, equipment);
            }
        }

        System.out.println(character.getName() + " 的" + slot + "槽位没有装备");
        return false;
    }

    /**
     * 检查库存中是否包含物品
     */
    public boolean containsItem(Item item) {
        return items.contains(item);
    }

    /**
     * 检查库存中是否包含指定名称的物品
     */
    public boolean containsItem(String name) {
        for (Item item : items) {
            if (item.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取物品数量
     */
    public int getItemCount() {
        return items.size();
    }

    /**
     * 计算总重量
     */
    private int calculateTotalWeight() {
        return items.stream().mapToInt(Item::getWeight).sum();
    }

    /**
     * 显示库存信息
     */
    public void displayInventory() {
        System.out.println("=== 背包信息 ===");
        System.out.println("物品数量: " + items.size());
        System.out.println("当前重量: " + currentWeight + "/" + maxWeight);
        System.out.println("空余重量: " + (maxWeight - currentWeight));

        if (items.isEmpty()) {
            System.out.println("背包为空");
            return;
        }

        System.out.println("\n物品列表:");
        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            String rarity = "";
            if (item instanceof MaterialItem) {
                MaterialItem mat = (MaterialItem) item;
                // rarity = "★".repeat(mat.getRarity());
                StringBuilder starBuilder = new StringBuilder();
                for (int j = 0; j < mat.getRarity(); j++) {
                    starBuilder.append("★");
                }
                rarity = starBuilder.toString();
            } else if (item instanceof Equipment) {
                Equipment eq = (Equipment) item;
                if (eq instanceof LightCone) {
                    LightCone lc = (LightCone) eq;
                    rarity = lc.getRequiredLevel() >= 60 ? "★★★★★" : "★★★★";
                } else {
                    rarity = "★★★";
                }
            } else {
                rarity = "★★";
            }

            System.out.println(String.format("  %3d. %s %s (重量: %d, 价值: %d)",
                    i + 1, rarity, item.getName(), item.getWeight(), item.getValue()));
        }
    }

    /**
     * 显示角色装备信息
     */
    public void displayEquipment(Character character) {
        System.out.println("=== " + character.getName() + " 的装备 ===");

        List<Equipment> characterEquipment = equippedItems.get(character);
        if (characterEquipment == null || characterEquipment.isEmpty()) {
            System.out.println("没有装备任何物品");
            return;
        }

        for (Equipment equipment : characterEquipment) {
            System.out.println("槽位: " + equipment.getSlot() + " - " + equipment.getName());
            System.out.println("  需求等级: " + equipment.getRequiredLevel());
            System.out.println("  攻击: " + equipment.getStat("attack") +
                    ", 防御: " + equipment.getStat("defense") +
                    ", HP: " + equipment.getStat("hp"));
        }
    }

    /**
     * 获取所有装备了指定装备的角色
     */
    public List<Character> getCharactersWithEquipment(Equipment equipment) {
        List<Character> characters = new ArrayList<>();

        for (Map.Entry<Character, List<Equipment>> entry : equippedItems.entrySet()) {
            if (entry.getValue().contains(equipment)) {
                characters.add(entry.getKey());
            }
        }

        return characters;
    }

    /**
     * 获取所有物品
     */
    public List<Item> getAllItems() {
        return new ArrayList<>(items);
    }

    /**
     * 获取物品（通过索引）
     */
    public Item getItem(int index) {
        if (index >= 0 && index < items.size()) {
            return items.get(index);
        }
        return null;
    }

    /**
     * 清空库存
     */
    public void clear() {
        items.clear();
        currentWeight = 0;
        System.out.println("背包已清空");
    }

    /**
     * 扩容背包
     */
    public void expandCapacity(int additionalCapacity) {
        maxWeight += additionalCapacity;
        System.out.println("背包扩容 " + additionalCapacity + " 点重量");
        System.out.println("新容量: " + maxWeight);
    }

    /**
     * 实现Sortable接口的方法
     */
    @Override
    public void sort() {
        // 默认按名称排序
        sortByName();
    }

    @Override
    public int compare(Object other) {
        if (other instanceof Inventory) {
            Inventory otherInventory = (Inventory) other;
            return Integer.compare(this.items.size(), otherInventory.items.size());
        }
        return -1;
    }

    /**
     * 实现Searchable接口的方法
     */
    @Override
    public Object search(String criteria) {
        return searchItem(criteria);
    }

    @Override
    public Object search(String criteria, int startIndex) {
        // 从指定索引开始搜索
        if (startIndex < 0 || startIndex >= items.size()) {
            System.out.println("无效的起始索引");
            return null;
        }

        for (int i = startIndex; i < items.size(); i++) {
            Item item = items.get(i);
            if (item.getName().contains(criteria) ||
                    item.getItemType().equals(criteria) ||
                    (item instanceof MaterialItem && ((MaterialItem) item).getMaterialType().equals(criteria))) {
                System.out.println("从索引 " + startIndex + " 找到物品: " + item.getName());
                return item;
            }
        }

        System.out.println("从索引 " + startIndex + " 开始未找到: " + criteria);
        return null;
    }

    /**
     * 导出库存为CSV格式
     */
    public String exportToCSV() {
        StringBuilder csv = new StringBuilder();
        csv.append("ID,名称,类型,稀有度,重量,价值,堆叠\n");

        for (Item item : items) {
            String rarity = "★";
            if (item instanceof MaterialItem) {
                MaterialItem mat = (MaterialItem) item;
                StringBuilder starBuilder = new StringBuilder();
                for (int j = 0; j < mat.getRarity(); j++) {
                    starBuilder.append("★");
                }
                rarity = starBuilder.toString();
            }

            csv.append(String.format("%d,%s,%s,%s,%d,%d,%b\n",
                    item.getId(),
                    item.getName(),
                    item.getItemType(),
                    rarity,
                    item.getWeight(),
                    item.getValue(),
                    item.isStackable()
            ));
        }

        return csv.toString();
    }

    /**
     * 计算库存总价值
     */
    public int calculateTotalValue() {
        return items.stream().mapToInt(Item::getValue).sum();
    }

    /**
     * 获取库存统计信息
     */
    public Map<String, Integer> getStatistics() {
        Map<String, Integer> stats = new HashMap<>();

        // 按类型统计
        Map<String, Long> typeCount = items.stream()
                .collect(Collectors.groupingBy(Item::getItemType, Collectors.counting()));

        for (Map.Entry<String, Long> entry : typeCount.entrySet()) {
            stats.put(entry.getKey(), entry.getValue().intValue());
        }

        // 添加总数量和总重量
        stats.put("总数量", items.size());
        stats.put("总重量", currentWeight);
        stats.put("总价值", calculateTotalValue());

        return stats;
    }
}
