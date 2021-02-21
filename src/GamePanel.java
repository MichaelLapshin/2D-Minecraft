import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Map;

public class GamePanel extends JPanel {
    //World world;
    public int seed, width;
    private JButton escButton, backFront;
    private JTextField renderDistance;
    private int displayRatio;
    private Client client;
    private Player player;
    private World world;
    private long timeElapsed = 0;

    private double hotBarBoxSizeRatio = 1.0 / 25.0;
    private double hotBarBoxYRatio = 8.4 / 10.0;
    private double hotBarBoxXRatio = 6.2 / 20.0;
    private int hotBarOffset = 1;
    private double heartSizeRatio = 1.0 / 45.0;
    private double heartYRatio = 7.8 / 10.0;
    private double heartXRatio = 8.7 / 20.0;
    private int heartOffset = 1;
    private BufferedImage[] heartSprites;
    private JButton currentItem, extrapolate;
    private JButton[] hotBar = new JButton[9];
    private byte[] hotBarByte = new byte[9];
    private JButton[] hearts = new JButton[5];
    private int currentHotBarItem = 0;
    private ImageIcon item;
    private JLabel coords, render;

    private boolean front = true;

    GamePanel(int width, int seed, Client client) throws IOException {
        setLayout(null);
        //addKeyListener(new GameKeyListener());
        setFocusable(true);
        requestFocusInWindow();
        displayRatio = 1;

        for (int i = 0; i < hotBar.length; i++) {
            JButton item = new JButton();
            item.setBorderPainted(false);
            item.setBackground(new Color(138, 117, 99, 5));
            item.setToolTipText(i + "");
            item.addActionListener(new HotBarButtonListener());
            item.setBounds((int) (client.getWidthJ() * hotBarBoxXRatio) + i * (int) (client.getWidthJ() * hotBarBoxSizeRatio) + i * hotBarOffset, (int) (client.getHeightJ() * hotBarBoxYRatio), (int) (client.getWidthJ() * hotBarBoxSizeRatio), (int) (client.getWidthJ() * hotBarBoxSizeRatio));
            item.setFont(client.getCustomFont(15));
            hotBar[i] = item;
            add(hotBar[i]);
        }
        item = new ImageIcon("Images/currentItem.png");
        currentItem = new JButton("");
        currentItem.setIcon(new ImageIcon("Images/currentItem.png"));
        currentItem.setBorderPainted(false);

        heartSprites = new BufferedImage[5];
        for (int i = 0; i < 5; i++) {
            BufferedImage img = ImageIO.read(new File("Images/hearts.png"));
            heartSprites[i] = img.getSubimage(i * img.getWidth() / 5, 0, img.getWidth() / 5, img.getHeight());
        }


        this.client = client;
        // world = new World(width, seed);

        escButton = new JButton("Esc");
        escButton.addActionListener(new EscButtonListener());
        escButton.setBorderPainted(false);
        escButton.setFont(client.getCustomFont(7));
        escButton.setBackground(new Color(155, 151, 151));
        escButton.setBounds(0, 0, 55 * displayRatio, 35 * displayRatio);

        backFront = new JButton("Front");
        backFront.addActionListener(new EscButtonListener());
        backFront.setFont(client.getCustomFont(7));
        backFront.setBackground(new Color(99, 152, 96));
        backFront.setBorderPainted(false);
        backFront.setBounds(55 * displayRatio, 0, 64 * displayRatio, 35 * displayRatio);

        extrapolate = new JButton("EXTRAP");
        extrapolate.addActionListener(new EscButtonListener());
        extrapolate.setFont(client.getCustomFont(7));
        extrapolate.setBackground(new Color(99, 152, 96));
        extrapolate.setBorderPainted(false);
        extrapolate.setBounds(104 * displayRatio, 0, 64 * displayRatio, 35 * displayRatio);

        renderDistance = new JTextField("30");
        renderDistance.setFont(client.getCustomFont(8));
        renderDistance.setBounds(158 * displayRatio, 0, 75 * displayRatio, 35 * displayRatio);

        coords = new JLabel();
        coords.setFont(client.getCustomFont(8));
        coords.setForeground(Color.BLACK);
        coords.setBounds(158 * displayRatio, 0, 70 * displayRatio, 35 * displayRatio);

        render = new JLabel("Render Distance:");
        render.setFont(client.getCustomFont(8));
        render.setBounds(158 * displayRatio, 0, 54 * displayRatio, 35 * displayRatio);

        add(escButton);
        add(backFront);
        add(currentItem);
        add(extrapolate);
        add(renderDistance);
        add(render);
        add(coords);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(new Color(135, 206, 235));
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(Color.BLACK);
        render.setLocation(getWidth() - 230, 0);
        renderDistance.setLocation(getWidth() - 130, 0);
        coords.setLocation(getWidth() - 66, 0);
        coords.setText("X: " + (int) player.getX() + ",Y: " + (int) player.getY());

        long startTime = System.nanoTime();

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

        if (renderDistance.getText().equals("") || containsString(renderDistance.getText())) {
            world.draw(g, new Dimension(client.getWidth(), client.getHeight()), 20, player.getX(), player.getY(), player);
        } else {
            world.draw(g, new Dimension(client.getWidth(), client.getHeight()), Integer.parseInt(renderDistance.getText()), player.getX(), player.getY(), player);
        }
        displayHotBar(g);
        displayHearts(g);

        long t = System.nanoTime() - startTime;
        timeElapsed = t;

//        g.setColor(Color.RED);
//        g.fillRect(getWidth() / 2 - 2, getHeight() / 2 - 2, 4, 4);

        g.setColor(Color.BLACK);

        g.drawString("Add items to hotbar by selecting item and corresponding number in inventory", getWidth() / 2 - 100, (int) hotBarBoxYRatio * getWidth() / 2);
    }

