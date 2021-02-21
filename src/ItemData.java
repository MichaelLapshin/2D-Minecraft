import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.TreeMap;

public class ItemData {
    // Place holders (ideal since primitive array search time is O(1))
    private static BufferedImage[] itemImages;
    private static TreeMap<Byte, Integer>[] itemCosts; // Item, quantity
    private static String[] itemNames;

    // Starts the block data loading sequence
    public ItemData() {
        // Initialized the arrays
        itemImages = new BufferedImage[127];
        itemCosts = new TreeMap[127];
        itemNames = new String[127];

        // Loads the item data into the arrays
        loadItemNames(itemNames);
        loadItemImage(itemImages);
        loadItemCosts(itemCosts);
    }

    /////=== Loading methods ===\\\\\
    private void loadItemNames(String[] itemNames) {
        itemNames[0] = "Air";
        itemNames[1] = "Grass Block";
        itemNames[2] = "Dirt Block";
        itemNames[3] = "Stone Block";
        itemNames[4] = "Oak Wood Block";
        itemNames[5] = "Oak Leaves Block";
        itemNames[6] = "Oak Planks Block";
        itemNames[7] = "Coal Ore Block";
        itemNames[8] = "Iron Ore Block";
        itemNames[9] = "Gold Ore Block";

        itemNames[10] = "Diamond Ore Block";
        itemNames[11] = "Emerald Ore Block";
        itemNames[12] = "Cobblestone Block";
        itemNames[13] = "Netherrack Block";
        itemNames[14] = "Obsidian";
        itemNames[15] = "Glass Block";
        itemNames[16] = "Sand";
        itemNames[17] = "Gravel";
        itemNames[18] = "Clay Block";
        itemNames[19] = "Brick Block";

        itemNames[20] = "Water";
        itemNames[21] = "Dandelion";
        itemNames[22] = "Shrub";
        itemNames[23] = "Rose";
        itemNames[24] = "Fern";
        itemNames[25] = "Sugar Cane Block";
        itemNames[26] = "Grass";
        itemNames[27] = "Wool Block";
        itemNames[28] = "Wooden Sign";
        itemNames[29] = "Cobweb";

        itemNames[30] = "Chest";
        itemNames[31] = "Crafting Table";
        itemNames[32] = "Furnace";
        itemNames[33] = "Torch";
        itemNames[34] = "Redstone Torch";
        itemNames[35] = "Soul Sand";
        itemNames[36] = "Bedrock";
        itemNames[37] = "Nether Quartz Ore";
        itemNames[38] = "Glowstone";
        itemNames[39] = "Nether Quartz Block";

        itemNames[40] = "Lava";
        itemNames[41] = "Gold Ingot";
        itemNames[42] = "Emerald";
        itemNames[43] = "Diamond";
        itemNames[44] = "Iron Ingot";
        itemNames[45] = "Charcoal";
        itemNames[46] = "Redstone Dust";
        itemNames[47] = "Glowstone Dust";
        itemNames[48] = "Nether Quartz";
        itemNames[49] = "Coal";

        itemNames[50] = "Stick";
        itemNames[51] = "Oak Sapling";
        itemNames[52] = "Apple";
        itemNames[53] = "Golden Apple";
        itemNames[54] = "Sugar";
        itemNames[55] = "Wheat Seed";
        itemNames[56] = "Wheat"; // Use seed
        itemNames[57] = "Bread"; // Use wheat
        itemNames[58] = "Redstone Ore Block";
        itemNames[59] = "Sugar Cane";

        itemNames[60] = "Raw Porkchop";
        itemNames[61] = "Cooked Porkchop";
        itemNames[62] = "Clay";
        itemNames[63] = "Brick";
        itemNames[64] = "Nether Brick";
        itemNames[65] = "Nether Brick Block";
        itemNames[66] = "Rotten Flesh";
        itemNames[67] = "Bucket";
        itemNames[68] = "Flint";
        itemNames[69] = "String";

        itemNames[70] = "Birch Wood Block";
        itemNames[71] = "Birch Planks Block";
        itemNames[72] = "Birch Leaves Block";
        itemNames[73] = "Spruce Wood Block";
        itemNames[74] = "Spruce Planks Block";
        itemNames[75] = "Spruce Leaves Block";
        itemNames[76] = "Birch Sapling";
        itemNames[77] = "Spruce Sapling";
        itemNames[78] = "Lava Bucket";
        itemNames[79] = "Water Bucket";

    }

