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

    public void processMessage(Chat chat, Message message) {
    	//listener.auctionClosed() was added in P.116, but it broke the test of notifiesBidDetailWhenCurrentPriceMessageReceived(), 
    	//so it was commented out and replaced with correct uses 
        //listener.auctionClosed();
        HashMap<String, String> event = unpackEventFrom(message);
        
        String type = event.get("Event");
        if ("CLOSE".equals(type) ) {
        	//It causes Main to display "Lost"
            listener.auctionClosed();
        } else if ("PRICE".equals(type)) {
        	
        	//Main hasn't implemented currentPrice
            listener.currentPrice(Integer.parseInt(event.get("CurrentPrice")), 
                    Integer.parseInt(event.get("Increment")));
        }
    }

    private HashMap<String, String> unpackEventFrom(Message message) {
        // TODO Auto-generated method stub
        HashMap<String, String> event = new HashMap<String, String>();
        for (String element : message.getBody().split(";")) {
            String[] pair = element.split(":");
            event.put(pair[0].trim(), pair[1].trim());
        }
        return event;
    }
}
