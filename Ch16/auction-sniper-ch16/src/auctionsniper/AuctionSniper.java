package auctionsniper;

import java.util.logging.Logger;

//Ch13, p.124
public class AuctionSniper implements AuctionEventListener{
    private final Auction auction;
    private final SniperListener sniperListener;
    private SniperSnapshot snapshot;
    private Logger logger = Logger.getLogger("MyLog");
    
    public AuctionSniper(String itemId, Auction auction, SniperListener sniperListener) {
        this.auction = auction;
        this.sniperListener = sniperListener;
        this.snapshot = SniperSnapshot.joining(itemId);
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
            auction.bid(bid);
            snapshot = snapshot.bidding(price, bid);
            logger.info(String.format("Item: %s, Last Price: %s, Last Bid: %s, State: %s", snapshot.itemId, snapshot.lastPrice, snapshot.lastBid, snapshot.state.toString()));
            break;
        }
        notifyChange();
    }
    
    private void notifyChange() {
        sniperListener.sniperStateChanged(snapshot);
    }    
}
