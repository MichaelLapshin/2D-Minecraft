import java.util.Map;

public class HostileMob extends Mob {
    // Variables
    private static final double ATTACK_RANGE = 2.5;
    private int damage;

    // Path related variables
    private int pathFindRange;

    public HostileMob(World world, int id, double x, double y, double vx, double vy, int health, int maxHealth, double walkingSpeed, double jumpPower, int damage, int pathFindRange) {
        super(world, id, x, y, vx, vy, health, maxHealth, walkingSpeed, jumpPower);
        this.damage = damage;
        this.pathFindRange = pathFindRange;
        this.setR(Math.PI / 2);
    }

    public HostileMob(World world, int maxHealth, double walkingSpeed, double jumpPower, int damage, int pathFindRange) {
        super(world, maxHealth, walkingSpeed, jumpPower);
        this.damage = damage;
        this.pathFindRange = pathFindRange;
        this.setR(Math.PI / 2);
    }

    @Override
    public void move() {
        super.move();
        // Hostile mob AI
        // Path find to player
        Player player = null;
        int nearest = Integer.MAX_VALUE;
        for (Map.Entry<Integer, Player> entry : world.getPlayers().entrySet()) {
            int distance = (int) (Math.abs(entry.getValue().getX() - getX()) + Math.abs(entry.getValue().getY() - getY()));
            if (distance < pathFindRange && distance < nearest) {
                nearest = distance;
                player = entry.getValue();
            }
        }

        if (player != null && nearest < pathFindRange) {
            if (player.getX() > getX()) {
                moveRight() ;
            } else if (player.getX() < getX()) {
                moveLeft();
            }
            walking = false;
        }else if (player == null){
            walking = true;
        }

        if(this instanceof Zombie && nearest < 1 && getTick() % Zombie.ATTACK_COOLDOWN_SECONDS == 0){
            player.damage(Zombie.ATTACK_DAMAGE);

            player.addVy(Mob.VERTICAL_KNOCK_BACK);
            if (this.getX() - player.getX() > 0) player.addVx(-Mob.HORIZONTAL_KNOCK_BACK);
            else if (this.getX() - player.getX() < 0) player.addVx(Mob.HORIZONTAL_KNOCK_BACK);
        }

    }
}
