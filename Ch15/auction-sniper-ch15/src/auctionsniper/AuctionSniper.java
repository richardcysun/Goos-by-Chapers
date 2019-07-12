package auctionsniper;

//Ch13, p.124
public class AuctionSniper implements AuctionEventListener{
    private final Auction auction;
    private final SniperListener sniperListener;
    //private boolean isWinning = false;
    private SniperSnapshot snapshot;
    
    public AuctionSniper(String itemId, Auction auction, SniperListener sniperListener) {
        this.auction = auction;
        this.sniperListener = sniperListener;
        this.snapshot = SniperSnapshot.joining(itemId);
    }
    
    //Ch14, p.147 revised
    //Ch15, p.164 revised
    public void auctionClosed() {
        snapshot = snapshot.closed();
        notifyChange();
    }
    
    //Ch14, p.143, revise in p.147
    //Ch15, revised in p.163, 164
    public void currentPrice(int price, int increment, PriceSource priceSource) {
        switch(priceSource) {
        case FromSniper:
            snapshot = snapshot.winning(price);
            break;
        case FromOtherBidder:
            int bid = price + increment;
            auction.bid(bid);
            snapshot = snapshot.bidding(price, bid);
            break;
        }
        notifyChange();
    }
    
    private void notifyChange() {
        sniperListener.sniperStateChanged(snapshot);
    }    
}
