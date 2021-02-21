import java.awt.*;
import java.util.HashMap;
import java.util.LinkedList;

public class Block {
    // Block related data variables
    private int x, y;
    private double health;
    private int maxHealth;
    private byte blockType;
    private boolean background;
    private int timeToDie;
    private final int NEW_TIME_TO_DIE = 60; // Counted in terms of ticks
    private int animationFrame = 0;

    public Block(int x, int y, byte blockType) {
        this.blockType = blockType;

        this.x = x;
        this.y = y;

        this.maxHealth = BlockData.getBlockHealth(this.blockType);
        this.health = BlockData.getBlockHealth(this.blockType);

        if (blockType < 0) this.background = true;
    }

    // Updates the block by one tick
    // Animated blocks would override this method
    public void tick() {
        if (timeToDie <= 0) {
            health = 0;
        } else {
            this.timeToDie--;
        }
    }

    public boolean damage(double damage) { // Returns true if the block is dead
        this.timeToDie = this.NEW_TIME_TO_DIE;
        this.health -= damage;
        if (this.health <= 0) {
            return true;
        }
        return false;
    }

    public boolean isAlive(){return health > 0;}

    public byte getBlockType() {
        return blockType;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    // Draws the block cracking (depending on the health)
    public void draw(Graphics graphics, Dimension dimension, int renderDistance, double drawX, double drawY, int animationFrame) {
        //System.out.println("!!! Block.draw() is not complete yet!");
    }

    public int getAnimationFrame() {
        return (int) (10 - (health * 10) / maxHealth);
    }

    public void setAnimationFrame(int animationFrame) {
        this.animationFrame = animationFrame;
    }
}
