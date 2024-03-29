package test.integration.auctionsniper.ui;

import auctionsniper.UserRequestListener;
import auctionsniper.ui.MainWindow;
import auctionsniper.ui.SnipersTableModel;

import org.junit.Test;

import com.objogate.wl.swing.probe.ValueMatcherProbe;

import test.endtoend.auctionsniper.AuctionSniperDriver;
import static org.hamcrest.Matchers.*;

//Ch16, p.186
public class MainWindowTest {
	private final SnipersTableModel tableModel = new SnipersTableModel();
	private final MainWindow mainWindow = new MainWindow(tableModel);
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
