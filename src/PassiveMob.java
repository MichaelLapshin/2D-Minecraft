public class PassiveMob extends Mob{


    public PassiveMob(World world, int id, double x, double y, double vx, double vy, int health, int maxHealth, double walkingSpeed, double jumpPower, double sprintSpeed){
        super(world,id, x, y, vx, vy, health, maxHealth, walkingSpeed, jumpPower);
        setSprintSpeed(sprintSpeed);
    }

    public PassiveMob(World world, int maxHealth, double walkingSpeed, double jumpPower, double sprintSpeed){
        super(world, maxHealth, walkingSpeed, jumpPower);
        setSprintSpeed(sprintSpeed);
    }

    @Override
    public int damage(int damage) {
        super.damage(damage);
        // TODO make passive mobs sprint after taking damage
        // Makes the passive sprint when taken damage

        return getHealth();
    }

    @Override
    public void move() {
        super.move();


    }
}
