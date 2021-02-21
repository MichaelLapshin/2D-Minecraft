import java.awt.*;
import java.io.File;
import java.io.PrintWriter;
import java.util.Map;

public class Player extends Mob {
    // Player constants
    private static final double WALKING_SPEED = 0.23;
    private static final double SPRINT_SPEED = 0.23;
    private static final double JUMP_POWER = 0.71;

    public static final double DROP_POWER = 0.2;

    private static final int MAX_HEALTH = 100;

    // World interaction constants
    public final static double BLOCK_BREAK_RANGE = 3.6;
    public final static double BLOCK_DAMAGE_PER_SECOND = 1000000.0 / WorldServer.TICK_RATE;
    public final static double ATTACK_RANGE = 4.0;
    public final static int ATTACK_DAMAGE = 20;
    public final static int ATTACK_COOLDOWN_SECONDS = (int) (0.5 * WorldServer.TICK_RATE);
    private final static double RAY_DRAW_INTERVAL = 0.2;

    // Walking/visual variables + constants
    public final static double PLAYER_HEIGHT = 1.8;
    public final static double[] HIT_BOX = new double[]{-0.3, -0.5, 0.6, PLAYER_HEIGHT};
    private final int HEALTH_REGENERATE_PER_SECOND = (int) (WorldServer.TICK_RATE);

    // Controls related
    private boolean placeBlockFront = true;
    private byte[] hotBar;
    private byte itemHeld;

    // Connection variables
    private final int TIMEOUT_TIME_MAX = WorldServer.TICK_RATE * 15;
    private int timeoutTimer = 0;
    private boolean isClientPlayer = false;

    // Player data
    private String name;
    private Inventory inventory;
    private ConnectionHandler client;
    private int renderDistance;

    // Clicking buffers
    private boolean clickL = false;
    private boolean clickR = false;

    public Player(World world, int id, double x, double y, double vx, double vy, int health, String name) {
        super(world, id, x, y, vx, vy, health, MAX_HEALTH, WALKING_SPEED, JUMP_POWER);
        this.name = name;
        this.renderDistance = 10;
        setSkin(DrawTools.getRandomPlayerModel());
        setHeight(PLAYER_HEIGHT);
        this.hotBar = new byte[9];
        this.timeoutTimer = TIMEOUT_TIME_MAX;
        this.inventory = new Inventory();
        setSprintSpeed(SPRINT_SPEED);
        setHitBox(HIT_BOX);
    }

    public Player(World world, String name) {
        super(world, MAX_HEALTH, WALKING_SPEED, JUMP_POWER);
        this.name = name;
        this.renderDistance = 10;
        this.inventory = new Inventory();
        setSkin(DrawTools.getRandomPlayerModel());
        setHeight(PLAYER_HEIGHT);
        this.setX(world.getTerrain().getWidth() / 2 + 0.5);
        this.setY(world.getTerrain().getSpawnHeight());
        this.hotBar = new byte[9];
        this.timeoutTimer = TIMEOUT_TIME_MAX;
        setSprintSpeed(SPRINT_SPEED);
        setHitBox(HIT_BOX);
    }

    public Player(World world, int id, String name) {
        super(world, id, 0, 0, 0, 0, MAX_HEALTH, MAX_HEALTH, WALKING_SPEED, JUMP_POWER);
        this.name = name;
        this.renderDistance = 10;
        this.inventory = new Inventory();
        setSkin(DrawTools.getRandomPlayerModel());
        setHeight(PLAYER_HEIGHT);
        this.hotBar = new byte[9];
        this.timeoutTimer = TIMEOUT_TIME_MAX;
        setSprintSpeed(SPRINT_SPEED);
    }

    public Inventory getInventory() {
        return inventory;
    }

    public String getName() {
        return name;
    }

    @Override
    public void move() {
        super.move();
        // Player health regeneration
        if (getTick() % HEALTH_REGENERATE_PER_SECOND == 0) setHealth(Integer.min(getHealth() + 1, getMaxHealth()));
    }

