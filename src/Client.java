import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.*;

public class Client extends JFrame {
    public static void main(String[] args) throws IOException {
        Client game = new Client();
        game.runSystem();
    }

    //MISCELLANEOUS
    //World world;
    private String currentPanel;
    private Queue<Object> sendData;
    private boolean[] movement = new boolean[3];
    private int renderDistance = 100;
    private World world;
    private Player player;
    double angle = 0;
    private long timeElapsed = 0;
    private double timeOut = 10.0 / 1000.0 * Math.pow(10, 9); // TOP NUM (20.0) is milliseconds
    private boolean toggle = true;
    public boolean drawFrame = false;

    // SERVER---------------------
    private Socket mySocket; //socket for connection
    private BufferedReader input; //reader for network stream
    private PrintWriter output;  //printwriter for network output
    private boolean running = true; //thread status via boolean
    private boolean loggedIn = false;

    // SWING----------------------
    private GamePanel game;
    private JButton currentServerVisual, currentServer, progress, leftInvItem, rightInvItem;
    private JPanel welcomePage, settings, credits, serverSelector, servers, escScreen, inventory, rightInv, leftInv, panels, loading;
    private CardLayout cardLayout = new CardLayout();
    private JTextField ipPort, playerName, serverName, itemDrop, itemCraft;

    // CONSTANTS------------------
    private final int SCREEN_WIDTH = Toolkit.getDefaultToolkit().getScreenSize().width;
    private final int SCREEN_HEIGHT = Toolkit.getDefaultToolkit().getScreenSize().height;

    public final String GAME = "game";
    public final String SETTINGS = "settings";
    public final String WELCOME = "welcome";
    public final String CREDITS = "credits";
    public final String SERVER_SELECTOR = "server selector";
    public final String INVENTORY = "inventory";
    public final String ESC_SCREEN = "esc screen";
    public final String LOADING = "loading";

    private final String JUMP = "jump";
    private final String MOVE_LEFT = "left";
    private final String MOVE_RIGHT = "right";
    private final String LEFT_CLICK = "clickL";
    private final String RIGHT_CLICK = "clickR";

    private final String LOGIN = "/LOGIN";
    private final String LOGIN_SEED = "/LOGIN [World_Seed] Yes";
    private final String LOGIN_STATUS_GOOD = "[Login] Yes";
    private final String LOGIN_STATUS_BAD = "[Login] No";
    private final String PLAYER_STATUS = "[Login_Player]";
    private final String INVENTORY_STATUS = "[Login_Inventory]";
    private final String SEED_STATUS = "[Login_Seed]";
    private final String WORLD_STATUS = "[Login_World]";
    private final String ENTITIES_STATUS = "[Login_Entities]";
    private final String LOGIN_READY = "[World] Ready";
    private final String USERNAME = "[Username]";

    private final String ENTITY = "[Entity]";
    private final String DEAD_ENTITIY = "[Entity_dead]";
    private final String BLOCK = "[Block]";
    private final String BLOCK_CHANGE = "[Block_change]";
    private final String MOB_DAMAGE = "[Mob_damage]";
    private final String NEW_ENTITY = "[Entity_new]";
    private final String ADD_ITEM = "[Item_add]";
    private final String REMOVE_ITEM = "[Item_remove]";

