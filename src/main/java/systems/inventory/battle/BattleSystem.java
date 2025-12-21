package systems.battle;
import java.util.List;
import java.util.ArrayList;
import entities.abs.BattleUnit;
import entities.characters.Character;
import entities.enemies.Enemy;

public class BattleSystem {
    private final BattleUnit[][] battleGrid = new BattleUnit[2][4];
    private final Arraylist<BattleUnit> turnOrder = new ArrayList<>();
    private int currentTurn=0;
    private String battleState = "IN"; //when it just starts (IN), then IN_PROGRESS, WON, LOST, or DRAW

    private BattleSystem() {}

    public void initializeBattle(List<Character> playerTeam, List<Enemy> enemies) {
        for (int j = 0; j < 2; j++) {   //clear grid
            for (int i =0; i<4; i++) {
                battleGrid[j][i] = null;
            }
        }
        int col = 0; //row 0, players move left to right
        for (int i =0; i<playerTeam.size() && col<4; i++, col++) {
            battleGrid[0][col] = playerTeam.get(i);
        }
        //row 1, enemies move right to left
        col = 3;
        for (int i = 0; i <enemies.size() && col>=0; i++, col--) {
            battleGrid[1][col] = enemies.get(i);
        }

        calculateTurnOrder();
        currentTurn = 0;
        battleState = "IN_PROGRESS";
    }

    public void executeTurn() {
        if ("IN_PROGRESS".equals(battleState)) {
            return;
        }
        if (turnOrder.isEmpty()) {
            calculateTurnOrder();
            currentTurn = 0;
            if (turnOrder.isEmpty()) {
                return;
            }
        }

        BattleUnit acting = turnOrder.get(currentTurn);
        if (!acting.isAlive()) {
            advanceTurn();
            return;
        }
        BattleUnit target = pickAliveOpponent(acting);
        if (target == null) {
            if (checkBattleEnd()) {
                return;
            }
            advanceTurn();
            return;
        }

        int dmg = Damage.compute(acting.getATK(), target.getDEF(), acting.getCrit(), acting.getCritDmg());
        target.setHP(Math.max(0, target.getHP() - dmg));

        if (checkBattleEnd()) {
            return;
            advanceTurn();
        }
    }

    public void calculateTurnOrder() {
        turnOrder.clear();
        for (int j = 0; j < 2; j++) {
            for (int i = 0; i <4; i++) {
                BattleUnit u = battleGrid[j][i];
                if (u != null && u.isAlive()) {
                    turnOrder.add(u);
                }
            }
        }
        //insertion sort
        for (int i= 1; i<turnOrder.size(); i++) {
            BattleUnit key = turnOrder.get(i);
            int j = i-1;
            while (j>=0 && faster(key, turnOrder.get(j))) {
                turnOrder.set(j + 1, turnOrder.get(j));
                j--;
            }
        }
    }

    public boolean checkBattleEnd() {
        boolean playersAlive = false;
        boolean enemiesAlive = false;
        for (int j = 0; j < 2; j++) {
            for (int i = 0; i < 4; i++) {
                BattleUnit u = battleGrid[j][i];
                if (u != null && u.isAlive()) {
                    if (u.isPlayer()) {
                        playersAlive = true;
                    } else {
                        enemiesAlive = true;
                    }
                }
            }
        }
        if (!playersAlive && enemeis.Alive) {
            battleState = "LOST";
            return true;
        } else if (playersAlive && !enemiesAlive) {
            battleState = "WON";
            return true;
        } else if (!playersAlive && !enemiesAlive) {
            battleState = "DRAW";
            return true;
        }
    }

    public void useSkill(BattleUnit unit, int skillIndex) {
        if (!"IN_PROGRESS".equals(battleState) || unit==null || !unit.isAlive()) {
            return;
        }
        BattleUnit target = pickAliveOpponent(unit);
        if (target == null) {
            return;
        }

        double multi;
        if (skillIndex == 1) {
            multi = 1.5;
        } else if (skillIndex == 2) {
            multi = 2.0;
        } else {
            multi = 1.0;
        }
        int base = Damage.compute(unit.getATK(), target.getDEF(), unit.getCrit(), unit.getCritDmg());
        int dmg = (int)Math.floor(base*multi);
        target.setHP(Math.max(0, target.getHP() - Math.max(0, dmg)));

        checkBattleEnd();
    }

    //helpers

    private void advanceTurn() {
        currentTurn++;
        if (currentTurn >= turnOrder.size()) {
            calculateTurnOrder();
            currentTurn = 0;
        }
    }
    private boolean faster(BattleUnit a, BattleUnit b) {
        if (a.getSPD() != b.getSPD()) {
            return a.getSPD() > b.getSPD();
            return a.getName().compareTo(b.getName()) < 0;
        }
    }
    private BattleUnit pickAliveOpponent(BattleUnit acting) {
        int enemyRow;
        if (acting.isPlayer()) {
            enemyRow = 1;  //emeny on row 1
        } else {
            enemyRow = 0; //players row 0
        }

        for (int i = 0; i < 4; i++) {
            BattleUnit u = battleGrid[enemyRow][i];
            if (u != null && u.isAlive) {
                return u;
            }
        }
        return null;  //for when no target found ig
    }
    // //getters for UI/tests if needed (I will delete if not)
    // public BattleUnit[][] getBattleGrid() {
    //     return battleGrid;
    // }
    // public List<BattleUnit> getTurnOrder() {
    //     return new ArrayList<>(turnOrder);
    // }
    // public int getCurrentTurn() {
    //     return currentTurn;
    // }
    // public String get BattleState() {
    //     return battleState;
    // }
}
