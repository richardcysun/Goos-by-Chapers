package auctionsniper;

import java.util.EventListener;

//Ch13, p.124
public interface SniperListener extends EventListener {
    //void sniperWon(final SniperSnapshot snapshot);
    void sniperLost();
    //Ch15, p.156
    //void sniperBidding(final SniperSnapshot snapshot);
    //void sniperWinning(final SniperSnapshot snapshot);
    //Ch15, 
    void sniperStateChanged(final SniperSnapshot snapshot);
}
