package auctionsniper;

import java.util.EventListener;

//Ch16, p.186
public interface UserRequestListener extends EventListener {
	void joinAuction(String itemId);
}