    private void loadItemImage(BufferedImage[] itemImages) {
        BufferedImage blockCollection = null;
        BufferedImage transparentBlockCollection = null;
        BufferedImage itemCollection = null;
        try {
            blockCollection = ImageIO.read(new File("Images/Minecraft2D-BlockTextures.bmp"));
            transparentBlockCollection = ImageIO.read(new File("Images/Minecraft2D-BlockTextures.png"));
            itemCollection = ImageIO.read(new File("Images/Minecraft2D-ItemTextures.png"));
            // more items
        } catch (Exception E) {
            System.out.println("[STATIC BLOCK DATA] Failed to load block collection image");
            return;
        }

        itemImages[1] = DrawTools.loadSubImage(blockCollection, 16, 2, 0); // Grass block
        itemImages[2] = DrawTools.loadSubImage(blockCollection, 16, 3, 0); // Dirt block
        itemImages[3] = DrawTools.loadSubImage(blockCollection, 16, 0, 0); // Stone block
        itemImages[4] = DrawTools.loadSubImage(blockCollection, 16, 4, 1); // Oak Wood block
        itemImages[5] = DrawTools.loadSubImage(transparentBlockCollection, 16, 8, 1); // Oak Leaves block
        itemImages[6] = DrawTools.loadSubImage(blockCollection, 16, 5, 0); // Oak Planks block
        itemImages[7] = DrawTools.loadSubImage(blockCollection, 16, 2, 1); // Coal Ore block
        itemImages[8] = DrawTools.loadSubImage(blockCollection, 16, 1, 1); // Iron Ore block
        itemImages[9] = DrawTools.loadSubImage(blockCollection, 16, 0, 1); // Gold Ore block

        itemImages[10] = DrawTools.loadSubImage(blockCollection, 16, 9, 6); // Diamond Ore block
        itemImages[11] = DrawTools.loadSubImage(blockCollection, 16, 6, 12); // Emerald Ore block
        itemImages[12] = DrawTools.loadSubImage(blockCollection, 16, 4, 0); // Cobblestone
        itemImages[13] = DrawTools.loadSubImage(blockCollection, 16, 5, 9); // Netherrack
        itemImages[14] = DrawTools.loadSubImage(blockCollection, 16, 5, 11); // Obsidian
        itemImages[15] = DrawTools.loadSubImage(transparentBlockCollection, 16, 13, 1); // Glass Block
        itemImages[16] = DrawTools.loadSubImage(blockCollection, 16, 14, 0); // Sand
        itemImages[17] = DrawTools.loadSubImage(blockCollection, 16, 15, 0); // Gravel
        itemImages[18] = DrawTools.loadSubImage(blockCollection, 16, 14, 8); // Clay Block
        itemImages[19] = DrawTools.loadSubImage(blockCollection, 16, 5, 5); // Brick Block

        itemImages[20] = DrawTools.loadSubImage(blockCollection, 16, 12, 15); // Water
        itemImages[21] = DrawTools.loadSubImage(transparentBlockCollection, 16, 12, 3); // Dandelion
        itemImages[22] = DrawTools.loadSubImage(transparentBlockCollection, 16, 11, 3); // Shrub
        itemImages[23] = DrawTools.loadSubImage(transparentBlockCollection, 16, 13, 3); // Rose
        itemImages[24] = DrawTools.loadSubImage(transparentBlockCollection, 16, 10, 3); // Fern
        itemImages[25] = DrawTools.loadSubImage(transparentBlockCollection, 16, 15, 8); // Sugar Cane Block
        itemImages[26] = DrawTools.loadSubImage(transparentBlockCollection, 16, 9, 3); // Grass
        itemImages[27] = DrawTools.loadSubImage(blockCollection, 16, 0, 4); // Wool Block
        itemImages[28] = DrawTools.loadSubImage(itemCollection, 16, 10, 2); // Wooden Sign
        itemImages[29] = DrawTools.loadSubImage(transparentBlockCollection, 16, 0, 5); // Cobweb

        itemImages[30] = DrawTools.loadSubImage(blockCollection, 16, 14, 5); // Chest
        itemImages[31] = DrawTools.loadSubImage(blockCollection, 16, 12, 6); // Crafting Table
        itemImages[32] = DrawTools.loadSubImage(blockCollection, 16, 9, 7); // Furnace
        itemImages[33] = DrawTools.loadSubImage(transparentBlockCollection, 16, 12, 5); // Torch
        itemImages[34] = DrawTools.loadSubImage(transparentBlockCollection, 16, 3, 8); // Redstone Torch
        itemImages[35] = DrawTools.loadSubImage(blockCollection, 16, 6, 9); // Soul Sand
        itemImages[36] = DrawTools.loadSubImage(blockCollection, 16, 13, 0); // Bedrock
        itemImages[37] = DrawTools.loadSubImage(blockCollection, 16, 1, 14); // Nether Quartz Ore
        itemImages[38] = DrawTools.loadSubImage(blockCollection, 16, 7, 9); // Glowstone
        itemImages[39] = DrawTools.loadSubImage(blockCollection, 16, 5, 14); // Nether Quartz Block

        itemImages[40] = DrawTools.loadSubImage(blockCollection, 16, 13, 15); // Lava
        itemImages[41] = DrawTools.loadSubImage(itemCollection, 16, 7, 2); // Gold Ingot
        itemImages[42] = DrawTools.loadSubImage(itemCollection, 16, 10, 11); // Emerald
        itemImages[43] = DrawTools.loadSubImage(itemCollection, 16, 7, 3); // Diamond
        itemImages[44] = DrawTools.loadSubImage(itemCollection, 16, 7, 1); // Iron Ingot
        itemImages[45] = DrawTools.loadSubImage(itemCollection, 16, 8, 10); // Charcoal
        itemImages[46] = DrawTools.loadSubImage(itemCollection, 16, 8, 3); // Redstone Dust
        itemImages[47] = DrawTools.loadSubImage(itemCollection, 16, 9, 4); // Glowstone Dust
        itemImages[48] = DrawTools.loadSubImage(itemCollection, 16, 12, 12); // Nether Quartz
        itemImages[49] = DrawTools.loadSubImage(itemCollection, 16, 7, 0); // Coal

        itemImages[50] = DrawTools.loadSubImage(itemCollection, 16, 5, 3); // Stick
        itemImages[51] = DrawTools.loadSubImage(transparentBlockCollection, 16, 9, 0); // Oak Sapling Drop
        itemImages[52] = DrawTools.loadSubImage(itemCollection, 16, 10, 0); // Apple
        itemImages[53] = DrawTools.loadSubImage(itemCollection, 16, 11, 0); // Golden Apple
        itemImages[54] = DrawTools.loadSubImage(itemCollection, 16, 13, 0); // Sugar
        itemImages[55] = DrawTools.loadSubImage(itemCollection, 16, 9, 0); // Wheat Seeds
        itemImages[56] = DrawTools.loadSubImage(itemCollection, 16, 9, 1); // Wheat
        itemImages[57] = DrawTools.loadSubImage(itemCollection, 16, 9, 2); // Bread
        itemImages[58] = DrawTools.loadSubImage(blockCollection, 16, 1, 8); // Redstone Ore Block
        itemImages[59] = DrawTools.loadSubImage(itemCollection, 16, 11, 1); // Sugar Cane

        itemImages[60] = DrawTools.loadSubImage(itemCollection, 16, 7, 5); // Raw Porkchop
        itemImages[61] = DrawTools.loadSubImage(itemCollection, 16, 8, 5); // Cooked Porkchop
        itemImages[62] = DrawTools.loadSubImage(itemCollection, 16, 9, 3); // Clay
        itemImages[63] = DrawTools.loadSubImage(itemCollection, 16, 6, 1); // Brick
        itemImages[64] = DrawTools.loadSubImage(itemCollection, 16, 5, 10); // Nether Brick
        itemImages[65] = DrawTools.loadSubImage(blockCollection, 16, 0, 11); // Nether Brick Block
        itemImages[66] = DrawTools.loadSubImage(itemCollection, 16, 11, 5); // Rotten Flesh
        itemImages[67] = DrawTools.loadSubImage(itemCollection, 16, 10, 4); // Bucket
        itemImages[68] = DrawTools.loadSubImage(itemCollection, 16, 6, 0); // Flint
        itemImages[69] = DrawTools.loadSubImage(itemCollection, 16, 8, 0); // String

        itemImages[70] = DrawTools.loadSubImage(blockCollection, 16, 6, 1); // Birch Wood Block
        itemImages[71] = DrawTools.loadSubImage(blockCollection, 16, 7, 0); // Birch Planks Block
        itemImages[72] = DrawTools.loadSubImage(transparentBlockCollection, 16, 10, 1); // Birch Leaves Block
        itemImages[73] = DrawTools.loadSubImage(blockCollection, 16, 5, 1); // Spruce Wood Block
        itemImages[74] = DrawTools.loadSubImage(blockCollection, 16, 6, 0); // Spruce Planks Block
        itemImages[75] = DrawTools.loadSubImage(transparentBlockCollection, 16, 9, 1); // Spruce Leaves Block
        itemImages[76] = DrawTools.loadSubImage(transparentBlockCollection, 16, 11, 0); // Birch Sapling Drop
        itemImages[77] = DrawTools.loadSubImage(transparentBlockCollection, 16, 10, 0); // Spruce Sapling Drop
        itemImages[78] = DrawTools.loadSubImage(itemCollection, 16, 12, 4); // Lava Bucket
        itemImages[79] = DrawTools.loadSubImage(itemCollection, 16, 11, 4); // Water Bucket

    }

