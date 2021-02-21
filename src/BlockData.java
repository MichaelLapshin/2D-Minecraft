import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.TreeMap;

public class BlockData {
    // Place holders (ideal since primitive array search time is O(1))
    private static BufferedImage[] blockImages;
    private static TreeMap<Byte, Integer>[] blockDrops;
    private static int[] blockHealth;
    private static boolean[] blockCanWalkThrough;

    // Image optimization
    private static BufferedImage[] blockImagesResized;
    private static int staticRenderDistance = 0;
    private static int staticDimensionX = 0;
    private static int staticDimensionY = 0;

    // Starts the block data loading sequence
    public BlockData() {
        // Initialized the arrays
        blockImages = new BufferedImage[127];
        blockImagesResized = new BufferedImage[127];
        blockDrops = new TreeMap[127];
        blockHealth = new int[127];
        blockCanWalkThrough = new boolean[127];

        // Loads the block data into the arrays
        loadBlockImage(blockImages);
        loadBlockDrops(blockDrops);
        loadBlockHealth(blockHealth);
        loadBlockCanWalkThrough(blockCanWalkThrough);
    }

    /////=== Loading methods for all block data ===\\\\\
    private void loadBlockImage(BufferedImage[] blockImages) {

        blockImages[1] = ItemData.getItemImage((byte) 1); // Grass block
        blockImages[2] = ItemData.getItemImage((byte) 2); // Dirt block
        blockImages[3] = ItemData.getItemImage((byte) 3); // Stone block
        blockImages[4] = ItemData.getItemImage((byte) 4); // Oak Wood block
        blockImages[5] = ItemData.getItemImage((byte) 5); // Oak Leaves block
        blockImages[6] = ItemData.getItemImage((byte) 6); // Oak Planks block
        blockImages[7] = ItemData.getItemImage((byte) 7); // Coal Ore block
        blockImages[8] = ItemData.getItemImage((byte) 8); // Iron Ore block
        blockImages[9] = ItemData.getItemImage((byte) 9); // Gold Ore block

        blockImages[10] = ItemData.getItemImage((byte) 10); // Diamond Ore block
        blockImages[11] = ItemData.getItemImage((byte) 11); // Emerald Ore block
        blockImages[12] = ItemData.getItemImage((byte) 12); // Cobblestone
        blockImages[13] = ItemData.getItemImage((byte) 13); // Netherrack
        blockImages[14] = ItemData.getItemImage((byte) 14); // Obsidian
        blockImages[15] = ItemData.getItemImage((byte) 15); // Glass Block
        blockImages[16] = ItemData.getItemImage((byte) 16); // Sand
        blockImages[17] = ItemData.getItemImage((byte) 17); // Gravel
        blockImages[18] = ItemData.getItemImage((byte) 18); // Clay Block
        blockImages[19] = ItemData.getItemImage((byte) 19); // Brick Block

        blockImages[20] = ItemData.getItemImage((byte) 20); // Water
        blockImages[21] = ItemData.getItemImage((byte) 21); // Dandelion
        blockImages[22] = ItemData.getItemImage((byte) 22); // Shrub
        blockImages[23] = ItemData.getItemImage((byte) 23); // Rose
        blockImages[24] = ItemData.getItemImage((byte) 24); // Fern
        blockImages[25] = ItemData.getItemImage((byte) 25); // Sugar Cane Block
        blockImages[26] = ItemData.getItemImage((byte) 26); // Grass
        blockImages[27] = ItemData.getItemImage((byte) 27); // Wool Block
        blockImages[29] = ItemData.getItemImage((byte) 29); // Cobweb

        blockImages[30] = ItemData.getItemImage((byte) 30); // Chest
        blockImages[31] = ItemData.getItemImage((byte) 31); // Crafting Table
        blockImages[32] = ItemData.getItemImage((byte) 32); // Furnace
        blockImages[33] = ItemData.getItemImage((byte) 33); // Torch
        blockImages[34] = ItemData.getItemImage((byte) 34); // Redstone Torch
        blockImages[35] = ItemData.getItemImage((byte) 35); // Soul Sand
        blockImages[36] = ItemData.getItemImage((byte) 36); // Bedrock
        blockImages[37] = ItemData.getItemImage((byte) 37); // Nether Quartz Ore
        blockImages[38] = ItemData.getItemImage((byte) 38); // Glowstone
        blockImages[39] = ItemData.getItemImage((byte) 39); // Nether Quartz Block

        blockImages[40] = ItemData.getItemImage((byte) 40); // Lava

        blockImages[51] = ItemData.getItemImage((byte) 51); // Oak Sapling Drop
        blockImages[58] = ItemData.getItemImage((byte) 58); // Redstone Ore Block

        blockImages[65] = ItemData.getItemImage((byte) 65); // Nether Brick Block

        blockImages[70] = ItemData.getItemImage((byte) 70); // Birch Wood Block
        blockImages[71] = ItemData.getItemImage((byte) 71); // Birch Planks Block
        blockImages[72] = ItemData.getItemImage((byte) 72); // Birch Leaves Block
        blockImages[73] = ItemData.getItemImage((byte) 73); // Spruce Wood Block
        blockImages[74] = ItemData.getItemImage((byte) 74); // Spruce Planks Block
        blockImages[75] = ItemData.getItemImage((byte) 75); // Spruce Leaves Block
        blockImages[76] = ItemData.getItemImage((byte) 76); // Birch Sapling Drop
        blockImages[77] = ItemData.getItemImage((byte) 77); // Spruce Sapling Drop

//        BufferedImage blockCollection = null;
//        BufferedImage transparentBlockCollection = null;
//        try {
//            blockCollection = ImageIO.read(new File("Images/Minecraft2D-BlockTextures.bmp"));
//            transparentBlockCollection = ImageIO.read(new File("Images/Minecraft2D-BlockTextures.png"));
//        } catch (Exception E) {
//            E.printStackTrace();
//            System.out.println("[STATIC BLOCK DATA] Failed to load block collection image");
//            return;
//        }


//        blockImages[1] = DrawTools.loadSubImage(blockCollection, 16, 2, 0); // Grass block
//        blockImages[2] = DrawTools.loadSubImage(blockCollection, 16, 3, 0); // Dirt block
//        blockImages[3] = DrawTools.loadSubImage(blockCollection, 16, 0, 0); // Stone block
//        blockImages[4] = DrawTools.loadSubImage(blockCollection, 16, 4, 1); // Oak Wood block
//        blockImages[5] = DrawTools.loadSubImage(transparentBlockCollection, 16, 8, 1); // Oak Leaves block
//        blockImages[6] = DrawTools.loadSubImage(blockCollection, 16, 5, 0); // Oak Planks block
//        blockImages[7] = DrawTools.loadSubImage(blockCollection, 16, 2, 1); // Coal Ore block
//        blockImages[8] = DrawTools.loadSubImage(blockCollection, 16, 1, 1); // Iron Ore block
//        blockImages[9] = DrawTools.loadSubImage(blockCollection, 16, 0, 1); // Gold Ore block
//
//        blockImages[10] = DrawTools.loadSubImage(blockCollection, 16, 9, 6); // Diamond Ore block
//        blockImages[11] = DrawTools.loadSubImage(blockCollection, 16, 6, 12); // Emerald Ore block
//        blockImages[12] = DrawTools.loadSubImage(blockCollection, 16, 4, 0); // Cobblestone
//        blockImages[13] = DrawTools.loadSubImage(blockCollection, 16, 5, 9); // Netherrack
//        blockImages[14] = DrawTools.loadSubImage(blockCollection, 16, 5, 11); // Obsidian
//        blockImages[15] = DrawTools.loadSubImage(transparentBlockCollection, 16, 13, 1); // Glass Block
//        blockImages[16] = DrawTools.loadSubImage(blockCollection, 16, 14, 0); // Sand
//        blockImages[17] = DrawTools.loadSubImage(blockCollection, 16, 15, 1); // Gravel
//        blockImages[18] = DrawTools.loadSubImage(blockCollection, 16, 14, 8); // Clay Block
//        blockImages[19] = DrawTools.loadSubImage(blockCollection, 16, 5, 5); // Brick Block
//
//        blockImages[20] = DrawTools.loadSubImage(blockCollection, 16, 12, 15); // Water
//        blockImages[21] = DrawTools.loadSubImage(transparentBlockCollection, 16, 12, 3); // Dandelion
//        blockImages[22] = DrawTools.loadSubImage(transparentBlockCollection, 16, 11, 3); // Shrub
//        blockImages[23] = DrawTools.loadSubImage(transparentBlockCollection, 16, 13, 3); // Rose
//        blockImages[24] = DrawTools.loadSubImage(transparentBlockCollection, 16, 10, 3); // Fern
//        blockImages[25] = DrawTools.loadSubImage(transparentBlockCollection, 16, 15, 8); // Sugar Cane Block
//        blockImages[26] = DrawTools.loadSubImage(transparentBlockCollection, 16, 9, 3); // Grass
//        blockImages[27] = DrawTools.loadSubImage(blockCollection, 16, 0, 4); // Wool Block
//        blockImages[29] = DrawTools.loadSubImage(transparentBlockCollection, 16, 0, 5); // Cobweb
//
//        blockImages[30] = DrawTools.loadSubImage(blockCollection, 16, 14, 5); // Chest
//        blockImages[31] = DrawTools.loadSubImage(blockCollection, 16, 12, 6); // Crafting Table
//        blockImages[32] = DrawTools.loadSubImage(blockCollection, 16, 9, 7); // Furnace
//        blockImages[33] = DrawTools.loadSubImage(transparentBlockCollection, 16, 12, 5); // Torch
//        blockImages[34] = DrawTools.loadSubImage(transparentBlockCollection, 16, 3, 8); // Redstone Torch
//        blockImages[35] = DrawTools.loadSubImage(blockCollection, 16, 6, 9); // Soul Sand
//        blockImages[36] = DrawTools.loadSubImage(blockCollection, 16, 13, 0); // Bedrock
//        blockImages[37] = DrawTools.loadSubImage(blockCollection, 16, 1, 14); // Nether Quartz Ore
//        blockImages[38] = DrawTools.loadSubImage(blockCollection, 16, 7, 9); // Glowstone
//        blockImages[39] = DrawTools.loadSubImage(blockCollection, 16, 5, 14); // Nether Quartz Block
//
//        blockImages[40] = DrawTools.loadSubImage(blockCollection, 16, 13, 15); // Lava
//
//        blockImages[51] = DrawTools.loadSubImage(transparentBlockCollection, 16, 9, 0); // Oak Sapling Drop
//        blockImages[58] = DrawTools.loadSubImage(blockCollection, 16, 1, 8); // Redstone Ore Block
//
//        blockImages[65] = DrawTools.loadSubImage(blockCollection, 16, 0, 11); // Nether Brick Block
//        blockImages[70] = DrawTools.loadSubImage(blockCollection, 16, 6, 1); // Birch Wood Block
//
//        blockImages[71] = DrawTools.loadSubImage(blockCollection, 16, 7, 0); // Birch Planks Block
//        blockImages[72] = DrawTools.loadSubImage(transparentBlockCollection, 16, 10, 1); // Birch Leaves Block
//        blockImages[73] = DrawTools.loadSubImage(blockCollection, 16, 5, 1); // Spruce Wood Block
//        blockImages[74] = DrawTools.loadSubImage(blockCollection, 16, 6, 0); // Spruce Planks Block
//        blockImages[75] = DrawTools.loadSubImage(transparentBlockCollection, 16, 9, 1); // Spruce Leaves Block
//        blockImages[76] = DrawTools.loadSubImage(transparentBlockCollection, 16, 11, 0); // Birch Sapling Drop
//        blockImages[77] = DrawTools.loadSubImage(transparentBlockCollection, 16, 10, 0); // Spruce Sapling Drop
    }

