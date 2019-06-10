package test.endtoend.auctionsniper;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

import org.hamcrest.Matcher;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import auctionsniper.Main;

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

    //Log in to OpenFire as "auction-item-54321" and open a Chat, and pretend it's Southabee On-line
    public void startSellingItem() throws XMPPException {
        connection.connect();
        //The ITEM_ID_AS_LOGIN will be "auction-item-54321"
        connection.login(String.format(ITEM_ID_AS_LOGIN, itemId),
                AUCTION_PASSWORD, AUCTION_RESOURCE);
        //ChatManagerListener is a class to talk with OpenFire XMPP Server
        connection.getChatManager().addChatListener(new ChatManagerListener() {
            public void chatCreated(Chat chat, boolean createdLocally) {
                currentChat = chat;
                chat.addMessageListener(messageListener);
            }
        });
    }
    
    //Ch11, p.194, sniperJoinsAuctionUntilAuctionCloses() need this method
    public void hasReceivedJoinRequestFromSniper() throws InterruptedException {
        messageListener.receiveAMessage(is(anything()));
    }
    
    //Ch12 p.107, receive JOIN from "sniper@localhost/Auction"
    public void hasReceivedJoinRequestFrom(String sniperId) throws InterruptedException {
        receiveAMessageMatching(sniperId, equalTo(Main.JOIN_COMMAND_FORMAT));
    }

    //Ch12, p. 108
    //This is a multiple-user system, auction server has to confirm message is from expected user
    private void receiveAMessageMatching(String sniperId, Matcher<? super String> messageMatcher) throws InterruptedException {
    	messageListener.receiveAMessage(messageMatcher);
    	assertThat(currentChat.getParticipant(), equalTo(sniperId));
	}

	public void announceClosed() throws XMPPException {
        //In Ch11, this is just a empty message, but in Ch12, "CLOSE" event is set  
        currentChat.sendMessage("SOLVersion: 1.1; Event: CLOSE;");
    }

    public String getItemId() {
        return itemId;
    }

    public void stop() {
        connection.disconnect();
    }

    //Ch12, p.107, send price to Chat
	public void reportPrice(int price, int increment, String bidder) throws XMPPException {
		currentChat.sendMessage(String.format("SOLVersion: 1.1; Event: PRICE "
				+ "CurrentPrice: %d; Increment: %d; Bidder: %s;",
				price, increment, bidder));
	}
	
	//Ch12, p.108 revise p.107
	public void hasReceivedBid(int bid, String sniperId) throws InterruptedException {
		receiveAMessageMatching(sniperId, equalTo(String.format(Main.BID_COMMAND_FORMAT, bid)));
	}

}
