package test.endtoend.auctionsniper;

import auctionsniper.Main;
import auctionsniper.ui.MainWindow;

import static test.endtoend.auctionsniper.FakeAuctionServer.XMPP_HOSTNAME;

//Ch11, p.90
public class ApplicationRunner {

    public static final String SNIPER_ID = "sniper";
    public static final String SNIPER_PASSWORD = "sniper";
	public static final String SNIPER_XMPP_ID = "sniper@localhost/Auction";

    private AuctionSniperDriver driver;

    //Drive Main to login OpenFire Chat with "sniper/sniper"
    public void startBiddingIn(final FakeAuctionServer auction) {
        Thread thread = new Thread("Test Application") {
        @Override public void run() {
            try {
                //Application Runner drives Main of production codes
                Main.main(XMPP_HOSTNAME, SNIPER_ID, SNIPER_PASSWORD, auction.getItemId());
            }//try
            catch (Exception e) {
               e.printStackTrace(); 
            }//catch
        }//run
    };//thread
    thread.setDaemon(true);
    thread.start();
    
    //Check WindowLicker swing queue every seconds
    driver = new AuctionSniperDriver(1000);
    //if the "Joining" is appeared on Main's UI
    driver.showsSniperStatus(MainWindow.STATUS_JOINING);
    }

    public void showsSniperHasLostAuction() {
        //if the "Lost" is appeared on UI
        driver.showsSniperStatus(MainWindow.STATUS_LOST);
    }
    
    public void stop() {
        if (driver != null) {
            driver.dispose();
        }
    }

    //Ch12, p.110
	public void hasShownSniperIsBidding() {
		driver.showsSniperStatus(MainWindow.STATUS_BIDDING);
	}

    public void hasShownSniperIsWinning() {
        driver.showsSniperStatus(MainWindow.STATUS_WINNING);
    }

    public void showsSniperHasWonAuction() {
        driver.showsSniperStatus(MainWindow.STATUS_WON);
    }
}
