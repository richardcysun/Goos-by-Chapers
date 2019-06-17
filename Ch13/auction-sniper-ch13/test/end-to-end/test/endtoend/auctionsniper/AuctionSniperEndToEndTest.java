package test.endtoend.auctionsniper;

import org.junit.After;
import org.junit.Test;

public class AuctionSniperEndToEndTest {
    private final FakeAuctionServer auction = new FakeAuctionServer("item-54321");
    private final ApplicationRunner application = new ApplicationRunner();

  //Ch10, p.85
    @Test public void sniperJoinsAuctionUntilAuctionCloses() throws Exception {
        auction.startSellingItem();                 //Step 1
        
        //If the UI doesn't show "Joining", throw exception i think
        application.startBiddingIn(auction);        //Step 2
        
        //If fake auction server doesn't receive any message in 5 seconds, assert!
        auction.hasReceivedJoinRequestFromSniper(); //Step 3
        auction.announceClosed();                   //Step 4
        
        //If the UI doesn't show "Lost", throw exception i think
        application.showsSniperHasLostAuction();     //Step 5
    }
    
    //Ch12, p.106
    @Test public void sniperMakesAHigherBidButLoses() throws Exception {
    	auction.startSellingItem();                 //Step 1
    	application.startBiddingIn(auction);        //Step 2

    	//Ch12, p.109, revise Ch12, p.106
        auction.hasReceivedJoinRequestFrom(ApplicationRunner.SNIPER_XMPP_ID); //Step 3
    	
    	auction.reportPrice(1000, 98, "other bidder");	//Step 4
    	
    	//In Ch12, the Main is not bidding, the test fails here
    	application.hasShownSniperIsBidding();		//Step 5
    	
    	auction.hasReceivedBid(1098, ApplicationRunner.SNIPER_XMPP_ID);	//Step 6
    	
    	auction.announceClosed();                   //Step 7
        application.showsSniperHasLostAuction();     //Step 8    	
    }
    
    @After public void stopAuction() {
        auction.stop();
    }
    
    @After public void stopApplication() {
        application.stop();
    }
}
