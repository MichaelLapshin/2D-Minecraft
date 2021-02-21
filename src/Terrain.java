import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.Queue;

public class Terrain {
    // Terrain related variables
    private byte[][] terrain;
    private int width;
    private int seed;
    private int spawnHeight;
    private final int MAX_WORLD_SIZE = 50000000; // Safety mechanism, just in case so that the world isn't too big (Max 50 million)
    final static int WORLD_HEIGHT = 1600;

    private final int MAX_RANGE_RAY = 5; // Range in blocks-length
    private final double RAY_STEP_INTERVAL = 0.05;

    // Path-finding related variables (Note, all time-related events are to be calculated in ticks)
    private final int MAX_RANGE_PATH = 20; // Number of blocks from source

    /////=== Terrain constants ===\\\\\
    // Dirt constants
    private final int DIRT_LAYER_AVERAGE = 5;
    private final int DIRT_LAYER_CHANGE = 4;
    private final double DIRT_WAVE_FACTOR = 50.0;

    // Stone constants
    private final int STONE_LAYER_AVERAGE = 1200;
    private final int STONE_LAYER_CHANGE = 50;
    private final double STONE_WAVE_FACTOR = 100.0;

    // Netherrack constants
    private final int NETHERRACK_LAYER_AVERAGE = 800;
    private final int NETHERRACK_LAYER_CHANGE = 100;
    private final double NETHERRACK_WAVE_FACTOR = 10.0;

    // Cave constants
    private final double CAVE_X_FACTOR = 40.0;
    private final double CAVE_Y_FACTOR = 23.0;
    private final double CAVE_THRESHOLD = 0.15;
    private final double CAVE_THRESHOLD_CHANGE_PER_LEVEL = 0.00042;

    /////=== Ores ===\\\\\
    // Sand constants
    private final double SAND_X_FACTOR = 60.0;
    private final double SAND_Y_FACTOR = 25.0;
    private final double SAND_THRESHOLD = 0.7;
    private final double SAND_THRESHOLD_CHANGE_PER_LEVEL = 0;

    // Gravel constants
    private final double GRAVEL_X_FACTOR = 10.0;
    private final double GRAVEL_Y_FACTOR = 15.0;
    private final double GRAVEL_THRESHOLD = 0.85;
    private final double GRAVEL_THRESHOLD_CHANGE_PER_LEVEL = -0.0001;

    // Clay constants
    private final double CLAY_X_FACTOR = 10.0;
    private final double CLAY_Y_FACTOR = 20.0;
    private final double CLAY_THRESHOLD = 0.85;
    private final double CLAY_THRESHOLD_CHANGE_PER_LEVEL = -0.0001;

    // Coal Ore constants
    private final double COAL_X_FACTOR = 8.0;
    private final double COAL_Y_FACTOR = 5.0;
    private final double COAL_THRESHOLD = 0.75;
    private final double COAL_THRESHOLD_CHANGE_PER_LEVEL = 0.00005;

    // Iron Ore constants
    private final double IRON_X_FACTOR = 5.0;
    private final double IRON_Y_FACTOR = 4.0;
    private final double IRON_THRESHOLD = 0.78;
    private final double IRON_THRESHOLD_CHANGE_PER_LEVEL = 0.0001;

    // Redstone Ore constants
    private final double REDSTONE_X_FACTOR = 6.0;
    private final double REDSTONE_Y_FACTOR = 5.0;
    private final double REDSTONE_THRESHOLD = 0.65;
    private final double REDSTONE_THRESHOLD_CHANGE_PER_LEVEL = 0.0002;

    // Gold Ore constants
    private final double GOLD_X_FACTOR = 3.0;
    private final double GOLD_Y_FACTOR = 2.0;
    private final double GOLD_THRESHOLD = 0.65;
    private final double GOLD_THRESHOLD_CHANGE_PER_LEVEL = 0.0002;

    // Diamond Ore constants
    private final double DIAMOND_X_FACTOR = 2.0;
    private final double DIAMOND_Y_FACTOR = 1.8;
    private final double DIAMOND_THRESHOLD = 0.65;
    private final double DIAMOND_THRESHOLD_CHANGE_PER_LEVEL = 0.00025;

