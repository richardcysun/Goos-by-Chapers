package auctionsniper;

//Ch12, p.119
public interface AuctionEventListener {
    enum PriceSource {
        FromSniper, FromOtherBidder;
    };
    void auctionClosed();
    //Ch14, p.143, add parameter PriceSource
    void currentPrice(int price, int increment, PriceSource priceSource);
}
