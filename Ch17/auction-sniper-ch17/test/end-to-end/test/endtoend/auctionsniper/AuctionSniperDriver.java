package test.endtoend.auctionsniper;

import com.objogate.wl.swing.AWTEventQueueProber;
import com.objogate.wl.swing.driver.JButtonDriver;
import com.objogate.wl.swing.driver.JFrameDriver;
import com.objogate.wl.swing.driver.JTableDriver;
import com.objogate.wl.swing.driver.JTableHeaderDriver;
import com.objogate.wl.swing.driver.JTextFieldDriver;
import com.objogate.wl.swing.gesture.GesturePerformer;

import static java.lang.String.valueOf;

import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.table.JTableHeader;

import static com.objogate.wl.swing.matcher.JLabelTextMatcher.withLabelText;
import static com.objogate.wl.swing.matcher.IterableComponentsMatcher.matching;

//import auctionsniper.Main;
import auctionsniper.ui.MainWindow;

//Ch11, p.91
//extends means inherit
//super is a bit similar to this, but it can call father class interface 
public class AuctionSniperDriver extends JFrameDriver {
	@SuppressWarnings("unchecked")
	public void startBiddingFor(String itemId) {
		itemIdField().replaceAllText(itemId);
		bidButton().click();
	}
	
    public AuctionSniperDriver(int timeoutMillis) {
        super(new GesturePerformer(),
                JFrameDriver.topLevelFrame(
                        named(MainWindow.MAIN_WINDOW_NAME),
                        showingOnScreen()),
                new AWTEventQueueProber(timeoutMillis, 100));
    }
       
    //Ch15, p.153
    public void showsSniperStatus(String itemId, int lastPrice, int lastBid, String statusText) {
        JTableDriver table = new JTableDriver(this);
        table.hasRow(
                matching(withLabelText(itemId), withLabelText(valueOf(lastPrice)), 
                withLabelText(valueOf(lastBid)), withLabelText(statusText)));
    }

    //Ch15, p.169
    public void hasColumnTitles() {
        JTableHeaderDriver headers = new JTableHeaderDriver(this, JTableHeader.class);
        headers.hasHeaders(matching(withLabelText("Item"), withLabelText("Last Price"), withLabelText("Last Bid"), withLabelText("State")));
    }
	
    //Ch16, p.184
	private JTextFieldDriver itemIdField() {
		JTextFieldDriver newItemId = new JTextFieldDriver(this, JTextField.class, named(MainWindow.NEW_ITEM_ID_NAME));
		newItemId.focusWithMouse();
		return newItemId;
	}
	
	//Ch16, p.184
	private JButtonDriver bidButton() {
		return new JButtonDriver(this, JButton.class, named(MainWindow.JOIN_BUTTON_NAME));
	}
}
