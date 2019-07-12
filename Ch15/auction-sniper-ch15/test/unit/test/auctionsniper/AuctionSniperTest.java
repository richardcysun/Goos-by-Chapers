package test.auctionsniper;

import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.States;
import org.jmock.integration.junit4.JMock;
import org.junit.Test;
import org.junit.runner.RunWith;

import auctionsniper.Auction;
import auctionsniper.AuctionEventListener.PriceSource;
import auctionsniper.AuctionSniper;
import auctionsniper.SniperListener;
import auctionsniper.SniperSnapshot;
import auctionsniper.SniperState;

import static org.hamcrest.Matchers.*;

//Ch13, p.124
@RunWith(JMock.class)
public class AuctionSniperTest {
    private final String ITEM_ID = "";
    private final Mockery context = new Mockery();
    //SniperListener is an interface, be noticed
    private final SniperListener sniperListener = context.mock(SniperListener.class);
    private final Auction auction = context.mock(Auction.class);
    private final AuctionSniper sniper = new AuctionSniper(ITEM_ID, auction, sniperListener); 
    private final States sniperState = context.states("sniper");
    
    @Test public void reportsLostWhenAuctionCloses() {
        context.checking(new Expectations() {
            {
                one(sniperListener).sniperLost();                   
                }
            });
        sniper.auctionClosed();
    }
    
    //Ch13, p.127
    @Test public void bidsHigherAndReportsBiddingWhenNewPriceArrives() {
        final int price = 1001;
        final int increment = 25;
        final int bid = price + increment;
        context.checking(new Expectations() {
            {
                one(auction).bid(bid);
                //Ch15, p.155
                atLeast(1).of(sniperListener).sniperStateChanged(
                        new SniperSnapshot(ITEM_ID, price, bid, SniperState.BIDDING));                
            }
        });
        //Set FromOtherBidder due to interface enhancement of currentPrice
        //It should be FromOtherBidder so Sniper can bid higher price
        sniper.currentPrice(price, increment, PriceSource.FromOtherBidder);
    }
    
    //Ch14, p.143
    //CH15, p.162 revised
    @Test public void reportsIsWinningWhenCurrentPriceComesFromSniper() {
        context.checking(new Expectations() {
            {
                ignoring(auction);
                allowing(sniperListener).sniperStateChanged(with(aSniperThatIs(SniperState.BIDDING)));
                    then(sniperState.is("bidding"));
                    
                atLeast(1).of(sniperListener).sniperStateChanged(
                        new SniperSnapshot(ITEM_ID, 135, 135, SniperState.WINNING));
                    when(sniperState.is("bidding"));
            }
        });
        sniper.currentPrice(123, 12, PriceSource.FromOtherBidder);
        sniper.currentPrice(135, 45, PriceSource.FromSniper);
    }
    
    //Ch14, p.145
    @Test public void reportsLostIfAuctionClosesImmediately() {
        context.checking(new Expectations() {
            {
                atLeast(1).of(sniperListener).sniperStateChanged(new SniperSnapshot(ITEM_ID, 0, 0, SniperState.LOST));
            }
        });
        sniper.auctionClosed();
    }
    
    @Test public void reportsLostIfAuctionClosesWhenBidding() {
        context.checking(new Expectations() {
            {
                ignoring(auction);
                //Ch15, p.155
                //Ch15, p.161 revised
                allowing(sniperListener).sniperStateChanged(with(aSniperThatIs(SniperState.BIDDING)));
                    then(sniperState.is("bidding"));
                atLeast(1).of(sniperListener).sniperStateChanged(new SniperSnapshot(ITEM_ID, 0, 0, SniperState.LOST));
                    when(sniperState.is("bidding"));
            }
        });
        sniper.currentPrice(123, 45, PriceSource.FromOtherBidder);
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
    @Test public void reportsWonIfAuctionClosesWhenWining() {
        context.checking(new Expectations() {
            {
                ignoring(auction);
                allowing(sniperListener).sniperStateChanged(with(aSniperThatIs(SniperState.WINNING)));
                    then(sniperState.is("winning"));
                atLeast(1).of(sniperListener).sniperStateChanged(new SniperSnapshot(ITEM_ID, 0, 0, SniperState.WON));
                    when(sniperState.is("winning"));
            }
        });
        sniper.currentPrice(123, 45, PriceSource.FromSniper);
        sniper.auctionClosed();
    }        
}
