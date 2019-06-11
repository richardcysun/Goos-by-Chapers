package test.auctionsniper;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.packet.Message;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.jmock.integration.junit4.JMock;

import auctionsniper.AuctionEventListener;
import auctionsniper.AuctionMessageTranslator;

//Ch12, p.114, 115, 116
@RunWith(JMock.class)
public class AuctionMessageTranslatorTest {
    private final Mockery context = new Mockery();
    private final AuctionEventListener listener = context.mock(AuctionEventListener.class);
    private final AuctionMessageTranslator translator = new AuctionMessageTranslator(listener);
    
    public static final Chat UNUSED_CHAT = null;//argument doesn't matter
    
    //This Unit Test verifies whether AuctionMessageTranslator can parse CLOSE event correctly
    @Test public void notifiesAuctionClosedWhenCloseMessageReceived() {
        context.checking(new Expectations() {
            {
            	//JMock method, the AuctionEventListener.auctionClosed() will be called once
                oneOf(listener).auctionClosed();
        }
            
        });
        
        Message message = new Message();
        message.setBody("SOLVersion: 1.1; Event: CLOSE;");
        
        translator.processMessage(UNUSED_CHAT, message);
    }
    
    @Test public void notifiesBidDetailWhenCurrentPriceMessageReceived() {
        context.checking(new Expectations() {
            {
                exactly(1).of(listener).currentPrice(192, 7);
            }
        });
        Message message = new Message();
        message.setBody("SOLVersion: 1.1; Event: PRICE; CurrentPrice: 192; Increment: 7; Bidder: Someone else;");
        
        translator.processMessage(UNUSED_CHAT, message);
    }
}