    private void loadBlockDrops(TreeMap<Byte, Integer>[] blockDrops) {
        blockDrops[1] = addItemsToMap("2-1");
        blockDrops[2] = addItemsToMap("1-1");
        blockDrops[3] = addItemsToMap("12-1");
        blockDrops[4] = addItemsToMap("4-1");
        blockDrops[5] = addItemsToMap("51-1 52-1 50-1");
        blockDrops[6] = addItemsToMap("6-1");
        blockDrops[7] = addItemsToMap("49-3");
        blockDrops[8] = addItemsToMap("8-1");
        blockDrops[9] = addItemsToMap("9-1");

        blockDrops[10] = addItemsToMap("43-1");
        blockDrops[11] = addItemsToMap("42-1");
        blockDrops[12] = addItemsToMap("12-1");
        blockDrops[13] = addItemsToMap("13-1");
        blockDrops[14] = addItemsToMap("14-1");
        blockDrops[15] = null;
        blockDrops[16] = addItemsToMap("16-1");
        blockDrops[17] = addItemsToMap("17-1");
        blockDrops[18] = addItemsToMap("62-8");
        blockDrops[19] = addItemsToMap("19-1");

        blockDrops[20] = addItemsToMap("79-1");
        blockDrops[21] = addItemsToMap("21-1");
        blockDrops[22] = addItemsToMap("22-1");
        blockDrops[23] = addItemsToMap("23-1");
        blockDrops[24] = addItemsToMap("24-1");
        blockDrops[25] = addItemsToMap("59-3");
        blockDrops[26] = addItemsToMap("55-3");
        blockDrops[27] = addItemsToMap("27-1");
        blockDrops[28] = addItemsToMap("28-1");
        blockDrops[29] = addItemsToMap("69-3");

        blockDrops[30] = addItemsToMap("30-1");
        blockDrops[31] = addItemsToMap("31-1");
        blockDrops[32] = addItemsToMap("32-1");
        blockDrops[33] = addItemsToMap("33-1");
        blockDrops[34] = addItemsToMap("34-1");
        blockDrops[35] = addItemsToMap("35-1");
        blockDrops[36] = addItemsToMap("36-1");
        blockDrops[37] = addItemsToMap("13-2 48-3");
        blockDrops[38] = addItemsToMap("47-4");
        blockDrops[39] = addItemsToMap("39-1");

        blockDrops[40] = addItemsToMap("78-1");

    }

