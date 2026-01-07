package entities.abs;

/**
 * Represents an abstract battle unit entity in game.
 * Extends {@link GameEntity} with combat-specific attributes and behaviors.
 * Battle units have HP, attack, defense, and speed statistics, and can participate in combat by attacking other units and using skills.
 * <p>
 * This class provides basic battle functions including damage calculation, healing, and health management. Subclasses must implement specific skill behaviors.
 * </p>
 * @author Shaoyang Chen
 * @version 1.4.1
 * @see GameEntity
 */
public abstract class BattleUnit extends GameEntity {

    /** maxHP of this battle unit. */
    protected int maxHP;

    /** Current HP of this battle unit. */
    protected int currentHP;

    /** Attack power of this battle unit. */
    protected int attack;

    /** Defense that reduces incoming damage. */
    protected int defense;

    /** Speed that may affect turn order in combat. */
    protected int speed;

    /**
     * Constructs a BattleUnit with default attribute values.
     * Default values: maxHP=100, currentHP=100, attack=10, defense=5, speed=10.
     * The unit's name and ID are initialized by the superclass' default constructor.
     */
    public BattleUnit() {
        super();
        this.maxHP = 100;
        this.currentHP = maxHP;
        this.attack = 10;
        this.defense = 5;
        this.speed = 10;
    }

    /**
     * Constructs a BattleUnit with specified name, maxHP, and attack values.
     * Defense and speed are set to default values.
     *
     * @param name  the name of this battle unit
     * @param maxHP the maxHP for this unit
     * @param attack the attack power value for this unit
     */
    public BattleUnit(String name, int maxHP, int attack) {
        super(name, nextId);
        this.maxHP = maxHP;
        this.currentHP = maxHP;
        this.attack = attack;
        this.defense = 5;
        this.speed = 10;
    }

    /**
     * Constructs a BattleUnit with all combat attributes specified.
     *
     * @param name    the name of this battle unit
     * @param maxHP   the maxHP for this unit
     * @param attack  the attack power value for this unit
     * @param defense the defense value that reduces incoming damage
     * @param speed   the speed value that may affect turn order in battle
     */
    public BattleUnit(String name, int maxHP, int attack, int defense, int speed) {
        super(name, nextId);
        this.maxHP = maxHP;
        this.currentHP = maxHP;
        this.attack = attack;
        this.defense = defense;
        this.speed = speed;
    }

    /**
     * Returns the maxHP value.
     *
     * @return the maxHP value
     */
    public int getMaxHP() { return maxHP; }

    /**
     * Sets the maxHP value.
     * If the current HP exceeds the new max, current HP will be reduced to match the new max.
     *
     * @param maxHP the new maxHP value
     */
    public void setMaxHP(int maxHP) {
        this.maxHP = maxHP;
        if (currentHP > maxHP) {
            currentHP = maxHP;
        }
    }

    /**
     * Returns the current HP value.
     *
     * @return the current HP value
     */
    public int getCurrentHP() { return currentHP; }

    /**
     * Sets the current HP value.
     * The value will be automatically set between 0 and the maxHP.
     *
     * @param currentHP the new current HP value
     */
    public void setCurrentHP(int currentHP) {
        this.currentHP = Math.min(Math.max(currentHP, 0), maxHP);
    }

    /**
     * Returns the attack value.
     *
     * @return the attack value
     */
    public int getAttack() { return attack; }

    /**
     * Sets the attack value.
     *
     * @param attack the new attack value
     */
    public void setAttack(int attack) { this.attack = attack; }

    /**
     * Returns the defense value.
     *
     * @return the defense value
     */
    public int getDefense() { return defense; }

    /**
     * Sets the defense value.
     *
     * @param defense the new defense value
     */
    public void setDefense(int defense) { this.defense = defense; }

    /**
     * Returns the speed value.
     *
     * @return the speed value
     */
    public int getSpeed() { return speed; }

    /**
     * Sets the speed value.
     *
     * @param speed the new speed value
     */
    public void setSpeed(int speed) { this.speed = speed; }

    /**
     * Attacks a target battle unit.
     * Calculates damage based on this unit's attack minus the target's defense, min damage is 1. Prints battle messages to standard output.
     *
     * @param target the entity to attack, cannot be null
     */
    public void attack(BattleUnit target) {
        if (target == null) {
            System.out.println(name + " Tried to attack, but target is null.");
            return;
        }

        int damage = Math.max(attack - target.defense, 1);
        System.out.println(name + " Caused " + damage + " to " + target.name + "!");
        target.takeDamage(damage);
    }

    /**
     * Reduces this unit's HP by the specified damage.
     * HP will not drop below 0. Print damage information to standard output.
     * If HP reaches 0 or below, a death message will be printed.
     *
     * @param damage the amount of damage to take
     */
    public void takeDamage(int damage) {
        currentHP = Math.max(currentHP - damage, 0);
        System.out.println(name + " take " + damage + " damage, remaining HP: " + currentHP);

        if (currentHP <= 0) {
            System.out.println(name + " is dead!");
        }
    }

    /**
     * Uses this unit's special skill.
     * This is an abstract method, subclasses must define unique skills.
     */
    public abstract void useSkill();

    /**
     * Checks if this unit is still alive (has a positive HP).
     *
     * @return true if current HP > 0, false otherwise
     */
    public boolean isAlive() {
        return currentHP > 0;
    }

    /**
     * Heals this unit by the specified amount.
     * Current HP increases by the healing amount, but will not exceed maxHP.
     * Prints healing information to standard output.
     *
     * @param amount the amount of HP to heal
     */
    public void heal(int amount) {
        currentHP = Math.min(currentHP + amount, maxHP);
        System.out.println(name + " healed " + amount + " HP, current HP: " + currentHP);
    }

    /**
     * Returns a CSV of this battle unit's attributes.
     * The format includes the superclass CSV data followed by the battle attributes.
     *
     * @return a string containing all battle unit attributes
     * @see GameEntity#toCSVFormat()
     */
    @Override
    public String toCSVFormat() {
        String text = maxHP + "," + currentHP + "," + attack + "," + defense + "," + speed;
        return super.toCSVFormat() + text;
    }
}