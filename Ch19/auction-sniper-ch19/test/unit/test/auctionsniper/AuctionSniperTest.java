package test.auctionsniper;

import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.States;
import org.jmock.integration.junit4.JMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import auctionsniper.Auction;
import auctionsniper.AuctionEventListener.PriceSource;
import auctionsniper.AuctionSniper;
import auctionsniper.Item;
import auctionsniper.SniperListener;
import auctionsniper.SniperSnapshot;
import auctionsniper.SniperState;

import static org.hamcrest.Matchers.*;

import java.util.logging.Logger;

import static auctionsniper.SniperState.BIDDING;
import static auctionsniper.SniperState.WINNING;
import static auctionsniper.SniperState.LOSING;
import static auctionsniper.SniperState.LOST;
import static auctionsniper.SniperState.WON;
import static auctionsniper.SniperState.FAILED;

//Ch13, p.124
@RunWith(JMock.class)
public class AuctionSniperTest {
    private static final String ITEM_ID = "item-id";
    public static final Item ITEM = new Item(ITEM_ID, 1234);
    private final Mockery context = new Mockery();
    //SniperListener is an interface, be noticed
    private final SniperListener sniperListener = context.mock(SniperListener.class);
    private final Auction auction = context.mock(Auction.class);
    //Ch18, revised, not in the book
    private AuctionSniper sniper = new AuctionSniper(ITEM, auction); 
    private final States sniperState = context.states("sniper");
    private final Logger logger = Logger.getLogger("MyLog");
    
    //Ch17, not in the book
    @Before public void addSniper()
    {
    	sniper.addSniperListener(sniperListener);
    }
    
    @Test public void reportsLostWhenAuctionCloses() {
        logger.info(">>>");
        context.checking(new Expectations() {
            {
                one(sniperListener).sniperStateChanged(new SniperSnapshot(ITEM_ID, 0, 0, LOST));                   
                }
            });
        //This auctionClosed() triggers SniperSnapshot(itemId=item-id,lastPrice=0,lastBid=0,state=LOST)
        //because the initial state is JOINING!
        sniper.auctionClosed();
        logger.info("<<<");
    }
    
    //Ch13, p.127
    @Test public void bidsHigherAndReportsBiddingWhenNewPriceArrives() {
        final int price = 1001;
        final int increment = 25;
        final int bid = price + increment;
        logger.info(">>>");
        context.checking(new Expectations() {
            {
                one(auction).bid(bid);
                //Ch15, p.155
                atLeast(1).of(sniperListener).sniperStateChanged(
                        new SniperSnapshot(ITEM_ID, price, bid, BIDDING));                
            }
        });
        //Due to FromOtherBidder, AuctionSniper bids higher price 
        sniper.currentPrice(price, increment, PriceSource.FromOtherBidder);
        logger.info("<<<");
    }
    
    //Ch14, p.143
    //CH15, p.162 revised
    @Test public void reportsIsWinningWhenCurrentPriceComesFromSniper() {
    	logger.info(">>>");
        context.checking(new Expectations() {
            {
                ignoring(auction);
                allowing(sniperListener).sniperStateChanged(with(aSniperThatIs(BIDDING)));
                    then(sniperState.is("bidding"));
                    
                atLeast(1).of(sniperListener).sniperStateChanged(
                        new SniperSnapshot(ITEM_ID, 135, 135, WINNING));
                    when(sniperState.is("bidding"));
            }
        });
        //This currentPrice() from other bidder makes AuctionSniper creates SniperSnapshot(itemId=item-id,lastPrice=123,lastBid=135,state=BIDDING)
        sniper.currentPrice(123, 12, PriceSource.FromOtherBidder);
        //This currentPrice() from AuctionSniper creates SniperSnapshot(itemId=item-id,lastPrice=135,lastBid=135,state=WINNING)
        sniper.currentPrice(135, 45, PriceSource.FromSniper);
        logger.info("<<<");
    }
    
    //Ch14, p.145
    @Test public void reportsLostIfAuctionClosesImmediately() {
    	logger.info(">>>");
        context.checking(new Expectations() {
            {
                atLeast(1).of(sniperListener).sniperStateChanged(new SniperSnapshot(ITEM_ID, 0, 0, LOST));  
            }
        });
        //This auctionClosed() triggers SniperSnapshot(itemId=item-id,lastPrice=0,lastBid=0,state=LOST)
        //because the initial state is JOINING!        
        sniper.auctionClosed();
        logger.info("<<<");
    }
    
