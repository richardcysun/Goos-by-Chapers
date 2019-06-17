package auctionsniper;

import java.util.HashMap;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;

//Ch12, p.115, 116, 120
public class AuctionMessageTranslator implements MessageListener {
    private final AuctionEventListener listener;
    
    public AuctionMessageTranslator(AuctionEventListener listener) {
    	//the listener is actually the Main
        this.listener = listener;
    }

    //Ch13, p.135 revise Ch12, p.120 and p.116 
    public void processMessage(Chat chat, Message message) {
    	//listener.auctionClosed() was added in P.116, but it broke the test of notifiesBidDetailWhenCurrentPriceMessageReceived(), 
    	//so it was commented out and replaced with correct uses 
        //listener.auctionClosed();
        //HashMap<String, String> event = unpackEventFrom(message);
        AuctionEvent event = AuctionEvent.from(message.getBody());
        
        String eventType = event.type();
        if ("CLOSE".equals(eventType) ) {
        	//It causes Main to display "Lost"
            listener.auctionClosed();
        } else if ("PRICE".equals(eventType)) {
        	//It causes Main to display "Bidding"
            listener.currentPrice(event.currentPrice(), event.increment());
        }
    }
    
    //Nested Class
    //Ch13, p.135
    private static class AuctionEvent {
        private final HashMap<String, String> fields = new HashMap<String, String>();
        
        private String get(String fieldName) {
            return fields.get(fieldName);
        }
        
        private int getInt(String fieldName) {
            return Integer.parseInt(get(fieldName));
        }
        
        public String type() {
            return get("Event");
        }
        
        public int currentPrice() {
            return getInt("CurrentPrice");
        }
        
        public int increment() {
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
    }
}
