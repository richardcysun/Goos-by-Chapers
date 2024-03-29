package test.integration.auctionsniper.xmpp;


import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.CountDownLatch;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import auctionsniper.Auction;
import auctionsniper.AuctionEventListener;
import auctionsniper.xmpp.XMPPAuction;
import auctionsniper.xmpp.XMPPAuctionHouse;
import test.endtoend.auctionsniper.ApplicationRunner;
import test.endtoend.auctionsniper.FakeAuctionServer;

//Ch17, p.195, @Before and @After are not in the book
public class XMPPAuctionTest {
	private final FakeAuctionServer auctionServer = new FakeAuctionServer("item-54321");
	private XMPPAuctionHouse auctionHouse;
	
	@Before public void openConnection() throws XMPPException
	{
		auctionHouse = XMPPAuctionHouse.connect(FakeAuctionServer.XMPP_HOSTNAME,
				ApplicationRunner.SNIPER_ID, ApplicationRunner.SNIPER_PASSWORD);
	}
	
	@After public void closeConnection()
	{
		auctionHouse.disconnect();
	}
	
	@Before public void startAuction() throws XMPPException
	{
		auctionServer.startSellingItem();
	}
	
	@After public void stopAuction()
	{
		auctionServer.stop();
	}
	
	@Test public void receivesEventsFromAuctionServerAfterJoining() throws Exception {
		CountDownLatch auctionWasClosed = new CountDownLatch(1);
		
		Auction auction = auctionHouse.auctionFor(auctionServer.getItemId());
		auction.addAuctionEventListener(auctionClosedListener(auctionWasClosed));
		
		auction.join();
		auctionServer.hasReceivedJoinRequestFrom(ApplicationRunner.SNIPER_XMPP_ID);
		auctionServer.announceClosed();
		
		assertTrue("should have been closed", auctionWasClosed.await(2, SECONDS));
	}

	private AuctionEventListener auctionClosedListener(final CountDownLatch auctionWasClosed) {
		return new AuctionEventListener() {
			public void auctionClosed() {
				auctionWasClosed.countDown();
			}
			public void currentPrice(int price, int increment, PriceSource proceSource) {
				//not implemented
			}
		};
	}
}
