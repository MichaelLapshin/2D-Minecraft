import java.awt.*;

public class Entity {
    // Position related variables
    private double x, y;
    private double vx, vy;
    private double r;

    private static final double GRAVITY = -2.5 / WorldServer.TICK_RATE;
    static final double FRICTION_PER_TICK = 0.06;

    private long tick;
    private int id;
    private int health;
    private int maxHealth;

    // Hit box related
    private double[] hitBox; // (x, y, width, height) .(x, y) represents the bottom-left corner. Width and height go right-up from (x, y).
    private boolean entityCollision; // Whether of not it participates in collisions with other entities
    protected World world;

    // Constructor
    public Entity(World world, int id, double x, double y, double vx, double vy, int health, int maxHealth, boolean entityCollision) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.entityCollision = entityCollision;

        // Assigns id
        if (id == -1) this.id = world.generateEntityID();
        else this.id = id;

        // Assigns health
        if (health == -1) this.health = maxHealth;
        else this.health = health;

        this.maxHealth = maxHealth;
        this.tick = 0;
        this.hitBox = new double[]{-0.5, -0.5, 1, 2};
    }

    // Constructor
    public Entity(World world, int maxHealth, boolean entityCollision) {
        this.world = world;
        this.x = 0;
        this.y = 0;
        this.vx = 0;
        this.vy = 0;
        this.entityCollision = entityCollision;
        this.id = world.generateEntityID();
        this.health = maxHealth;
        this.maxHealth = maxHealth;
        this.tick = 0;
        this.hitBox = new double[]{-0.5, -0.5, 1, 2};
    }

    /*
    * Smart moves: (to be overridden by sub-classes, potentially)
    * - If there is an ai, moves based on the at (path finds if it needs to)
    * - Changes position based on velocity
    * - Changes velocity based on gravity
    */
    public void move() {
        // Head hit
        if (vy > 0 && world.getTerrain().isSolidBlock(x, y + 2) == true) {
            vy = 0;
        }

        double velocityX = vx;

        this.vy += GRAVITY;

        // Collision with left wall
        if (world.getTerrain().isSolidBlock(x + velocityX, y) || world.getTerrain().isSolidBlock(x + velocityX, y + 1)) {
            // Walking on the ground
            if (velocityX > 0) x = (int) x + 1 - 0.0001;
            if (velocityX < 0) x = (int) x;
            velocityX = 0;
        }

        // Finds highest block below player
        int highestBlock = (int) y - 1;
        for (; highestBlock > 0; highestBlock--) {
            if (world.getTerrain().isSolidBlock(x, highestBlock) == true) {
                break;
            }
        }

        if (world.getTerrain().isSolidBlock(x, y + vy)) {
            // Walking on the ground
            y = highestBlock + 1;
            vy = 0;

            // Friction
            if (this.vx < 0) {
                if (this.vx >= -FRICTION_PER_TICK / 2) this.vx = 0;
                else this.vx += FRICTION_PER_TICK;
            } else if (this.vx > 0) {
                if (this.vx <= FRICTION_PER_TICK / 2) this.vx = 0;
                else this.vx -= FRICTION_PER_TICK;
            }
        }

        this.x += velocityX;
        this.y += vy;

        // Implements the void
        if (getY() < -40) damage(1);
    }

    public void tick() {
        tick++;
    }

    public long getTick() {
        return tick;
    }

    /////=== Drawing related ===\\\\\
    public void draw(Graphics graphics, Dimension dimension, int renderDistance, double drawX, double drawY) {
        // Nothing here
    }

    public double oscillateAnimation(double valueRange, double animationSpeed, int x) {
        return valueRange * (Math.sin(x * animationSpeed) + 1) / 2.0;
    }

    /////=== Hit box related ===\\\\\
    public boolean containsPoint(double x, double y) {
        return ((hitBox[0] + this.x) < x) && (x < (hitBox[0] + hitBox[2] + this.x)) &&
                ((hitBox[1] + this.y) < y) && (y < (hitBox[1] + hitBox[3] + this.y));
    }

    /////=== Getters ===\\\\\
    public double[] getHitBox() {
        return hitBox;
    }

    public void setHitBox(double[] hitBox) {
        this.hitBox = hitBox;
    }

    public int getId() {
        return id;
    }

    public boolean getEntityCollision() {
        return this.entityCollision;
    }

    public int getHealth() {
        return health;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }

    // Returns true if mobs is still alive, otherwise returns false
    public int damage(int damage) {
        this.health -= damage;
        if (this.health <= 0) {
            health = 0;
            return 0;
        }
        return this.health;
    }

    // Returns if the mob is alive
    public boolean isAlive() {
        return health > 0;
    }

    /////=== Movement related ===\\\\\
    // Getters
    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getVx() {
        return this.vx;
    }

    public double getVy() {
        return this.vy;
    }

    public double getR() {
        return this.r;
    }

    // Setters
    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setVx(double vx) {
        this.vx = vx;
    }

    public void setVy(double vy) {
        this.vy = vy;
    }

    public void setR(double r) {
        this.r = r;
    }

    // Adders
    public void addX(double x) {
        this.x += x;
    }

    public void addY(double y) {
        this.y += y;
    }

    public void addVx(double vx) {
        this.vx += vx;
    }

    public void addVy(double vy) {
        this.vy += vy;
    }

    public void addR(double r) {
        this.r += r;
    }


}
