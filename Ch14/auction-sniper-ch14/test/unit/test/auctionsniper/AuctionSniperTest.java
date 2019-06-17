package test.auctionsniper;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.Test;
import org.junit.runner.RunWith;

import auctionsniper.Auction;
import auctionsniper.AuctionEventListener.PriceSource;
import auctionsniper.AuctionSniper;
import auctionsniper.SniperListener;

//Ch13, p.124
@RunWith(JMock.class)
public class AuctionSniperTest {
    private final Mockery context = new Mockery();
    //SniperListener is an interface, be noticed
    private final SniperListener sniperListener = context.mock(SniperListener.class);
    private final Auction auction = context.mock(Auction.class);
    private final AuctionSniper sniper = new AuctionSniper(auction, sniperListener); 
    
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
        context.checking(new Expectations() {
            {
                one(auction).bid(price+increment);
                atLeast(1).of(sniperListener).sniperBidding();
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
}
