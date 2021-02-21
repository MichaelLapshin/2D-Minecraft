import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class DrawTools {
    // Player model place holders
    private static int NUMBER_OF_PLAYER_MODELS = 6;
    private static int NUMBER_OF_HOSTILE_MODELS = 4;
    private static int NUMBER_OF_PASSIVE_MODELS = 2;
    private static BufferedImage[][][] playerModel;
    private static BufferedImage[][][] passiveModel;
    private static BufferedImage[][][] hostileModel;

    // Constructor that starts the loading sequence
    public DrawTools() {
        loadAllPlayerModels();
    }

    public void loadAllPlayerModels() {
        // All player models are 64 x 64 pixels large
        playerModel = new BufferedImage[NUMBER_OF_PLAYER_MODELS][][];
        hostileModel = new BufferedImage[NUMBER_OF_HOSTILE_MODELS][][];
        passiveModel = new BufferedImage[NUMBER_OF_PASSIVE_MODELS][][];

        // playerModel[model#][side][part]
        /*
        * Side: 0 = right, 1 = front, 2 = left, 3 = back
        * Side: 0 = left, 1 = front, 2 = right, 3 = back
        * Part: 0 = head, 1 = body, 2 = right arm, 3 = left arm, 4 = right leg, 5 = left leg
        */
        for (int i = 0; i < NUMBER_OF_PLAYER_MODELS; i++) {
            try {
                playerModel[i] = loadPlayerModel(ImageIO.read(new File("Images/PlayerModel-" + i + ".png")));
            } catch (Exception E) {
                E.printStackTrace();
                System.out.println("[LOAD] Could not load 'Images/PlayerModel-" + i + ".png' player model.");
            }
        }

        for (int i = 0; i < NUMBER_OF_HOSTILE_MODELS; i++) {
            try {
                hostileModel[i] = loadPlayerModel(ImageIO.read(new File("Images/HostileModel-" + i + ".png")));
            } catch (Exception E) {
                E.printStackTrace();
                System.out.println("[LOAD] Could not load 'Images/HostileModel-" + i + ".png' mob model.");
            }
        }

        for (int i = 0; i < NUMBER_OF_PASSIVE_MODELS; i++) {
            try {
                passiveModel[i] = loadPlayerModel(ImageIO.read(new File("Images/PassiveModel-" + i + ".png")));
            } catch (Exception E) {
                E.printStackTrace();
                System.out.println("[LOAD] Could not load 'Images/PassiveModel-" + i + ".png' mob model.");
            }
        }
    }

    // Parses an image into the player model
    private BufferedImage[][] loadPlayerModel(BufferedImage image) {
        BufferedImage[][] parts = new BufferedImage[4][];

        for (int side = 0; side < 4; side++) {
            int index = side;
            if (index == 0) index = 2;
            else if (index == 2) index = 0;
            parts[index] = new BufferedImage[6];
            parts[index][0] = loadSubImage(image, 8, side, 1); // Loads Head
            parts[index][4] = image.getSubimage(side * 4, 20, 4, 12); // Loads Right Leg
            parts[index][2] = image.getSubimage(40 + side * 4, 20, 4, 12); // Loads Right Arm
            parts[index][5] = image.getSubimage(16 + side * 4, 52, 4, 12); // Loads Left Leg
            parts[index][3] = image.getSubimage(32 + side * 4, 52, 4, 12); // Loads Left Arm

            // Loads body part
            if (side == 0) parts[index][1] = image.getSubimage(16, 20, 4, 12);
            else if (side == 1) parts[index][1] = image.getSubimage(20, 20, 8, 12);
            else if (side == 2) parts[index][1] = image.getSubimage(32, 20, 4, 12);
            else if (side == 3) parts[index][1] = image.getSubimage(36, 20, 8, 12);
        }
        return parts;
    }

    public static BufferedImage[][] getRandomPlayerModel() {
        return getPlayerModel((int) (Math.random() * NUMBER_OF_PLAYER_MODELS));
    }

    public static BufferedImage[][] getRandomHostileModel() {
        return getHostileModel((int) (Math.random() * NUMBER_OF_HOSTILE_MODELS));
    }

    public static BufferedImage[][] getRandomPassiveModel() {
        return getPassiveModel((int) (Math.random() * NUMBER_OF_PASSIVE_MODELS));
    }

    public static BufferedImage[][] getPlayerModel(int index) {
        return playerModel[index];
    }

    public static BufferedImage[][] getHostileModel(int index) {
        return hostileModel[index];
    }

    public static BufferedImage[][] getPassiveModel(int index) {
        return passiveModel[index];
    }

//    public static BufferedImage[][] getZombieModel() {
//        return zombieModel;
//    }
//
//    public static BufferedImage[][] getPigModel() {
//        return pigModel;
//    }

    /////=== Random Tools ===\\\\\
    // Get sub image of a grid of images. (0, 0) is top left corner
    public static BufferedImage loadSubImage(BufferedImage original, int subImageSize, int x, int y) {
        return original.getSubimage(x * subImageSize, y * subImageSize, subImageSize, subImageSize);
    }

    /////=== Converting game coordinates to screen coordinates ===\\\\\
    public static double game2ScreenX(double drawX, Dimension dimension, int blockRenderDistance, double x) {
        return (x - drawX) * screenDisplayRatio(dimension, blockRenderDistance) + dimension.width / 2.0;
        //return (int) Math.round((x - player.getX()) * screenDisplayRatio(dimension, blockRenderDistance) + dimension.width / 2.0);
    }

    public static double game2ScreenY(double drawY, Dimension dimension, int blockRenderDistance, double y) {
        return (drawY - y) * screenDisplayRatio(dimension, blockRenderDistance) + dimension.height / 2.0;
        //return (int) Math.round((player.getY() - y) * screenDisplayRatio(dimension, blockRenderDistance) + dimension.height / 2.0);
    }

    public static double screenDisplayRatio(Dimension screenDimension, int blockRenderDistance) {
        return Double.max(screenDimension.width, screenDimension.height) / (blockRenderDistance * 2.0);
    }

    public static double game2screenLength(Dimension dimension, int blockRenderDistance, double length) {
        return length * screenDisplayRatio(dimension, blockRenderDistance);
    }

    public static BufferedImage image2Buffered(Image image) {
        BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = bufferedImage.createGraphics();
        g2.drawImage(image, 0, 0, null);
        g2.dispose();

        return bufferedImage;
    }
}
