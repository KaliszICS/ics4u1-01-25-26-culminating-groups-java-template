package systems.gacha;

public class PitySystem {
    private int fiveStarPity;
    private int fourStarPity;
    private boolean guaranteeFlag;

    public PitySystem() {
        fiveStarPity = 0;
        fourStarPity = 0;
        guaranteeFlag = false;
    }

    public void incrementPity() {
        fiveStarPity++;
        fourStarPity++;
    }

    public void resetPity(int rarity) {
        switch (rarity) {
            case 5:
                fiveStarPity = 0;
                break;
            case 4:
                fourStarPity = 0;
                break;
        }
    }

    public boolean checkGuarantee() {
        // 检查5星硬保底
        if (fiveStarPity >= 90) {
            guaranteeFlag = true;
            return true;
        }

        // 检查4星保底
        if (fourStarPity >= 10) {
            return true;
        }

        return false;
    }

    public double calculateSoftPity() {
        // 75抽后开始计算软保底概率
        if (fiveStarPity < 75) {
            return 0.006; // 基础概率0.6%
        }

        int softPityCount = fiveStarPity - 74; // 75抽时开始计算
        double increasedRate = 0.006 + (softPityCount * 0.06); // 每抽增加6%

        // 确保概率不超过100%
        return Math.min(increasedRate, 1.0);
    }

    // Getter方法
    public int getFiveStarPity() { return fiveStarPity; }
    public int getFourStarPity() { return fourStarPity; }
    public boolean getGuaranteeFlag() { return guaranteeFlag; }
}