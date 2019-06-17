package auctionsniper;

//Ch13, p.124
public class AuctionSniper implements AuctionEventListener{
    private final Auction auction;
    private final SniperListener sniperListener;
    
    public AuctionSniper(Auction auction, SniperListener sniperListener) {
        this.auction = auction;
        this.sniperListener = sniperListener;
    }
    
    public void auctionClosed() {
        sniperListener.sniperLost();
    }
    
    //Ch14, p.143
    public void currentPrice(int price, int increment, PriceSource priceSource) {
        switch (priceSource) {
        case FromSniper:
            sniperListener.sniperWinning();
        break;
        case FromOtherBidder:
            auction.bid(price + increment);
            sniperListener.sniperBidding();
            break;
        }
    }
}
