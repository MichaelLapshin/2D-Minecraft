/**
 * [WorldServer.java]
 * @description     A sub-class of World which includes the game logic run by the server
 * @author          Michael Lapshin
 */

import java.io.File;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.Map;

public class WorldServer extends World implements Runnable {
    // Server related variables
    private int blockUpdatesPerTick;
    static final int TICK_RATE = 20; // Ticks per second
    final int UPDATE_ALL_BLOCKS_AVERAGE_TICK_RATE = 6000;
    private long tick = 0;

    private int SAVE_ENTITY_INTERVAL = WorldServer.TICK_RATE * 10;

    public static final double ITEM_DROP_MAX_SPEED_X = 0.05;
    public static final double ITEM_DROP_MAX_SPEED_Y = 0.8;
    public static final double PICKUP_COOLDOWN = WorldServer.TICK_RATE * 1.2;

    private Thread thread;

    private final int MAX_RANGE_RAY = 5; // Range in blocks-length
    private final double RAY_STEP_INTERVAL = 0.05;

    // Mob count constants
    private final int HOSTILE_MOB_COUNT_PER_PLAYER = 8;
    private final int HOSTILE_MOB_SPAWN_RATE = WorldServer.TICK_RATE * 10;
    private final double HOSTILE_MOB_SPAWN_SPACE_CHANCE = 0.1;

    private final int PASSIVE_MOB_COUNT_PER_PLAYER = 10;
    private final int PASSIVE_MOB_SPAWN_RATE = WorldServer.TICK_RATE * 60;
    private final double PASSIVE_MOB_SPAWN_SPACE_CHANCE = 0.2;

    private final int MOB_SPAWN_DISTANCE = 36;
    private final int MOB_SPAWN_COLUMNS = 8;
    public static final int DESPAWN_DISTANCE = 240;

    private final double MOB_PUSH_STRENGTH = 0.12;

    private String serverDataToEcho;

    /** Generates the world */
    public WorldServer(Terrain terrain) {
        super(terrain);

        blockUpdatesPerTick = terrain.getWidth() * terrain.WORLD_HEIGHT / UPDATE_ALL_BLOCKS_AVERAGE_TICK_RATE;
        serverDataToEcho = "/TICK";

        this.thread = new Thread(this);
        this.thread.start();
    }

