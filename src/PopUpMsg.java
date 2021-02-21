import javax.swing.JOptionPane;

public class PopUpMsg {
    /**
     * infoBox
     * used to display a pop up info box with a message and title
     * @param infoMessage
     * @param titleBar
     */
    public static void infoBox(String infoMessage, String titleBar) {
        JOptionPane.showMessageDialog(null, infoMessage, "InfoBox: " + titleBar, JOptionPane.INFORMATION_MESSAGE);
    }
}