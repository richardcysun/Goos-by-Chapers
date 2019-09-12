package auctionsniper;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.SwingUtilities;

import auctionsniper.ui.MainWindow;
import auctionsniper.xmpp.XMPPAuctionHouse;

//Ch13, p.125 replace AuctionEventListener with SniperListener
public class Main {
    //Ch17, p.200
    //private final SnipersTableModel snipers = new SnipersTableModel();
    private final SniperPortfolio portfolio = new SniperPortfolio();
    private MainWindow ui;
    
    private static final int ARG_HOSTNAME = 0;
    private static final int ARG_USERNAME = 1;
    private static final int ARG_PASSWORD = 2;

	//Ch15, p.168
    public Main() throws Exception {
        SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {
                ui = new MainWindow(portfolio);
            }
        });          
    }

    public static void main(String... args) throws Exception {
        Main main = new Main();
        //Ch17, p.196
        XMPPAuctionHouse auctionHouse = XMPPAuctionHouse.connect(args[ARG_HOSTNAME], args[ARG_USERNAME], args[ARG_PASSWORD]);
        main.disconnectWhenUICloses(auctionHouse);
        main.addUserRequestListenerFor(auctionHouse);
    }

    //Ch16, p188
    private void addUserRequestListenerFor(final AuctionHouse auctionHouse) {
    	//Ch17, p.200
    	ui.addUserRequestListener(new SniperLauncher(auctionHouse, portfolio));		
	}

	private void disconnectWhenUICloses(final XMPPAuctionHouse auctionHouse) {
    	ui.addWindowListener(new WindowAdapter() {
    		@Override public void windowClosed(WindowEvent e) {
    			auctionHouse.disconnect();
    		}
    	});
	}  
}
