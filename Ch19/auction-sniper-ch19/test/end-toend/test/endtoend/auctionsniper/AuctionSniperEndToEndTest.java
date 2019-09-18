package test.endtoend.auctionsniper;

import org.junit.After;
import org.junit.Test;

public class AuctionSniperEndToEndTest {
    private final FakeAuctionServer auction = new FakeAuctionServer("item-54321");
    private final FakeAuctionServer auction2 = new FakeAuctionServer("item-65432");
    private final ApplicationRunner application = new ApplicationRunner();

    private void holdOn()
    {
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace(); 
        }   
    }

    //Ch10, p.85
    @Test public void sniperJoinsAuctionUntilAuctionCloses() throws Exception {
        auction.startSellingItem();                 //Step 1
        
        //If the UI doesn't show "Joining", throw exception i think
        application.startBiddingIn(auction);        //Step 2

        //If fake auction server doesn't receive any message in 5 seconds, assert!
        auction.hasReceivedJoinRequestFromSniper(); //Step 3
        auction.announceClosed();                   //Step 4
        holdOn();
        //revise this function based on the hint of Ch16, p.176
        application.showsSniperHasLostAuction(auction, 0, 0);     //Step 5
        holdOn();
    }
    
    //Ch12, p.106
    @Test public void sniperMakesAHigherBidButLoses() throws Exception {
    	auction.startSellingItem();                 //Step 1
    	
    	application.startBiddingIn(auction);        //Step 2
    	//Ch12, p.109, revise Ch12, p.106
        auction.hasReceivedJoinRequestFrom(ApplicationRunner.SNIPER_XMPP_ID); //Step 3
        holdOn();
    	auction.reportPrice(1000, 98, "other bidder"); //Step 4
    	
    	//In Ch12, the Main is not bidding, the test fails here
    	application.hasShownSniperIsBidding(auction, 1000, 1098);     //Step 5
    	holdOn();
    	auction.hasReceivedBid(1098, ApplicationRunner.SNIPER_XMPP_ID);	//Step 6
    	holdOn();
    	auction.announceClosed();                  //Step 7
    	//revise this function based on the hint of Ch16, p.176
    	application.showsSniperHasLostAuction(auction, 1000, 1098);   //Step 8    	
    	holdOn();
    }

    //Ch14, p.140
    //Ch15, p.152, revise
    @Test public void sniperWinsAnAuctionByBiddingHigher() throws Exception {
        auction.startSellingItem();                 //Step 1
        
        application.startBiddingIn(auction);        //Step 2
        auction.hasReceivedJoinRequestFrom(ApplicationRunner.SNIPER_XMPP_ID); //Step 3
        holdOn();
        auction.reportPrice(1000, 98, "other bidder");  //Step 4
        application.hasShownSniperIsBidding(auction, 1000, 1098);//Step 5, last price, last bid
        holdOn();
        auction.hasReceivedBid(1098, ApplicationRunner.SNIPER_XMPP_ID); //Step 6
        holdOn();
        auction.reportPrice(1098, 97, ApplicationRunner.SNIPER_XMPP_ID);  //Step 7
        application.hasShownSniperIsWinning(auction, 1098);  //Step 8, winning bid
        holdOn();
        auction.announceClosed();                   //Step 9
        application.showsSniperHasWonAuction(auction, 1098); //Step 10, last price
        holdOn();
    }
    
    //Ch16, p.176
    @Test public void sniperBidsForMultipleItems() throws Exception {
        auction.startSellingItem();                 //Step 1
        auction2.startSellingItem();             	//Step 2
        
        application.startBiddingIn(auction, auction2);	//Step 3
        holdOn();
        auction.hasReceivedJoinRequestFrom(ApplicationRunner.SNIPER_XMPP_ID);	//Step 4
        auction2.hasReceivedJoinRequestFrom(ApplicationRunner.SNIPER_XMPP_ID);	//Step 5
        holdOn();
        auction.reportPrice(1000, 98, "other bidder");	//Step 6
        auction.hasReceivedBid(1098, ApplicationRunner.SNIPER_XMPP_ID);	//Step 7
        holdOn();
        auction2.reportPrice(500, 21, "other bidder");	//Step 8
        auction2.hasReceivedBid(521, ApplicationRunner.SNIPER_XMPP_ID); //Step 9   
        holdOn();
        auction.reportPrice(1098, 97, ApplicationRunner.SNIPER_XMPP_ID);  //Step 10
        holdOn();
        auction2.reportPrice(521, 22, ApplicationRunner.SNIPER_XMPP_ID);  //Step 11
        application.hasShownSniperIsWinning(auction, 1098);  //Step 12
        application.hasShownSniperIsWinning(auction2, 521);  //Step 13
        holdOn();
        auction.announceClosed();              		//Step 14
        holdOn();
        auction2.announceClosed();                 	//Step 15
        application.showsSniperHasWonAuction(auction, 1098);//Step 16
        application.showsSniperHasWonAuction(auction2, 521);//Step 17
        holdOn();
    }

    //Ch18, p.206
    @Test public void sniperLosesAnAuctionWhenThePriceIsTooHigh() throws Exception {
    	auction.startSellingItem();                 			//Step 1
    	application.startBiddingWithStopPrice(auction, 1100);	//Step 2
    	holdOn();
    	auction.hasReceivedJoinRequestFrom(ApplicationRunner.SNIPER_XMPP_ID);	//Step 3
    	holdOn();
    	auction.reportPrice(1000, 98, "other bidder");			//Step 4
    	application.hasShownSniperIsBidding(auction, 1000, 1098);//Step 5, last price, last bid
    	holdOn();
    	auction.hasReceivedBid(1098, ApplicationRunner.SNIPER_XMPP_ID); //Step 6
    	
    	auction.reportPrice(1197, 10, "third party");			//Step 7
    	application.hasShownSniperIsLosing(auction, 1197, 1098);//Step 8
    	holdOn();
    	auction.reportPrice(1207, 10, "forth party");			//Step 9
    	application.hasShownSniperIsLosing(auction, 1207, 1098);//Step 10
    	holdOn();
    	auction.announceClosed();              					//Step 11
        holdOn();
        application.showsSniperHasLostAuction(auction, 1207, 1098); //Step 12
    }

    //Ch19, p.216
    @Test public void sniperReportsInvalidAuctionMessageAndStopsRespondingToEvents() throws Exception {
    	String brokenMessage = "a broken message";
        auction.startSellingItem();                 	//Step 1
        auction2.startSellingItem();             		//Step 2
        
        application.startBiddingIn(auction, auction2);	//Step 3
        holdOn();
        auction.hasReceivedJoinRequestFrom(ApplicationRunner.SNIPER_XMPP_ID);	//Step 4        
        auction.reportPrice(500, 20, "other bidder");	//Step 5
        holdOn();
        auction.hasReceivedBid(520, ApplicationRunner.SNIPER_XMPP_ID); //Step 6
        holdOn();
        auction.sendInvalidMessageContaining(brokenMessage);	//Step 7
        application.showsSniperHasFailed(auction);		//Step 8
        holdOn();
        auction.reportPrice(520, 21, "other bidder");	//Step 9
        waitForAnotherAuctionEvent();					//Step 10
        holdOn();
        application.reportsInvalidMessage(auction, brokenMessage);	//Step 11
        application.showsSniperHasFailed(auction);		//Step 12
        auction2.announceClosed();
    }
    
    //Ch19, p.216
    private void waitForAnotherAuctionEvent() throws Exception{
    	auction2.hasReceivedJoinRequestFrom(ApplicationRunner.SNIPER_XMPP_ID);
    	auction2.reportPrice(600, 6, "other bidder");
    	application.hasShownSniperIsBidding(auction2, 600, 606);
	}

	@After public void stopAuction() {
        auction.stop();
        auction2.stop();
    }
    
    @After public void stopApplication() {
        application.stop();
    }
}
