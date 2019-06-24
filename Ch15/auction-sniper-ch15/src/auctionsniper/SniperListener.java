package auctionsniper;

import java.util.EventListener;

//Ch13, p.124
public interface SniperListener extends EventListener {
    void sniperWon();
    void sniperLost();
    void sniperBidding();
    void sniperWinning();
}
