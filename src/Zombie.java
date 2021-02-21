import java.awt.*;
import java.awt.image.BufferedImage;

public class Zombie extends HostileMob {
    public static final int ATTACK_DAMAGE = 15;
    public static final int ATTACK_COOLDOWN_SECONDS = (int) (1.2 * WorldServer.TICK_RATE);
    private static final int PATH_FIND_RANGE = 12;

    private static final int MAX_HEALTH = 100;
    private static final double SPRINT_SPEED = 0.13;
    private static final double WALKING_SPEED = 0.05;
    private static final double JUMP_POWER = 0.7;

    private static final double ZOMBIE_HEIGHT = 2.0;
    public final static double[] HIT_BOX = new double[]{-0.3, -0.5, 0.6, ZOMBIE_HEIGHT};


    public Zombie(World world,int id, double x, double y, double vx, double vy, int health) {
        super(world,id, x, y, vx, vy, health, MAX_HEALTH, WALKING_SPEED, JUMP_POWER, ATTACK_DAMAGE, PATH_FIND_RANGE);
        setSkin(DrawTools.getRandomHostileModel());
        setHeight(ZOMBIE_HEIGHT);
        setSprintSpeed(SPRINT_SPEED);
        setHitBox(HIT_BOX);
    }

    public Zombie(World world) {
        super(world, MAX_HEALTH, WALKING_SPEED, JUMP_POWER, ATTACK_DAMAGE, PATH_FIND_RANGE);
        setSkin(DrawTools.getRandomHostileModel());
        setHeight(ZOMBIE_HEIGHT);
        setSprintSpeed(SPRINT_SPEED);
        setHitBox(HIT_BOX);
    }
}