    /** Includes Game loop */
    public void run() {
        double time = System.currentTimeMillis();
        int mspt = 1000 / TICK_RATE;

        while (Server.running) {
            if (System.currentTimeMillis() - time > mspt) {
                time += mspt;
                tick++;

                // Saves the world
                if (tick % SAVE_ENTITY_INTERVAL == 0) {

                    try {
                        PrintWriter writer = new PrintWriter(new File("World-Entities.txt"));

                        for (Map.Entry<Integer, Player> entry : players.entrySet()) {
                            Player player = entry.getValue();
                            writer.println("0 " + player.getId() + " " + player.getX() + " " + player.getY() + " " + player.getVx() + " " + player.getVy() + " " + player.getHealth() + " " + player.getName());
                        }

                        writer.close();
                    } catch (Exception E) {
                        E.printStackTrace();
                        System.out.println("Failed to save entities.");
                    }
                }


                /////=== Game logic goes here ===\\\\\

                ///== Random block ticking ==\\\
                //terrain.randomBlockUpdates(blockUpdatesPerTick);

                // Player action-response
                int numberOfPlayersOnline = 0;
                for (Map.Entry<Integer, Player> entry : players.entrySet()) {
                    Player player = entry.getValue();
                    if (player.isOnline() == true) {
                        numberOfPlayersOnline++;

                        // Times out player if they do not respond
                        player.tickTimeoutTimer();
                        if ((player.timeoutTimerAlive() == false || player.getHealth() <= 0)) {
                            System.out.println("Player " + player.getName() + "#" + player.getId() + " disconnected.");
                            serverDataToEcho += " [Entity] 0 " + player.getId() + " " +
                                    player.getX() + " " + player.getY() + " " +
                                    player.getVx() + " " + player.getVy() + " " +
                                    player.getR() + " 0";
                            player.disconnect();


                        } else {
                            if (player.getMouseLeftState() == true) { // Attack
                                player.mouseLeftRelease();

                                // Attack mob
                                Mob mob = mobRayCast(player.getX(), player.getY() + Player.PLAYER_HEIGHT * (22.0 / 32.0) - 0.5, player.getR(), Player.ATTACK_RANGE, player);
                                if (mob != null) {
                                    mob.damage(Player.ATTACK_DAMAGE);

                                    mob.addVy(Mob.VERTICAL_KNOCK_BACK);
                                    if (player.getX() - mob.getX() > 0) mob.addVx(-Mob.HORIZONTAL_KNOCK_BACK);
                                    else if (player.getX() - mob.getX() < 0) mob.addVx(Mob.HORIZONTAL_KNOCK_BACK);
                                }

                                // Attack block
                                int[] pos = terrain.blockRayCast(player.getX(), player.getY() + Player.PLAYER_HEIGHT * (22.0 / 32.0) - 0.5, player.getR(), Player.BLOCK_BREAK_RANGE);
                                if (pos != null) {
                                    if (focusBlocks.containsKey(pos[0] + " " + pos[1]) == false) {
                                        focusBlocks.put(pos[0] + " " + pos[1], new Block(pos[0], pos[1], terrain.getBlock(pos[0], pos[1])));
                                    }
                                    focusBlocks.get(pos[0] + " " + pos[1]).damage(Player.BLOCK_DAMAGE_PER_SECOND);
                                }
                            }

                            if (player.getMouseRightState() == true) { // Place block
                                player.mouseRightRelease();

                                int[] pos = terrain.blockRayCastReturnOpen(player.getX(), player.getY() + Player.PLAYER_HEIGHT * (22.0 / 32.0) - 0.5, player.getR(), Player.BLOCK_BREAK_RANGE);
                                if (pos != null && BlockData.getBlockImage(player.getItemHeld()) != null && player.getInventory().canAfford(player.getItemHeld(), 1)) {
                                    if (BlockData.getBlockCanWalkThrough(player.getItemHeld()) == true) {

                                        if (player.getPlaceBlockFront() == false) {
                                            terrain.replaceBlock(pos[0], pos[1], (byte) (-player.getItemHeld()), true);
                                            serverDataToEcho += " [Block_change] " + pos[0] + " " + pos[1] + " " + (-player.getItemHeld());
                                        } else {
                                            terrain.replaceBlock(pos[0], pos[1], player.getItemHeld(), true);
                                            serverDataToEcho += " [Block_change] " + pos[0] + " " + pos[1] + " " + player.getItemHeld();
                                        }
                                        player.getClient().inventoryRemove(player.getItemHeld(), 1);

                                    } else if ((pos[0] == (int) player.getX() && pos[1] == (int) player.getY()) == false
                                            && (pos[0] == (int) player.getX() && pos[1] == (int) player.getY() + 1) == false) {
                                        if (player.getPlaceBlockFront() == false) {
                                            terrain.replaceBlock(pos[0], pos[1], (byte) (-player.getItemHeld()), true);
                                            serverDataToEcho += " [Block_change] " + pos[0] + " " + pos[1] + " " + (-player.getItemHeld());
                                        } else {
                                            terrain.replaceBlock(pos[0], pos[1], player.getItemHeld(), true);
                                            serverDataToEcho += " [Block_change] " + pos[0] + " " + pos[1] + " " + player.getItemHeld();
                                        }
                                        player.getClient().inventoryRemove(player.getItemHeld(), 1);
                                    }

                                }


                            }

                        }

                        // Allows the player to pickup drops from the ground
                        for (Map.Entry<Integer, EntityItem> dropEntry : drops.entrySet()) {
                            EntityItem entityItem = dropEntry.getValue();

                            if (entityItem.getMaxHealth() - entityItem.getHealth() > PICKUP_COOLDOWN
                                    && entityItem.getHealth() > 0
                                    && player.containsPoint(entityItem.getX(), entityItem.getY())) {
                                player.getClient().inventoryAdd(entityItem.getItem(), 1);
                                entityItem.setHealth(0);

                            }
                        }

                    }

                }

                // TODO entity collisions body

                // Removes broken blocks
                focusBlockDead();

                // Movement
                entityMove();

                // Ticks what has a life-span
                entityTick();
                focusBlocksTick();

                // Spawn mobs
//                spawnHostileMobs(numberOfPlayersOnline);
//                spawnPassiveMobs(numberOfPlayersOnline);

                clientUpdateForwardAll(); //  Echos message to all users

                // Removes any dead entities
                entityDeadRemove();
            }
        }

        // End of the run()
    }

