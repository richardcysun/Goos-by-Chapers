package auctionsniper;

import java.util.EventListener;

//Ch13, p.124
public interface SniperListener extends EventListener {
    void sniperWon();
    void sniperLost();
    //Ch15, p.156
    void sniperBidding(final SniperSnapshot snapshot);
    void sniperWinning();
}
