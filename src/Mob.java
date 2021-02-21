import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Map;

public class Mob extends Entity {
    private double sprintSpeed;
    private double walkingSpeed;
    private double jumpPower;
    protected boolean walking = true;

    private int walkState = 0; // 0 = left, 1 = still, 2 = right, 3 = back, 4 = jump, 5 = fall
    public static double STAND_SENSITIVITY = 0.02;
    public static final double VERTICAL_KNOCK_BACK = 0.55;
    public static double HORIZONTAL_KNOCK_BACK = 0.4;

    // Drawing the mob
    private BufferedImage[][] skin;
    private double height;

    // Casual mob walking
    protected int walkTimer = 0;
    protected int WALK_TIMER_RESET = WorldServer.TICK_RATE * 3;
    protected int WALK_TIMER_VARIANCE = WorldServer.TICK_RATE * 3;

    public Mob(World world, int id, double x, double y, double vx, double vy, int health, int maxHealth, double walkingSpeed, double jumpPower) {
        super(world, id, x, y, vx, vy, health, maxHealth, true);

        this.walkingSpeed = walkingSpeed;
        this.jumpPower = jumpPower;
    }

    public Mob(World world, int maxHealth, double walkingSpeed, double jumpPower) {
        super(world, maxHealth, true);
        this.walkingSpeed = walkingSpeed;
        this.jumpPower = jumpPower;
    }

    public void move() {
        super.move();

        if (this instanceof HostileMob == false) walking = true;

        if (this instanceof Player == false) {
            if (walking) {
                if (walking && walkTimer <= 0) {
                    walkTimer = WALK_TIMER_RESET + (int) (Math.random() * WALK_TIMER_VARIANCE);

                    // Determines how to path find
                    if (walkState == 1) {
                        if (Math.random() > 0.5) {
                            walkState = 0;
                        } else {
                            walkState = 2;
                        }
                    } else {
                        walkState = 1;
                    }
                }

                if (walkState == 0) walkLeft();
                else if (walkState == 2) walkRight();
            }

            if (getVx() > 0 && world.getTerrain().isSolidBlock(getX() + 0.8, getY())) jump();
            else if (getVx() < 0 && world.getTerrain().isSolidBlock(getX() - 0.8, getY())) jump();

            walkTimer--;
        }

        // Mob despawning logic
        Player player = null;
        int nearest = 240;
        for (Map.Entry<Integer, Player> entry : world.getPlayers().entrySet()) {
            int distance = (int) (Math.abs(entry.getValue().getX() - getX()) + Math.abs(entry.getValue().getY() - getY()));
            if (distance < nearest) {
                nearest = distance;
                player = entry.getValue();
            }
        }

        if (this instanceof Player == false && (nearest > WorldServer.DESPAWN_DISTANCE || player == null) && Math.random() > 0.8)
            setHealth(getHealth() - (int) (2 * Math.random()));

    }

    ////=== Movement (Sets velocity) ===\\\\\
    public void moveRight() {
        setVx(sprintSpeed);
    }

    public void moveLeft() {
        setVx(-sprintSpeed);
    }

    public void jump() {
        if (world.getTerrain().isSolidBlock(getX(), getY() - 0.01) == true)
            setVy(jumpPower);
    }

    public void walkRight() {
        setVx(walkingSpeed);
    }

    public void walkLeft() {
        setVx(-walkingSpeed);
    }

    public int getWalkState() {
        return walkState;
    }

    protected boolean getWalking() {
        return this.walking;
    }

    // Returns the direction of the mob based on its velocity (for drawing purposes)
    public int getVelocityDirection() {
        if (getVx() > STAND_SENSITIVITY) return 2;
        if (getVx() < -STAND_SENSITIVITY) return 0;
        if (getVy() > STAND_SENSITIVITY) return 4;
        return 1;
    }

    /////=== Drawing related ===\\\\\
    // Animates the body part relative to entity's position and velocity
    public double animatedAngle(double angleRange, double x, double animationSpeed, double maxVelocity) {
        return -angleRange / 2 + (angleRange * (Math.sin(x * animationSpeed) + 1) / 2) * (Math.abs(getVx()) / maxVelocity);
    }

