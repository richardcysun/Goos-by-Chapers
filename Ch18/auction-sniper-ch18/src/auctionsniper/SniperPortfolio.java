package auctionsniper;

import java.util.ArrayList;

//import auctionsniper.ui.SnipersTableModel;
import auctionsniper.util.Announcer;

//Ch17, not in the book
//from https://github.com/sf105/goos-code/blob/master/src/auctionsniper/SniperPortfolio.java
public class SniperPortfolio implements SniperCollector{	  
	private final Announcer<PortfolioListener> announcer = Announcer.to(PortfolioListener.class);
	private final ArrayList<AuctionSniper> snipers = new ArrayList<AuctionSniper>();
	
	public void addSniper(AuctionSniper sniper)
	{
		snipers.add(sniper);
		announcer.announce().sniperAdded(sniper);
	}
	
	public void sniperAdded(AuctionSniper sniper)
	{
	}
	
	public void addPortfolioListener(PortfolioListener listener) {
		announcer.addListener(listener);
	}
}
