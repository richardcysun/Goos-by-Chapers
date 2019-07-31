package auctionsniper;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.SwingUtilities;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import auctionsniper.ui.MainWindow;
import auctionsniper.ui.SnipersTableModel;

//Ch13, p.125 replace AuctionEventListener with SniperListener
public class Main {
    @SuppressWarnings("unused") private Chat notTobeGCd;
    private final SnipersTableModel snipers = new SnipersTableModel();
    private MainWindow ui;
    
    private static final int ARG_HOSTNAME = 0;
    private static final int ARG_USERNAME = 1;
    private static final int ARG_PASSWORD = 2;
    
    public static final String AUCTION_RESOURCE = "Auction";
    public static final String ITEM_ID_AS_LOGIN = "auction-%s";
    public static final String AUCTION_ID_FORMAT = ITEM_ID_AS_LOGIN + "@%s/" + AUCTION_RESOURCE;
	public static final String JOIN_COMMAND_FORMAT = "SOLVersion: 1.1; Command: JOIN;";
	public static final String BID_COMMAND_FORMAT = "SOLVersion: 1.1; Command: BID; Price: %d;";

	//Ch15, p.168
    public Main() throws Exception {
        SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {
                ui = new MainWindow(snipers);
            }
        });        
    }

    public static void main(String... args) throws Exception {
        Main main = new Main();
        //Ch16, p.179
        XMPPConnection connection = connection(args[ARG_HOSTNAME], args[ARG_USERNAME], args[ARG_PASSWORD]);
        main.disconnectWhenUICloses(connection);
        //Add multiple items
        for (int i = 3; i < args.length; i++) {
            main.joinAuction(connection, args[i]);
        }
    }

    //Ch13, p.130, 132 revise entire function
    //Login as "sniper/sniper" and join "auction-item-54321" chat    
    //Ch13, p.133, Because end-to-end test is passed, we can safely refactor XMPPAuction and SniperStateDisplayer
    //Ch16, p.180 revise
    private void joinAuction(XMPPConnection connection, String itemId) throws Exception {
    	safelyAddItemToModel(itemId);
        Chat chat = connection.getChatManager().createChat(auctionId(itemId, connection), null);
        this.notTobeGCd = chat;
        
        //XMPPAuction is the first step to make entire structure more clean and reasonable
        Auction auction = new XMPPAuction(chat);       
        
        //SniperStateDisplayer is another step to make entire structure more clean and reasonable
        //So we can move sniperLost and sniperBidding to better places
        //Ch14, p.142, add connection.getUser()
        //Ch15, p.168, adjust constructor of AuctionSniper
        chat.addMessageListener(
                new AuctionMessageTranslator(
                        connection.getUser(), 
                        new AuctionSniper(itemId, auction, 
                                new SwingThreadSniperListener(snipers))));
        auction.join();
    }
    
    //Ch16, p.180
    private void safelyAddItemToModel(final String itemId) throws Exception {
        SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {
                snipers.addSniper(SniperSnapshot.joining(itemId));
            }
        });  
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
    //Ch15, p.168 rename
    public class SwingThreadSniperListener implements SniperListener {     
        private final SniperListener delegate;
        public SwingThreadSniperListener(SniperListener delegate) {
          this.delegate = delegate;
        } 
        //Ch15, p.168
        public void sniperStateChanged(final SniperSnapshot snapshot) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    delegate.sniperStateChanged(snapshot);
                }
            });  
        }
    }
}
