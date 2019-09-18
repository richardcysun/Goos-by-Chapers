package auctionsniper;

import java.util.EventListener;

//Ch16, p.186
public interface UserRequestListener extends EventListener {
	//Ch18, p.209 revised
	void joinAuction(Item item);
}
