package auctionsniper;

//Ch13, p.128
public interface Auction {
    void bid(int amount);
    void join();
    //Ch17, p.194, but not mentioned in the book
    void addAuctionEventListener(AuctionEventListener listener);
}
