package test.integration.auctionsniper.ui;

import auctionsniper.SniperPortfolio;
import auctionsniper.UserRequestListener;
import auctionsniper.ui.MainWindow;
import auctionsniper.ui.SnipersTableModel;

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
	
	@Test public void makesUserRequestWhenJoinButtonClicked() {
		final ValueMatcherProbe<String> buttonProbe = 
				new ValueMatcherProbe<String>(equalTo("an item-id"), "join request");
		mainWindow.addUserRequestListener(
				new UserRequestListener() {
					public void joinAuction(String itemId) {
						buttonProbe.setReceivedValue(itemId);
					}
				});
		
		driver.startBiddingFor("an item-id");
		driver.check(buttonProbe);
	}
}
