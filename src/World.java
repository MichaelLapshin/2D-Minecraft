/**
 * [World.java]
 * @description     Basic world data storage used by client server program
 * @author          Michael Lapshin
 */

import javax.naming.NoPermissionException;
import java.awt.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class World {
    // World related variables
    protected Terrain terrain;

    // Game related variables
    protected HashMap<Integer, Player> players;
    protected HashMap<Integer, HostileMob> hostileMobs;
    protected HashMap<Integer, PassiveMob> passiveMobs;
    protected HashMap<Integer, EntityItem> drops;
    protected HashMap<String, Block> focusBlocks;

    /** Generates the world */
    public World(Terrain terrain) {
        this.terrain = terrain;

        // Initializes all entity LinkedLists
        players = new HashMap<>();
        hostileMobs = new HashMap<>();
        passiveMobs = new HashMap<>();
        drops = new HashMap<>();
        focusBlocks = new HashMap<>();

        new ItemData(); // Loads item data
        new BlockData(); // Loads block data
        new DrawTools(); // Loads mob model data
    }

    /** This functions should be called from the player's object (that is linked to the game panel) */
    public void draw(Graphics graphics, Dimension dimension, int renderDistance, double drawX, double drawY, Player player) {

        // Displays the blocks around the player (within render distance)
        double sideLength = DrawTools.game2screenLength(dimension, renderDistance, 1);

        int minX = Integer.max(0, (int) (drawX - dimension.width / 2.0 / sideLength));
        int maxX = Integer.min(terrain.getWidth(), (int) (drawX + dimension.width / 2.0 / sideLength + 1));

        int minY = Integer.max(0, (int) (drawY - dimension.height / 2.0 / sideLength));
        int maxY = Integer.min(Terrain.WORLD_HEIGHT, (int) (drawY + dimension.height / 2.0 / sideLength + 1));

        for (int x = minX; x < maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                double screenX = DrawTools.game2ScreenX(drawX, dimension, renderDistance, x);
                double screenY = DrawTools.game2ScreenY(drawY, dimension, renderDistance, y);

                graphics.drawImage(BlockData.getBlockImageResized(dimension, renderDistance, terrain.getBlock(x, y)), (int) screenX, (int) (screenY - sideLength / 2), null);
//                graphics.drawImage(BlockData.getBlockImage(terrain.getBlock(x, y)), (int) screenX, (int) (screenY - sideLength / 2), (int) Math.ceil(sideLength), (int) Math.ceil(sideLength), null);

                if (terrain.getBlock(x, y) < 0 && BlockData.getBlockCanWalkThrough(terrain.getBlock(x, y)) == false) {
                    graphics.setColor(new Color(50, 50, 50, 120));
                    //int sideLength = (int) Math.ceil(DrawTools.game2screenLength(dimension, renderDistance, 1)) + 1;
                    graphics.fillRect((int) screenX, (int) (screenY - sideLength / 2), (int) Math.ceil(sideLength), (int) Math.ceil(sideLength));
                }

            }
        }

        // Draws all entities
        try {
            for (Map.Entry<Integer, Player> entry : players.entrySet())
                if ((entry.getValue().isOnline() || entry.getValue().getIsClientPlayer()) && entry.getValue() != player)
                    entry.getValue().draw(graphics, dimension, renderDistance, drawX, drawY);
            for (Map.Entry<Integer, HostileMob> entry : hostileMobs.entrySet())
                entry.getValue().draw(graphics, dimension, renderDistance, drawX, drawY);
            for (Map.Entry<Integer, PassiveMob> entry : passiveMobs.entrySet())
                entry.getValue().draw(graphics, dimension, renderDistance, drawX, drawY);
            for (Map.Entry<Integer, EntityItem> entry : drops.entrySet())
                entry.getValue().draw(graphics, dimension, renderDistance, drawX, drawY);
        } catch (Exception E) {
        }

        drawX = player.getX();
        drawY = player.getY();

        player.draw(graphics, dimension, renderDistance, drawX, drawY);

        // Draws all focus blocks
        for (Map.Entry<String, Block> entry : focusBlocks.entrySet())
            entry.getValue().draw(graphics, dimension, renderDistance, drawX, drawY, entry.getValue().getAnimationFrame());

    }

    public void addDrop(EntityItem item) {
        drops.put(item.getId(), item);
    }

    public void addPlayer(Player player) {
        players.put(player.getId(), player);
    }

    public void addHostileMob(HostileMob mob) {
        hostileMobs.put(mob.getId(), mob);
    }

    public void addPassiveMob(PassiveMob mob) {
        passiveMobs.put(mob.getId(), mob);
    }

    /** /////=== World getters ===\\\\\ */
    public Terrain getTerrain() {
        return terrain;
    }

    public HashMap<Integer, Player> getPlayers() {
        return players;
    }

    public HashMap<Integer, HostileMob> getHostileMobs() {
        return hostileMobs;
    }

    public HashMap<Integer, PassiveMob> getPassiveMobs() {
        return passiveMobs;
    }

    public HashMap<Integer, EntityItem> getDrops() {
        return drops;
    }

    /** /////=== Entity METHODS ===\\\\\ */
    public void entityDeadRemove() {
        // Find entity to remove
        LinkedList<Entity> toRemove = new LinkedList<>();
        for (Map.Entry<Integer, Player> entry : players.entrySet())
            if (entry.getValue().isAlive() == false) toRemove.add(entry.getValue());
        for (Map.Entry<Integer, HostileMob> entry : hostileMobs.entrySet())
            if (entry.getValue().isAlive() == false) toRemove.add(entry.getValue());
        for (Map.Entry<Integer, PassiveMob> entry : passiveMobs.entrySet())
            if (entry.getValue().isAlive() == false) toRemove.add(entry.getValue());
        for (Map.Entry<Integer, EntityItem> entry : drops.entrySet())
            if (entry.getValue().isAlive() == false) toRemove.add(entry.getValue());

        // Removes entity
        for (Entity entity : toRemove) {
            if (entity instanceof Player) players.remove(entity.getId());
            if (entity instanceof HostileMob) hostileMobs.remove(entity.getId());
            if (entity instanceof PassiveMob) passiveMobs.remove(entity.getId());
            if (entity instanceof EntityItem) drops.remove(entity.getId());
        }
    }

    public void entitySetHealth(int id, int newHealth) {
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).getId() == id) {
                players.get(i).setHealth(newHealth);
                return;
            }
        }
        for (int i = 0; i < hostileMobs.size(); i++) {
            if (hostileMobs.get(i).getId() == id) {
                hostileMobs.get(i).setHealth(newHealth);
                return;
            }
        }
        for (int i = 0; i < passiveMobs.size(); i++) {
            if (passiveMobs.get(i).getId() == id) {
                passiveMobs.get(i).setHealth(newHealth);
                return;
            }
        }
        for (int i = 0; i < drops.size(); i++) {
            if (drops.get(i).getId() == id) {
                drops.get(i).setHealth(newHealth);
                return;
            }
        }
    }

    public int generateEntityID() {
        try {
            System.out.println("Client cannot generate ID :/");
            throw new NoPermissionException();
        } catch (Exception E) {
            E.printStackTrace();
        }
        return -1;
    }

    public HashMap<String, Block> getFocusBlocks() {
        return focusBlocks;
    }


}
