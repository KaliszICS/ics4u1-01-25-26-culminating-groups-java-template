package systems.gacha;

import entities.items.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class GachaSystem {
    private double[][] probabilityTable; // Probability table for different rarities
    private int pityCounter5Star;
    private int pityCounter4Star;
    private boolean guaranteed5Star; // pity 5 star
    private boolean guaranteed4Star; // pity 4 star
    private List<String> pullHistory;
    private Random random;
    private SimpleDateFormat dateFormat;

    // predefined item pool
    private List<CharacterItem> characterPool;
    private List<LightConeItem> lightConePool;
    private List<MaterialItem> materialPool;

    public GachaSystem() {
        // probability table [rarity][status] - status "0": base chance, status "1" : soft pity possibility
        this.probabilityTable = new double[3][2];
        initializeProbabilities();

        this.pityCounter5Star = 0;
        this.pityCounter4Star = 0;
        this.guaranteed5Star = false;
        this.guaranteed4Star = false;
        this.pullHistory = new ArrayList<>();
        this.random = new Random();
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // initialize pool
        initializePools();
    }

    private void initializeProbabilities() {
        // bace chances
        probabilityTable[0][0] = 0.006; // 5⭐ 0.6%
        probabilityTable[1][0] = 0.051; // 4⭐ 5.1%
        probabilityTable[2][0] = 0.943; // 3⭐ 94.3%

        // soft chances adds
        probabilityTable[0][1] = 0.0; // base softchance = 0
        probabilityTable[1][1] = 0.0; // 4⭐ softchance addon
    }

    private void initializePools() {
        characterPool = new ArrayList<>();
        lightConePool = new ArrayList<>();
        materialPool = new ArrayList<>();

        // 5⭐ up chances
        characterPool.add(new CharacterItem("Jingyuan", 1001, 5, true, "Lightning", "Erudition"));
        characterPool.add(new CharacterItem("Xier", 1002, 5, false, "Quantum", "The Hunt"));

        // 4⭐ up chances
        characterPool.add(new CharacterItem("Tingyun", 2001, 4, true, "Lightning", "Harmony"));
        characterPool.add(new CharacterItem("Surang", 2002, 4, true, "Physical", "The Hunt"));
        characterPool.add(new CharacterItem("Peila", 2003, 4, false, "Ice", "Nihility"));

        // 5⭐ lightcones
        lightConePool.add(new LightConeItem("Before Dawn", 3001, 5, true, 582, 463, 1058, "Increase combat skill and finishing move damage"));
        lightConePool.add(new LightConeItem("Night on the Milky Way", 3002, 5, false, 582, 463, 1058, "Give additional damage to multiple enemies."));

        // 4⭐ lightcones
        lightConePool.add(new LightConeItem("Dance! Dance! Dance!", 4001, 4, true, 423, 396, 953, "Faster action and moves"));
        lightConePool.add(new LightConeItem("Swordplay", 4002, 4, false, 423, 396, 953, "Increasing damage to the same enemy"));

        // 3⭐ materials
        materialPool.add(new MaterialItem("Credit", 5001, 3, false, "Currency", 1000));
        materialPool.add(new MaterialItem("Exp. Material", 5002, 3, false, "Enhancement", 5));
        materialPool.add(new MaterialItem("Relic Remains", 5003, 3, false, "Ascension", 3));
    }

    public Item pullSingle() {
        // update pity counter
        pityCounter5Star++;
        pityCounter4Star++;

        // check and update soft pity
        updateSoftPity();

        // determine rarity
        int rarity = determineRarity();

        // pull
        Item pulledItem = getItemByRarity(rarity);

        // update pity status
        updatePityStatus(rarity, pulledItem);

        // record history
        String historyEntry = String.format("%s - %s (★%d) - Pity: 5★:%d, 4★:%d",
                dateFormat.format(new Date()),
                pulledItem.getName(),
                pulledItem.getRarity(),
                pityCounter5Star,
                pityCounter4Star);

        // 2D array to store
        String[][] historyArray = convertHistoryTo2DArray();

        pullHistory.add(historyEntry);
        System.out.println(historyEntry);

        // display item info
        pulledItem.displayInfo();

        return pulledItem;
    }

    public List<Item> pullTen() {
        List<Item> results = new ArrayList<>();
        System.out.println("********** Ten Pulls **********");

        for (int i = 0; i < 10; i++) {
            System.out.printf("\nPull %d :%n", i + 1);
            Item item = pullSingle();
            results.add(item);
        }

        System.out.println("\n********** Ten Pulls ended **********");
        return results;
    }

    private void updateSoftPity() {
        // 5⭐ softchances: add on pityCount 74
        if (pityCounter5Star >= 74) {
            int softPityCount = pityCounter5Star - 73;
            probabilityTable[0][1] = softPityCount * 0.06; // add 6% per pull
        } else {
            probabilityTable[0][1] = 0.0;
        }

        // 4⭐ softchances: add on pityCount 9
        if (pityCounter4Star >= 9) {
            probabilityTable[1][1] = 0.5; // add 50% per pull
        } else {
            probabilityTable[1][1] = 0.0;
        }
    }

    private int determineRarity() {
        // check for hard pity (= 90)
        if (pityCounter5Star >= 90) {
            return 5; // 5⭐
        }
        if (pityCounter4Star >= 10) {
            return 4; // 4⭐
        }

        double rand = random.nextDouble();
        double current5StarProb = probabilityTable[0][0] + probabilityTable[0][1];
        double current4StarProb = probabilityTable[1][0] + probabilityTable[1][1];

        if (rand < current5StarProb) {
            return 5;
        } else if (rand < current5StarProb + current4StarProb) {
            return 4;
        } else {
            return 3;
        }
    }

    private Item getItemByRarity(int rarity) {
        List<Item> eligibleItems = new ArrayList<>();

        // collect rarity
        for (CharacterItem charItem : characterPool) {
            if (charItem.getRarity() == rarity) {
                eligibleItems.add(charItem);
            }
        }

        for (LightConeItem lcItem : lightConePool) {
            if (lcItem.getRarity() == rarity) {
                eligibleItems.add(lcItem);
            }
        }

        for (MaterialItem matItem : materialPool) {
            if (matItem.getRarity() == rarity) {
                eligibleItems.add(matItem);
            }
        }

        if (eligibleItems.isEmpty()) {
            // If no item of the corresponding rarity is found, a default material will be returned.
            return new MaterialItem("Default material", 9999, rarity, false, "Default", 1);
        }

        // check if pity + UP guarantee
        return selectItemWithGuarantee(eligibleItems, rarity);
    }

    private Item selectItemWithGuarantee(List<Item> eligibleItems, int rarity) {
        List<Item> upItems = new ArrayList<>();
        List<Item> nonUpItems = new ArrayList<>();

        // seperate UP / non-UP
        for (Item item : eligibleItems) {
            if (item.isUpItem()) {
                upItems.add(item);
            } else {
                nonUpItems.add(item);
            }
        }

        // 5⭐ pity ruleset
        if (rarity == 5) {
            if (guaranteed5Star && !upItems.isEmpty()) {
                // big pity (Non UP 5⭐ prevously)
                return upItems.get(random.nextInt(upItems.size()));
            } else {
                // small pity 50% UP 5⭐ item
                if (random.nextDouble() < 0.5 && !upItems.isEmpty()) {
                    return upItems.get(random.nextInt(upItems.size()));
                } else if (!nonUpItems.isEmpty()) {
                    return nonUpItems.get(random.nextInt(nonUpItems.size()));
                }
            }
        }

        // 4⭐ pity rules
        if (rarity == 4 && guaranteed4Star && !upItems.isEmpty()) {
            return upItems.get(random.nextInt(upItems.size()));
        }

        // selecte
        if (!eligibleItems.isEmpty()) {
            return eligibleItems.get(random.nextInt(eligibleItems.size()));
        }

        return null;
    }

    private void updatePityStatus(int rarity, Item item) {
        if (rarity == 5) {
            pityCounter5Star = 0;
            pityCounter4Star = 0; // 5 star will reset 4 star pity count

            // if Non up 5⭐ then next 5⭐ will be an UP
            if (!item.isUpItem()) {
                guaranteed5Star = true;
            } else {
                guaranteed5Star = false;
            }
        } else if (rarity == 4) {
            pityCounter4Star = 0;

            // if Non up 4⭐ then next 4⭐ will be an UP
            if (!item.isUpItem()) {
                guaranteed4Star = true;
            } else {
                guaranteed4Star = false;
            }
        }
    }

    //check pity is only for debugs

    private void checkPity() {
        System.out.println("Current pity status:");
        System.out.printf("5⭐ pity count: %d/90%n", pityCounter5Star);
        System.out.printf("4⭐ pity count: %d/10%n", pityCounter4Star);
        System.out.printf("5⭐ up guarantee status: %s%n", guaranteed5Star ? "True" : "False");
        System.out.printf("4⭐ up guarantee status: %s%n", guaranteed4Star ? "True" : "False");

        if (pityCounter5Star >= 74) {
            System.out.printf("软保底激活! 当前5星概率: %.2f%%%n",
                    (probabilityTable[0][0] + probabilityTable[0][1]) * 100);
        }
    }

    //CLASS displayProbabilities WAS ADDED

    // display current probabilities
    public void displayProbabilities() {
        System.out.println("Current probabilities:");
        System.out.printf("5⭐: %.3f%% (Base: %.3f%% + Soft: %.3f%%)%n",
                (probabilityTable[0][0] + probabilityTable[0][1]) * 100,
                probabilityTable[0][0] * 100,
                probabilityTable[0][1] * 100);
        System.out.printf("4⭐: %.3f%% (Base: %.3f%% + Soft: %.3f%%)%n",
                (probabilityTable[1][0] + probabilityTable[1][1]) * 100,
                probabilityTable[1][0] * 100,
                probabilityTable[1][1] * 100);
        System.out.printf("3⭐: %.3f%%%n", probabilityTable[2][0] * 100);
    }

    // 2D Array to deal with history data
    private String[][] convertHistoryTo2DArray() {
        if (pullHistory.isEmpty()) {
            return new String[0][0];
        }

        // create history [records count][info]
        int rows = Math.min(pullHistory.size(), 100); // save recent 100 only
        String[][] historyArray = new String[rows][4];

        int startIndex = Math.max(0, pullHistory.size() - rows);
        for (int i = 0; i < rows; i++) {
            String record = pullHistory.get(startIndex + i);
            String[] parts = record.split(" - ");

            if (parts.length >= 3) {
                historyArray[i][0] = parts[0]; // time stamp
                historyArray[i][1] = parts[1]; // item name
                historyArray[i][2] = extractRarity(parts[1]); // ⭐
                historyArray[i][3] = parts[2]; // pity info
            }
        }

        return historyArray;
    }

    private String extractRarity(String itemName) {
        if (itemName.contains("★5") || itemName.contains("(★5)")) {
            return "5";
        } else if (itemName.contains("★4") || itemName.contains("(★4)")) {
            return "4";
        } else {
            return "3";
        }
    }

    // save to CSV
    public void saveHistoryToFile(String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            // csv head writes
            writer.write("Timestamp,Item Name,Rarity,Is UP,Pity 5★,Pity 4★");
            writer.newLine();

            // write history
            for (String record : pullHistory) {
                String[] parts = record.split(" - ");
                if (parts.length >= 3) {
                    String itemName = parts[1];
                    String rarity = extractRarity(itemName);
                    String isUp = itemName.contains("[UP]") ? "Yes" : "No";

                    String csvLine = String.format("%s,%s,%s,%s,%d,%d",
                            parts[0], parts[1], rarity, isUp, pityCounter5Star, pityCounter4Star);
                    writer.write(csvLine);
                    writer.newLine();
                }
            }

            System.out.println("Pull history has save to: " + filename);
        } catch (IOException e) {
            System.err.println("Save with error: " + e.getMessage());
        }
    }

    // save to TXT
    public void saveHistoryToTxt(String filename) {
        try (BufferedWriter fw = new BufferedWriter(new FileWriter(filename))) {
            fw.write("========== PULL HISTORY ==========");
            fw.newLine();
            fw.write("Total: " + pullHistory.size());
            fw.newLine();
            fw.write("Current 5⭐ pity: " + pityCounter5Star + "/90");
            fw.newLine();
            fw.write("Current 4⭐ pity: " + pityCounter4Star + "/10");
            fw.newLine();
            fw.write("Current 5⭐ UP pity: " + (guaranteed5Star ? "True" : "False"));
            fw.newLine();
            fw.write("==========================================");
            fw.newLine();
            for (String record : pullHistory) {
                fw.write(record);
                fw.newLine();
            }

            System.out.println("Pull history has save to: " + filename);
        } catch (IOException e) {
            System.err.println("Save with error: " + e.getMessage());
        }
    }

    // get history count
    public void displayStatistics() {
        System.out.println("\nPull statistics:");
        System.out.println("Total pulls: " + pullHistory.size());

        int count5Star = 0, count4Star = 0, count3Star = 0;
        for (String record : pullHistory) {
            if (record.contains("(★5)")) count5Star++;
            else if (record.contains("(★4)")) count4Star++;
            else count3Star++;
        }

        System.out.println("5⭐ Tot: " + count5Star);
        System.out.println("4⭐ Tot: " + count4Star);
        System.out.println("3⭐ Tot: " + count3Star);

        if (!pullHistory.isEmpty()) {
            System.out.println("Your 5⭐ percentage: " +  (count5Star * 100.0 / pullHistory.size()));
            System.out.println("Your 5⭐ percentage: " + (count4Star * 100.0 / pullHistory.size()));
        }
    }
}