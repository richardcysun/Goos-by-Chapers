package test.endtoend.auctionsniper;

import org.junit.After;
import org.junit.Test;

//Ch10, p.85
public class AuctionSniperEndToEndTest {
    private final FakeAuctionServer auction = new FakeAuctionServer("item-54321");
    private final ApplicationRunner application = new ApplicationRunner();

    @Test public void sniperJoinsAuctionUntilAuctionCloses() throws Exception {
        auction.startSellingItem();                 //Step 1
        
        //If the UI doesn't show "Joining", throw exception i think
        application.startBiddingIn(auction);        //Step 2
        
        //If fake auction server doesn't receive any message in 5 seconds, assert!
        auction.hasReceivedJoinRequestFromSniper(); //Step 3
        auction.announceClosed();                   //Step 4
        
        //If the UI doesn't show "Lost", throw exception i think
        application.showSniperHasLostAuction();     //Step 5
    }
    
    @After public void stopAuction() {
        auction.stop();
    }
    
    @After public void stopApplication() {
        application.stop();
    }
}
