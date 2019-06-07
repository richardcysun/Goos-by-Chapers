package auctionsniper.ui;

import java.awt.Color;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.border.LineBorder;

//Ch11, p.97, 98, 102
public class MainWindow extends JFrame{

    public static final String MAIN_WINDOW_NAME = "Auction Sniper Main";
    public static final String STATUS_JOINING = "Joining";
    public static final String STATUS_LOST = "Lost";
    public static final String SNIPER_STATUS_NAME = "sniper status";
    
    private final JLabel sniperStatus = createLabel(STATUS_JOINING);

    public MainWindow() {
        super("Auction Sniper");
        setName(MAIN_WINDOW_NAME);
        add(sniperStatus);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private JLabel createLabel(String initailText) {
        // TODO Auto-generated method stub
        JLabel result = new JLabel(initailText);
        result.setName(SNIPER_STATUS_NAME);
        result.setBorder(new LineBorder(Color.BLACK));
        return result;
    }

    public void showStaus(String status) {
        sniperStatus.setText(status);
        
    }
}
