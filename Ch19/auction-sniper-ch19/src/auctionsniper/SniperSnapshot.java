package auctionsniper;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

//Ch15, p.154
public class SniperSnapshot {
    public final String itemId;
    public final int lastPrice;
    public final int lastBid;
    public final SniperState state;
    
    public SniperSnapshot(String itemId, int lastPrice, int lastBid, SniperState state)
    {
        this.itemId = itemId;
        this.lastPrice = lastPrice;
        this.lastBid = lastBid;
        this.state = state;
    }

    //Copy equals() and toString() from https://github.com/sf105/goos-code/blob/master/src/auctionsniper/SniperSnapshot.java
    ///Without equals(), different SniperSnapshot objects with the same values will be considered as not equal.
    @Override
    public boolean equals(Object obj) {
      return EqualsBuilder.reflectionEquals(this, obj);
    }
    
    //With toString(), it can dump details of SniperSnapshot, it's very helpful to trace details with
    @Override
    public String toString() {
      return ToStringBuilder.reflectionToString(this);
    }
    
    //Ch15, p.156
    public SniperSnapshot bidding(int newLastPrice, int newLastBid) {
        return new SniperSnapshot(itemId, newLastPrice, newLastBid, SniperState.BIDDING);
    }
    
    //Ch15, p.156    
    public SniperSnapshot winning(int newLastPrice) {
        return new SniperSnapshot(itemId, newLastPrice, lastBid, SniperState.WINNING);
    }
    
    //Ch18, p.211    
    public SniperSnapshot losing(int newLastPrice) {
        return new SniperSnapshot(itemId, newLastPrice, lastBid, SniperState.LOSING);
    }
    
    //Ch15, p.156    
    public static SniperSnapshot joining(String itemId) {
        return new SniperSnapshot(itemId, 0, 0, SniperState.JOINING);
    }

    //Ch15, p.164
    public SniperSnapshot closed() {
        return new SniperSnapshot(itemId, lastPrice, lastBid, state.whenAuctionClosed());
    }

    //Ch19, p.219
    public SniperSnapshot failed() {
        return new SniperSnapshot(itemId, 0, 0, SniperState.FAILED);
    }
    
    //Copy isForSameItemAs() from https://github.com/sf105/goos-code/blob/master/src/auctionsniper/SniperSnapshot.java
	public boolean isForSameItemAs(SniperSnapshot sniperSnapshot) {
		return itemId.equals(sniperSnapshot.itemId);
	}
}
