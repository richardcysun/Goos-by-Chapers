package test.integration.auctionsniper.ui;

import auctionsniper.Item;
import auctionsniper.SniperPortfolio;
import auctionsniper.UserRequestListener;
import auctionsniper.ui.MainWindow;

import org.junit.Test;

import com.objogate.wl.swing.probe.ValueMatcherProbe;

import test.endtoend.auctionsniper.AuctionSniperDriver;
import static org.hamcrest.Matchers.*;

//Ch16, p.186
public class MainWindowTest {
	//Ch17, not in the book
	private final SniperPortfolio portfolio = new SniperPortfolio(); 
	private final MainWindow mainWindow = new MainWindow(portfolio);
	private final AuctionSniperDriver driver = new AuctionSniperDriver(100);
	
	//Ch18, p.209 replace String with Item  
	@Test public void makesUserRequestWhenJoinButtonClicked() {
		final ValueMatcherProbe<Item> itemProbe = 
				new ValueMatcherProbe<Item>(equalTo(new Item("an item-id", 789)), "item request");
		mainWindow.addUserRequestListener(
				new UserRequestListener() {
					public void joinAuction(Item item) {
						itemProbe.setReceivedValue(item);
					}
				});
		
		driver.startBiddingFor("an item-id", 789);
		driver.check(itemProbe);
	}
}
