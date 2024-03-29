package auctionsniper;

import com.objogate.exception.Defect;

//Ch15, p.165
public enum SniperState {
    JOINING {
        @Override public SniperState whenAuctionClosed() {
            return LOST;
        }
    },
    BIDDING {
        @Override public SniperState whenAuctionClosed() {
            return LOST;
        }        
    },
    WINNING {
        @Override public SniperState whenAuctionClosed() {
            return WON;
        }        
    },
    LOSING {
        @Override public SniperState whenAuctionClosed() {
            return LOST;
        }        
    },    
    LOST,
    WON;

    public SniperState whenAuctionClosed() {
        throw new Defect("Auction is already closed");
    }
}
