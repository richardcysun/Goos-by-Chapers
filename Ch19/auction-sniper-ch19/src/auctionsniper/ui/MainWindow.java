package auctionsniper.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import auctionsniper.Item;
import auctionsniper.SniperPortfolio;
import auctionsniper.SniperSnapshot;
import auctionsniper.UserRequestListener;
import auctionsniper.util.Announcer;

//Ch11, p.97, 98, 102
public class MainWindow extends JFrame{
	//Ch16, p.187
	private final Announcer<UserRequestListener> userRequests =
			Announcer.to(UserRequestListener.class);
	
    public static final String MAIN_WINDOW_NAME = "Auction Sniper Main";
    public static final String SNIPER_STATUS_NAME = "sniper status";    
    public static final String STATUS_JOINING = "Joining";
	public static final String STATUS_BIDDING = "Bidding";
	public static final String STATUS_WINNING = "Winning";
	public static final String STATUS_LOSING = "Losing";
    public static final String STATUS_LOST = "Lost";
    public static final String STATUS_WON = "Won";
    public static final String STATUS_FAILED = "Failed";
    private static final String SNIPER_TABLE_NAME = "Auction Sniper Table";
    public static final String APPLICATION_TITLE = "Auction Sniper Title";
	public static final String JOIN_BUTTON_NAME = "join Auction";
	public static final String NEW_ITEM_ID_NAME = "Item Id";
	public static final String NEW_ITEM_STOP_PRICE_NAME = "Stop Price";
    
    private SnipersTableModel snipers;
    
    //Ch15, p.151, replace JLabel with JTable
    //Ch17, not in the book, replace SnipersTableModel with SniperPortfolio
    public MainWindow(SniperPortfolio portfolio) {
        super(APPLICATION_TITLE);
        setName(MainWindow.MAIN_WINDOW_NAME);
        //Ch16, p.185
        fillContentPane(makeSnipersTable(portfolio), makeControls());
        //It shows "Joining" by default
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    //Ch16, p.185
    private JPanel makeControls() {
		JPanel controls = new JPanel(new FlowLayout());
		final JTextField itemIdField = new JTextField();
		final JFormattedTextField stopPriceField = new JFormattedTextField();
		itemIdField.setColumns(15);
		itemIdField.setName(NEW_ITEM_ID_NAME);
		controls.add(itemIdField);
		stopPriceField.setColumns(10);
		stopPriceField.setName(NEW_ITEM_STOP_PRICE_NAME);
		controls.add(stopPriceField);
		
		JButton joinAuctionButton = new JButton("join Auction");
		joinAuctionButton.setName(JOIN_BUTTON_NAME);
		
		joinAuctionButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//Ch18, p.210 revise
				userRequests.announce().joinAuction(new Item(itemId(), stopPrice()));
			}
			//Ch18, p.210
			private String itemId() {
				return itemIdField.getText();
			}
			//Ch18, p.210
			private int stopPrice() {
				//Below LoC mentioned in book causes exception, so I rewrite it in alternative way
				//return ((Number)stopPriceField.getValue()).intValue();
				return Integer.valueOf(stopPriceField.getText());
			}
		});
		controls.add(joinAuctionButton);
		return controls;
	}

	private void fillContentPane(JTable snipersTable, JPanel controls) {
        final Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(controls, BorderLayout.NORTH);
        contentPane.add(new JScrollPane(snipersTable), BorderLayout.CENTER);
    }

	//Ch17, p.199 revise
    private JTable makeSnipersTable(SniperPortfolio portfolio) {
    	SnipersTableModel model = new SnipersTableModel();
    	portfolio.addPortfolioListener(model);
        final JTable snipersTable = new JTable(model);
        snipersTable.setName(SNIPER_TABLE_NAME);
        return snipersTable;
    }
    
    //Ch15, p.156
    //Ch15, p.167 revised
    public void sniperStateChanged(SniperSnapshot snapshot) {
        snipers.sniperStateChanged(snapshot);
    }

    //Ch16, p.187
	public void addUserRequestListener(UserRequestListener userReqeustListener) {
		userRequests.addListener(userReqeustListener);
	}
}
