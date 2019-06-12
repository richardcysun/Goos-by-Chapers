package auctionsniper;

//Ch13, p.124
public class AuctionSniper implements AuctionEventListener{
    private final SniperListener sniperListener;
    
    public AuctionSniper(SniperListener sniperListener) {
        this.sniperListener = sniperListener;
    }
    
    public void auctionClosed() {
        sniperListener.sniperLost();
    }
    
    public void currentPrice(int price, int increment) {
        
    }
}
