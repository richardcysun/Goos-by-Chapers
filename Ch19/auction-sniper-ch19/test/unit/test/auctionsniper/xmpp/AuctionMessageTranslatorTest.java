package test.auctionsniper.xmpp;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.packet.Message;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.jmock.integration.junit4.JMock;

import auctionsniper.AuctionEventListener;
import auctionsniper.AuctionEventListener.PriceSource;
import auctionsniper.xmpp.XMPPFailureReporter;
import auctionsniper.AuctionMessageTranslator;

//Ch12, p.114, 115, 116
@RunWith(JMock.class)
public class AuctionMessageTranslatorTest {
    private static final String SNIPER_ID = "sniper";
    private final Mockery context = new Mockery();
    //AuctionEventListener is an interface, be noticed
    private final AuctionEventListener listener = context.mock(AuctionEventListener.class);
    private final XMPPFailureReporter failureReport = context.mock(XMPPFailureReporter.class);
    private final AuctionMessageTranslator translator = new AuctionMessageTranslator(SNIPER_ID, listener, failureReport);
    
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
    
    //Ch19, p.217
    //Ch19, p.222 revised
    @Test public void notifiesAuctionFailedWhenBadMessageReceived() {
    	String badMessage = "a bad message";
    	expectFailureWithMessage(badMessage);
    	translator.processMessage(UNUSED_CHAT, message(badMessage));
    }
    
    private void expectFailureWithMessage(String badMessage) {
        context.checking(new Expectations() {
            {
                oneOf(listener).auctionFailed();
                oneOf(failureReport).cannotTranslateMessage(
                		with(SNIPER_ID), with(badMessage), with(any(Exception.class)));
            }
        });
	}

	//Ch19, p.222
    private Message message(String body) {
    	Message message = new Message();
    	message.setBody(body);
    	return message;
    }
    
    //Ch19, p.218   
    //Ch19, not in the book, simply follow the same way as notifiesAuctionFailedWhenBadMessageReceived()
    @Test public void notifiesAuctionFailedWhenEventTypeMissing() {
    	String badMessage = "SOLVersion: 1.1; CurrentPrice: 234; Increment: 5; Bidder: " + SNIPER_ID + ";";
    	expectFailureWithMessage(badMessage);
    	translator.processMessage(UNUSED_CHAT, message(badMessage));
    }
}
