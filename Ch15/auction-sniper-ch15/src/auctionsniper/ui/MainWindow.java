package auctionsniper.ui;

import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import auctionsniper.SniperState;

//Ch11, p.97, 98, 102
public class MainWindow extends JFrame{

    public static final String MAIN_WINDOW_NAME = "Auction Sniper Main";
    public static final String SNIPER_STATUS_NAME = "sniper status";    
    public static final String STATUS_JOINING = "Joining";
	public static final String STATUS_BIDDING = "Bidding";
	public static final String STATUS_WINNING = "Winning";
    public static final String STATUS_LOST = "Lost";
    public static final String STATUS_WON = "Won";
    private static final String SNIPER_TABLE_NAME = "Auction Sniper Table";
    private static final String APPLICATION_TITLE = "Auction Sniper Title";
    
    private final SnipersTableModel snipers = new SnipersTableModel();

    //Ch15, p.151, replace JLabel with JTable
    public MainWindow() {
        super(APPLICATION_TITLE);
        setName(MainWindow.MAIN_WINDOW_NAME);
        fillContentPane(makeSnipersTable());
        //It shows "Joining" by default
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void fillContentPane(JTable snipersTable) {
        final Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        
        contentPane.add(new JScrollPane(snipersTable), BorderLayout.CENTER);
    }

    private JTable makeSnipersTable() {
        final JTable snipersTable = new JTable(snipers);
        snipersTable.setName(SNIPER_TABLE_NAME);
        return snipersTable;
    }
    
    public void showStatusText(String statusText) {
        snipers.setStatusText(statusText);
    }

    //Ch15, p.156
    public void sniperStatusChanged(SniperState sniperState, String statusText) {
        snipers.sniperStatusChanged(sniperState, statusText);
        
    }
}
