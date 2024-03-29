package auctionsniper;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.SwingUtilities;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import auctionsniper.ui.MainWindow;

//Ch13, p.125 replace AuctionEventListener with SniperListener
public class Main {
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

    //Ch13, p.130, 132 revise entire function
    //Login as "sniper/sniper" and join "auction-item-54321" chat    
    //Ch13, p.133, Because end-to-end test is passed, we can safely refactor XMPPAuction and SniperStateDisplayer
    private void joinAuction(XMPPConnection connection, String itemId) throws XMPPException {
    	disconnectWhenUICloses(connection);
        Chat chat = connection.getChatManager().createChat(auctionId(itemId, connection), null);
        this.notTobeGCd = chat;
        
        //XMPPAuction is the first step to make entire structure more clean and reasonable
        Auction auction = new XMPPAuction(chat);       
                
        //SniperStateDisplayer is another step to make entire structure more clean and reasonable
        //So we can move sniperLost and sniperBidding to better places
        chat.addMessageListener(new AuctionMessageTranslator(new AuctionSniper(auction, new SniperStateDisplayer())));
        auction.join();
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
    
    //Nested class
    //Ch13, p.132
    public static class XMPPAuction implements Auction {
        private final Chat chat;
        
        public XMPPAuction(Chat chat) {
            this.chat = chat;
        }
        
        public void bid(int amount) {
            sendMessage(String.format(BID_COMMAND_FORMAT, amount));
        }
        
        public void join() {
            sendMessage(JOIN_COMMAND_FORMAT);
        }
        
        private void sendMessage(String message) {
            try {
                chat.sendMessage(message);
            }
            catch(XMPPException e) {
                e.printStackTrace();
            }
        }
    }    
    
    //Ch13, p.134    
    public class SniperStateDisplayer implements SniperListener {       
        public void sniperLost() {
            showStatus(MainWindow.STATUS_LOST);
        }

        public void sniperBidding() {
            showStatus(MainWindow.STATUS_BIDDING);
        }

        public void sniperWinning() {
            showStatus(MainWindow.STATUS_WINNING);
        }
        
        private void showStatus(final String status) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    ui.showStaus(status);
                }
            });          
        }
    }
}
