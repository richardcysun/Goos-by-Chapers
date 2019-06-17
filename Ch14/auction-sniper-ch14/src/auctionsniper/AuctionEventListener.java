package auctionsniper;

//Ch12, p.119
public interface AuctionEventListener {
    void auctionClosed();
    void currentPrice(int price, int increment);
}
