package auctionsniper.xmpp;

//Ch19, not in the book
//from https://github.com/sf105/goos-code/blob/master/src/auctionsniper/xmpp/XMPPAuctionException.java
public class XMPPAuctionException extends Exception {
	public XMPPAuctionException(String message, Exception cause) {
		super(message, cause);
	}
}