    // MISC-------------------------------
    public String getBackFront() {
        if (front) {
            return "1";
        } else {
            return "0";
        }
    }

    public boolean containsString(String string) {
        try {
            Integer.parseInt(string);
            return false;
        } catch (Exception e) {
            return true;
        }
    }

    public byte[] getHotBarByte() {
        return hotBarByte;
    }

    public int getCurrentHotBarItem() {
        return currentHotBarItem;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    // COM
    public void displayHearts(Graphics g) {
        int health = player.getHealth();

        for (int i = 0; i < 5; i++) {
            if (health - 20 >= 0) {
                g.drawImage(heartSprites[0], (int) (client.getWidthJ() * heartXRatio) + i * (int) (client.getWidthJ() * heartSizeRatio) + i * heartOffset, (int) (client.getHeightJ() * heartYRatio), (int) (client.getWidthJ() * heartSizeRatio), (int) (client.getWidthJ() * heartSizeRatio), null);
                health -= 20;
            } else if (health - 15 >= 0) {
                g.drawImage(heartSprites[1], (int) (client.getWidthJ() * heartXRatio) + i * (int) (client.getWidthJ() * heartSizeRatio) + i * heartOffset, (int) (client.getHeightJ() * heartYRatio), (int) (client.getWidthJ() * heartSizeRatio), (int) (client.getWidthJ() * heartSizeRatio), null);
                health -= 15;
            } else if (health - 10 >= 0) {
                g.drawImage(heartSprites[2], (int) (client.getWidthJ() * heartXRatio) + i * (int) (client.getWidthJ() * heartSizeRatio) + i * heartOffset, (int) (client.getHeightJ() * heartYRatio), (int) (client.getWidthJ() * heartSizeRatio), (int) (client.getWidthJ() * heartSizeRatio), null);
                health -= 10;
            } else if (health - 5 >= 0) {
                g.drawImage(heartSprites[3], (int) (client.getWidthJ() * heartXRatio) + i * (int) (client.getWidthJ() * heartSizeRatio) + i * heartOffset, (int) (client.getHeightJ() * heartYRatio), (int) (client.getWidthJ() * heartSizeRatio), (int) (client.getWidthJ() * heartSizeRatio), null);
                health -= 5;
            } else {
                g.drawImage(heartSprites[4], (int) (client.getWidthJ() * heartXRatio) + i * (int) (client.getWidthJ() * heartSizeRatio) + i * heartOffset, (int) (client.getHeightJ() * heartYRatio), (int) (client.getWidthJ() * heartSizeRatio), (int) (client.getWidthJ() * heartSizeRatio), null);
            }
            //hearts[i].setBounds((int)(client.getWidthJ() * heartXRatio) + i *(int)(client.getWidthJ()  * heartSizeRatio) + i*heartOffset, (int)(client.getHeightJ()  * heartYRatio), (int)(client.getWidthJ() * heartSizeRatio), (int)(client.getWidthJ()  * heartSizeRatio));
        }

        // TODO display hints
//        g.setColor(Color.BLACK);
//        setFont(client.getCustomFont(18));
//        g.drawString("Hint #1: You can equip selected items in your inventory by pressing a numerical key.", getWidth() / 2 - 300, getHeight() - 25);
//        g.drawString("Hint #2: Left click to attack/break. Right click to place selected blocks.", getWidth() / 2 - 240, getHeight() - 15);
    }

    public void displayHotBar(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect((int) (client.getWidthJ() * hotBarBoxXRatio) + currentHotBarItem * (int) (client.getWidthJ() * hotBarBoxSizeRatio) + currentHotBarItem * hotBarOffset - 2, (int) (client.getHeightJ() * hotBarBoxYRatio) - 2, (int) (client.getWidthJ() * hotBarBoxSizeRatio) + 4, (int) (client.getWidthJ() * hotBarBoxSizeRatio) + 4);

        for (int i = 0; i < hotBar.length; i++) {
            g.drawRect((int) (client.getWidthJ() * hotBarBoxXRatio) + i * (int) (client.getWidthJ() * hotBarBoxSizeRatio) + i * hotBarOffset - 2, (int) (client.getHeightJ() * hotBarBoxYRatio) - 2, (int) (client.getWidthJ() * hotBarBoxSizeRatio) + 4, (int) (client.getWidthJ() * hotBarBoxSizeRatio) + 4);
            hotBar[i].setBackground(new Color(138, 85, 79, 5));
            hotBar[i].setToolTipText(ItemData.getItemName(hotBarByte[i]) + "");
            hotBar[i].setBounds((int) (client.getWidthJ() * hotBarBoxXRatio) + i * (int) (client.getWidthJ() * hotBarBoxSizeRatio) + i * hotBarOffset, (int) (client.getHeightJ() * hotBarBoxYRatio), (int) (client.getWidthJ() * hotBarBoxSizeRatio), (int) (client.getWidthJ() * hotBarBoxSizeRatio));
            if (hotBarByte[i] != 0) {
                g.drawImage(new ImageIcon(ItemData.getItemImage(hotBarByte[i])).getImage(), hotBar[i].getX(), hotBar[i].getY(), hotBar[i].getWidth(), hotBar[i].getHeight(), null);
            } else {
                hotBar[i].setText(i + 1 + "");
            }
        }

        //item.paintIcon(this, g, 100, 100);
        //currentItem.setBounds((int)(client.getWidthJ() * hotBarBoxXRatio) + currentHotBarItem *(int)(client.getWidthJ()  * hotBarBoxSizeRatio) + currentHotBarItem*hotBarOffset, (int)(client.getHeightJ()  * hotBarBoxYRatio), (int)(client.getWidthJ() * hotBarBoxSizeRatio), (int)(client.getWidthJ()  * hotBarBoxSizeRatio));
        //currentItem.setBounds((int)(client.getWidthJ() / 2), (int)(client.getHeightJ()  /2), 100, 100);
    }

    public void hotBarScrollUp() {
        if (currentHotBarItem == hotBar.length - 1) {
            currentHotBarItem = 0;
        } else {
            currentHotBarItem += 1;
        }
    }

    public void hotBarScrollDown() {
        if (currentHotBarItem == 0) {
            currentHotBarItem = hotBar.length - 1;
        } else {
            currentHotBarItem -= 1;
        }
    }

    class EscButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (((JButton) e.getSource()).getText().equals("Esc")) {
                client.changeScreen(client.ESC_SCREEN);
            } else if (((JButton) e.getSource()).getText().equals("Front") || ((JButton) e.getSource()).getText().equals("Back")) {
                if (front) {
                    backFront.setText("Back");
                    backFront.setBackground(new Color(152, 83, 83));
                    front = false;
                } else {
                    backFront.setText("Front");
                    backFront.setBackground(new Color(99, 152, 96));
                    front = true;
                }
            } else if (((JButton) e.getSource()).getText().equals("EXTRAP")) {
                if (client.getToggle()) {
                    client.setToggle(false);
                    extrapolate.setBackground(new Color(152, 83, 83));
                } else {
                    client.setToggle(true);
                    extrapolate.setBackground(new Color(99, 152, 96));
                }
            }
        }
    }

    class HotBarButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {

            for (byte i = 0; i < hotBar.length; i++) {
//                if(((JButton) e.getSource()).getToolTipText().equals(ItemData.getItemName(i))) {
                if (((JButton) e.getSource()).getToolTipText().equals(hotBar[i].getName())) {
                    currentHotBarItem = i;
                    return;
                }
            }
            client.changeScreen(client.ESC_SCREEN);
            client.changeScreen(client.GAME);
        }
    }

}

