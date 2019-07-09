package auctionsniper;

//Ch13, p.124
public class AuctionSniper implements AuctionEventListener{
    private final Auction auction;
    private final SniperListener sniperListener;
    private boolean isWinning = false;
    private final String itemId = "item-54321";
    
    public AuctionSniper(Auction auction, SniperListener sniperListener) {
        this.auction = auction;
        this.sniperListener = sniperListener;
    }
    
    //Ch14, p.147 revise
    public void auctionClosed() {
        if (isWinning) {
            sniperListener.sniperWon();
        } else {
            sniperListener.sniperLost();
        }
    }
    
    //Ch14, p.143, revise in p.147
    public void currentPrice(int price, int increment, PriceSource priceSource) {
        isWinning = priceSource == PriceSource.FromSniper;
        
        if (isWinning) {            
            sniperListener.sniperWinning();
        } else {
            //Ch15, p.155            
            int bid = price + increment;
            auction.bid(bid);
            sniperListener.sniperBidding(new SniperSnapshot(itemId, price, bid));
        }
    }
}