    @Test public void reportsLostIfAuctionClosesWhenBidding() {
        context.checking(new Expectations() {
            {
                ignoring(auction);
                //Ch15, p.155
                //Ch15, p.161 revised
                allowing(sniperListener).sniperStateChanged(with(aSniperThatIs(BIDDING)));
                    then(sniperState.is("bidding"));
                atLeast(1).of(sniperListener).sniperStateChanged(new SniperSnapshot(ITEM_ID, 123, 168, LOST));
                    when(sniperState.is("bidding"));
            }
        });
        //In the currentPrice(), it create a SniperSnapshot(itemId=item-id,lastPrice=123,lastBid=168,state=BIDDING)
        sniper.currentPrice(123, 45, PriceSource.FromOtherBidder);
        //In the auctionClosed(), it creates a SniperSnapshot(itemId=item-id,lastPrice=123,lastBid=168,state=LOST)
        //In the case of BIDDING, auctionClosed() triggers LOST!
        //Therefore, in above atLeast(1), only SniperSnapshot(ITEM_ID, 123, 168, LOST) can pass the test        
        sniper.auctionClosed();
    }    
    
    //Ch15, p.161
    private Matcher<SniperSnapshot> aSniperThatIs(final SniperState state) {
        return new FeatureMatcher<SniperSnapshot, SniperState>(
                equalTo(state), "sniper that is", "was")
                {
                    @Override
                    protected SniperState featureValueOf(SniperSnapshot actual) {
                        return actual.state;
                    }
                };
    }

    //Ch14, p.147
    @Test public void reportsWonIfAuctionClosesWhenWinning() {
    	logger.info(">>>");
        context.checking(new Expectations() {
            {
                ignoring(auction);
                allowing(sniperListener).sniperStateChanged(with(aSniperThatIs(WINNING)));
                    then(sniperState.is("winning"));
                atLeast(1).of(sniperListener).sniperStateChanged(new SniperSnapshot(ITEM_ID, 123, 0, WON));
                    when(sniperState.is("winning"));
            }
        });
        //In the currentPrice(), it create a SniperSnapshot(itemId=item-id,lastPrice=123,lastBid=0,state=WINNING)
        sniper.currentPrice(123, 45, PriceSource.FromSniper);
        //In the auctionClosed(), it creates a SniperSnapshot(itemId=item-id,lastPrice=123,lastBid=0,state=WON)
        //In the case of WINNING, auctionClosed() triggers WON!
        //Therefore, in above atLeast(1), only SniperSnapshot(ITEM_ID, 123, 0, WON) can pass the test
        sniper.auctionClosed();
        logger.info("<<<");
    }     
    
    //Ch18, p.210
    @Test public void doesNotBidAndReportsLosingIfSubsequentPriceIsAboveStopPrice() {
    	logger.info(">>>");
    	allowingSniperBidding();
    	context.checking(new Expectations() {
    		{
    			int bid = 123 + 45;
    			allowing(auction).bid(bid);
    			atLeast(1).of(sniperListener).sniperStateChanged(new SniperSnapshot(ITEM_ID, 2345, bid, LOSING));
    				when(sniperState.is("bidding"));
    		}
    	});
    	sniper.currentPrice(123, 45, PriceSource.FromOtherBidder);
    	sniper.currentPrice(2345, 25, PriceSource.FromOtherBidder);
    	logger.info("<<<");
    }
    
    private void allowingSniperBidding() {
    	context.checking(new Expectations() {
    		{
    			allowing(sniperListener).sniperStateChanged(with(aSniperThatIs(BIDDING)));
    				then(sniperState.is("bidding"));
    		}
    	});
    }
    
    //Ch19, p.218
    @Test public void reportsFailedIfAuctionFailsWhenBidding() {
    	ignoringAuction();
    	allowingSniperBidding();
    	
    	expectSniperToFailWhenItIs("bidding");
    	
    	sniper.currentPrice(123, 45, PriceSource.FromOtherBidder);
    	sniper.auctionFailed();
    }    
    
    private void expectSniperToFailWhenItIs(final String state) {
    	context.checking(new Expectations() {
    		{
    			atLeast(1).of(sniperListener).sniperStateChanged(new SniperSnapshot(ITEM_ID, 0, 0, FAILED));
					when(sniperState.is(state));
    		}
    	});		
	}

	//Ch19, not in the book 
    private void ignoringAuction() {
        context.checking(new Expectations() {{ 
          ignoring(auction);
        }});
      }
}