    // Saves the player information into file (inventory, name, id)
    private void save() {
        try {
            /*
            * Format:
            * playerName
            * playerID
            * player health
            * player coordinates (x y vx vy)
            * player inventory
            */
            PrintWriter writer = new PrintWriter(new File("Player-" + this.getName() + "#" + this.getId() + ".txt"));

            writer.println(getName());
            writer.println(getId());
            writer.println(getHealth());
            writer.println(getX() + " " + getY() + " " + getVx() + " " + getVy());

            // Saves inventory
            for (Map.Entry<Byte, Integer> entry : this.inventory.getInventory().entrySet()) {
                writer.println(entry.getKey() + " " + entry.getValue());
            }

            writer.close();
            System.out.println("[SAVE] Saved player " + this.getName() + "#" + this.getId() + " data successfully.");
        } catch (Exception E) {
            E.printStackTrace();
            System.out.println("[SAVE] Failed to save data for player " + this.getName() + "#" + this.getId() + ".");
        }

    }

    public int getRenderDistance() {
        return renderDistance;
    }

    public void setRenderDistance(int renderDistance) {
        this.renderDistance = Integer.max(3, renderDistance);
    }

    /////=== Client Handling ===\\\\\
    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    // Connects player to client
    public void connect(ConnectionHandler client) {
        this.client = client;
    }

    // Saves and disconnects the player from the client
    public void disconnect() {
        save();
        this.client = null;
    }

    public ConnectionHandler getClient() {
        return client;
    }

    public boolean isOnline() {
        return client != null;
    }

    public void sendServerMessage(String message) {
        if (client != null) client.echoMessage(message);
    }

    /////=== Hotbar === \\\\\
    public byte getHotBarItem(int position) {
        return hotBar[position];
    }

    public void setHotBarItem(int position, byte item) {
        hotBar[position] = item;
    }

    /////=== Timout methods ===\\\\\
    public void resetTimeoutTimer() {
        timeoutTimer = TIMEOUT_TIME_MAX;
    }

    public void tickTimeoutTimer() {
        if (timeoutTimer > 0) timeoutTimer--;
    }

    public boolean timeoutTimerAlive() {
        return (timeoutTimer > 0);
    }

    /////=== User Player controls ===\\\\\
    // Mouse controls
    public void mouseLeftPress(byte itemHeld) {
        clickL = true;
        this.itemHeld = itemHeld;
    }

    public void mouseRightPress(byte itemHeld) {
        clickR = true;
        this.itemHeld = itemHeld;
    }

    public void mouseLeftRelease() {
        clickL = false;
    }

    public void mouseRightRelease() {
        clickR = false;
    }

    public boolean getMouseLeftState() {
        return clickL;
    }

    public boolean getMouseRightState() {
        return clickR;
    }

    public byte getItemHeld() {
        return this.itemHeld;
    }

    public void setPlaceBlockFront(boolean placeBlockFront) {
        this.placeBlockFront = placeBlockFront;
    }

    public boolean getPlaceBlockFront() {
        return this.placeBlockFront;
    }

    // Is client
    public boolean getIsClientPlayer() {
        return isClientPlayer;
    }

    public void setIsClientPlayer(boolean isClientPlayer) {
        this.isClientPlayer = isClientPlayer;
    }

    @Override
    public void draw(Graphics graphics, Dimension dimension, int renderDistance, double drawX, double drawY) {
        super.draw(graphics, dimension, renderDistance, drawX, drawY);

        // Displays names above player heads
//        int nameSize = (int) DrawTools.game2screenLength(dimension, renderDistance, 0.5);
//        int nameX = (int) DrawTools.game2ScreenX(drawX, dimension, renderDistance, getX() - getName().length()*0.01);
//        int nameY = (int) DrawTools.game2ScreenY(drawY, dimension, renderDistance, getY() + PLAYER_HEIGHT + 0.2);
//        graphics.setColor(Color.BLACK);
//        graphics.setFont(Client.getCustomFont(nameSize));
//        graphics.drawString(getName(), nameX / 2 - 240, nameY);


        // Draws player's attacking
        int steps = (int) (BLOCK_BREAK_RANGE / RAY_DRAW_INTERVAL);

        int halfCircle = (int) DrawTools.game2screenLength(dimension, renderDistance, 0.06);

        double x = getX();
        double y = getY() + Player.PLAYER_HEIGHT * (22.0 / 32.0) - 0.5;

        for (int i = 0; i < steps; i++) {
            x += RAY_DRAW_INTERVAL * Math.cos(getR());
            y += RAY_DRAW_INTERVAL * Math.sin(getR());

            int screenX = (int) DrawTools.game2ScreenX(drawX, dimension, renderDistance, x);
            int screenY = (int) DrawTools.game2ScreenY(drawY, dimension, renderDistance, y);

            graphics.setColor(Color.BLACK);
            graphics.fillOval(screenX - halfCircle, screenY - halfCircle, halfCircle * 2, halfCircle * 2);
        }

    }
}
