package test.auctionsniper;

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
import auctionsniper.SniperState;

//Ch13, p.124
@RunWith(JMock.class)
public class AuctionSniperTest {
    private final Mockery context = new Mockery();
    //SniperListener is an interface, be noticed
    private final SniperListener sniperListener = context.mock(SniperListener.class);
    private final Auction auction = context.mock(Auction.class);
    private final AuctionSniper sniper = new AuctionSniper(auction, sniperListener); 
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
        final String ITEM_ID = "item-54321";
        final int price = 1001;
        final int increment = 25;
        final int bid = price + increment;
        context.checking(new Expectations() {
            {
                one(auction).bid(bid);
                //Ch15, p.155
                atLeast(1).of(sniperListener).sniperBidding(new SniperState(ITEM_ID, price, bid));
            }
        });
        //Set FromOtherBidder due to interface enhancement of currentPrice
        //It should be FromOtherBidder so Sniper can bid higher price
        sniper.currentPrice(price, increment, PriceSource.FromOtherBidder);
    }
    
    //Ch14, p.143
    @Test public void reportsIsWinningWhenCurrentPriceComesFromSniper() {
        context.checking(new Expectations() {
            {
                atLeast(1).of(sniperListener).sniperWinning();
            }
        });
        sniper.currentPrice(123, 45, PriceSource.FromSniper);
    }
    
    //Ch14, p.145
    @Test public void reportsLostIfAuctionClosesImmediately() {
        context.checking(new Expectations() {
            {
                atLeast(1).of(sniperListener).sniperLost();
            }
        });
        sniper.auctionClosed();
    }
    
    @Test public void reportsLostIfAuctionClosesWhenBidding() {
        context.checking(new Expectations() {
            {
                ignoring(auction);
                //Ch15, p.155
                allowing(sniperListener).sniperBidding(with(any(SniperState.class)));
                    then(sniperState.is("bidding"));
                atLeast(1).of(sniperListener).sniperLost();
                    when(sniperState.is("bidding"));
            }
        });
        sniper.currentPrice(123, 45, PriceSource.FromOtherBidder);
        sniper.auctionClosed();
    }    
    
    //Ch14, p.147
    @Test public void reportsWonIfAuctionClosesWhenWining() {
        context.checking(new Expectations() {
            {
                ignoring(auction);
                allowing(sniperListener).sniperWinning();
                    then(sniperState.is("winning"));
                atLeast(1).of(sniperListener).sniperWon();
                    when(sniperState.is("winning"));
            }
        });
        sniper.currentPrice(123, 45, PriceSource.FromSniper);
        sniper.auctionClosed();
    }        
}
