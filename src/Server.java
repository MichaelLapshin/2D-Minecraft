/**
 * [Server.java]
 * @description     Server in charge of accepting connections from clients
 * @author          Michael Lapshin
 */

import java.io.File;
import java.io.PrintWriter;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class Server {

    ServerSocket serverSock; // server socket for connection
    static Boolean running = true;  // controls if the server is accepting clients

    // Server Data
    private ArrayList<Player> players;
    private WorldServer world;

    /** Server data setup */
    public void go() {
        // Server Data Setup
        players = new ArrayList<>();

        // Loads all data
        questionnaire(); //  Questions the user about the creation of the world

        /////=== Client Stuff ===\\\\\

        System.out.println("Waiting for a client connection..");

        Socket clientSocket = null;//hold the client connection

        try {
            serverSock = new ServerSocket(5000);  //assigns an port to the server
            serverSock.setSoTimeout(0 * 1000);  // no timeout

            while (running) {  //this loops to accept multiple clients
                clientSocket = serverSock.accept();  //wait for connection
                //Note: you might want to keep references to all clients if you plan to broadcast messages
                //Also: Queues are good tools to buffer incoming/outgoing messages

                new ConnectionHandler(this, clientSocket, world);
            }
        } catch (Exception e) {
            // System.out.println("Error accepting connection");
            //close all and quit
            try {
                if (clientSocket != null && clientSocket.isClosed() == false) {
                    System.out.println("Closed client socket.");
                    clientSocket.close();
                }
                if (serverSock != null) {
                    serverSock.close();
                    System.out.println("Server socket closed.");
                } else {
                    System.out.println("Server socket is null.");
                }
            } catch (Exception E) {
                E.printStackTrace();
                System.out.println("Failed to close socket");
            }
            System.exit(-1);
        }
    }

     /** Questionnaire at the beginning of the program, includes loading of the world. */
    public void questionnaire() {
        /////=== Loading the world (questionnaire) ===\\\\\
        Scanner in = new Scanner(System.in);
        System.out.println("[START] Do you want to generate a new world? (yes/no)");
        boolean newWorld = true;

        if (binaryResponse(in) == false) {
            try {
                newWorld = true;
                System.out.println("[START] Attempting to load world from file...");
                Scanner fileScanner = new Scanner(new File("World-Save.txt"));

                int worldSeed = Integer.parseInt(fileScanner.nextLine());
                int worldWidth = Integer.parseInt(fileScanner.nextLine());
                fileScanner.close();


                world = new WorldServer(new Terrain(worldWidth, worldSeed)); // Created new world

                // Updates world with block changes
                // Format inside file: block x, block y, block type#
                fileScanner = new Scanner(new File("World-Changes.txt"));
                while (fileScanner.hasNext()) {
                    String[] line = fileScanner.nextLine().split(" ");
                    world.getTerrain().replaceBlock(Integer.parseInt(line[0]), Integer.parseInt(line[1]), Byte.parseByte(line[2]));
                }
                fileScanner.close();


                fileScanner = new Scanner(new File("World-Entities.txt"));
                // Format inside file: EntityType#, x, y, vx, vy...
                while (fileScanner.hasNext()) {
                    String[] lineRaw = fileScanner.nextLine().split(" ");
                    double[] line = new double[lineRaw.length];

                    for (int i = 0; i < 7; i++) {
                        line[i] = Double.parseDouble(lineRaw[i]);
                    }

                    Player player = null;
                    if ((int) line[0] == 0) {
                        player = new Player(world, (int) line[1], line[2], line[3], line[4], (int) (line[5]), (int) line[6], lineRaw[7]);
                        world.addPlayer(player);
                        addPlayer(player);
                    } else if ((int) line[0] == 1) {
                        world.addHostileMob(new Zombie(world, (int) line[1], line[2], line[3], line[4], (int) (line[5]), (int) line[6]));
                    } else if ((int) line[0] == 2) {
                        world.addPassiveMob(new Pig(world, (int) line[1], line[2], line[3], line[4], (int) (line[5]), (int) line[6]));
                    } else if ((int) line[0] == 3) {
                        world.addDrop(new EntityItem(world, (int) line[1], line[2], line[3], line[4], (int) (line[5]), (byte) line[6], ((int) line[7] == 1)));
                    }

                    /*
                    * Format:
                    * playerName
                    * playerID
                    * player health
                    * player coordinates (x y vx vy)
                    * player inventory
                    */

                    if (player != null) {
                        try {
                            Scanner playerData = new Scanner(new File("Player-" + player.getName() + "#" + player.getId() + ".txt"));

                            // Skips first few lines
                            playerData.nextLine();
                            playerData.nextLine();
                            playerData.nextLine();
                            playerData.nextLine();

                            // Reads and records player inventory
                            while (playerData.hasNext()) {
                                String[] inventoryString = playerData.nextLine().split(" ");
                                player.getInventory().addItem(Byte.parseByte(inventoryString[0]), Integer.parseInt(inventoryString[1]));
                            }

                            playerData.close();
                            System.out.println("Save file of player: " + player.getName() + "#" + player.getId() + " successfully loaded.");
                        } catch (Exception E) {
                            System.out.println("Save file of player: " + player.getName() + "#" + player.getId() + " failed to load.");
                        }
                    }
                }

                fileScanner.close();
                newWorld = false;
            } catch (Exception E) {
                System.out.println("[START] Failed to load world from file...");
                newWorld = true;
            }
        }

        // Creates new world
        if (newWorld == true) {
            System.out.println("Creating new world...");
            System.out.println("[START] What is the seed of the new world? (int)");
            int worldSeed = in.nextInt();
            System.out.println("[START] What is the width of the world? (int)");
            int worldWidth = in.nextInt();
            System.out.println("[START] Creating new world...");
            world = new WorldServer(new Terrain(worldWidth, worldSeed));
            try {
                PrintWriter writer = new PrintWriter(new File("World-Save.txt"));
                writer.println(worldSeed);
                writer.println(worldWidth);
                writer.close();

                // Wipes the other files
                writer = new PrintWriter(new File("World-Changes.txt"));
                writer.print("");
                writer.close();

                writer = new PrintWriter(new File("World-Entities.txt"));
                writer.print("");
                writer.close();
            } catch (Exception E) {
                E.printStackTrace();
                System.out.println("Could not save world data.");
            }
        }

        System.out.println("Finished loading the world...");
    }

    /** Waits for a binary response from Server Owner */
    public boolean binaryResponse(Scanner in) {
        String string;
        do {
            string = in.nextLine();
            if (string.toLowerCase().equals("yes") == false && string.toLowerCase().equals("no") == false)
                System.out.println("Wrong input... respond with either 'yes' or 'no'.");
        } while (string.toLowerCase().equals("yes") == false && string.toLowerCase().equals("no") == false);
        return string.toLowerCase().equals("yes");
    }

    /** ////=== User bank methods ===\\\\\ */
    public boolean isOnline(String name) {
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).isOnline() == true && players.get(i).getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    /** Checks if user exists, if not, then returns null */
    public Player exists(String name) {
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).getName().equals(name)) {
                return players.get(i);
            }
        }
        return null;
    }

    /** Adds player to the server */
    public void addPlayer(Player player) {
        players.add(player);
    }

    /** Returns time in 24-hour string format */
    public static String getTime() {
        Date date = new Date(new Date().getTime());
        return new SimpleDateFormat("HH:mm:ss").format(date);
    }

    /** Closes the server socket */
    public void closeServerSocket() {
        try {
            serverSock.close();
        } catch (Exception E) {
            E.printStackTrace();
            System.out.println("[SERVER INFO] Failed to close server socket.");
        }
    }
}
