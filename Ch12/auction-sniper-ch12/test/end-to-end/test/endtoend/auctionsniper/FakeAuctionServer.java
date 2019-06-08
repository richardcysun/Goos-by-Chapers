package test.endtoend.auctionsniper;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

//Ch11, p.93, p.94
public class FakeAuctionServer {
    private final SingleMessageListener messageListener = new SingleMessageListener();
    
    //static final means this variable is unique in the process, and it's a constant one
    public static final String XMPP_HOSTNAME = "localhost";
    private static final String ITEM_ID_AS_LOGIN = "auction-%s";
    private static final String AUCTION_RESOURCE = "Auction";
    private static final String AUCTION_PASSWORD = "auction";
            
    private final String itemId;
    private final XMPPConnection connection;
    protected Chat currentChat;

    public FakeAuctionServer(String itemId) {
        this.itemId = itemId;
        this.connection = new XMPPConnection(XMPP_HOSTNAME);
    }

    public void startSellingItem() throws XMPPException {
        connection.connect();
        //The ITEM_ID_AS_LOGIN will be "auction-item-54321"
        connection.login(String.format(ITEM_ID_AS_LOGIN, itemId),
                AUCTION_PASSWORD, AUCTION_RESOURCE);
        connection.getChatManager().addChatListener(new ChatManagerListener() {
            public void chatCreated(Chat chat, boolean createdLocally) {
                currentChat = chat;
                chat.addMessageListener(messageListener);
            }
        });
    }
    
    //Ch12, p.107, revise Ch11
    public void hasReceivedJoinRequestFromSniper() throws InterruptedException {
        messageListener.receiveAMessage(is(anything()));
    }

    public void announceClosed() throws XMPPException {
        //Actually, this is just a empty message, 
        currentChat.sendMessage(new Message());
    }

    public String getItemId() {
        return itemId;
    }

    public void stop() {
        connection.disconnect();
    }

    //Ch12, p.107
	public void reportPrice(int price, int increment, String bidder) throws XMPPException {
		currentChat.sendMessage(String.format("SOLVersion: 1.1; Event: PRICE "
				+ "CurrentPrice: %d; Increment: %d; Bidder: %s;",
				price, increment, bidder));
	}
	
	//Ch12, p.107
	public void hasReceivedBid(int bid, String sniperId) throws InterruptedException {
		assertThat(currentChat.getParticipant(), equalTo(sniperId));
		messageListener.receiveAMessage(equalTo(String.format("SQLVersion: 1.1; Command: BID; Price: %d;", bid)));
	}

}