    // Emerald Ore constants
    private final double EMERALD_X_FACTOR = 1.5;
    private final double EMERALD_Y_FACTOR = 1.0;
    private final double EMERALD_THRESHOLD = 0.65;
    private final double EMERALD_THRESHOLD_CHANGE_PER_LEVEL = 0.00025;

    /////=== Nether Constants ===\\\\\
    // Quartz Ore constants
    private final double QUARTZ_X_FACTOR = 50.0;
    private final double QUARTZ_Y_FACTOR = 25.0;
    private final double QUARTZ_THRESHOLD = 0.3;
    private final double QUARTZ_THRESHOLD_CHANGE_PER_LEVEL = 0.00044;

    // Glowstone
    private final double GLOWSTONE_X_FACTOR = 50.0;
    private final double GLOWSTONE_Y_FACTOR = 25.0;
    private final double GLOWSTONE_THRESHOLD = 0.3;
    private final double GLOWSTONE_THRESHOLD_CHANGE_PER_LEVEL = 0.00044;

    // Soul sand
    private final double SOUL_SAND_X_FACTOR = 50.0;
    private final double SOUL_SAND_Y_FACTOR = 25.0;
    private final double SOUL_SAND_THRESHOLD = 0.3;
    private final double SOUL_SAND_THRESHOLD_CHANGE_PER_LEVEL = 0.00044;

    // Foliage
    private final double DANDELION_SPAWN_CHANCE = 0.47;
    private final double SHRUB_SPAWN_CHANCE = 0.48;
    private final double ROSE_SPAWN_CHANCE = 0.4567;
    private final double FERN_SPAWN_CHANCE = 0.45;
    private final double GRASS_SPAWN_CHANCE = 0.5;

    private final double TREE_SPAWN_CHANCE = 0.45;
    private final double TREE_MIN_HEIGHT = 2.5;
    private final double TREE_MAX_HEIGHT = 8.5;

    public Terrain(int width, int seed) {
        terrain = new byte[width][WORLD_HEIGHT];

        if (width * WORLD_HEIGHT > MAX_WORLD_SIZE) width = MAX_WORLD_SIZE / WORLD_HEIGHT;
        this.width = width;
        this.seed = seed;

        generateTerrain(terrain);
        calculateSpawnHeight();
    }

    /////=== Terrain Logic ===\\\\\
    public void randomBlockUpdates(int numberOfAttempts) {
        int x, y;
        for (int i = 0; i < numberOfAttempts; i++) {
            x = (int) Math.random() * getWidth();
            y = (int) Math.random() * WORLD_HEIGHT;

            // Conditions for ticking blocks:
//                    if(getBlock(x, y) == Dirt) check around for grass blocks if no block above
//                    if(getBlock(x, y) == Plant) grow
        }
    }

    /////=== Terrain Generation Methods ===\\\\\
    public void generateTerrain(byte[][] terrain) {
        // Creates inner-array inside the terrain
        for (int x = 0; x < width; x++) {
            terrain[x] = new byte[WORLD_HEIGHT];
        }

        // Plains Constants
        for (int x = 0; x < width; x++) {

            // Generates stone
            int netherrackHeight = perlinHeight(NETHERRACK_LAYER_AVERAGE, NETHERRACK_LAYER_CHANGE, NETHERRACK_WAVE_FACTOR, x, seed - 10000);
            for (int y = 0; y < netherrackHeight; y++) {
                replaceBlock(x, y, 13);
            }

            // Generates stone
            int stoneHeight = perlinHeight(STONE_LAYER_AVERAGE, STONE_LAYER_CHANGE, STONE_WAVE_FACTOR, x, seed + 10000);
            for (int y = netherrackHeight; y < stoneHeight; y++) {
                replaceBlock(x, y, 3);
            }

            // Generates dirt
            int dirtHeight = perlinHeight(DIRT_LAYER_AVERAGE, DIRT_LAYER_CHANGE, DIRT_WAVE_FACTOR, x, seed + 20000);
            for (int y = stoneHeight; y < stoneHeight + dirtHeight; y++) {
                replaceBlock(x, y, 2);
            }

            // Generates top layer of grass blocks
            replaceBlock(x, stoneHeight + dirtHeight, 1);

            generateOres(stoneHeight, dirtHeight, netherrackHeight, x); // Generates all of the ores
            generateGroundFoliage(stoneHeight, dirtHeight, netherrackHeight, x);

            /////=== Cave Constants ===\\\\\
            for (int y = 0; y < stoneHeight + dirtHeight + 1; y++) {
                if (noise2D(x, y, CAVE_X_FACTOR, Double.max(0, CAVE_Y_FACTOR), seed - 1000) > CAVE_THRESHOLD + y * CAVE_THRESHOLD_CHANGE_PER_LEVEL) {
                    replaceBlock(x, y, -getBlock(x, y));
                }
            }
        }

    }

