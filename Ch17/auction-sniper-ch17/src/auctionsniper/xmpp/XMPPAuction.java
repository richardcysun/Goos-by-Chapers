package auctionsniper.xmpp;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import auctionsniper.Auction;
import auctionsniper.AuctionEventListener;
import auctionsniper.AuctionMessageTranslator;
import auctionsniper.util.Announcer;

import static auctionsniper.Main.AUCTION_ID_FORMAT;

//Ch17, p.193, most codes are not in the book
public class XMPPAuction implements Auction{
	public static final String JOIN_COMMAND_FORMAT = "SOLVersion: 1.1; Command: JOIN;";
	public static final String BID_COMMAND_FORMAT = "SOLVersion: 1.1; Command: BID; Price: %d;";
	
	private final Announcer<AuctionEventListener> auctionEventListeners = Announcer.to(AuctionEventListener.class);
	private final Chat chat;
	
	public XMPPAuction(XMPPConnection connection, String itemId) {
		chat = connection.getChatManager().createChat(auctionId(itemId, connection), 
				new AuctionMessageTranslator(
                        connection.getUser(), 
                        auctionEventListeners.announce()));
	}
	
	private static String auctionId(String itemId, XMPPConnection connection) {
        return String.format(AUCTION_ID_FORMAT, itemId, connection.getServiceName());
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