    private void loadItemCosts(TreeMap<Byte, Integer>[] itemCosts) {
        itemCosts[0] = null;
        itemCosts[1] = null;
        itemCosts[2] = addItemsToMap("1-1");
        itemCosts[3] = addItemsToMap("12-1 49-1");
        itemCosts[4] = null;
        itemCosts[5] = addItemsToMap("4-1 55-4");
        itemCosts[6] = addItemsToMap("4-1");
        itemCosts[7] = null;
        itemCosts[8] = null;
        itemCosts[9] = null;

        itemCosts[10] = null;
        itemCosts[11] = null;
        itemCosts[12] = addItemsToMap("3-1");
        itemCosts[13] = null;
        itemCosts[14] = addItemsToMap("78-1 79-1");
        itemCosts[15] = addItemsToMap("16-1 49-1");
        itemCosts[16] = null;
        itemCosts[17] = null;
        itemCosts[18] = addItemsToMap("62-4");
        itemCosts[19] = addItemsToMap("63-9");

        itemCosts[20] = null;
        itemCosts[21] = null;
        itemCosts[22] = null;
        itemCosts[23] = null;
        itemCosts[24] = null;
        itemCosts[25] = null;
        itemCosts[26] = addItemsToMap("55-3");
        itemCosts[27] = addItemsToMap("69-9");
        itemCosts[28] = addItemsToMap("50-1 6-6");
        itemCosts[29] = null;

        itemCosts[30] = addItemsToMap("6-8");
        itemCosts[31] = addItemsToMap("6-4");
        itemCosts[32] = addItemsToMap("12-8");
        itemCosts[33] = addItemsToMap("50-6 49-1");
        itemCosts[34] = addItemsToMap("46-1 49-1");
        itemCosts[35] = null;
        itemCosts[36] = null;
        itemCosts[37] = addItemsToMap("13-3 48-3");
        itemCosts[38] = addItemsToMap("47-9");
        itemCosts[39] = addItemsToMap("48-9");

        itemCosts[40] = null;
        itemCosts[41] = addItemsToMap("9-1 49-1");
        itemCosts[42] = addItemsToMap("11-1 49-1");
        itemCosts[43] = addItemsToMap("10-1 49-1");
        itemCosts[44] = addItemsToMap("8-1 49-1");
        itemCosts[45] = addItemsToMap("4-1 49-1");
        itemCosts[46] = addItemsToMap("58-1 49-1");
        itemCosts[47] = null;
        itemCosts[48] = null;
        itemCosts[49] = null;

        itemCosts[50] = addItemsToMap("6-2");
        itemCosts[51] = null;
        itemCosts[52] = null;
        itemCosts[53] = addItemsToMap("52-1 41-8");
        itemCosts[54] = addItemsToMap("59-1");
        itemCosts[55] = addItemsToMap("26-1");
        itemCosts[56] = addItemsToMap("55-1");
        itemCosts[57] = addItemsToMap("56-3");
        itemCosts[58] = null;
        itemCosts[59] = null;

        itemCosts[60] = null;
        itemCosts[61] = addItemsToMap("60-1 49-1");
        itemCosts[62] = addItemsToMap("18-1 49-1");
        itemCosts[63] = addItemsToMap("62-4 49-1");
        itemCosts[64] = addItemsToMap("13-1 49-1");
        itemCosts[65] = addItemsToMap("64-9");
        itemCosts[66] = null;
        itemCosts[67] = addItemsToMap("44-3");
        itemCosts[68] = addItemsToMap("17-1");
        itemCosts[69] = addItemsToMap("29-1");

        itemCosts[70] = addItemsToMap("4-1");
        itemCosts[71] = addItemsToMap("70-1");
        itemCosts[72] = addItemsToMap("70-1 55-4");
        itemCosts[73] = addItemsToMap("4-1");
        itemCosts[74] = addItemsToMap("73-1");
        itemCosts[75] = addItemsToMap("13-1");
        itemCosts[76] = addItemsToMap("55-5");
        itemCosts[77] = addItemsToMap("55-5");
        itemCosts[78] = addItemsToMap("67-1 40-1");
        itemCosts[79] = addItemsToMap("67-1 20-1");

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

    /////=== Getters for the item data ===\\\\\

    public static BufferedImage getItemImage(byte itemType) {
        return itemImages[itemType];
    }

    public static TreeMap<Byte, Integer> getItemCost(int itemType) {
        return itemCosts[itemType];
    }

    public static String getItemName(byte itemType) {
        return itemNames[itemType];
    }

    public static TreeMap<Byte, Integer>[] getItemCosts() {
        return itemCosts;
    }
}
