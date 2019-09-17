package auctionsniper;

import java.util.logging.Logger;

//Ch13, p.124
public class AuctionSniper implements AuctionEventListener{
    private final Auction auction;
    private SniperListener sniperListener;
    private SniperSnapshot snapshot;
    //Ch18, not in the book
    private Item item;
    private Logger logger = Logger.getLogger("MyLog");
    
    public AuctionSniper(Item item, Auction auction) {
        this.auction = auction;
        this.item = item;
        this.snapshot = SniperSnapshot.joining(item.identifier);
        logger.info(String.format("Item: %s, Last Price: %s, Last Bid: %s, State: %s", snapshot.itemId, snapshot.lastPrice, snapshot.lastBid, snapshot.state.toString()));
    }
    
    //Ch14, p.147 revised
    //Ch15, p.164 revised
    public void auctionClosed() {
        snapshot = snapshot.closed();
        logger.info(String.format("Item: %s, Last Price: %s, Last Bid: %s, State: %s", snapshot.itemId, snapshot.lastPrice, snapshot.lastBid, snapshot.state.toString()));
        notifyChange();
    }
    
    //Ch14, p.143, revise in p.147
    //Ch15, revised in p.163, 164
    public void currentPrice(int price, int increment, PriceSource priceSource) {
        switch(priceSource) {
        case FromSniper:
            snapshot = snapshot.winning(price);
            logger.info(String.format("Item: %s, Last Price: %s, Last Bid: %s, State: %s", snapshot.itemId, snapshot.lastPrice, snapshot.lastBid, snapshot.state.toString()));
            break;
        case FromOtherBidder:
            int bid = price + increment;
            logger.info(String.format("Stop price: %d", item.stopPrice));
            if (item.allowsBid(bid))
            {           	
            	auction.bid(bid);
            	snapshot = snapshot.bidding(price, bid);
            	logger.info(String.format("Allow bid Item: %s, Last Price: %s, Last Bid: %s, State: %s", snapshot.itemId, snapshot.lastPrice, snapshot.lastBid, snapshot.state.toString()));
            }
            else
            {
            	snapshot = snapshot.losing(price);
            	logger.info(String.format("Deny bid Item: %s, Last Price: %s, Last Bid: %s, State: %s", snapshot.itemId, snapshot.lastPrice, snapshot.lastBid, snapshot.state.toString()));
            }            
            break;
        }
        notifyChange();
    }
    
    private void notifyChange() {
        sniperListener.sniperStateChanged(snapshot);
    }    
    
    //Ch17, not in the book
    public SniperSnapshot getSnapshot() {
    	return this.snapshot;
    }
    
    //Ch17, not in the book
    public void addSniperListener(SniperListener sniperListener) {
        this.sniperListener = sniperListener;
    }
}