    public double animatedAngle(double angleRange, double x, double animationSpeed, double maxVelocity, boolean opposite) {
        if (opposite == false) return animatedAngle(angleRange, x, animationSpeed, maxVelocity);
        return -angleRange / 2 + (angleRange * (Math.sin(x * animationSpeed + Math.PI) + 1) / 2) * (Math.abs(getVx()) / maxVelocity);
    }

    public double degreesToRadians(double degrees) {
        return (degrees * Math.PI / 180);
    }

    public void setSkin(BufferedImage[][] skin) {
        this.skin = skin;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public void setSprintSpeed(double speed) {
        this.sprintSpeed = speed;
    }

    @Override
    public void draw(Graphics graphics, Dimension dimension, int renderDistance, double drawX, double drawY) {
        super.draw(graphics, dimension, renderDistance, drawX, drawY);

        // Does not render anything outside of render distance
        if (Math.abs(getX() - drawX) > renderDistance + 4 || Math.abs(getY() - drawY) > renderDistance + 4) return;

        int screenX = (int) DrawTools.game2ScreenX(drawX, dimension, renderDistance, getX());
        int screenY = (int) DrawTools.game2ScreenY(drawY, dimension, renderDistance, getY());
        int pixel4 = (int) DrawTools.game2screenLength(dimension, renderDistance, height / 8.0);
        int halfBlockLength = (int) DrawTools.game2screenLength(dimension, renderDistance, 0.5);

        double maxSpeed;
        if(Math.abs(getVx()) > walkingSpeed){
            maxSpeed = sprintSpeed;
        }else{
            maxSpeed = walkingSpeed;
        }

        Graphics2D graphics2D = (Graphics2D) graphics;

        int side = getVelocityDirection();

        if (side == 0) { // Walking left
//            Graphics2D graphics2D = (Graphics2D) (GameGraphics.getGraphics());
//            graphics2D.rotate(angle, positionX, positionY);
            // Bottom, Clock-Wise

            double angle = animatedAngle(degreesToRadians(60), getX(), 2, maxSpeed, false);
            graphics2D.rotate(angle, screenX, screenY + halfBlockLength - pixel4 * 2.5);
            graphics2D.drawImage(skin[0][4], screenX - pixel4 / 2, screenY + halfBlockLength - pixel4 * 3, pixel4, pixel4 * 3, null); //  right leg
            graphics2D.rotate(-angle, screenX, screenY + halfBlockLength - pixel4 * 2.5);

            angle = animatedAngle(degreesToRadians(60), getX(), 2, maxSpeed, true);
            graphics2D.rotate(angle, screenX, screenY + halfBlockLength - pixel4 * 2.5);
            graphics2D.drawImage(skin[0][5], screenX - pixel4 / 2, screenY + halfBlockLength - pixel4 * 3, pixel4, pixel4 * 3, null); //  left leg
            graphics2D.rotate(-angle, screenX, screenY + halfBlockLength - pixel4 * 2.5);

            // Right arm
            angle = animatedAngle(degreesToRadians(50), getX(), 1, maxSpeed, false) + degreesToRadians(30);
            graphics2D.rotate(angle, screenX, screenY + halfBlockLength - pixel4 * 5.5);
            graphics2D.drawImage(skin[0][2], screenX - pixel4 / 2, screenY + halfBlockLength - pixel4 * 6, pixel4, pixel4 * 3, null); // right arm
            graphics2D.rotate(-angle, screenX, screenY + halfBlockLength - pixel4 * 5.5);

            graphics2D.drawImage(skin[0][1], screenX - pixel4 / 2, screenY + halfBlockLength - pixel4 * 6, pixel4, pixel4 * 3, null); // body

            // Left arm
            angle = animatedAngle(degreesToRadians(50), getX(), 1, maxSpeed, true) + degreesToRadians(30);

            graphics2D.rotate(angle, screenX, screenY + halfBlockLength - pixel4 * 5.5);
            graphics2D.drawImage(skin[0][3], screenX - pixel4 / 2, screenY + halfBlockLength - pixel4 * 6, pixel4, pixel4 * 3, null); // left arm
            graphics2D.rotate(-angle, screenX, screenY + halfBlockLength - pixel4 * 5.5);

            // Head
            graphics2D.drawImage(skin[0][0], screenX - pixel4, screenY + halfBlockLength - pixel4 * 8, pixel4 * 2, pixel4 * 2, null); //  head

        } else if (side == 1 || side == 4) { //  Standing still (jumping too for now)
            graphics2D.drawImage(skin[1][4], screenX, screenY + halfBlockLength - pixel4 * 3, pixel4, pixel4 * 3, null); //  right leg
            graphics2D.drawImage(skin[1][5], screenX - pixel4, screenY + halfBlockLength - pixel4 * 3, pixel4, pixel4 * 3, null); //  left leg

            graphics2D.drawImage(skin[1][1], screenX - pixel4, screenY + halfBlockLength - pixel4 * 6, pixel4 * 2, pixel4 * 3, null); // body

            graphics2D.drawImage(skin[1][2], screenX + pixel4, screenY + halfBlockLength - pixel4 * 6, pixel4, pixel4 * 3, null); // right arm
            graphics2D.drawImage(skin[1][3], screenX - pixel4 * 2, screenY + halfBlockLength - pixel4 * 6, pixel4, pixel4 * 3, null); // left arm

            graphics2D.drawImage(skin[1][0], screenX - pixel4, screenY + halfBlockLength - pixel4 * 8, pixel4 * 2, pixel4 * 2, null); //  head
        } else if (side == 2) { // Walking right
            double angle = animatedAngle(degreesToRadians(60), getX(), 2, maxSpeed, false);
            graphics2D.rotate(angle, screenX, screenY + halfBlockLength - pixel4 * 2.5);
            graphics2D.drawImage(skin[2][5], screenX - pixel4 / 2, screenY + halfBlockLength - pixel4 * 3, pixel4, pixel4 * 3, null); //  left leg
            graphics2D.rotate(-angle, screenX, screenY + halfBlockLength - pixel4 * 2.5);

            angle = animatedAngle(degreesToRadians(60), getX(), 2, maxSpeed, true);
            graphics2D.rotate(angle, screenX, screenY + halfBlockLength - pixel4 * 2.5);
            graphics2D.drawImage(skin[2][4], screenX - pixel4 / 2, screenY + halfBlockLength - pixel4 * 3, pixel4, pixel4 * 3, null); //  right leg
            graphics2D.rotate(-angle, screenX, screenY + halfBlockLength - pixel4 * 2.5);

            angle = animatedAngle(degreesToRadians(50), getX(), 1, maxSpeed, false) + degreesToRadians(330);
            graphics2D.rotate(angle, screenX, screenY + halfBlockLength - pixel4 * 5.5);
            graphics2D.drawImage(skin[2][3], screenX - pixel4 / 2, screenY + halfBlockLength - pixel4 * 6, pixel4, pixel4 * 3, null); // left arm
            graphics2D.rotate(-angle, screenX, screenY + halfBlockLength - pixel4 * 5.5);

            graphics2D.drawImage(skin[2][1], screenX - pixel4 / 2, screenY + halfBlockLength - pixel4 * 6, pixel4, pixel4 * 3, null); // body

            angle = animatedAngle(degreesToRadians(50), getX(), 1, maxSpeed, true) + degreesToRadians(330);
            graphics2D.rotate(angle, screenX, screenY + halfBlockLength - pixel4 * 5.5);
            graphics2D.drawImage(skin[2][2], screenX - pixel4 / 2, screenY + halfBlockLength - pixel4 * 6, pixel4, pixel4 * 3, null); // right arm
            graphics2D.rotate(-angle, screenX, screenY + halfBlockLength - pixel4 * 5.5);

            graphics2D.drawImage(skin[2][0], screenX - pixel4, screenY + halfBlockLength - pixel4 * 8, pixel4 * 2, pixel4 * 2, null); //  head
        }
    }
}
