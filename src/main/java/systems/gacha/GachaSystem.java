package systems.gacha;

import entities.items.*;
import entities.equipment.*;
import entities.characters.*;
import entities.characters.Character;
// import util.fileio.FileHandler;

import java.util.*;

public class GachaSystem {
    private double[][] probabilityTable;
    private int pityCounter5Star;
    private int pityCounter4Star;
    private boolean guaranteed5Star;
    private List<String> pullHistory;
    private List<String> characterPullHistory;
    private Random random;
    private PitySystem pitySystem;
    private PitySystem characterPitySystem; // 专门用于角色的保底系统

    public GachaSystem() {
        // 初始化概率表：3行（3星、4星、5星），3列（基础概率、UP概率、总概率）
        probabilityTable = new double[3][3];
        probabilityTable[0][0] = 0.943;  // 3星基础概率 94.3%
        probabilityTable[1][0] = 0.051;  // 4星基础概率 5.1%
        probabilityTable[2][0] = 0.006;  // 5星基础概率 0.6%
        probabilityTable[0][2] = 0.943;
        probabilityTable[1][2] = 0.051;
        probabilityTable[2][2] = 0.006;
        Random random = new Random();

        pityCounter5Star = 0;
        pityCounter4Star = 0;
        guaranteed5Star = false;
        pullHistory = new ArrayList<>();
        characterPullHistory = new ArrayList<>();
        pitySystem = new PitySystem();
        characterPitySystem = new PitySystem(); // 初始化角色保底系统
    }

    // 原有的物品抽取方法
    public Item pullSingle() {
        System.out.println("=== 单次祈愿（物品）===");

        // 更新保底计数器
        pityCounter5Star++;
        pityCounter4Star++;
        pitySystem.incrementPity();

        // 检查保底
        checkPity();

        // 更新概率
        updateProbabilities();

        // 确定稀有度
        int rarity = determineRarity();

        // 获取物品
        Item item = getRandomItemByRarity(rarity);

        // 记录历史
        String record = String.format("物品单抽,第%d抽,5星保底:%d,4星保底:%d,获得:%s",
                pullHistory.size() + 1, pityCounter5Star, pityCounter4Star, item.getName());
        pullHistory.add(record);

        // 重置保底计数器
        if (rarity == 5) {
            pityCounter5Star = 0;
            pitySystem.resetPity(5);
            guaranteed5Star = false;
        }
        if (rarity == 4) {
            pityCounter4Star = 0;
            pitySystem.resetPity(4);
        }

        System.out.println("获得: " + item.getName() + " (" + comeOnJustStarrrrrrrrs(rarity) + ")");
        return item;
    }

    public List<Item> pullTen() {
        System.out.println("=== 十连祈愿（物品）===");
        List<Item> results = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            results.add(pullSingle());
        }

        // 统计结果
        long fiveStarCount = results.stream()
                .filter(item -> {
                    // 5星物品：高价值或高级装备
                    if (item instanceof LightCone) {
                        return ((LightCone) item).getRequiredLevel() >= 60;
                    }
                    if (item instanceof MaterialItem) {
                        return ((MaterialItem) item).getRarity() >= 5;
                    }
                    // 其他情况根据价值判断
                    return item.getValue() >= 1000;
                })
                .count();

        long fourStarCount = results.stream()
                .filter(item -> {
                    // 4星物品：中等价值
                    if (item instanceof LightCone) {
                        return ((LightCone) item).getRequiredLevel() < 60 && ((LightCone) item).getRequiredLevel() > 1;
                    }
                    if (item instanceof MaterialItem) {
                        int rarity = ((MaterialItem) item).getRarity();
                        return rarity >= 3 && rarity < 5;
                    }
                    return item.getValue() >= 400 && item.getValue() < 1000;
                })
                .count();

