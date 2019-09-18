package auctionsniper;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

//Ch18, p.209
public class Item {
	public final String identifier;
	public final int stopPrice;
	
	public Item(String identifier, int stopPrice)
	{
		this.identifier = identifier;
		this.stopPrice = stopPrice;
	}
	
	//Ch18, p.211
	public boolean allowsBid(int bid) {
		return bid <= stopPrice;
	}
	
    @Override
    public boolean equals(Object obj) {
      return EqualsBuilder.reflectionEquals(this, obj);
    }
    
    @Override
    public int hashCode() {
      return HashCodeBuilder.reflectionHashCode(this);
    }
    
    @Override
    public String toString() {
      return ToStringBuilder.reflectionToString(this);
    }    
}