    /** /////=== Spawn mobs ===\\\\\ */
    public void addDrop(EntityItem item) {
        super.addDrop(item);
        serverDataToEcho += " [Entity_new] 3 " + item.getId() + " " + item.getItem();

    }

    public void addPlayer(Player player) {
        super.addPlayer(player);
        serverDataToEcho += " [Entity_new] 0 " + player.getId() + " " + player.getName();

    }

    public void addHostileMob(HostileMob mob) {
        super.addHostileMob(mob);
        serverDataToEcho += " [Entity_new] 1 " + mob.getId();

    }

    public void addPassiveMob(PassiveMob mob) {
        super.addPassiveMob(mob);
        serverDataToEcho += " [Entity_new] 2 " + mob.getId();

    }

    /** /////=== Server Game Logic ===\\\\\ */

    /** Finds a spawning space (2 block height) around the player in a column to potentially spawn a mob */
    public int find2BlockSpawnSpace(int x, int y, int minRange, int range, double spawnProbability) {
        for (int i = y + range; i > y - range; i--) {
            if (//(i > y + minRange || i < y - minRange) &&
                    (terrain.isSolidBlock(x, i) == false)
                            && (terrain.isSolidBlock(x, i + 1) == false)
                            && (terrain.isSolidBlock(x, i - 1) == true)) {
                if (Math.random() > spawnProbability) {
                    return i;
                }
            }
        }
        return -1;
    }

    /** Updates the block in focus */
    public void focusBlocksTick() {
        for (Map.Entry<String, Block> entry : focusBlocks.entrySet()) {
            entry.getValue().tick();
        }
    }

    /** Spawns hostile mobs around the player */
    public void spawnHostileMobs(int numberOfPlayersOnline) {
        if (tick % HOSTILE_MOB_SPAWN_RATE == 0) {
            for (int i = 0; i < HOSTILE_MOB_COUNT_PER_PLAYER * numberOfPlayersOnline - hostileMobs.size(); i++) {

                for (Map.Entry<Integer, Player> entry : players.entrySet()) {
                    Player player = entry.getValue();
                    if (player.isOnline() == true) {

                        int height;

                        // Right Side of the player Zombie spawn
                        for (int j = (int) player.getX() + MOB_SPAWN_DISTANCE + MOB_SPAWN_COLUMNS / 2 - 1; j > (int) player.getX() + MOB_SPAWN_DISTANCE; j--) {
                            height = find2BlockSpawnSpace((int) j, (int) player.getY(), MOB_SPAWN_DISTANCE, MOB_SPAWN_COLUMNS + MOB_SPAWN_DISTANCE, HOSTILE_MOB_SPAWN_SPACE_CHANCE);
                            if (height > 0) {
                                addHostileMob(new Zombie(this, -1, j + 0.5, height, 0, 0, -1));
                                break;
                            }
                        }

                        // Left side of the player Zombie spawn
                        for (int j = (int) player.getX() - MOB_SPAWN_DISTANCE - MOB_SPAWN_COLUMNS / 2; j < player.getX() + MOB_SPAWN_DISTANCE; j++) {
                            height = find2BlockSpawnSpace((int) j, (int) player.getY(), MOB_SPAWN_DISTANCE, MOB_SPAWN_COLUMNS + MOB_SPAWN_DISTANCE, HOSTILE_MOB_SPAWN_SPACE_CHANCE);
                            if (height > 0) {
                                addHostileMob(new Zombie(this, -1, j + 0.5, height, 0, 0, -1));
                                break;
                            }
                        }


                    }
                }

            }
        }
    }