    private TreeMap<Byte, Integer> addItemsToMap(String items) {
        TreeMap<Byte, Integer> map = new TreeMap<>();

        String[] list = items.split(" ");
        for (int i = 0; i < list.length; i++) {
            String[] element = list[i].split("-");
            map.put(Byte.parseByte(element[0]), Integer.parseInt(element[1]));
        }

        return map;
    }

    private void loadBlockHealth(int[] blockHealth) {
        blockHealth[1] = 100;
        blockHealth[2] = 100;
        blockHealth[3] = 120;
        blockHealth[4] = 100;
        blockHealth[5] = 50;
        blockHealth[6] = 100;
        blockHealth[7] = 120;
        blockHealth[8] = 130;
        blockHealth[9] = 130;

        blockHealth[10] = 150;
        blockHealth[11] = 130;
        blockHealth[12] = 120;
        blockHealth[13] = 100;
        blockHealth[14] = 250;
        blockHealth[15] = 50;
        blockHealth[16] = 80;
        blockHealth[17] = 80;
        blockHealth[18] = 80;
        blockHealth[19] = 100;

        blockHealth[20] = 50;
        blockHealth[21] = 10;
        blockHealth[22] = 10;
        blockHealth[23] = 10;
        blockHealth[24] = 10;
        blockHealth[25] = 10;
        blockHealth[26] = 10;
        blockHealth[27] = 70;
        blockHealth[28] = 70;
        blockHealth[29] = 100;

        blockHealth[30] = 100;
        blockHealth[31] = 100;
        blockHealth[32] = 120;
        blockHealth[33] = 10;
        blockHealth[34] = 10;
        blockHealth[35] = 80;
        blockHealth[36] = Integer.MAX_VALUE;
        blockHealth[37] = 120;
        blockHealth[38] = 70;
        blockHealth[39] = 100;

        blockHealth[40] = 50;


    }