    public void generateGroundFoliage(int stoneHeight, int dirtHeight, int netherrackHeight, double x) {
        if (PerlinNoise.noise(x, -40.1678, seed + 1.315) > 1.0 - DANDELION_SPAWN_CHANCE) {
            replaceBlock((int) x, stoneHeight + dirtHeight + 1, 21);
        } else if (PerlinNoise.noise(x, -50.78, seed + 1.245) > 1.0 - ROSE_SPAWN_CHANCE) {
            replaceBlock((int) x, stoneHeight + dirtHeight + 1, 23);
        } else if (PerlinNoise.noise(x, -20.653, seed + 1.226) > 1.0 - SHRUB_SPAWN_CHANCE) {
            replaceBlock((int) x, stoneHeight + dirtHeight + 1, 22);
        } else if (PerlinNoise.noise(x, -30.23, seed + 1.023461) > 1.0 - FERN_SPAWN_CHANCE) {
            replaceBlock((int) x, stoneHeight + dirtHeight + 1, 24);
        } else if (PerlinNoise.noise(x, -10.737, seed + 1.3146) > 1.0 - GRASS_SPAWN_CHANCE) {
            replaceBlock((int) x, stoneHeight + dirtHeight + 1, 26);
        }

        // GENERATES TREE
        if (PerlinNoise.noise(x, -10.036, seed + 1.06345) > 1.0 - TREE_SPAWN_CHANCE) {
            int height = Integer.max(3, (int) (PerlinNoise.noise(x + 0.252, -10.25322, seed + 1.05325) * 4 * (TREE_MAX_HEIGHT - TREE_MIN_HEIGHT) + TREE_MIN_HEIGHT) - 12);

            // Fills trunk
            for (int i = stoneHeight + dirtHeight + 1; i < stoneHeight + dirtHeight + 1 + height; i++) {
                replaceBlock((int) x, i, 4);
            }

            // Fills leaves
            for (int i = -4; i <= 4; i++) {
                for (int j = 0; j < 3; j++) {
                    replaceBlock((int) (x + i), (int) (j + stoneHeight + dirtHeight + height), (byte) 5);
                }
            }

            for (int i = -2; i <= 2; i++) {
                for (int j = 3; j < 6; j++) {
                    replaceBlock((int) (x + i), (int) (j + stoneHeight + dirtHeight + height), (byte) 5);
                }
            }

            // Can walk through bottom of tree trunk
            replaceBlock((int) x, stoneHeight + dirtHeight + 1, -getBlockType((int) x, stoneHeight + dirtHeight + 1));
            replaceBlock((int) x, stoneHeight + dirtHeight + 2, -getBlockType((int) x, stoneHeight + dirtHeight + 2));
            replaceBlock((int) x, stoneHeight + dirtHeight + 3, -getBlockType((int) x, stoneHeight + dirtHeight + 3));
        }


    }

