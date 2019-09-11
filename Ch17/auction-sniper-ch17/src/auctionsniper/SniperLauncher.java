package auctionsniper;

//Ch17, p.198
public class SniperLauncher implements UserRequestListener{
	private final AuctionHouse auctionHouse;
	public final SniperCollector collector;
	
	public SniperLauncher(AuctionHouse auctionHouse, SniperCollector collector)
	{
		this.auctionHouse = auctionHouse;
		this.collector = collector;
	}
	
	//This is a funny function, the original implementation in p.197 is improper, 
	//then authors change it right away in p.198
	public void joinAuction(String itemId)
	{
		Auction auction = auctionHouse.auctionFor(itemId);	
		AuctionSniper sniper = new AuctionSniper(itemId, auction);
		auction.addAuctionEventListener(sniper);

		collector.addSniper(sniper);
		auction.join();
	}
}
