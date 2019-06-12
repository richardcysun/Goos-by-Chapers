package test.auctionsniper;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.Test;
import org.junit.runner.RunWith;

import auctionsniper.AuctionSniper;
import auctionsniper.SniperListener;

//Ch13, p.124
@RunWith(JMock.class)
public class AuctionSniperTest {
    private final Mockery context = new Mockery();
    private final SniperListener sniperlistener = context.mock(SniperListener.class);
    private final AuctionSniper sniper = new AuctionSniper(sniperlistener); 
    
    @Test public void reportsLostWhenAuctionCloses() {
        context.checking(new Expectations() {
            {
                one(sniperlistener).sniperLost();
                }
            });
        sniper.auctionClosed();
    }
}
