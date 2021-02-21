import java.awt.*;
import java.awt.image.BufferedImage;

public class Pig extends PassiveMob {
    private static final double SPRINT_SPEED = 0.26;
    private static final double WALKING_SPEED = 0.08;

    private static final int MAX_HEALTH = 50;
    private static final double JUMP_POWER = 0.7;

    // Graphics
    private static final double PIG_HEIGHT = 1.2;
    public final static double[] HIT_BOX = new double[]{-0.3, -0.5, 0.6, PIG_HEIGHT};


    public Pig(World world, int id, double x, double y, double vx, double vy, int health) {
        super(world, id, x, y, vx, vy, health, MAX_HEALTH, WALKING_SPEED, JUMP_POWER, SPRINT_SPEED);
        setSkin(DrawTools.getRandomPassiveModel());
        setHeight(PIG_HEIGHT);
        setSprintSpeed(SPRINT_SPEED);
        setHitBox(HIT_BOX);
    }

    public Pig(World world) {
        super(world, MAX_HEALTH, WALKING_SPEED, JUMP_POWER, SPRINT_SPEED);
        setSkin(DrawTools.getRandomPassiveModel());
        setHeight(PIG_HEIGHT);
        setSprintSpeed(SPRINT_SPEED);
        setHitBox(HIT_BOX);
    }

}
