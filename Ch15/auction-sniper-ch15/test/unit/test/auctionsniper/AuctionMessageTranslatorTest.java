package test.auctionsniper;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.packet.Message;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.jmock.integration.junit4.JMock;

import auctionsniper.AuctionEventListener;
import auctionsniper.AuctionEventListener.PriceSource;
import auctionsniper.AuctionMessageTranslator;

//Ch12, p.114, 115, 116
@RunWith(JMock.class)
public class AuctionMessageTranslatorTest {
    private static final String SNIPER_ID = "sniper";
    private final Mockery context = new Mockery();
    //AuctionEventListener is an interface, be noticed
    private final AuctionEventListener listener = context.mock(AuctionEventListener.class);
    private final AuctionMessageTranslator translator = new AuctionMessageTranslator(SNIPER_ID, listener);
    
    public static final Chat UNUSED_CHAT = null;//argument doesn't matter
    
    //This Unit Test verifies whether AuctionMessageTranslator can parse CLOSE event correctly
    @Test public void notifiesAuctionClosedWhenCloseMessageReceived() {
        context.checking(new Expectations() {
            {
            	//JMock method, the AuctionEventListener.auctionClosed() will be called once
                oneOf(listener).auctionClosed();
        }
            
        });
        
        //If the form of CLOSE is correct, translator will call auctionClosed() of mock(AuctionEventListener)
        Message message = new Message();
        message.setBody("SOLVersion: 1.1; Event: CLOSE;");
        
        translator.processMessage(UNUSED_CHAT, message);
    }
    
    @Test public void notifiesBidDetailWhenCurrentPriceMessageReceived() {
        context.checking(new Expectations() {
            {
                exactly(1).of(listener).currentPrice(192, 7, PriceSource.FromOtherBidder);
            }
        });
        
        //If the form of PRICE is correct, translator will call currentPrice() of mock(AuctionEventListener)
        Message message = new Message();
        message.setBody("SOLVersion: 1.1; Event: PRICE; CurrentPrice: 192; Increment: 7; Bidder: Someone else;");
        
        translator.processMessage(UNUSED_CHAT, message);
    }
    
    //Ch14, p.141
    @Test public void notifiesBidDetailWhenCurrentPriceMessageReceivedFromOtherBid() {
        context.checking(new Expectations() {
            {
                exactly(1).of(listener).currentPrice(192, 7, PriceSource.FromOtherBidder);
            }
        });
        
        //If the form of PRICE is correct, translator will call currentPrice() of mock(AuctionEventListener)
        Message message = new Message();
        message.setBody("SOLVersion: 1.1; Event: PRICE; CurrentPrice: 192; Increment: 7; Bidder: Someone else;");
        
        translator.processMessage(UNUSED_CHAT, message);
    }   
    
    //Ch14, p.141    
    @Test public void notifiesBidDetailWhenCurrentPriceMessageReceivedFromSniper() {
        context.checking(new Expectations() {
            {
                exactly(1).of(listener).currentPrice(234, 5, PriceSource.FromSniper);
            }
        });
        
        //If the form of PRICE is correct, translator will call currentPrice() of mock(AuctionEventListener)
        Message message = new Message();
        message.setBody("SOLVersion: 1.1; Event: PRICE; CurrentPrice: 234; Increment: 5; Bidder: " + SNIPER_ID + ";");
        
        translator.processMessage(UNUSED_CHAT, message);
    }    
}