    public void generateOres(int stoneHeight, int dirtHeight, int netherrackHeight, int x) {
        /////=== Ores Generation ===\\\\\


        // Gravel
        for (int y = netherrackHeight; y < stoneHeight + dirtHeight + 1; y++) {
            if (noise2D(x, y, GRAVEL_X_FACTOR, Double.max(0, GRAVEL_Y_FACTOR), seed - 10000) > GRAVEL_THRESHOLD + y * GRAVEL_THRESHOLD_CHANGE_PER_LEVEL) {
                replaceBlock(x, y, 17);
            }
        }

        // Clay
        for (int y = netherrackHeight; y < stoneHeight; y++) {
            if (noise2D(x, y, CLAY_X_FACTOR, Double.max(0, CLAY_Y_FACTOR), seed - 20000) > CLAY_THRESHOLD + y * CLAY_THRESHOLD_CHANGE_PER_LEVEL) {
                replaceBlock(x, y, 18);
            }
        }

        // Sand
        for (int y = stoneHeight - 10; y < stoneHeight + dirtHeight + 1; y++) {
            if (noise2D(x, y, SAND_X_FACTOR, Double.max(0, SAND_Y_FACTOR), seed - 30000) > SAND_THRESHOLD + y * SAND_THRESHOLD_CHANGE_PER_LEVEL) {
                replaceBlock(x, y, 16);
            }
        }


        // Coal
        for (int y = netherrackHeight; y < stoneHeight; y++) {
            if (noise2D(x, y, COAL_X_FACTOR, Double.max(0, COAL_Y_FACTOR), seed - 40000) > COAL_THRESHOLD + y * COAL_THRESHOLD_CHANGE_PER_LEVEL) {
                replaceBlock(x, y, 7);
            }
        }

        // Iron
        for (int y = netherrackHeight; y < stoneHeight; y++) {
            if (noise2D(x, y, IRON_X_FACTOR, Double.max(0, IRON_Y_FACTOR), seed - 50000) > IRON_THRESHOLD + y * IRON_THRESHOLD_CHANGE_PER_LEVEL) {
                replaceBlock(x, y, 8);
            }
        }

        // Redstone
        for (int y = netherrackHeight; y < stoneHeight; y++) {
            if (noise2D(x, y, REDSTONE_X_FACTOR, Double.max(0, REDSTONE_Y_FACTOR), seed - 60000) > REDSTONE_THRESHOLD + y * REDSTONE_THRESHOLD_CHANGE_PER_LEVEL) {
                replaceBlock(x, y, 9);
            }
        }

        // Gold
        for (int y = netherrackHeight; y < stoneHeight; y++) {
            if (noise2D(x, y, GOLD_X_FACTOR, Double.max(0, GOLD_Y_FACTOR), seed - 70000) > GOLD_THRESHOLD + y * GOLD_THRESHOLD_CHANGE_PER_LEVEL) {
                replaceBlock(x, y, 9);
            }
        }

        // Diamond
        for (int y = netherrackHeight; y < stoneHeight; y++) {
            if (noise2D(x, y, DIAMOND_X_FACTOR, Double.max(0, DIAMOND_Y_FACTOR), seed - 80000) > DIAMOND_THRESHOLD + y * DIAMOND_THRESHOLD_CHANGE_PER_LEVEL) {
                replaceBlock(x, y, 10);
            }
        }

        // Emerald
        for (int y = netherrackHeight; y < stoneHeight; y++) {
            if (noise2D(x, y, EMERALD_X_FACTOR, Double.max(0, EMERALD_Y_FACTOR), seed - 90000) > EMERALD_THRESHOLD + y * EMERALD_THRESHOLD_CHANGE_PER_LEVEL) {
                replaceBlock(x, y, 11);
            }
        }

        /////=== Nether Biome ===\\\\\

        // Nether quartz ore
        for (int y = 0; y < netherrackHeight; y++) {
            if (noise2D(x, y, QUARTZ_X_FACTOR, Double.max(0, QUARTZ_Y_FACTOR), seed - 100000) > QUARTZ_THRESHOLD + y * QUARTZ_THRESHOLD_CHANGE_PER_LEVEL) {
                replaceBlock(x, y, 37);
            }
        }

        // Glowstone
        for (int y = 0; y < netherrackHeight; y++) {
            if (noise2D(x, y, GLOWSTONE_X_FACTOR, Double.max(0, GLOWSTONE_Y_FACTOR), seed - 110000) > GLOWSTONE_THRESHOLD + y * GLOWSTONE_THRESHOLD_CHANGE_PER_LEVEL) {
                replaceBlock(x, y, 38);
            }
        }

        // Soul sand
        for (int y = 0; y < netherrackHeight; y++) {
            if (noise2D(x, y, SOUL_SAND_X_FACTOR, Double.max(0, SOUL_SAND_Y_FACTOR), seed - 120000) > SOUL_SAND_THRESHOLD + y * SOUL_SAND_THRESHOLD_CHANGE_PER_LEVEL) {
                replaceBlock(x, y, 35);
            }
        }
    }

    private int perlinHeight(int average, int change, double waveFactor, int x, int seed) {
        return (int) (average + 2 * change * (0.5 - PerlinNoise.noise(x / waveFactor, 0, seed)));
    }

