/**
 * [ConnectionHandler.java]
 * @description     Client connection and IO handler
 * @author          Michael Lapshin
 */

import java.io.*;
import java.net.Socket;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

public class ConnectionHandler implements Runnable {
    // Connection details
    private PrintWriter output;
    private BufferedReader input;
    private Socket client;
    private boolean running;
    private Player player;
    private Server server;
    private Thread thread;
    private WorldServer world;
    private String personalizedMessaged = "";

    /**
     * Initializes input/output streams with the client
     * @param server - initialized Server object
     * @param s - opened client connection socket
     * @param world - game data
     */
    ConnectionHandler(Server server, Socket s, WorldServer world) {
        this.thread = new Thread(this);
        this.server = server;
        this.world = world;

        this.client = s;  //constructor assigns client to this

        try {  //assign all connections to client
            this.output = new PrintWriter(client.getOutputStream());
            InputStreamReader stream = new InputStreamReader(client.getInputStream());
            this.input = new BufferedReader(stream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        running = true;

        System.out.println("[" + server.getTime() + " INFO] Socket reader and writer established. Starting connection thread...");

        thread.start();
    }

    /** In charge of the communication with the client */
    public void run() {
        userLogin();

        if (userLoginData() == false) {
            System.out.println("[LOGIN] Stopping client from entering the world.");
            stop();
        }

        world.addPlayer(player); // Adds client to the server

        while (running) {
            try {
                if (input.ready()) {

                    // Resets player disconnection timeout
                    player.resetTimeoutTimer();

                    // Reads message
                    String message = input.readLine();

//                    System.out.println("GOT: " + message);

                    if (message.substring(0, 5).equals("/TICK") == false) {
                        sendMessage("/Wrong input... client sent: " + message);
                        continue;
                    }

                    String[] line = message.split(" ");

                    player.setPlaceBlockFront(line[1].equals("1")); // Sets player placing front/back
                    player.setR(Double.parseDouble(line[2])); // Sets player's rotation

                    int index = 0;

                    for (index = 3; index < line.length; index++) {
                        if (line[index].equals("left")) player.moveLeft();
                        else if (line[index].equals("right")) player.moveRight();
                        else if (line[index].equals("jump")) player.jump();
                        else if (line[index].equals("clickL")) {
                            index++;
                            player.mouseLeftPress(Byte.parseByte(line[index]));
                        } else if (line[index].equals("clickR")) {
                            index++;
                            player.mouseRightPress(Byte.parseByte(line[index]));
                        } else if (line[index].equals("[Drop]") == true || line[index].equals("[Craft]") == true) {
                            break;
                        }
                    }

                    if (line.length > index) {
                        if (line[index].equals("[Drop]")) {
                            index++;

                            inventoryRemove(Byte.parseByte(line[index]), Integer.parseInt(line[index + 1])); // removes item from inventory

                            // Drops individual items into the world
                            int numberOfDrops = Integer.parseInt(line[index + 1]);
                            for (int j = 0; j < numberOfDrops; j++) {
                                world.addDrop(new EntityItem(world, world.generateEntityID(), player.getX() + Math.random() * 0.3, player.getY(),
                                        Math.cos(player.getR()) * Player.DROP_POWER,
                                        Math.sin(player.getR()) * Player.DROP_POWER,
                                        Byte.parseByte(line[index]), false));
                            }


                        } else if (line[index].equals("[Craft]")) {
                            index++;
                            craft(Byte.parseByte(line[index]), Integer.parseInt(line[index + 1])); // Crafts the item for the user,
                        }


                    }

                    // End of client inputs
                }
            } catch (Exception E) {
                try {
                    stop();
                    input.close();
                    output.close();
                    client.close();
                } catch (Exception e) {
                    System.out.println("Failed to close socket from the ConnectionHandler");
                }
            }
        }
    }

    /** Computes crating results */
    private void craft(byte itemType, int quantity) {

        TreeMap<Byte, Integer> cost = new TreeMap<>();

        // Computes the cost of the transaction
        for (Map.Entry<Byte, Integer> entry : ItemData.getItemCost(itemType).entrySet()) {
            cost.put(entry.getKey(), entry.getValue() * quantity);
        }

        // Can afford?
        if (player.getInventory().canAfford(cost) == true) {
            inventoryAdd(itemType, quantity);

            for (Map.Entry<Byte, Integer> entry : cost.entrySet()) {
                inventoryRemove(entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * Adds item to inventory and appends changes to client data buffer
     * @param itemType the item id number
     * @param quantity the quantity of the item
     */
    public void inventoryAdd(byte itemType, int quantity) {
        player.getInventory().addItem(itemType, quantity);
        personalizedMessaged += " [Item_add] " + itemType + " " + quantity;
    }

    /**
     * Removes item to inventory and appends changes to client data buffer
     * @param itemType the item id number
     * @param quantity the quantity of the item
     */
    public void inventoryRemove(byte itemType, int quantity) {
        player.getInventory().removeItem(itemType, quantity);
        personalizedMessaged += " [Item_remove] " + itemType + " " + quantity;
    }

    /**
     * Sends world data to the user.
     * @return success/fail of data transfer
     */
    private boolean userLoginData() {
        // Sends seed to client
        sendMessage("/LOGIN [Login_Seed] " + world.getTerrain().getWidth() + " " + world.getTerrain().getSeed());

        System.out.println("Waiting for yes response");

        try {
            while (input.readLine().equals("/LOGIN [World_Seed] Yes") == false) System.out.print("");
        } catch (Exception E) {
            System.out.println("[LOGIN] User failed to load world from seed.");
        }

        System.out.println("Sending world changes...");

        // Sends player data to client
        sendMessage("/LOGIN [Login_Player] " + player.getName() + " " + player.getId() + " " + player.getX() + " " + player.getY() + " " + player.getVx() + " " + player.getVy() + " " + player.getHealth());

        System.out.println("Sending player inventory...");

        // Sends inventory info to client
        String inventoryMessage = "/LOGIN [Login_Inventory]";

        for (Map.Entry<Byte, Integer> entry : player.getInventory().getInventory().entrySet()) {
            inventoryMessage += " " + entry.getKey() + " " + entry.getValue();
        }
        sendMessage(inventoryMessage);


        // World block changes
        String worldChangeMessage = "/LOGIN [Login_World]";
        try {
            Scanner fileScanner = new Scanner(new File("World-Changes.txt"));
            while (fileScanner.hasNext()) {
                String line = fileScanner.nextLine();
                worldChangeMessage += " " + line;
            }
        } catch (Exception E) {
            System.out.println("[LOGIN] User failed to load changes to the world.");
            return false;
        }

        sendMessage(worldChangeMessage);

        // Loads Entities
        System.out.println("Sending player data...");

        String entityMessage = "/LOGIN [Login_Entities]";
        for (Map.Entry<Integer, Player> entry : world.getPlayers().entrySet()) {
            Player mob = entry.getValue();
            if (mob.isOnline() && mob.getId() != player.getId())
                entityMessage += " [Entity] 0 " + mob.getId() + " " + mob.getName();
        }

        System.out.println("Sending hostile mob data...");

        for (Map.Entry<Integer, HostileMob> entry : world.getHostileMobs().entrySet()) {
            HostileMob mob = entry.getValue();
            entityMessage += " [Entity] ";
            if (mob instanceof Zombie) entityMessage += 1;
            entityMessage += " " + mob.getId();
        }

        System.out.println("Sending passing mob data...");

        for (Map.Entry<Integer, PassiveMob> entry : world.getPassiveMobs().entrySet()) {
            PassiveMob mob = entry.getValue();
            entityMessage += " [Entity] ";
            if (mob instanceof Pig) entityMessage += 2;
            entityMessage += " " + mob.getId();
        }

        System.out.println("Sending drop data...");

        for (Map.Entry<Integer, EntityItem> entry : world.getDrops().entrySet()) {
            EntityItem entityItem = entry.getValue();
            entityMessage += " [Entity] 3 ";
            entityMessage += entityItem.getId() + " " + entityItem.getItem();
        }

        sendMessage(entityMessage);

        System.out.println("Waiting for ready note from user.");

        try {
            while (input.readLine().equals("/LOGIN [World] Ready") == false) System.out.print("");
        } catch (Exception E) {
            E.printStackTrace();
            System.out.println("[LOGIN] Did not obtain ready note from user.");
            return false;
        }
        System.out.println("Got a ready note from client. Spawning in client...");
        player.resetTimeoutTimer();
        player.connect(this);
        world.addPlayer(player);
        player.resetTimeoutTimer();
        return true;
    }

    /** Establish initial with user */
    private void userLogin() {
        // Assigns this Socket to a unique User based on the name
        String name = "";

        do {
            try {
                // Reads username and password from client
                String string = input.readLine();
                if (string.substring(0, 6).equals("/LOGIN") && string.substring(7, 17).equals("[Username]")) {
                    name = string.substring(18, string.length()).trim();
                    System.out.println("Login attempt as: " + name);
                }
                System.out.println("[" + server.getTime() + " LOGIN INFO] Login attempt with : {name : " + name + "}");
            } catch (Exception E) {
                System.out.println("Failed to take username and/or password.");
                sendMessage("/LOGIN [Login] Error");
                E.printStackTrace();
                running = false;
                break;
            }

            // Makes sure that all inputs are valid
            if (name == null || name.equals("") || name.substring(0, 1).equals("/") || server.isOnline(name) == true) {
                System.out.println("Sent /LOGIN [Login] No to client.");
                sendMessage("/LOGIN [Login] No");
                continue;
            }

            name = name.replace(' ', '_');

            // Assigns a player or creates a new one
            player = server.exists(name);

            if (player != null && player.isOnline() == true) {
                System.out.println("Sent /LOGIN [Login] No to client.");
                sendMessage("/LOGIN [Login] No");
                continue;
            }

            if (player == null) {
                System.out.println("[" + Server.getTime() + " LOGIN INFO] Detected unique username. Creating new user...");
                player = new Player(world, name);
                player.getInventory().addItem((byte) 15, 160);
                player.getInventory().addItem((byte) 6, 160);
                player.getInventory().addItem((byte) 27, 160);
                player.getInventory().addItem((byte) 31, 160);
                player.getInventory().addItem((byte) 32, 160);
                player.getInventory().addItem((byte) 51, 160);
                player.getInventory().addItem((byte) 76, 160);
                player.getInventory().addItem((byte) 77, 160);
                player.getInventory().addItem((byte) 70, 160);
                player.getInventory().addItem((byte) 71, 160);
                player.getInventory().addItem((byte) 73, 160);
                player.getInventory().addItem((byte) 74, 160);
                player.getInventory().addItem((byte) 72, 160);
                player.getInventory().addItem((byte) 75, 160);
                player.getInventory().addItem((byte) 65, 160);

                server.addPlayer(player);
            }

            sendMessage("/LOGIN [Login] Yes");
        } while (name.equals(""));

    }

    /** Echos world data and specific-player data to the client */
    public void echoMessage(String worldData) {
//        System.out.println(worldData + personalizedMessaged);
        sendMessage(worldData + personalizedMessaged);
//        System.out.println("PersonalizedMessages to " + player.getName() + "#" + player.getId() + " :" + personalizedMessaged);
        personalizedMessaged = "";
    }

    /** Sends message to the client */
    public void sendMessage(String message) {
        output.println(message);
        output.flush();
    }

    /** Disconnects the user */
    public void stop() {
        System.out.println("[" + server.getTime() + " LOGOUT] User " + player.getName() + " #" + player.getId());
        if (player != null) this.player.disconnect();
        running = false;
    }
}