    private void loadBlockCanWalkThrough(boolean[] blockCanWalkThrough) {
        blockCanWalkThrough[0] = true;
        blockCanWalkThrough[1] = false;
        blockCanWalkThrough[2] = false;
        blockCanWalkThrough[3] = false;
        blockCanWalkThrough[4] = false;
        blockCanWalkThrough[5] = true;
        blockCanWalkThrough[6] = false;
        blockCanWalkThrough[7] = false;
        blockCanWalkThrough[8] = false;
        blockCanWalkThrough[9] = false;
        blockCanWalkThrough[10] = false;
        blockCanWalkThrough[11] = false;
        blockCanWalkThrough[12] = false;
        blockCanWalkThrough[13] = false;

        blockCanWalkThrough[20] = true;
        blockCanWalkThrough[21] = true;
        blockCanWalkThrough[22] = true;
        blockCanWalkThrough[23] = true;
        blockCanWalkThrough[24] = true;
        blockCanWalkThrough[25] = true;
        blockCanWalkThrough[26] = true;
        blockCanWalkThrough[29] = true;
        blockCanWalkThrough[33] = true;
        blockCanWalkThrough[34] = true;
        blockCanWalkThrough[40] = true;
        blockCanWalkThrough[51] = true;
        blockCanWalkThrough[69] = true;
        blockCanWalkThrough[76] = true;
        blockCanWalkThrough[77] = true;
    }

