import java.awt.*;

public class EntityItem extends Entity {
    private double width, height;
    private byte item;

    // Floating animation
    private boolean floating;
    private double floatHeight;
    private final double FLOAT_MAX_HEIGHT = 0.16;
    private final double FLOAT_SPEED = 0.16;

    // Despawn variables
    private final int PARTICLE_DESAPWN_SECONDS = 300;
    private final int DROPS_DESAPWN_SECONDS = 120;
    //private int despawnTimer; // replaced by entity health

    // Sizes
    private final double PARTICLE_SIZE = 0.3;
    private final double DROP_SIZE = 0.4;


    // Constructor
    public EntityItem(World world, int id, double x, double y, double vx, double vy, byte itemType, boolean particle) {
        super(world, id, x, y, vx, vy, 10, 10, false);

        if (particle == true) {
            this.width = PARTICLE_SIZE / 2 + Math.random() * PARTICLE_SIZE;
            this.height = PARTICLE_SIZE / 3 + Math.random() * PARTICLE_SIZE / 3;
            this.floating = false;
            setHealth((int) WorldServer.TICK_RATE * PARTICLE_DESAPWN_SECONDS);
            setMaxHealth((int) WorldServer.TICK_RATE * PARTICLE_DESAPWN_SECONDS);
        } else {
            this.width = DROP_SIZE;
            this.height = DROP_SIZE;
            this.floating = true;
            setHealth((int) WorldServer.TICK_RATE * DROPS_DESAPWN_SECONDS);
            setMaxHealth((int) WorldServer.TICK_RATE * DROPS_DESAPWN_SECONDS);
        }

        this.item = itemType;
    }

    // For drops only
    public EntityItem(World world, byte item) {
        super(world, 10, false);
        this.width = DROP_SIZE;
        this.height = DROP_SIZE;
        this.floating = true;
        setHealth((int) WorldServer.TICK_RATE * DROPS_DESAPWN_SECONDS);
        this.item = item;
    }

    public void tick() {
        super.tick();
        setHealth(Integer.max(0, getHealth() - 1));
    }

    @Override
    public void draw(Graphics graphics, Dimension dimension, int renderDistance, double drawX, double drawY) {
        //super.draw(graphics);

        // Does not render anything outside of render distance
        if (Math.abs(getX() - drawX) > renderDistance + 4 || Math.abs(getY() - drawY) > renderDistance + 4) return;

        if (floating == true) {
            //oscillating height
            //System.out.println("!!! EntityItem.draw() is not complete yet!");

            int screenX = (int) DrawTools.game2ScreenX(drawX, dimension, renderDistance, getX());
            double screenY = (int) DrawTools.game2ScreenY(drawY, dimension, renderDistance, getY());
            int displayWidth = (int) DrawTools.game2screenLength(dimension, renderDistance, width);
            int displayHeight = (int) DrawTools.game2screenLength(dimension, renderDistance, height);
            int halfBlockLength = (int) DrawTools.game2screenLength(dimension, renderDistance, 0.5);

            double floatHeight = DrawTools.game2screenLength(dimension, renderDistance, FLOAT_MAX_HEIGHT / 5.0 + oscillateAnimation(FLOAT_MAX_HEIGHT, FLOAT_SPEED, getHealth()));
            graphics.drawImage(ItemData.getItemImage(item), screenX - displayWidth / 2, (int) (screenY + halfBlockLength - displayHeight - floatHeight), displayWidth, displayHeight, null);
        }


        // draw item
    }

    public byte getItem() {
        return item;
    }
}