    /** Spawns passive mobs around the player */
    public void spawnPassiveMobs(int numberOfPlayersOnline) {
        if (tick % PASSIVE_MOB_SPAWN_RATE == 0) {
            for (int i = 0; i < PASSIVE_MOB_COUNT_PER_PLAYER * numberOfPlayersOnline - passiveMobs.size(); i++) {

                for (Map.Entry<Integer, Player> entry : players.entrySet()) {
                    Player player = entry.getValue();
                    if (player.isOnline() == true) {

                        int height;

                        // Right Side of the player Pig spawn
                        for (int j = (int) player.getX() + MOB_SPAWN_DISTANCE + MOB_SPAWN_COLUMNS / 2; j > (int) player.getX() + MOB_SPAWN_DISTANCE; j--) {
                            height = find2BlockSpawnSpace((int) j, (int) player.getY(), MOB_SPAWN_DISTANCE, MOB_SPAWN_COLUMNS + MOB_SPAWN_DISTANCE, PASSIVE_MOB_SPAWN_SPACE_CHANCE);
                            if (height != -1) {
                                PassiveMob pig = new Pig(this, -1, j + 0.5, height, 0, 0, -1);

                                passiveMobs.put(pig.getId(), pig);

                                serverDataToEcho += " [Entity_new] 2 " + pig.getId();

                            }
                        }

                        // Left side of the player Pig spawn
                        for (int j = (int) player.getX() - MOB_SPAWN_DISTANCE - MOB_SPAWN_COLUMNS / 2; j < player.getX() + MOB_SPAWN_DISTANCE; j++) {
                            height = find2BlockSpawnSpace((int) j, (int) player.getY(), MOB_SPAWN_DISTANCE, MOB_SPAWN_COLUMNS + MOB_SPAWN_DISTANCE, PASSIVE_MOB_SPAWN_SPACE_CHANCE);
                            if (height != -1) {
                                PassiveMob pig = new Pig(this, -1, j + 0.5, height, 0, 0, -1);

                                passiveMobs.put(pig.getId(), pig);

                                serverDataToEcho += " [Entity_new] 2 " + pig.getId();
                                break;
                            }
                        }


                    }
                }

            }
        }
    }

    /** Removes any broken blocks */
    public void focusBlockDead() {
        LinkedList<Block> toRemove = new LinkedList<>();
        for (Map.Entry<String, Block> entry : focusBlocks.entrySet()) {
            Block block = entry.getValue();
            if (block.isAlive() == false) {
                terrain.replaceBlock(block.getX(), block.getY(), (byte) 0, true);
                serverDataToEcho += " [Block_change] " + block.getX() + " " + block.getY() + " 0";
                if (focusBlocks.containsKey(block.getX() + " " + block.getY())) {
                    toRemove.add(block);
                }
            }
        }

        // Removes block
        for (Block block : toRemove) {
            for (Map.Entry<Byte, Integer> entry : BlockData.getBlockDrops(block.getBlockType()).entrySet()) {
                for (int i = 0; i < entry.getValue(); i++) {
                    addDrop(new EntityItem(this, -1, block.getX() + 0.5, block.getY(),
                            (Math.random() - 0.5) * 2 * ITEM_DROP_MAX_SPEED_X, Math.random() * ITEM_DROP_MAX_SPEED_Y, entry.getKey(), false));
                }
            }
            focusBlocks.remove(block.getX() + " " + block.getY());
        }
    }

    /** Updates all entities */
    public void entityTick() {
        for (Map.Entry<Integer, Player> entry : players.entrySet())
            if (entry.getValue().isOnline() == true)
                entry.getValue().tick();
        for (Map.Entry<Integer, HostileMob> entry : hostileMobs.entrySet()) entry.getValue().tick();
        for (Map.Entry<Integer, PassiveMob> entry : passiveMobs.entrySet()) entry.getValue().tick();
        for (Map.Entry<Integer, EntityItem> entry : drops.entrySet()) entry.getValue().tick();
    }

    /** Moves all entities */
    public void entityMove() {
        for (Map.Entry<Integer, Player> entry : players.entrySet())
            if (entry.getValue().isOnline() == true)
                entry.getValue().move();
        for (Map.Entry<Integer, HostileMob> entry : hostileMobs.entrySet()) entry.getValue().move();
        for (Map.Entry<Integer, PassiveMob> entry : passiveMobs.entrySet()) entry.getValue().move();
        for (Map.Entry<Integer, EntityItem> entry : drops.entrySet()) entry.getValue().move();
    }

    /** /////=== Methods that result in server echo message contribution ===\\\\\ */

    /** /////=== Server Logic ===\\\\\ */
    /** Echo server change */
    private void echoReplaceBlock(int x, int y, byte newBlockType) {
        terrain.replaceBlock(x, y, newBlockType);
    }

