package test.endtoend.auctionsniper;

import com.objogate.wl.swing.AWTEventQueueProber;
import com.objogate.wl.swing.driver.JFrameDriver;
import com.objogate.wl.swing.driver.JTableDriver;
import com.objogate.wl.swing.gesture.GesturePerformer;

import static org.hamcrest.Matchers.*;
import static com.objogate.wl.swing.matcher.JLabelTextMatcher.withLabelText;

//import auctionsniper.Main;
import auctionsniper.ui.MainWindow;

//Ch11, p.91
//extends means inherit
//super is a bit similar to this, but it can call father class interface 
public class AuctionSniperDriver extends JFrameDriver{

    public AuctionSniperDriver(int timeoutMillis) {
        super(new GesturePerformer(),
                JFrameDriver.topLevelFrame(
                        named(MainWindow.MAIN_WINDOW_NAME),
                        showingOnScreen()),
                new AWTEventQueueProber(timeoutMillis, 100));
    }
    
    //Ch15, p.150, replace JLabel with JTable
    public void showsSniperStatus(String statusText) {
        new JTableDriver(this).hasCell(withLabelText(equalTo(statusText)));
    }

}