    private double noise2D(int x, int y, double xFactor, double yFactor, int seed) {
        return PerlinNoise.noise(x / xFactor, y / yFactor, seed);
    }

    public void calculateSpawnHeight() {
        // Determines spawn height
        for (int y = WORLD_HEIGHT - 1; y >= 0; y--) {
            if (terrain[width / 2][y] != 0) {
                spawnHeight = y + 1;
                return;
            }
        }
    }

    /////=== Path-finding Methods ===\\\\\
//     Path Finding, returns null or blank path when cannot reach destination
//    public LinkedList<int[]> pathFindFloor(int x, int y, int dx, int dy, int range, int jump) {
//        if (range > MAX_RANGE_PATH) range = MAX_RANGE_PATH;
//        if (Math.abs(x - dx) + Math.abs(y - dy) > range) {
//            return null;
//        }
//
//        // BFS Algorithm
//        Queue<byte[]> grid = new LinkedList<>();
//
//        // Maps out area around
//        byte[][] visited = new byte[2 * range + 1][2 * range + 1];
//        grid.add(new byte[]{(byte) range, (byte) range}); // Adds center with highest value
//        visited[range][range] = MAX_RANGE_PATH + 1;
//
//        while (grid.isEmpty() == false) {
//            byte[] pos = grid.poll();
//
//            if (pos[0] + x == dx && pos[1] + y == dy) break;
//            if (visited[pos[0]][pos[1]] <= 0) break;
//            if (visited[dx - x + range][dy - y + range] > 0) break;
//
//            // Checks that path doesn't perform illegal jump
//            boolean jumpValid = false;
//            for (int i = 0; i < jump + 1; i++) {
//                if (BlockData.getBlockCanWalkThrough(getBlock(x + pos[0] - range, y + pos[1] - range - jump - 1)) == false) {
//                    jumpValid = true;
//                    break;
//                }
//            }
//            if (jumpValid == false) {
//                visited[pos[0]][pos[1]] = -1;
//                continue;
//            }
//
//            // Checks all directions for possible BFS movement
//            if (pos[0] + 1 <= range * 2 &&
//                    visited[pos[0] + 1][pos[1]] == 0 &&
//                    BlockData.getBlockCanWalkThrough(getBlock(x + pos[0] + 1, y + pos[1])) == true) {
//                grid.add(new byte[]{(byte) (pos[0] + 1), pos[1]}); // Adds neighbouring grid minus one value
//                visited[pos[0] + 1][pos[1]] = (byte) (pos[2] - 1);
//            }
//            if (pos[1] + 1 <= range * 2 &&
//                    visited[pos[0]][pos[1] + 1] == 0 &&
//                    BlockData.getBlockCanWalkThrough(getBlock(x + pos[0], y + pos[1] + 1)) == true) {
//                grid.add(new byte[]{pos[0], (byte) (pos[1] + 1)}); // Adds neighbouring grid minus one value
//                visited[pos[0]][pos[1] + 1] = (byte) (pos[2] - 1);
//            }
//            if (pos[0] - 1 >= 0 &&
//                    visited[pos[0] - 1][pos[1]] == 0 &&
//                    BlockData.getBlockCanWalkThrough(getBlock(x + pos[0] - 1, y + pos[1])) == true) {
//                grid.add(new byte[]{(byte) (pos[0] - 1), pos[1]}); // Adds neighbouring grid minus one value
//                visited[pos[0] - 1][pos[1]] = (byte) (pos[2] - 1);
//            }
//            if (pos[1] - 1 >= 0 &&
//                    visited[pos[0]][pos[1] - 1] == 0 &&
//                    BlockData.getBlockCanWalkThrough(getBlock(x + pos[0], y + pos[1] - 1)) == true) {
//                grid.add(new byte[]{pos[0], (byte) (pos[1] - 1)}); // Adds neighbouring grid minus one value
//                visited[pos[0]][pos[1] - 1] = (byte) (pos[2] - 1);
//            }
//        }
//
//
//        // Find official and most optimal path to the destination
//        LinkedList<int[]> path = new LinkedList<>();
//
//        int fx = dx, fy = dx;
//
//        // If no path was found to the destination
//        if (visited[fx - x + range][fy - y + range] == 0) return null;
//
//        do {
//            path.add(new int[]{fx, fy});
//
//            int highest = visited[fx - x + range][fy - y + range];
//
//            if (visited[fx - x + range + 1][fy - y + range] > highest) fx++;
//            else if (visited[fx - x + range][fy - y + range + 1] > highest) fy++;
//            else if (visited[fx - x + range - 1][fy - y + range] > highest) fx--;
//            else if (visited[fx - x + range][fy - y + range - 1] > highest) fy--;
//
//        } while (fx - x != 0 || fy - y != 0);
//        path.add(new int[]{dx, dy});
//
//        return path;
//    }
//
//    public LinkedList<int[]> pathFind(int x, int y, int dx, int dy, int range) {
//        return pathFindFloor(x, y, dx, dy, range, -1);
//    }