    Client() throws IOException {
        // FRAME VARS-------------
        setResizable(true);
        setVisible(true);
        setMinimumSize(new Dimension(SCREEN_WIDTH / 2, SCREEN_HEIGHT / 2));
        setLocationRelativeTo(null);
        setSize(600, 400);
        setLocationRelativeTo(null);
        setFocusable(true);


        // MISC-------------------
        currentPanel = WELCOME;
        sendData = new LinkedList<>();

        movement[0] = false;
        movement[1] = false;
        movement[2] = false;

        // create seed here
        game = new GamePanel(34, 123456789, this);

        // PANELS-----------------
        panels = new JPanel();
        panels.setLayout(cardLayout);

        // welcome page----
        welcomePage = new JPanel();
        welcomePage.setLayout(new BorderLayout());

        JPanel lPanel = new JPanel();
        lPanel.setSize(200, 1);
        lPanel.setBackground(new Color(95, 75, 60));
        JPanel rPanel = new JPanel();
        rPanel.setSize(200, 1);
        rPanel.setBackground(new Color(95, 75, 60));

        JButton logo = new JButton("");
        logo.setIcon(new ImageIcon("Images/logo.png"));
        logo.setSize(1, 200);
        logo.setBackground(new Color(95, 75, 60));
        logo.setFont(new Font("TimesRoman", Font.BOLD, 45));
        logo.setBorderPainted(false);

        JButton play = new JButton("Play");
        play.setSize(1, 100);
        play.setBackground(new Color(132, 109, 91));
        play.setFont(getCustomFont(40));
        //play.setFont(new Font("TimesRoman", Font.BOLD, 25));
        play.setBorderPainted(false);

        play.addActionListener(new WelcomeButtonListener());

        JPanel southWelcome = new JPanel();
        southWelcome.setLayout(new GridLayout(0, 2));

        JButton setting = new JButton("Settings");
        setting.setSize(1, 100);
        setting.setBackground(new Color(132, 109, 91));
        setting.setFont(getCustomFont(30));
        setting.setBorderPainted(false);
        setting.addActionListener(new WelcomeButtonListener());

        JButton credit = new JButton("Credits");
        credit.setSize(1, 100);
        credit.setBackground(new Color(132, 109, 91));
        credit.setFont(getCustomFont(30));
        credit.setBorderPainted(false);
        credit.addActionListener(new WelcomeButtonListener());

        southWelcome.add(setting);
        southWelcome.add(credit);

        // server selector----
        serverSelector = new JPanel();
        serverSelector.setLayout(new BorderLayout());

        servers = new JPanel();
        servers.setLayout(new GridLayout(0, 1));
        JScrollPane serverSelectorScroll = new JScrollPane(servers, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        setupServerSelector();

        JPanel northServerSelect = new JPanel();
        northServerSelect.setLayout(new GridLayout(0, 3));

        currentServerVisual = new JButton("No Server Selected :(");
        currentServerVisual.setEnabled(false);
        currentServerVisual.setBackground(new Color(132, 86, 79));
        currentServerVisual.setFont(getCustomFont(10));
        currentServerVisual.setBorderPainted(false);

        JButton joinServer = new JButton("Join Server");
        joinServer.setBackground(new Color(73, 132, 72));
        joinServer.setFont(getCustomFont(10));
        joinServer.setBorderPainted(false);
        joinServer.addActionListener(new JoinServerButtonListener());

        JButton deleteServer = new JButton("Delete Server");
        deleteServer.setBackground(new Color(132, 86, 79));
        deleteServer.setFont(getCustomFont(10));
        deleteServer.setBorderPainted(false);
        deleteServer.addActionListener(new DeleteServerButtonListener());

        northServerSelect.add(currentServerVisual);
        northServerSelect.add(joinServer);
        northServerSelect.add(deleteServer);

        JPanel southServerSelect = new JPanel();
        southServerSelect.setLayout(new GridLayout(0, 4));

        ipPort = new JTextField("IP:Port");
        ipPort.setFont(getCustomFont(10));
        playerName = new JTextField("Enter your in-game name");
        playerName.setFont(getCustomFont(10));
        serverName = new JTextField("Enter server name");
        serverName.setFont(getCustomFont(10));

        JButton addServer = new JButton("Add Server");
        addServer.setBackground(new Color(132, 86, 79));
        addServer.setFont(getCustomFont(10));
        addServer.setBorderPainted(false);
        addServer.addActionListener(new AddServerButtonListener());

        southServerSelect.add(ipPort);
        southServerSelect.add(serverName);
        southServerSelect.add(playerName);
        southServerSelect.add(addServer);

        // esc screen---------
        escScreen = new JPanel();
        escScreen.setLayout(new BorderLayout());

        JPanel centreEscScreen = new JPanel();
        centreEscScreen.setLayout(new GridLayout(0, 1));

        JButton logoEsc = new JButton("ENTER LOGO HERE");
        logoEsc.setSize(1, 200);
        logoEsc.setBackground(new Color(95, 75, 60));
        logoEsc.setFont(getCustomFont(45));
        logoEsc.setEnabled(false);
        logoEsc.setBorderPainted(false);

        JButton esc = new JButton();
        esc.setText("Back to game (esc)");
        esc.setBackground(new Color(132, 109, 91));
        esc.setFont(getCustomFont(25));
        esc.setBorderPainted(false);
        esc.addActionListener(new EscButtonListener());

        JButton settings = new JButton();
        settings.setText("Settings (o)");
        settings.setBackground(new Color(132, 109, 91));
        settings.setFont(getCustomFont(25));
        settings.setBorderPainted(false);
        settings.addActionListener(new EscButtonListener());

        JButton inventoryButton = new JButton();
        inventoryButton.setText("Inventory (e)");
        inventoryButton.setBackground(new Color(132, 109, 91));
        inventoryButton.setFont(getCustomFont(25));
        inventoryButton.setBorderPainted(false);
        inventoryButton.addActionListener(new EscButtonListener());

        JButton exit = new JButton();
        exit.setText("Exit (p)");
        exit.setBackground(new Color(132, 109, 91));
        exit.setFont(getCustomFont(25));
        exit.setBorderPainted(false);
        exit.addActionListener(new EscButtonListener());

        centreEscScreen.add(esc);
        centreEscScreen.add(settings);
        centreEscScreen.add(inventoryButton);
        centreEscScreen.add(exit);

        // inventory---------
        inventory = new JPanel();
        inventory.setLayout(new BorderLayout());

        JButton inventoryLogo = new JButton("Inventory");
        inventoryLogo.setSize(1, 200);
        inventoryLogo.setBackground(new Color(95, 75, 60));
        inventoryLogo.setFont(getCustomFont(45));
        inventoryLogo.setEnabled(false);
        inventoryLogo.setBorderPainted(false);

        JPanel centreInventory = new JPanel();
        centreInventory.setLayout(new GridLayout(0, 2));

        leftInv = new JPanel();
        leftInv.setLayout(new BorderLayout());


        rightInv = new JPanel();
        rightInv.setLayout(new BorderLayout());

        JPanel bottom = new JPanel();
        bottom.setLayout(new GridLayout(0, 2));

        JPanel bottomLeft = new JPanel();
        bottomLeft.setLayout(new GridLayout(1, 0));
        JButton drop = new JButton("Drop");
        drop.setBackground(new Color(132, 109, 91));
        drop.setFont(getCustomFont(15));
        drop.setBorderPainted(false);
        drop.addActionListener(new LeftInvButtonListener());

        leftInvItem = new JButton("No Item");
        leftInvItem.setBackground(new Color(132, 86, 79));
        leftInvItem.setFont(getCustomFont(15));
        leftInvItem.setBorderPainted(false);
        leftInvItem.setEnabled(false);

        itemDrop = new JTextField();
        itemDrop.setFont(getCustomFont(15));

        JPanel bottomRight = new JPanel();
        bottomRight.setLayout(new GridLayout(1, 0));
        JButton craft = new JButton("Craft");
        craft.setBackground(new Color(132, 109, 91));
        craft.setFont(getCustomFont(15));
        craft.setBorderPainted(false);
        craft.addActionListener(new RightInvButtonListener());

        itemCraft = new JTextField();
        itemCraft.setFont(getCustomFont(15));

        JButton maxCraft = new JButton("Max Craft");
        maxCraft.setBackground(new Color(132, 109, 91));
        maxCraft.setFont(getCustomFont(15));
        maxCraft.setBorderPainted(false);
        maxCraft.addActionListener(new RightInvButtonListener());

        rightInvItem = new JButton("No Item");
        rightInvItem.setBackground(new Color(132, 86, 79));
        rightInvItem.setFont(getCustomFont(15));
        rightInvItem.setBorderPainted(false);
        rightInvItem.setEnabled(false);

        bottomLeft.add(drop);
        bottomLeft.add(itemDrop);
        bottomLeft.add(leftInvItem);
        bottomRight.add(craft);
        bottomRight.add(itemCraft);
        bottomRight.add(maxCraft);
        bottomRight.add(rightInvItem);
        bottom.add(bottomLeft);
        bottom.add(bottomRight);
        centreInventory.add(leftInv);
        centreInventory.add(rightInv);

        // loading screen-------
        loading = new JPanel();
        loading.setLayout(new BorderLayout());

        progress = new JButton();
        progress.setSize(1, 200);
        progress.setBackground(new Color(95, 75, 60));
        progress.setFont(getCustomFont(50));
        progress.setEnabled(false);
        progress.setBorderPainted(false);

        // ADDING-----------------
        welcomePage.add(BorderLayout.EAST, rPanel);
        welcomePage.add(BorderLayout.WEST, lPanel);
        welcomePage.add(BorderLayout.NORTH, logo);
        welcomePage.add(BorderLayout.CENTER, play);
        welcomePage.add(BorderLayout.SOUTH, southWelcome);

        serverSelector.add(BorderLayout.NORTH, northServerSelect);
        serverSelector.add(BorderLayout.CENTER, serverSelectorScroll);
        serverSelector.add(BorderLayout.SOUTH, southServerSelect);

        escScreen.add(BorderLayout.NORTH, logoEsc);
        escScreen.add(BorderLayout.CENTER, centreEscScreen);

        inventory.add(BorderLayout.NORTH, inventoryLogo);
        inventory.add(BorderLayout.CENTER, centreInventory);
        inventory.add(BorderLayout.SOUTH, bottom);

        loading.add(BorderLayout.CENTER, progress);

        panels.add(game, GAME);
        panels.add(welcomePage, WELCOME);
        panels.add(serverSelector, SERVER_SELECTOR);
        panels.add(escScreen, ESC_SCREEN);
        panels.add(inventory, INVENTORY);
        panels.add(loading, LOADING);

        add(panels);
        addKeyListener(new GameKeyListener());
        setAutoRequestFocus(true);
        //inventory.addKeyListener(new GameKeyListener());
        //game.addKeyListener(new GameKeyListener());

        game.addMouseListener(new MouseClickListener());
        game.addMouseMotionListener(new MouseTracker());
        game.addMouseWheelListener(new HotBarScrollListener());

        //currentPanel = GAME;
        cardLayout.show(panels, currentPanel);

        runSystem();
    }

    public void runSystem() throws IOException {
        while (true) {
            if (loggedIn) {
                dataReceiveProtocol();
            }
            if (currentPanel.equals(GAME)) {
                game.revalidate();
                game.repaint();
                game.updateUI();
            } else if (currentPanel.equals(WELCOME)) {
                welcomePage.revalidate();
                welcomePage.repaint();
                welcomePage.updateUI();
            } else if (currentPanel.equals(SERVER_SELECTOR)) {
                serverSelector.revalidate();
                serverSelector.repaint();
                serverSelector.updateUI();
            } else if (currentPanel.equals(INVENTORY)) {
                inventory.revalidate();
                inventory.repaint();
                inventory.updateUI();
            } else if (currentPanel.equals(CREDITS)) {

            } else if (currentPanel.equals(SETTINGS)) {

            } else if (currentPanel.equals(LOADING)) {

                loading.revalidate();
                loading.repaint();
                loading.updateUI();
            }
            if (player != null) {
                if (player.getHealth() == 0) {
                    input.close();
                    output.close();
                    mySocket.close();
                    changeScreen(WELCOME);
                }
            }
            setFocusable(true);
        }
    }

    // SERVER METHODS---------------------------
    private Socket connect(String ip, int port) {
        System.out.println("Attempting to make a connection..");

        try {
            mySocket = new Socket(ip, port); //attempt socket connection (local address). This will wait until a connection is made

            InputStreamReader stream1 = new InputStreamReader(mySocket.getInputStream()); //Stream for network input
            input = new BufferedReader(stream1);
            output = new PrintWriter(mySocket.getOutputStream(), true); //assign printwriter to network stream
            System.out.println("Connection made.");
        } catch (IOException e) {  //connection error occured
            PopUpMsg.infoBox("Connection to Server Failed", ":(");
            System.out.println("Connection to Server Failed");
            e.printStackTrace();
        }


        return mySocket;
    }

    private void sendServerData(String msg) {
        output.println(msg);
        output.flush();
    }

    private void dataReceiveProtocol() {
        try {
            String msg = input.readLine(); //read the message
            if (msg != null) {
//                System.out.println("SERVER: " + msg);

                interperetData(msg);
                sendServerData(formulateSendData());
//                System.out.println("CLIENT: " + formulateSendData());
                resetSendData();
                setupSendData();
            }

        } catch (IOException e) {
            System.out.println("Failed to receive msg from the server");
            //e.printStackTrace();
        }
        /*
        try {  //after leaving the main loop we need to close all the sockets
            input.close();
            output.close();

        }catch (Exception e) {
            System.out.println("Failed to close socket");
        }
        */
    }

    private boolean login() {
        // CHAT LENGTH
        output.println(LOGIN + " " + USERNAME + " " + playerName.getText());
        output.flush();
//        System.out.println("1");
        // Wait for response
        boolean waiting = true;
        while (waiting) {
            try {
                String msg = input.readLine();
//                System.out.println(msg);
                if (msg.contains(LOGIN_STATUS_BAD)) {
                    return false;
                } else if (msg.contains(PLAYER_STATUS)) {
                    progress.setText("Getting Player Info");
                    loading.revalidate();
                    loading.repaint();
                    loading.updateUI();
                    createPlayer(msg);
                } else if (msg.contains(INVENTORY_STATUS)) {
                    progress.setText("Extracting Inventory");
                    loading.revalidate();
                    loading.repaint();
                    loading.updateUI();
                    createInventory(msg);
                } else if (msg.contains(SEED_STATUS)) {
                    progress.setText("Fetching Seed");
                    loading.revalidate();
                    loading.repaint();
                    loading.updateUI();
                    createWorld(msg);
                    output.println(LOGIN_SEED);
                    output.flush();
                } else if (msg.contains(WORLD_STATUS)) {
                    progress.setText("Editing World");
                    changeScreen(LOADING);
                    loading.revalidate();
                    loading.repaint();
                    loading.updateUI();
                    editWorld(msg);
                } else if (msg.contains(ENTITIES_STATUS)) {
                    progress.setText("Spawning Mobs");
                    loading.revalidate();
                    loading.repaint();
                    loading.updateUI();
                    createEntities(msg);
                    waiting = false;
                }


            } catch (IOException e) {
                PopUpMsg.infoBox("Could Not Sign In", ":(");
                e.printStackTrace();
            }
        }

        output.println(LOGIN + " " + LOGIN_READY);
        output.flush();
        loggedIn = true;
        return true;
    }

    // COM
    private void interperetData(String msg) {
        String[] data = msg.split(" ");
//        System.out.println(world.getPlayers().size());
        for (int i = 1; i < data.length; i++) {
//            System.out.print(data[i]);
            if (data[i].equals(ENTITY) && i + 8 < data.length) { // EVERY TICK
                String mobType = data[i + 1];
                Entity entity = null;
                if (mobType.equals("0")) {
//                    System.out.println("PLAYER "+Integer.valueOf(data[i + 2]));
                    entity = world.getPlayers().get(Integer.valueOf(data[i + 2]));
                } else if (mobType.equals("1")) {
//                    System.out.println("ZOMBIE");
                    entity = world.getHostileMobs().get(Integer.valueOf(data[i + 2]));
                } else if (mobType.equals("2")) {
//                    System.out.println("PIG");
                    entity = world.getPassiveMobs().get(Integer.valueOf(data[i + 2]));
                } else if (mobType.equals("3")) {
//                    System.out.println("DROP");
                    entity = world.getDrops().get(Integer.valueOf(data[i + 2]));
                }
                entity.setX(Double.parseDouble(data[i + 3]));
                entity.setY(Double.parseDouble(data[i + 4]));
                entity.setVx(Double.parseDouble(data[i + 5]));
                entity.setVy(Double.parseDouble(data[i + 6]));
                entity.setR(Double.parseDouble(data[i + 7]));
                entity.setHealth(Integer.valueOf(data[i + 8]));

                if (data[i + 8].equals("0") && mobType.equals("0")) {
                    world.getPlayers().remove(entity.getId());
                } else if (data[i + 8].equals("0") && mobType.equals("1")) {
                    world.getHostileMobs().remove(entity.getId());
                } else if (data[i + 8].equals("0") && mobType.equals("2")) {
                    world.getPassiveMobs().remove(entity.getId());
                } else if (data[i + 8].equals("0") && mobType.equals("3")) {
                    world.getDrops().remove(entity.getId());
                }

                i += 8;
            } else if (data[i].equals(NEW_ENTITY)) {
                String mobType = data[i + 1];
                if (mobType.equals("0")) { //DEBUG
                    if (!world.getPlayers().containsKey(Integer.valueOf(data[i + 2]))) {
                        if (Integer.valueOf(data[i + 1]) != player.getId()) {
                            Player player = new Player(world, Integer.valueOf(data[i + 2]), 0, 0, 0, 0, 0, data[i + 2]);
                            player.setIsClientPlayer(true);
                            world.addPlayer(player);
                        }
                    }
                    i += 2;
                } else if (mobType.equals("1")) {
//                    System.out.println("ZOMBIE");
                    world.addHostileMob(new Zombie(world, Integer.valueOf(data[i + 2]), 0, 0, 0, 0, 0));
                    i += 2;
                } else if (mobType.equals("2")) {
                    world.addPassiveMob(new Pig(world, Integer.valueOf(data[i + 2]), 0, 0, 0, 0, 0));
                    i += 2;
                } else if (mobType.equals("3")) {
                    world.addDrop(new EntityItem(world, Integer.valueOf(data[i + 2]), 0, 0, 0, 0, Byte.parseByte(data[i + 3]), false));

                    i += 3;
                }
            } else if (data[i].equals(DEAD_ENTITIY)) { //NOT USED RN
            /*
            if (data[i+1].equals(player.getId())){

            }
            */
                //world.entityRemove(data[i+1]);
                i += 2;
            } else if (data[i].equals(ADD_ITEM)) {
                player.getInventory().addItem(Byte.parseByte(data[i + 1]), Integer.valueOf(data[i + 2]));
                i += 2;
            } else if (data[i].equals(REMOVE_ITEM)) {
                player.getInventory().removeItem(Byte.parseByte(data[i + 1]), Integer.valueOf(data[i + 2]));
                i += 2;
            } else if (data[i].equals(BLOCK)) {
                if (Integer.valueOf(data[i + 1]) < player.getX() + renderDistance || Integer.valueOf(data[i + 1]) > player.getX() - renderDistance) {
                    if (!world.getFocusBlocks().containsKey(data[i + 1] + " " + data[i + 2])) {
                        world.getFocusBlocks().put(data[i + 1] + " " + data[i + 2], new Block(Integer.parseInt(data[i + 1]), Integer.parseInt(data[i + 2]), Byte.parseByte(data[i + 3])));
                    }
                    world.getFocusBlocks().get(data[i + 1] + " " + data[i + 2]).setAnimationFrame(Integer.valueOf(data[i + 3]));
                }
                i += 3;
            } else if (data[i].equals(BLOCK_CHANGE)) {
                world.getTerrain().replaceBlock(Integer.valueOf(data[i + 1]), Integer.valueOf(data[i + 2]), Integer.valueOf(data[i + 3]));
                if (world.getFocusBlocks().containsKey(data[i + 1] + " " + data[i + 2])) {
                    world.getFocusBlocks().remove(data[i + 1] + " " + data[i + 2]);
                }
                i += 3;
            } else if (data[i].equals(MOB_DAMAGE)) { //NOT USED RN
                //world.entitySetHealth(Integer.valueOf(data[i+1]), Integer.valueOf(data[i+2]));
                //wolrd.entityDeadRemove();
                i += 3;
            }
        }

    }

    // Generate Game
    private void createPlayer(String msg) {
        String[] data = msg.split(" ");
        player = new Player(world, Integer.parseInt(data[3]), Double.parseDouble(data[4]), Double.parseDouble(data[5]), Double.parseDouble(data[6]), Double.parseDouble(data[7]), Integer.parseInt(data[8]), data[2]);
        game.setPlayer(player);
        player.setIsClientPlayer(true);
        world.addPlayer(player);
    }

    private void createInventory(String msg) {
        String[] data = msg.split(" ");
        for (int i = 2; i < data.length; i += 2) {
            player.getInventory().addItem(Byte.parseByte(data[i]), Integer.parseInt(data[i + 1]));
        }
    }

    private void createWorld(String msg) {
        String[] data = msg.split(" ");
        world = new World(new Terrain(Integer.parseInt(data[2]), Integer.parseInt(data[3])));
    }

    private void editWorld(String msg) {
        String[] data = msg.split(" ");
        for (int i = 2; i < data.length; i += 3) {
            world.getTerrain().replaceBlock(Integer.valueOf(data[i]), Integer.valueOf(data[i + 1]), Integer.valueOf(data[i + 2]));
        }
    }

    private void createEntities(String msg) {
        String[] data = msg.split(" ");

        for (int i = 3; i < data.length; i++) {
            String mobType = data[i];
            if (mobType.equals("0")) {
//                System.out.println("player this = "+player.getId() + "   " + Integer.valueOf(data[i + 1]));
                Player player = null;
                if (Integer.valueOf(data[i + 1]) != this.player.getId()) {
                    player = new Player(world, Integer.valueOf(data[i + 1]), data[i + 2]);
                    player.setIsClientPlayer(true);
                    world.addPlayer(player);

                }else{
                    player = new Player(world, Integer.valueOf(data[i + 1]), data[i + 2]);
                    world.addPlayer(player);
                    player.setIsClientPlayer(true);
                    this.player = player;
                    game.setPlayer(player);
                }
                i += 2;
            } else if (mobType.equals("1")) {
                world.addHostileMob(new Zombie(world, Integer.valueOf(data[i + 1]), 0, 0, 0, 0, 0));
                i += 1;
            } else if (mobType.equals("2")) {
                world.addPassiveMob(new Pig(world, Integer.valueOf(data[i + 1]), 0, 0, 0, 0, 0));
                i += 1;
            } else if (mobType.equals("3")) {
                world.addDrop(new EntityItem(world, Integer.valueOf(data[i + 1]), 0, 0, 0, 0, Byte.parseByte(data[i + 2]), false));
                i += 2;
            }
        }
    }
    ////////

    public double getWidthJ() {
        return (double) this.getWidth();
    }

    public double getHeightJ() {
        return (double) this.getHeight();
    }

    // MISC-------------------------------------
    private void setupServerSelector() { // It would open up file here
        addToServerSelector("DEMO SERVER", "000.000.00.00:0000");
    }

    private void addToServerSelector(String name, String ipPort) {
        JButton ser = new JButton(name);
        ser.setToolTipText(ipPort);
        ser.setSize(1, 25);
        ser.setBackground(new Color(132, 109, 91));
        ser.setFont(getCustomFont(25));
        ser.setBorderPainted(false);
        ser.addActionListener(new ServerSelectorButtonListener());
        servers.add(ser);
    }

    public void changeScreen(String panel) {
        if (panel.equals(GAME)) {
            currentPanel = panel;
            cardLayout.show(panels, currentPanel);
        } else if (panel.equals(ESC_SCREEN)) {
            currentPanel = panel;
            cardLayout.show(panels, currentPanel);
        } else if (panel.equals(INVENTORY)) {
            currentPanel = panel;
            setupRightInv(player.getInventory());
            setupLeftInv(player.getInventory());
            cardLayout.show(panels, currentPanel);
        } else if (panel.equals(LOADING)) {
            currentPanel = panel;
            cardLayout.show(panels, currentPanel);
        }
    }

    public String getCurrentPanel() {
        return currentPanel;
    }

    private void setupLeftInv(Inventory inventory) {
        leftInv.removeAll();
        JPanel left = new JPanel();
        left.setLayout(new GridLayout(0, 1));
        JScrollPane leftScroll = new JScrollPane(left, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        JPanel middle = new JPanel();
        middle.setLayout(new GridLayout(0, 1));
        JScrollPane middleScroll = new JScrollPane(middle, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        middleScroll.setVerticalScrollBar(leftScroll.getVerticalScrollBar());
        JPanel right = new JPanel();
        right.setLayout(new GridLayout(0, 1));
        JScrollPane rightcroll = new JScrollPane(right, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        rightcroll.setVerticalScrollBar(leftScroll.getVerticalScrollBar());

        JButton leftHeader = new JButton("Item");
        leftHeader.setSize(1, 100);
        leftHeader.setBackground(new Color(95, 75, 60));
        leftHeader.setFont(getCustomFont(25));
        leftHeader.setEnabled(false);
        leftHeader.setBorderPainted(false);

        JButton middleHeader = new JButton("Name");
        middleHeader.setSize(1, 100);
        middleHeader.setBackground(new Color(95, 75, 60));
        middleHeader.setFont(getCustomFont(25));
        middleHeader.setEnabled(false);
        middleHeader.setBorderPainted(false);

        JButton rightHeader = new JButton("Count");
        rightHeader.setSize(1, 100);
        rightHeader.setHorizontalAlignment(SwingConstants.LEFT);
        rightHeader.setBackground(new Color(95, 75, 60));
        rightHeader.setFont(getCustomFont(25));
        rightHeader.setEnabled(false);
        rightHeader.setBorderPainted(false);

        left.add(leftHeader);
        middle.add(middleHeader);
        right.add(rightHeader);

        // Add items
        for (Map.Entry<Byte, Integer> entry : inventory.getInventory().entrySet()) {
            if (ItemData.getItemImage(entry.getKey()) != null) {
                if (entry.getValue() != 0) {
                    JButton leftItem = new JButton("");
                    leftItem.setToolTipText(entry.getKey() + "");
                    leftItem.setSize(1, 100);
                    leftItem.setBackground(new Color(0, 0, 0, 0));
                    leftItem.setIcon(new ImageIcon(ItemData.getItemImage(entry.getKey())));
                    leftItem.setFont(getCustomFont(20));
                    leftItem.addActionListener(new LeftInvButtonListener());
                    leftItem.setBorderPainted(false);
                    left.add(leftItem);

                    JButton middleItem = new JButton(ItemData.getItemName(entry.getKey()));
                    middleItem.setSize(1, 100);
                    middleItem.setBackground(new Color(159, 197, 168));
                    middleItem.setFont(getCustomFont(20));
                    middleItem.setEnabled(false);
                    middleItem.setBorderPainted(false);

                    JButton rightItem = new JButton("x" + entry.getValue());
                    rightItem.setSize(1, 100);
                    rightItem.setHorizontalAlignment(SwingConstants.LEFT);
                    rightItem.setBackground(new Color(159, 197, 168));
                    rightItem.setFont(getCustomFont(20));
                    rightItem.setEnabled(false);
                    rightItem.setBorderPainted(false);

                    middle.add(middleItem);
                    right.add(rightItem);
                }

                //leftItem.setIcon(getScaledIcon(new ImageIcon(ItemData.getItemImage(entry.getKey())).getImage()), leftItem.getSize().width, leftItem.getSize().width);
            }
        }

        leftInv.add(BorderLayout.WEST, leftScroll);
        leftInv.add(BorderLayout.CENTER, middleScroll);
        leftInv.add(BorderLayout.EAST, rightcroll);
    }

    private void setupRightInv(Inventory inventory) {
        rightInv.removeAll();
        JPanel left = new JPanel();
        left.setLayout(new GridLayout(0, 1));
        JScrollPane leftScroll = new JScrollPane(left, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        JPanel middle = new JPanel();
        middle.setLayout(new GridLayout(0, 5));
        JScrollPane middleScroll = new JScrollPane(middle, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        middleScroll.setVerticalScrollBar(leftScroll.getVerticalScrollBar());

        JButton leftHeader = new JButton("Item");
        leftHeader.setSize(1, 100);
        leftHeader.setBackground(new Color(95, 75, 60));
        leftHeader.setFont(getCustomFont(25));
        leftHeader.setEnabled(false);
        leftHeader.setBorderPainted(false);

        left.add(leftHeader);

        for (int i = 0; i < 5; i++) {
            JButton middleHeader1 = new JButton("-");
            middleHeader1.setSize(1, 100);
            middleHeader1.setBackground(new Color(95, 75, 60));
            middleHeader1.setFont(getCustomFont(25));
            middleHeader1.setEnabled(false);
            middleHeader1.setBorderPainted(false);
            middle.add(middleHeader1);
        }

        for (int i = 0; i < ItemData.getItemCosts().length; i++) {
            if (ItemData.getItemCosts()[i] != null) {
                for (Map.Entry<Byte, Integer> entry : ItemData.getItemCosts()[i].entrySet()) {

                    JButton leftItem = new JButton("");
                    leftItem.setToolTipText(ItemData.getItemName(entry.getKey()));
                    leftItem.setSize(1, 100);
                    leftItem.setIcon(new ImageIcon(ItemData.getItemImage(entry.getKey())));
                    leftItem.setFont(getCustomFont(20));
                    leftItem.addActionListener(new RightInvButtonListener());
                    leftItem.setBorderPainted(false);

                    int count = ItemData.getItemCosts()[i].size();
                    int count2 = 5 - count;

                    for (int j = 0; j < count; j++) {
                        JButton middleItem = new JButton("-");
                        middleItem.setToolTipText(entry.getValue() + "");
                        middleItem.setSize(1, 100);

                        middleItem.setIcon(new ImageIcon(ItemData.getItemImage(entry.getKey())));
                        middleItem.setBorderPainted(false);
                        middleItem.setSize(1, 100);
                        middleItem.setEnabled(false);
                        middle.add(middleItem);
                    }
                    for (int j = 0; j < count2; j++) {
                        JButton middleItem = new JButton("-");
                        middleItem.setSize(1, 100);
                        middleItem.setBackground(new Color(159, 197, 168));
                        middleItem.setFont(getCustomFont(20));
                        middleItem.setEnabled(false);
                        middleItem.setBorderPainted(false);
                        middle.add(middleItem);
                    }

                    left.add(leftItem);
                }
            }

        }


        rightInv.add(BorderLayout.WEST, leftScroll);
        rightInv.add(BorderLayout.CENTER, middleScroll);
    }

    private void resetSendData() {
        sendData = new LinkedList<>();

        //movement[0] = false;
        //movement[1] = false;
        //movement[2] = false;
    }

    // COM
    private void setupSendData() {
        //int x = (int) MouseInfo.getPointerInfo().getLocation().getX() - getWidth() / 2;
        //int y = getHeight() / 2 - (int) MouseInfo.getPointerInfo().getLocation().getY();
        //angle = Math.atan2(y, x);
        sendData.add("/TICK" + " " + game.getBackFront() + " " + angle);
    }

    private String formulateSendData() {
        // Compiles movement data to send
        if (movement[0] == true && sendData.contains(MOVE_LEFT) == false) sendData.add(MOVE_LEFT);
        if (movement[1] == true && sendData.contains(JUMP) == false) sendData.add(JUMP);
        if (movement[2] == true && sendData.contains(MOVE_RIGHT) == false) sendData.add(MOVE_RIGHT);

        String msg = "";
        Iterator i = sendData.iterator();


        while (i.hasNext()) {
            if (i != null) {
                msg += i.next();
                msg += " ";
            }

        }
        msg = msg.substring(0, msg.length() - 1);
        return msg;
    }

    public int getRenderDistance() {
        return renderDistance;
    }

    public void setRenderDistance(int renderDistance) {
        this.renderDistance = renderDistance;
    }

    public static Font getCustomFont(int size) {
        Font customFont = null;
        try {
            //create the font to use. Specify the size!
            customFont = Font.createFont(Font.TRUETYPE_FONT, new File("Fonts\\Minecraftia.ttf")).deriveFont((float) size);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            //register the font
            ge.registerFont(customFont);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (FontFormatException e) {
            e.printStackTrace();
        }
        return customFont;
    }

    public void setToggle(boolean toggle) {
        this.toggle = toggle;
    }

    public boolean getToggle() {
        return toggle;
    }

    private ImageIcon getScaledIcon(Image image, int x, int y) {
        ImageIcon scaledIcon = new ImageIcon(image) {
            public int getIconWidth() {
                return x;
            }

            public int getIconHeight() {
                return y;
            }

            public void paintIcon(Component c, Graphics g, int x, int y) {
                g.drawImage(image, x, y, getIconWidth(), getIconHeight(), c);
            }
        };
        return scaledIcon;
    }

    // LISTENERS--------------------------------
    //COM
    class MouseClickListener implements MouseListener {
        @Override
        public void mousePressed(MouseEvent e) {
            if (SwingUtilities.isRightMouseButton(e)) {
                sendData.add(RIGHT_CLICK + " " + game.getHotBarByte()[game.getCurrentHotBarItem()]);
            } else if (SwingUtilities.isLeftMouseButton(e)) {
                sendData.add(LEFT_CLICK + " " + game.getHotBarByte()[game.getCurrentHotBarItem()]);
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseClicked(MouseEvent e) {

        }
    }

    class WelcomeButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            if (((JButton) event.getSource()).getText().equals("Play")) {
                currentPanel = SERVER_SELECTOR;
                cardLayout.show(panels, SERVER_SELECTOR);
            } else if (((JButton) event.getSource()).getText().equals("Settings")) {
                PopUpMsg.infoBox("Service Unavailable \nCurrently under maintenance ", ":(");
            } else if (((JButton) event.getSource()).getText().equals("Credits")) {
                PopUpMsg.infoBox("Service Unavailable \nCurrently under maintenance ", ":(");
            }
        }
    }

    class ServerSelectorButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            currentServer = (JButton) event.getSource();
            currentServerVisual.setText("Current Server: " + ((JButton) event.getSource()).getText());
            currentServerVisual.setBackground(new Color(73, 132, 72));
        }
    }

    class JoinServerButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            if (currentServer != null) {
                String[] info = currentServer.getToolTipText().split(":");
                //System.out.println("IP: " + info[0]);
                //System.out.println("PORT: " + info[1]);
                connect(info[0], Integer.valueOf(info[1]));

                if (login()) {
                    setupSendData();
//                    System.out.println(player.getName());
                    game.setPlayer(player);
                    world.addPlayer(player);
                    game.setWorld(world);
                    changeScreen(GAME);
                    setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
                    //new Extrapolate(world);
                }
            } else {
                PopUpMsg.infoBox("Please select a server", ":)");
            }

        }
    }

    class DeleteServerButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            PopUpMsg.infoBox("Service Unavailable \nCurrently under maintenance ", ":(");
        }
    }

    class AddServerButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            if (!ipPort.getText().equals("") && !ipPort.getText().contains(" ") && ipPort.getText().contains(":") && !ipPort.getText().contains("IP:Port")) {
                if (!serverName.getText().equals("") && !serverName.getText().contains("Please enter server name")) {
                    if (!playerName.getText().equals("") && !playerName.getText().contains("Enter your in-game name")) {
                        addToServerSelector(serverName.getText(), ipPort.getText());
                    } else {
                        PopUpMsg.infoBox("Enter your in-game name", ":(");
                        playerName.setText("Enter your in-game name");
                    }
                } else {
                    PopUpMsg.infoBox("Please enter server name", ":(");
                    serverName.setText("Enter server name");
                }
            } else {
                PopUpMsg.infoBox("No info or wrong format\nPlease enter as [IP:Port] with no spaces", ":(");
                ipPort.setText("IP:Port");
            }
        }
    }

    class EscButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (((JButton) e.getSource()).getText().equals("Back to game (esc)")) {
                changeScreen(GAME);
            } else if (((JButton) e.getSource()).getText().equals("Settings (o)")) {
                //Client.changeScreen(Client.SETTINGS);
                PopUpMsg.infoBox("Service Unavailable \nCurrently under maintenance ", ":(");
            } else if (((JButton) e.getSource()).getText().equals("Inventory (e)")) {
                changeScreen(INVENTORY);
            } else if (((JButton) e.getSource()).getText().equals("Exit (p)")) {
                try {
                    input.close();
                    output.close();
                    mySocket.close();
                    running = false;
                } catch (Exception a) {
                    System.out.println("Failed to close socket");
                }
                System.exit(0);
            }
        }
    }

    class LeftInvButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            if (((JButton) event.getSource()).getText().equals("")) {
                leftInvItem.setText("Item: " + ItemData.getItemName(Byte.parseByte(((JButton) event.getSource()).getToolTipText())));
                leftInvItem.setToolTipText(Byte.parseByte(((JButton) event.getSource()).getToolTipText()) + "");
                leftInvItem.setBackground(new Color(73, 132, 72));
                itemDrop.setText("1");
                changeScreen(ESC_SCREEN);
                escScreen.revalidate();
                changeScreen(INVENTORY);
            } else if (((JButton) event.getSource()).getText().equals("Drop")) {
                if (!leftInvItem.getText().equals("No Item")) {
                    sendData.add("[Drop] " + leftInvItem.getToolTipText() + " " + itemDrop.getText());
                    PopUpMsg.infoBox("DROPPED ITEM", "");
                    setupLeftInv(player.getInventory());
                    changeScreen(ESC_SCREEN);
                    escScreen.revalidate();
                    changeScreen(INVENTORY);
                } else {
                    PopUpMsg.infoBox("Please select item to drop", "");
                }
            }

        }
    }

    class RightInvButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            if (((JButton) event.getSource()).getText().equals("")) {
                rightInvItem.setText("Item: " + ItemData.getItemName(Byte.parseByte(((JButton) event.getSource()).getToolTipText())));
                rightInvItem.setBackground(new Color(73, 132, 72));
                itemCraft.setText("1");
            } else if (((JButton) event.getSource()).getText().equals("Craft")) {
                if (!rightInvItem.getText().equals("No Item")) {
                    sendData.add("[Craft] " + ((JButton) event.getSource()).getToolTipText() + " " + itemCraft.getText());
                } else {
                    PopUpMsg.infoBox("Please select item to craft", "");
                }
            }
        }
    }

    class GameKeyListener implements KeyListener {
        @Override
        public void keyReleased(KeyEvent e) {

            if (e.getKeyCode() == 32) { // SPACEBAR
                movement[1] = false;
            }

            // WASD------
            if (e.getKeyChar() == 'w') { // left
                movement[1] = false;
            }
            if (e.getKeyChar() == 'a') { // jump
                movement[0] = false;
            }
            if (e.getKeyChar() == 's') {
                //player.crouch();
            }
            if (e.getKeyChar() == 'd') { // right
                movement[2] = false;
            }

            // ARROWS-----
            if (e.getKeyChar() == KeyEvent.VK_UP) {
                movement[1] = false;
            }
            if (e.getKeyChar() == KeyEvent.VK_LEFT) {
                movement[0] = false;
            }
            if (e.getKeyChar() == KeyEvent.VK_DOWN) {
                //player.crouch();
            }
            if (e.getKeyChar() == KeyEvent.VK_RIGHT) {
                movement[2] = false;
            }

            // MENU CLICKS
            if (currentPanel.equals(ESC_SCREEN) || currentPanel.equals(GAME) || currentPanel.equals(INVENTORY)) {
                if (e.getKeyCode() == 27) { //esc is pressed
                    if (getCurrentPanel().equals(GAME)) {
                        changeScreen(ESC_SCREEN);
                    } else {
                        changeScreen(GAME);
                    }
                } else if (e.getKeyChar() == 'e') { // Inventory
                    if (!currentPanel.equals(INVENTORY)) {
                        changeScreen(INVENTORY);
                    } else {
                        changeScreen(GAME);
                    }
                } else if (e.getKeyChar() == 'o') { // Settings

                } else if (e.getKeyChar() == 'p') { // Exit
                    System.exit(0);
                } else if (currentPanel.equals(INVENTORY)) {
                    if (!leftInvItem.getText().equals("No Item")) {
                        if (e.getKeyChar() == '1') { // Exit
                            game.getHotBarByte()[0] = Byte.parseByte(leftInvItem.getToolTipText());
                        } else if (e.getKeyChar() == '2') { // Exit
                            game.getHotBarByte()[1] = Byte.parseByte(leftInvItem.getToolTipText());
                        } else if (e.getKeyChar() == '3') { // Exit
                            game.getHotBarByte()[2] = Byte.parseByte(leftInvItem.getToolTipText());
                        } else if (e.getKeyChar() == '4') { // Exit
                            game.getHotBarByte()[3] = Byte.parseByte(leftInvItem.getToolTipText());
                        } else if (e.getKeyChar() == '5') { // Exit
                            game.getHotBarByte()[4] = Byte.parseByte(leftInvItem.getToolTipText());
                        } else if (e.getKeyChar() == '6') { // Exit
                            game.getHotBarByte()[5] = Byte.parseByte(leftInvItem.getToolTipText());
                        } else if (e.getKeyChar() == '7') { // Exit
                            game.getHotBarByte()[6] = Byte.parseByte(leftInvItem.getToolTipText());
                        } else if (e.getKeyChar() == '8') { // Exit
                            game.getHotBarByte()[7] = Byte.parseByte(leftInvItem.getToolTipText());
                        } else if (e.getKeyChar() == '9') { // Exit
                            game.getHotBarByte()[8] = Byte.parseByte(leftInvItem.getToolTipText());
                        }
                    }

                }
            }
        }

        @Override
        public void keyTyped(KeyEvent e) {

        }

        @Override
        public void keyPressed(KeyEvent e) {
            //
//            if(movement[1]){
//                sendData.add(JUMP);
//            }
//            if(movement[0]){
//                sendData.add(MOVE_LEFT);
//            }
//            if(movement[2]){
//                sendData.add(MOVE_RIGHT);
//            }

            if (e.getKeyCode() == 32) { // SPACEBAR
//                if (!movement[1]){
                movement[1] = true;
//                }
            }

            // WASD------
            if (e.getKeyChar() == 'w') {
//                if (!movement[1]){
                movement[1] = true;
            }          //  }
            if (e.getKeyChar() == 'a') {
//                if (!movement[0]){
                movement[0] = true;
//                }
            }
            if (e.getKeyChar() == 's') {
                //player.crouch();
            }
            if (e.getKeyChar() == 'd') {
//                if (!movement[2]){
                movement[2] = true;
//                }
            }

            // ARROWS-----
            if (e.getKeyChar() == KeyEvent.VK_UP) {
//                if (!movement[1]){
                movement[1] = true;
//                }
            }
            if (e.getKeyChar() == KeyEvent.VK_LEFT) {
//                if (!movement[0]){
                movement[0] = true;
//                }
            }
            if (e.getKeyChar() == KeyEvent.VK_DOWN) {
                //player.crouch();
            }
            if (e.getKeyChar() == KeyEvent.VK_RIGHT) {
//                if (!movement[2]){
                movement[2] = true;
//                }
            }
        }
    }

    class MouseTracker implements MouseMotionListener {
        @Override
        public void mouseDragged(MouseEvent e) {
//            int x = e.getX() - getWidth() / 2;
//            int y = -e.getY() + 31 + getHeight() / 2;
//            angle = Math.atan2(y, x);
            angle = Math.atan2(-e.getY() + getHeight() / 2, e.getX() - getWidth() / 2);
//            angle = Math.atan2(-e.getY() + 31 + getHeight() / 2, e.getX() - getWidth() / 2);
        }

        @Override
        public void mouseMoved(MouseEvent e) {
//            int x = e.getX() - getWidth() / 2;
//            int y = -e.getY() + 31 + getHeight() / 2;
//            angle = Math.atan2(y, x);
            angle = Math.atan2(-e.getY() + getHeight() / 2, e.getX() - getWidth() / 2);
//            angle = Math.atan2(-e.getY() + 31 + getHeight() / 2, e.getX() - getWidth() / 2);
        }
    }

    class HotBarScrollListener implements MouseWheelListener {
        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            if (e.getWheelRotation() < 0) {
                game.hotBarScrollUp();
            } else {
                game.hotBarScrollDown();
            }
        }
    }

    class Extrapolate implements Runnable {
        World world;

        public Extrapolate(World world) {
            this.world = world;
            Thread thread = new Thread(this);
            thread.start();
        }

        public void run() {
            while (true) {
                long startTime = System.nanoTime();
                if (toggle) {
                    for (Map.Entry<Integer, Player> entry : world.getPlayers().entrySet()) {
                        entry.getValue().setX(entry.getValue().getVx() * timeElapsed / 50000000.0 + entry.getValue().getX());
                    }
                    for (Map.Entry<Integer, PassiveMob> entry : world.getPassiveMobs().entrySet()) {
                        entry.getValue().setX(entry.getValue().getVx() * timeElapsed / 50000000.0 + entry.getValue().getX());
                    }
                    for (Map.Entry<Integer, HostileMob> entry : world.getHostileMobs().entrySet()) {
                        entry.getValue().setX(entry.getValue().getVx() * timeElapsed / 50000000.0 + entry.getValue().getX());
                    }
                    for (Map.Entry<Integer, EntityItem> entry : world.getDrops().entrySet()) {
                        entry.getValue().setX(entry.getValue().getVx() * timeElapsed / 50000000.0 + entry.getValue().getX());
                    }
                    timeElapsed = 0;
                }
                drawFrame = true;
                long t = System.nanoTime() - startTime;
                timeElapsed += t;

            }
        }
    }
}