    /** Appends and echos entity data to all clients */
    public void clientUpdateForwardAll() {
        // Sending entity data to clients
        for (Map.Entry<Integer, Player> entry : players.entrySet()) {
            if (entry.getValue().isOnline())
                serverDataToEcho += " [Entity] 0 " + entry.getValue().getId() + " " +
                        entry.getValue().getX() + " " + entry.getValue().getY() + " " +
                        entry.getValue().getVx() + " " + entry.getValue().getVy() + " " +
                        entry.getValue().getR() + " " + entry.getValue().getHealth();
        }

        for (Map.Entry<Integer, HostileMob> entry : hostileMobs.entrySet()) {
            serverDataToEcho += " [Entity] 1 " + entry.getValue().getId() + " " +
                    entry.getValue().getX() + " " + entry.getValue().getY() + " " +
                    entry.getValue().getVx() + " " + entry.getValue().getVy() + " " +
                    entry.getValue().getR() + " " + entry.getValue().getHealth();
        }

        for (Map.Entry<Integer, PassiveMob> entry : passiveMobs.entrySet()) {
            serverDataToEcho += " [Entity] 2 " + entry.getValue().getId() + " " +
                    entry.getValue().getX() + " " + entry.getValue().getY() + " " +
                    entry.getValue().getVx() + " " + entry.getValue().getVy() + " " +
                    entry.getValue().getR() + " " + entry.getValue().getHealth();
        }

        for (Map.Entry<Integer, EntityItem> entry : drops.entrySet()) {
            serverDataToEcho += " [Entity] 3 " + entry.getValue().getId() + " " +
                    entry.getValue().getX() + " " + entry.getValue().getY() + " " +
                    entry.getValue().getVx() + " " + entry.getValue().getVy() + " " +
                    entry.getValue().getR() + " " + entry.getValue().getHealth();
        }

        for (Map.Entry<String, Block> entry : focusBlocks.entrySet()) {
            serverDataToEcho += " [Block] " + entry.getKey() + " " + entry.getValue().getAnimationFrame();
        }


        // Echos data to all users online
        try {
            for (Map.Entry<Integer, Player> entry : players.entrySet()) {
                if (entry.getValue().isOnline())
                    entry.getValue().sendServerMessage(serverDataToEcho);
            }
        } catch (Exception E) {
            System.out.println("Concurrency Issue, user is gone.");
        }

        //System.out.println("ECHOING: ." + serverDataToEcho + ".   TO: " + players.size() + " users");

        // Resets message buffer
        serverDataToEcho = "/TICK";
    }

    /** Generates a unique ID among all entities */
    @Override
    public int generateEntityID() {
        int id = 0;
        while (id == 0) {
            id = (int) (Math.random() * Integer.MAX_VALUE);
            if (players.containsKey(id) || hostileMobs.containsKey(id) || passiveMobs.containsKey(id) || drops.containsKey(id))
                id = 0;
        }
        return id;
    }

    /** /////=== Ray Casting Methods ===\\\\\ */
    /** Shoots a virtual ray that travels until it collides with a mob (or not) */
    public Mob mobRayCast(double x, double y, double r, double range, Mob host) {
        if (range > MAX_RANGE_RAY) range = MAX_RANGE_RAY;

        int steps = (int) (range / RAY_STEP_INTERVAL);

        for (int i = 0; i < steps; i++) {
            // Checks if the ray is within the eneities
            for (Map.Entry<Integer, Player> entry : players.entrySet()) {
                if (entry.getValue() != host && entry.getValue().containsPoint(x, y)) return entry.getValue();
            }
            for (Map.Entry<Integer, HostileMob> entry : hostileMobs.entrySet()) {
                if (entry.getValue().containsPoint(x, y)) return entry.getValue();
            }

            for (Map.Entry<Integer, PassiveMob> entry : passiveMobs.entrySet()) {
                if (entry.getValue().containsPoint(x, y)) return entry.getValue();
            }

            x += RAY_STEP_INTERVAL * Math.cos(r);
            y += RAY_STEP_INTERVAL * Math.cos(r);
        }
        return null;
    }

    /** Return current game tick. Increments every 50ms. */
    public long getTick() {
        return tick;
    }
}
