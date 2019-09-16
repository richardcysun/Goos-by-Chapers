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
	public void joinAuction(Item item)
	{
		//Ch18, not in the book
		Auction auction = auctionHouse.auctionFor(item.identifier);	
		AuctionSniper sniper = new AuctionSniper(item, auction);
		auction.addAuctionEventListener(sniper);

		collector.addSniper(sniper);
		auction.join();
	}
}
