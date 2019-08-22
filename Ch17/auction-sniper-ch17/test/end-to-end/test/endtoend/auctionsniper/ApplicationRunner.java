package test.endtoend.auctionsniper;

import auctionsniper.Main;
import auctionsniper.SniperState;
import auctionsniper.ui.MainWindow;
import auctionsniper.ui.SnipersTableModel;

import static test.endtoend.auctionsniper.FakeAuctionServer.XMPP_HOSTNAME;

//Ch11, p.90
public class ApplicationRunner {

    public static final String SNIPER_ID = "sniper";
    public static final String SNIPER_PASSWORD = "sniper";
	public static final String SNIPER_XMPP_ID = "sniper@localhost/Auction";

    private AuctionSniperDriver driver;

    //Drive Main to login OpenFire Chat with "sniper/sniper"
    public void startBiddingIn(final FakeAuctionServer... auctions) {
    	startSniper(auctions);
        
    	for (FakeAuctionServer auction : auctions) {
    		final String itemId = auction.getItemId();
    		driver.startBiddingFor(itemId);
    		driver.showsSniperStatus(itemId, 0, 0, SnipersTableModel.textFor(SniperState.JOINING));
    	}
        
    }

    //Ch16, p.184
    private void startSniper(final FakeAuctionServer... auctions) {
        Thread thread = new Thread("Test Application") {
            @Override public void run() {
                try {
                    //Application Runner drives Main of production codes
                    Main.main(arguments(auctions));
                }//try
                catch (Throwable e) {
                    e.printStackTrace(); 
                }//catch
            }//run
        };//thread
        
        thread.setDaemon(true);
        thread.start();
    
        //Check WindowLicker swing queue every seconds
        //Ch15, p.169
        driver = new AuctionSniperDriver(1000);
        driver.hasTitle(MainWindow.APPLICATION_TITLE);
        driver.hasColumnTitles();    	
    }
    
    //Ch16, p.177
    protected static String[] arguments(FakeAuctionServer... auctions) {
    	String[] arguments = new String[auctions.length + 3];
    	arguments[0] = XMPP_HOSTNAME;
    	arguments[1] = SNIPER_ID;
    	arguments[2] = SNIPER_PASSWORD;
    	
    	for (int i = 0; i < auctions.length; i++) {
    		arguments[i + 3] = auctions[i].getItemId();
    	}   	
    	return arguments;
    }

    //revise this function based on the hint of Ch16, p.176
    public void showsSniperHasLostAuction(FakeAuctionServer auction, int lastPrice, int lastBid) {
        driver.showsSniperStatus(auction.getItemId(), lastPrice, lastBid, SnipersTableModel.textFor(SniperState.LOST));
    }
    
    public void stop() {
        if (driver != null) {
            driver.dispose();
        }
    }
    
    //Ch15, p.153
    //Ch16, p.176 revised
	public void hasShownSniperIsBidding(FakeAuctionServer auction, int lastPrice, int lastBid) {
		driver.showsSniperStatus(auction.getItemId(), lastPrice, lastBid, MainWindow.STATUS_BIDDING);
	}

    public void hasShownSniperIsWinning(FakeAuctionServer auction, int winningBid) {
        driver.showsSniperStatus(auction.getItemId(), winningBid, winningBid, MainWindow.STATUS_WINNING);
    }

    public void showsSniperHasWonAuction(FakeAuctionServer auction, int lastPrice) {
        driver.showsSniperStatus(auction.getItemId(), lastPrice, lastPrice, MainWindow.STATUS_WON);
    }
}