    /////=== Terrain Setters ===\\\\\
    public void replaceBlock(int x, int y, byte newBlockType) {
        if ((x >= terrain.length) || (x < 0) || (y >= terrain[0].length) || (y < 0)) return;
        this.terrain[x][y] = newBlockType;
    }

    public void replaceBlock(int x, int y, int newBlockType) {
        replaceBlock(x, y, (byte) newBlockType);

    }

    public void replaceBlock(int x, int y, byte newBlockType, boolean appendToFile) {
        try {
            PrintWriter appendTo = new PrintWriter(new FileOutputStream(new File("World-Changes.txt"), true));
            replaceBlock(x, y, newBlockType);
            appendTo.append(x + " " + y + " " + newBlockType + "\n");
            appendTo.close();
        } catch (Exception E) {
            E.printStackTrace();
        }
    }

    // Gets block from terrain given coordinates
    public byte getBlock(int x, int y) {
        if (x >= terrain.length) return 0;
        if (x < 0) return 0;
        if (y >= terrain[0].length) return 0;
        if (y < 0) return 0;
        return terrain[x][y];
    }

    public byte getBlockType(int x, int y) {
        return (byte) Math.abs(getBlock(x, y));
    }

    /////=== Terrain getters ===\\\\\
    public int getWidth() {
        return width;
    }

    public int getSeed() {
        return seed;
    }

    public int getSpawnHeight() {
        return spawnHeight;
    }

    /////=== Block ray casting ===\\\\\
    public int[] blockRayCast(double x, double y, double r, double range) {
        return blockRayCast(x, y, r, range, 0);
    }

    public int[] blockRayCast(double x, double y, double r, double range, double noCheckDistance) {
        if (range > MAX_RANGE_RAY) range = MAX_RANGE_RAY;
        range -= noCheckDistance;

        int steps = (int) (range / RAY_STEP_INTERVAL);

        x += noCheckDistance * Math.cos(r);
        y += noCheckDistance * Math.sin(r);

        for (int i = 0; i < steps; i++) {

            if (Math.abs(getBlock((int) x, (int) y)) > 0) return new int[]{(int) x, (int) y};

            x += RAY_STEP_INTERVAL * Math.cos(r);
            y += RAY_STEP_INTERVAL * Math.sin(r);
        }
        return null;
    }

    // returns the position of the open space before the block
    public int[] blockRayCastReturnOpen(double x, double y, double r, double range) {
        return blockRayCastReturnOpen(x, y, r, range, 0);
    }

    public int[] blockRayCastReturnOpen(double x, double y, double r, double range, double noCheckDistance) {
        if (range > MAX_RANGE_RAY) range = MAX_RANGE_RAY;
        range -= noCheckDistance;

        int steps = (int) (range / RAY_STEP_INTERVAL);

        x += noCheckDistance * Math.cos(r);
        y += noCheckDistance * Math.sin(r);

        // Previous position
        double px = x, py = y;

        for (int i = 0; i < steps; i++) {

            x += RAY_STEP_INTERVAL * Math.cos(r);
            y += RAY_STEP_INTERVAL * Math.sin(r);

            if (Math.abs(getBlock((int) x, (int) y)) > 0) return new int[]{(int) px, (int) py};

            px = x;
            py = y;
        }
        return null;
    }

    /////=== Terrain checkers ===\\\\\
    public boolean isSolidBlock(int x, int y) {
        return (BlockData.getBlockCanWalkThrough(getBlock(x, y)) == false && getBlock(x, y) > 0);
    }

    public boolean isSolidBlock(double x, double y) {
        return isSolidBlock((int) x, (int) y);
    }

}

