package auctionsniper;

import java.util.HashMap;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;

import auctionsniper.AuctionEventListener.PriceSource;
import auctionsniper.xmpp.XMPPFailureReporter;

//Ch12, p.115, 116, 120
public class AuctionMessageTranslator implements MessageListener {
    private final String sniperId;
    private final AuctionEventListener listener;
    private final XMPPFailureReporter failureReporter;
    
    public AuctionMessageTranslator(String sniperId, AuctionEventListener listener, XMPPFailureReporter reporter) {
    	//the listener is actually the Main
        this.sniperId = sniperId;
        this.listener = listener;
        this.failureReporter = reporter;
    }

    //Ch13, p.135 revise Ch12, p.120 and p.116 
    //Ch19, p.222 revised
    public void processMessage(Chat chat, Message message) {       
    	String messageBody = message.getBody();
    	try {
    		translate(messageBody);
    	} catch (Exception parseException) {
    		failureReporter.cannotTranslateMessage(sniperId, messageBody, parseException);
    		listener.auctionFailed();
    	}        
    }
    
    //Ch19, not in the book
    private void translate(String body) throws Exception{
        AuctionEvent event = AuctionEvent.from(body);
        
        String eventType = event.type();
        if ("CLOSE".equals(eventType) ) {
        	//It causes Main to display "Lost"
            listener.auctionClosed();
        } else if ("PRICE".equals(eventType)) {
        	//It causes Main to display "Bidding"
            listener.currentPrice(event.currentPrice(), event.increment(), event.isFrom(sniperId));
        }
	}

	//Nested Class
    //Ch13, p.135
    private static class AuctionEvent {
        private final HashMap<String, String> fields = new HashMap<String, String>();
        
        //Ch19, p.218
        private String get(String fieldName) throws MissingValueException {
        	String value = fields.get(fieldName);
        	if (null == value) {
        		throw new MissingValueException(fieldName);
        	}
            return value;
        }
        
        //Ch19, add exception, not in the book
        private int getInt(String fieldName) throws MissingValueException{
            return Integer.parseInt(get(fieldName));
        }
        
        //Ch19, add exception, not in the book
        public String type() throws MissingValueException {
            return get("Event");
        }
        
        //Ch19, add exception, not in the book
        public int currentPrice() throws MissingValueException {
            return getInt("CurrentPrice");
        }
        
        //Ch19, add exception, not in the book
        public int increment() throws MissingValueException {
            return getInt("Increment");
        }
        
        private void addField(String field) {
            String[] pair = field.split(":");
            fields.put(pair[0].trim(), pair[1].trim());
        }
        
        static String[] fieldsIn(String messageBody) {
            return messageBody.split(";");
        }
        
        static AuctionEvent from(String messageBody) {
            AuctionEvent event = new AuctionEvent();
            for (String field : fieldsIn(messageBody)) {
                event.addField(field);
            }
            return event;
        }
        
        //Ch14, p.142
        //Ch19, add exception, not in the book
        public PriceSource isFrom(String sniperId) throws MissingValueException {
            return sniperId.equals(bidder()) ? PriceSource.FromSniper: PriceSource.FromOtherBidder;
        }
        
        //Ch19, add exception, not in the book
        private String bidder() throws MissingValueException {
            return get("Bidder");
        }
    }
    
    //CH19, not in the book
    private static class MissingValueException extends Exception {
        public MissingValueException(String fieldName) {
          super("Missing value for " + fieldName);
        }
      }
}
