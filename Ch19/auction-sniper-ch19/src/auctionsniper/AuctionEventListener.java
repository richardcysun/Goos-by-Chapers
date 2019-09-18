package auctionsniper;

import java.util.EventListener;

//Ch12, p.119
//Ch17, p.193, should add extends but not mentioned in the book
public interface AuctionEventListener extends EventListener {
    enum PriceSource {
        FromSniper, FromOtherBidder;
    };
    void auctionClosed();
    //Ch14, p.143, add parameter PriceSource
    void currentPrice(int price, int increment, PriceSource priceSource);
    
    //Ch19, not in the book 
    void auctionFailed();
}
