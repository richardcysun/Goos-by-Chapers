package auctionsniper.xmpp;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import auctionsniper.Auction;
import auctionsniper.AuctionEventListener;
import auctionsniper.AuctionMessageTranslator;
import auctionsniper.util.Announcer;

//Ch17, p.193, most codes are not in the book
public class XMPPAuction implements Auction{
	public static final String JOIN_COMMAND_FORMAT = "SOLVersion: 1.1; Command: JOIN;";
	public static final String BID_COMMAND_FORMAT = "SOLVersion: 1.1; Command: BID; Price: %d;";
	
	private final Announcer<AuctionEventListener> auctionEventListeners = Announcer.to(AuctionEventListener.class);
	private final Chat chat;
	private final XMPPFailureReporter failureReporter;  
	
	//Ch19, p.220 revised
	public XMPPAuction(XMPPConnection connection, String itemId, XMPPFailureReporter failureReporter) {
		this.failureReporter = failureReporter;
		AuctionMessageTranslator translator = translatorFor(connection);
		
		this.chat = connection.getChatManager().createChat(itemId, translator);
		addAuctionEventListener(chatDissconectorFor(translator));
	}
	
	//Ch19, p.220
	private AuctionEventListener chatDissconectorFor(final AuctionMessageTranslator translator) {
		return new AuctionEventListener() {
			public void auctionFailed() {
				chat.removeMessageListener(translator);
			}
			public void auctionClosed() {}
			public void currentPrice(int price, int increment, PriceSource priceSource) {}
		};
	}

	private AuctionMessageTranslator translatorFor(XMPPConnection connection) {
		return 	new AuctionMessageTranslator(connection.getUser(), auctionEventListeners.announce(), failureReporter);
	}
	
    public void bid(int amount) {
        sendMessage(String.format(BID_COMMAND_FORMAT, amount));
    }
    
    public void join() {
        sendMessage(JOIN_COMMAND_FORMAT);
    }
    
    public void addAuctionEventListener(AuctionEventListener listener) {
        auctionEventListeners.addListener(listener);
      }
    
    private void sendMessage(String message) {
        try {
            chat.sendMessage(message);
        }
        catch(XMPPException e) {
            e.printStackTrace();
        }
    }    
}