        System.out.println("十连统计 - 5星: " + fiveStarCount + "个, 4星: " + fourStarCount + "个");
        return results;
    }

    // 新增：角色抽取方法
    public Character pullSingleCharacter() {
        System.out.println("=== 单次祈愿（角色）===");

        // 使用角色保底系统
        characterPitySystem.incrementPity();

        // 检查角色保底
        if (characterPitySystem.checkGuarantee()) {
            System.out.println("角色保底触发！");
        }

        // 确定角色稀有度
        int characterRarity = determineCharacterRarity();

        // 获取角色
        Character character = getRandomCharacterByRarity(characterRarity);

        // 记录角色抽取历史
        String record = String.format("角色单抽,第%d抽,获得:%s,稀有度:%d星",
                characterPullHistory.size() + 1, character.getName(), characterRarity);
        characterPullHistory.add(record);

        // 重置角色保底计数器
        if (characterRarity == 5) {
            characterPitySystem.resetPity(5);
        } else if (characterRarity == 4) {
            characterPitySystem.resetPity(4);
        }

        System.out.println("获得角色: " + character.getName() + " (" + comeOnJustStarrrrrrrrs(characterRarity) + ")");
        character.displayInfo();

        return character;
    }

    public List<Character> pullTenCharacter() {
        System.out.println("=== 十连祈愿（角色）===");
        List<Character> results = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            results.add(pullSingleCharacter());
        }

        // 统计角色结果
        long fiveStarCount = results.stream()
                .filter(character -> character instanceof FiveStarCharacter)
                .count();
        long fourStarCount = results.stream()
                .filter(character -> character instanceof FourStarCharacter)
                .count();

        System.out.println("角色十连统计 - 5星角色: " + fiveStarCount + "个, 4星角色: " + fourStarCount + "个");
        return results;
    }

    private int determineCharacterRarity() {
        // 使用独立的角色保底系统
        if (characterPitySystem.getFiveStarPity() >= 90) {
            return 5; // 角色5星硬保底
        }
        if (characterPitySystem.getFourStarPity() >= 10) {
            return 4; // 角色4星保底
        }

        // 使用概率表决定角色稀有度
        double random = Math.random();
        double cumulative = 0.0;

        // 检查5星概率
        if (random < 0.006) { // 角色5星概率0.6%
            return 5;
        }
        cumulative += 0.006;

        // 检查4星概率
        if (random < cumulative + 0.051) { // 角色4星概率5.1%
            return 4;
        }

        // 默认4星（假设角色抽卡没有3星）
        return 4;
    }

    private Character getRandomCharacterByRarity(int rarity) {

        switch (rarity) {
            case 5:
                // 五星角色
                String[] fiveStarNames = {"姬子", "瓦尔特", "布洛妮娅", "杰帕德", "希儿", "景元"};
                return new FiveStarCharacter(fiveStarNames[random.nextInt(fiveStarNames.length)]);

            case 4:
            default:
                // 四星角色
                String[] fourStarNames = {"希露瓦", "佩拉", "卢卡", "虎克", "素裳", "阿兰"};
                return new FourStarCharacter(fourStarNames[random.nextInt(fourStarNames.length)], true);
        }
    }

    private int determineRarity() {
        // 硬保底检查
        if (pityCounter5Star >= 90) {
            return 5;
        }
        if (pityCounter4Star >= 10) {
            return 4;
        }

        // 使用概率表决定稀有度
        double random = Math.random();
        double cumulative = 0.0;

        // 检查5星概率
        if (random < probabilityTable[2][2]) {
            return 5;
        }
        cumulative += probabilityTable[2][2];

        // 检查4星概率
        if (random < cumulative + probabilityTable[1][2]) {
            return 4;
        }

        // 默认3星
        return 3;
    }

    private Item getRandomItemByRarity(int rarity) {
        switch (rarity) {
            case 5:
                // 50%概率光锥，50%概率材料
                if (random.nextBoolean()) {
                    String[] names = {"银河铁道之夜", "无可取代的东西", "但战斗还未结束"};
                    String[] paths = {"智识", "毁灭", "同谐"};
                    LightCone lc = new LightCone(names[random.nextInt(names.length)],
                            paths[random.nextInt(paths.length)]);
                    lc.setRequiredLevel(60);
                    lc.setValue(1000);
                    return lc;
                } else {
                    String[] names = {"传说材料", "史诗材料", "稀有材料"};
                    MaterialItem item = new MaterialItem(names[random.nextInt(names.length)],
                            "稀有材料", 5, 800);
                    return item;
                }

            case 4:
                // 50%概率光锥，50%概率材料
                if (random.nextBoolean()) {
                    String[] names = {"早餐的仪式感", "唯有沉默", "记忆中的模样"};
                    String[] paths = {"智识", "巡猎", "同谐"};
                    LightCone lc = new LightCone(names[random.nextInt(names.length)],
                            paths[random.nextInt(paths.length)]);
                    lc.setRequiredLevel(40);
                    lc.setValue(400);
                    return lc;
                } else {
                    String[] names = {"高级材料", "中级材料", "基础材料"};
                    MaterialItem item = new MaterialItem(names[random.nextInt(names.length)],
                            "普通材料", 4, 300);
                    return item;
                }

            case 3:
            default:
                String[] names = {"信用点", "冒险记录", "旅情见闻", "提纯以太"};
                String[] types = {"货币", "经验材料", "经验材料", "突破材料"};
                int[] rarities = {1, 1, 2, 3};
                int index = random.nextInt(names.length);
                return new MaterialItem(names[index],
                        types[index],
                        rarities[index],
                        100);
        }
    }

    public void checkPity() {
        // 检查5星硬保底
        if (pityCounter5Star >= 90) {
            System.out.println("触发5星硬保底！");
            guaranteed5Star = true;
        }

        // 检查4星保底
        if (pityCounter4Star >= 10) {
            System.out.println("触发4星保底！");
        }

        // 使用PitySystem检查保底
        if (pitySystem.checkGuarantee()) {
            System.out.println("物品PitySystem检测到保底触发");
        }
    }

    public void updateProbabilities() {
        // 软保底机制：75抽后每抽增加概率
        if (pityCounter5Star >= 75) {
            int softPityPulls = pityCounter5Star - 74; // 75抽时开始
            double increase = softPityPulls * 0.06; // 每抽增加6%
            probabilityTable[2][2] = 0.006 + increase; // 基础0.6%

            // 确保不超过1
            if (probabilityTable[2][2] > 1.0) {
                probabilityTable[2][2] = 1.0;
            }

            // 调整其他概率
            probabilityTable[0][2] = 0.943 - (probabilityTable[2][2] - 0.006);
            probabilityTable[1][2] = 0.051; // 4星概率不变

            System.out.println(String.format("软保底生效：5星概率提升至 %.2f%%", probabilityTable[2][2] * 100));
        } else {
            // 重置为标准概率
            probabilityTable[0][2] = 0.943;
            probabilityTable[1][2] = 0.051;
            probabilityTable[2][2] = 0.006;
        }
    }

