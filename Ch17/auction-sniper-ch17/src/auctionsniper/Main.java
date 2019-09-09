package auctionsniper;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.SwingUtilities;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import auctionsniper.ui.MainWindow;
import auctionsniper.ui.SnipersTableModel;
import auctionsniper.xmpp.XMPPAuction;
import auctionsniper.xmpp.XMPPAuctionHouse;

//Ch13, p.125 replace AuctionEventListener with SniperListener
public class Main {
    @SuppressWarnings("unused") private Auction notTobeGCd;
    private final SnipersTableModel snipers = new SnipersTableModel();
    private MainWindow ui;
    
    private static final int ARG_HOSTNAME = 0;
    private static final int ARG_USERNAME = 1;
    private static final int ARG_PASSWORD = 2;

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
        //Ch17, p.
        XMPPAuctionHouse auctionHouse = XMPPAuctionHouse.connect(args[ARG_HOSTNAME], args[ARG_USERNAME], args[ARG_PASSWORD]);
        main.disconnectWhenUICloses(auctionHouse);
        main.addUserRequestListenerFor(auctionHouse);
    }

    //Ch16, p188
    private void addUserRequestListenerFor(final AuctionHouse auctionHouse) {
		ui.addUserRequestListener(new UserRequestListener() {
			public void joinAuction(String itemId) {
				snipers.addSniper(SniperSnapshot.joining(itemId));
				
				Auction auction = auctionHouse.auctionFor(itemId);	        
				notTobeGCd = auction;
				
				auction.addAuctionEventListener(new AuctionSniper(itemId, auction, 
                        new SwingThreadSniperListener(snipers)));
				
		        auction.join();				
			}
		});
		
	}

	private void disconnectWhenUICloses(final XMPPAuctionHouse auctionHouse) {
    	ui.addWindowListener(new WindowAdapter() {
    		@Override public void windowClosed(WindowEvent e) {
    			auctionHouse.disconnect();
    		}
    	});
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
