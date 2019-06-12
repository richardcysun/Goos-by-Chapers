package auctionsniper;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.SwingUtilities;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import auctionsniper.ui.MainWindow;

//Ch13, p.125 replace AuctionEventListener with SniperListener
public class Main implements SniperListener {
    @SuppressWarnings("unused") private Chat notTobeGCd;
    private MainWindow ui;
    
    private static final int ARG_HOSTNAME = 0;
    private static final int ARG_USERNAME = 1;
    private static final int ARG_PASSWORD = 2;
    private static final int ARG_ITEM_ID  = 3;
    
    public static final String AUCTION_RESOURCE = "Auction";
    public static final String ITEM_ID_AS_LOGIN = "auction-%s";
    public static final String AUCTION_ID_FORMAT = ITEM_ID_AS_LOGIN + "@%s/" + AUCTION_RESOURCE;
	public static final String JOIN_COMMAND_FORMAT = "SOLVersion: 1.1; Command: JOIN;";
	public static final String BID_COMMAND_FORMAT = "SOLVersion: 1.1; Command: BID; Price: %d;";
    
    public Main() throws Exception {
        startUserInterface();
    }

    public static void main(String... args) throws Exception {
        Main main = new Main();
        main.joinAuction(connection(args[ARG_HOSTNAME],
                args[ARG_USERNAME], args[ARG_PASSWORD]), args[ARG_ITEM_ID]);
    }

    //Ch12, p.117 revise Ch11
    //Login as "sniper/sniper" and join "auction-item-54321" chat    
    private void joinAuction(XMPPConnection connection, String itemId) throws XMPPException {
    	disconnectWhenUICloses(connection);
        Chat chat = connection.getChatManager().createChat(auctionId(itemId, connection),
                new AuctionMessageTranslator(new AuctionSniper(this)));
        
        //Turn on below 3 second delay so we may have chance the observe the Joining->Lost messages
        
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace(); 
        }     
        
        //Ch12, p.110 revise Ch11
        chat.sendMessage(JOIN_COMMAND_FORMAT);
        this.notTobeGCd = chat;
    }
    
    private void disconnectWhenUICloses(final XMPPConnection connection) {
    	ui.addWindowListener(new WindowAdapter() {
    		@Override public void windowClosed(WindowEvent e) {
    			connection.disconnect();
    		}
    	});
	}

	private static String auctionId(String itemId, XMPPConnection connection) {
        return String.format(AUCTION_ID_FORMAT, itemId, connection.getServiceName());
    }

	//Login as "sniper/sniper"
    private static XMPPConnection connection(String hostname, String username, String password) throws XMPPException{
        XMPPConnection connection = new XMPPConnection(hostname);
        connection.connect();
        connection.login(username, password, AUCTION_RESOURCE);
        return connection;
    }

    private void startUserInterface() throws Exception {
        SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {
                ui = new MainWindow();
            }
        });
    }
    
    //Ch12, p.117
    public void auctionClosed() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                ui.showStaus(MainWindow.STATUS_LOST);
            }
        });
    }
    
    //Add a null method for now to make application worked
    public void currentPrice(int price, int increment) {
        
    }
    
    //Ch13, p.125
    public void sniperLost() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                ui.showStaus(MainWindow.STATUS_LOST);
            }
        });        
    }
}
