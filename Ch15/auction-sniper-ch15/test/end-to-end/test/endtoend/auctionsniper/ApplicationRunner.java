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
    private String itemId;

    //Drive Main to login OpenFire Chat with "sniper/sniper"
    public void startBiddingIn(final FakeAuctionServer auction) {
        itemId = auction.getItemId();
        
        Thread thread = new Thread("Test Application") {
            @Override public void run() {
                try {
                    //Application Runner drives Main of production codes
                    Main.main(XMPP_HOSTNAME, SNIPER_ID, SNIPER_PASSWORD, itemId);
                }//try
                catch (Exception e) {
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
        //if the "Joining" is appeared on Main's UI
        driver.showsSniperStatus("", 0, 0, SnipersTableModel.textFor(SniperState.JOINING));
    }

    public void showsSniperHasLostAuction(int lastPrice, int lastBid) {
        //if the "Lost" is appeared on UI
        //driver.showsSniperStatus(MainWindow.STATUS_LOST);
        driver.showsSniperStatus(itemId, lastPrice, lastBid, SnipersTableModel.textFor(SniperState.LOST));
    }
    
    public void stop() {
        if (driver != null) {
            driver.dispose();
        }
    }

    //Ch12, p.110
    /*public void hasShownSniperIsBidding() {
        driver.showsSniperStatus(MainWindow.STATUS_BIDDING);
    }*/
    
    //Ch15, p.153
	public void hasShownSniperIsBidding(int lastPrice, int lastBid) {
		driver.showsSniperStatus(itemId, lastPrice, lastBid, MainWindow.STATUS_BIDDING);
	}

    public void hasShownSniperIsWinning(int winningBid) {
        driver.showsSniperStatus(itemId, winningBid, winningBid, MainWindow.STATUS_WINNING);
    }

    public void showsSniperHasWonAuction(int lastPrice) {
        driver.showsSniperStatus(itemId, lastPrice, lastPrice, MainWindow.STATUS_WON);
    }
}