    /////=== Getters after the data is loaded ===\\\\\
    public static BufferedImage getBlockImage(int blockType) {
        return blockImages[Math.abs(blockType)];
    }

    public static TreeMap<Byte, Integer> getBlockDrops(int blockType) {
        return blockDrops[Math.abs(blockType)];
    }

    public static int getBlockHealth(int blockType) {
        return blockHealth[Math.abs(blockType)];
    }

    public static boolean getBlockCanWalkThrough(int blockType) {
        return blockCanWalkThrough[Math.abs(blockType)];
    }

    /////=== Optimal block rendering ===\\\\\
    public static BufferedImage getBlockImageResized(Dimension dimension, int renderDistance, int blockType) {
        if (blockImages[Math.abs(blockType)] == null) return null;

        if (staticDimensionX == (int) dimension.getWidth() && staticDimensionY == (int) dimension.getHeight() && staticRenderDistance == renderDistance && blockImagesResized[Math.abs(blockType)] != null) {
            return blockImagesResized[Math.abs(blockType)];
        }

        staticDimensionX = (int) dimension.getWidth();
        staticDimensionY = (int) dimension.getHeight();
        staticRenderDistance = renderDistance;

        int sideLength = (int) Math.ceil(DrawTools.game2screenLength(dimension, renderDistance, 1));

        for (int i = 0; i < blockImagesResized.length; i++) {
            if (blockImages[i] != null)
                blockImagesResized[i] = DrawTools.image2Buffered(blockImages[i].getScaledInstance(sideLength, sideLength, Image.SCALE_DEFAULT));
        }

        //blockImagesResized[Math.abs(blockType)] = blockImages[Math.abs(blockType)].;
        return blockImagesResized[Math.abs(blockType)];
    }
}