//    public void saveHistoryToFile() {
//        FileHandler fileHandler = new FileHandler();
//
//        // 合并物品和角色的抽卡记录
//        String data = "=== 抽卡历史记录 ===\n\n";
//
//        data += "物品抽取记录：\n";
//        data += "================\n";
//        for (String record : pullHistory) {
//            data += record + "\n";
//        }
//
//        data += "\n角色抽取记录：\n";
//        data += "================\n";
//        for (String record : characterPullHistory) {
//            data += record + "\n";
//        }
//
//        data += "\n=== 统计信息 ===\n\n";
//        data += "物品抽取统计：\n";
//        data += "总抽数: " + pullHistory.size() + "\n";
//        data += "当前5星保底: " + pityCounter5Star + "/90\n";
//        data += "当前4星保底: " + pityCounter4Star + "/10\n";
//        data += "5星保底状态: " + (guaranteed5Star ? "触发" : "未触发") + "\n";
//
//        data += "\n角色抽取统计：\n";
//        data += "总抽数: " + characterPullHistory.size() + "\n";
//        data += "角色5星保底: " + characterPitySystem.getFiveStarPity() + "/90\n";
//        data += "角色4星保底: " + characterPitySystem.getFourStarPity() + "/10\n";
//        data += "角色保底标志: " + characterPitySystem.getGuaranteeFlag() + "\n";
//
//        boolean success = fileHandler.exportToTXT(data, "gacha_history.txt");
//        if (success) {
//            System.out.println("抽卡记录已保存到文件");
//        } else {
//            System.out.println("保存抽卡记录失败");
//        }
//    }

    // Getter方法
    public double[][] getProbabilityTable() { return probabilityTable; }
    public int getPityCounter5Star() { return pityCounter5Star; }
    public int getPityCounter4Star() { return pityCounter4Star; }
    public boolean isGuaranteed5Star() { return guaranteed5Star; }
    public List<String> getPullHistory() { return new ArrayList<>(pullHistory); }
    public List<String> getCharacterPullHistory() { return new ArrayList<>(characterPullHistory); }
    public PitySystem getPitySystem() { return pitySystem; }
    public PitySystem getCharacterPitySystem() { return characterPitySystem; }

    // 显示统计信息
    public void printStatistics() {
        System.out.println("=== 物品抽取统计 ===");
        System.out.println("总抽数: " + pullHistory.size());
        System.out.println("当前5星保底: " + pityCounter5Star + "/90");
        System.out.println("当前4星保底: " + pityCounter4Star + "/10");
        System.out.println("5星保底状态: " + (guaranteed5Star ? "触发" : "未触发"));

        System.out.println("\n=== 角色抽取统计 ===");
        System.out.println("总抽数: " + characterPullHistory.size());
        System.out.println("角色5星保底: " + characterPitySystem.getFiveStarPity() + "/90");
        System.out.println("角色4星保底: " + characterPitySystem.getFourStarPity() + "/10");
        System.out.println("角色保底标志: " + characterPitySystem.getGuaranteeFlag());

        // 计算概率
        if (pullHistory.size() > 0) {
            long fiveStarItems = pullHistory.stream()
                    .filter(record -> record.contains("5星保底:0"))
                    .count();
            System.out.println(String.format("\n物品5星出货率: %.2f%%",
                    (fiveStarItems * 100.0) / pullHistory.size()));
        }

        if (characterPullHistory.size() > 0) {
            long fiveStarCharacters = characterPullHistory.stream()
                    .filter(record -> record.contains("稀有度:5星"))
                    .count();
            System.out.println(String.format("角色5星出货率: %.2f%%",
                    (fiveStarCharacters * 100.0) / characterPullHistory.size()));
        }
    }

    private String comeOnJustStarrrrrrrrs (int numOfStars) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < numOfStars; i++) {
            builder.append("★");
        }
        return builder.toString();
    }
}