package auctionsniper.xmpp;

import java.util.logging.Handler;
import java.util.logging.Logger;

import org.apache.commons.io.FilenameUtils;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import auctionsniper.Auction;
import auctionsniper.AuctionHouse;

//Entire class are not in the book, it's only mentioned in p.197 and says 
//"Implementing XMPPAuctionHouse is straightforward..."
public class XMPPAuctionHouse implements AuctionHouse{
	private static final String LOGGER_NAME = "auction-sniper";
	public static final String AUCTION_RESOURCE = "Auction";
	public static final String ITEM_ID_AS_LOGIN = "auction-%s";
	public static final String AUCTION_ID_FORMAT = ITEM_ID_AS_LOGIN + "@%s/" + AUCTION_RESOURCE;
	
	private final XMPPConnection connection;
	private final LoggingXMPPFailureReporter failureReporter;
	
	//Ch19, p.224 revised
	public XMPPAuctionHouse(XMPPConnection connection) throws XMPPAuctionException
	{
		this.connection = connection;
		this.failureReporter = new LoggingXMPPFailureReporter(makeLogger());
	}
	
	//Ch19, p.224
	private Logger makeLogger() throws XMPPAuctionException {
		Logger logger = Logger.getLogger(LOGGER_NAME);
		logger.setUseParentHandlers(false);
		logger.addHandler(simpleFileHandler());
		return logger;
	}

	private Handler simpleFileHandler() throws XMPPAuctionException {
		try {
			return null;
		}
		catch (Exception exception) {
			throw new XMPPAuctionException("could not create logger FileHandler " + FilenameUtils.getFullPath(LOGGER_NAME), exception);
		}
	}

	//Must declare static, otherwise static main() can't call it
	public static XMPPAuctionHouse connect(String hostname, String username, String password) throws XMPPAuctionException{
		XMPPConnection connection = new XMPPConnection(hostname);
		
		try {
			connection.connect();
			connection.login(username, password, AUCTION_RESOURCE);
			return new XMPPAuctionHouse(connection);
		}
		catch (XMPPException exception) {
			throw new XMPPAuctionException("could not connect to auction: " + connection, exception);
		}
	}
	
	public void disconnect()
	{
		connection.disconnect();
	}
	
	//Ch19, revised, not in the book
	public Auction auctionFor(String itemId) {
		return new XMPPAuction(connection, auctionId(itemId, connection), failureReporter);
	}
	
	 private static String auctionId(String itemId, XMPPConnection connection) { 
		    return String.format(AUCTION_ID_FORMAT, itemId, connection.getServiceName());
		  }
}
