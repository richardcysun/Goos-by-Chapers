package test.auctionsniper.ui;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import auctionsniper.SniperSnapshot;
import auctionsniper.SniperState;
import auctionsniper.ui.Column;
import auctionsniper.ui.MainWindow;
import auctionsniper.ui.SnipersTableModel;

import static junit.framework.Assert.assertEquals;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

//Ch15, p.156, p.157
@RunWith(JMock.class)
public class SnipersTableModelTest {
    private final Mockery context = new Mockery();
    private TableModelListener listener = context.mock(TableModelListener.class);
    private final SnipersTableModel model = new SnipersTableModel();
    
    @Before public void attachModelListener() {
        model.addTableModelListener(listener);
    }
    
    @Test public void hasEnoughColumns() {
        assertThat(model.getColumnCount(), equalTo(Column.values().length));
    }
    
    //Ch16, p.181 revise
    @Test public void setsSniperValuesInColumns() {
    	SniperSnapshot joining = SniperSnapshot.joining("item xyz");
    	SniperSnapshot bidding = joining.bidding(555, 666);
        context.checking(new Expectations() {{
        	allowing(listener).tableChanged(with(anyInsertionEvent()));
        	
            one(listener).tableChanged(with(aChangeInRow(0)));
        }});
        
        model.addSniper(joining);
        model.sniperStateChanged(bidding);
        
        assertRowMatchesSnapshot(0, bidding);
    }

    @Test public void setsUpColumnHeadings() {
        for (Column column: Column.values()) {
            assertEquals(column.name, model.getColumnName(column.ordinal()));
        }
    }
    
    //Ch16, p.180
    @Test public void notifiesListenersWhenAddingASniper() {
    	SniperSnapshot joining = SniperSnapshot.joining("item123");
        context.checking(new Expectations() {{
            one(listener).tableChanged(with(anInsertionAtRow(0)));
        }});
        
        assertEquals(0, model.getRowCount());
        model.addSniper(joining);
        
        assertEquals(1, model.getRowCount());
        assertRowMatchesSnapshot(0, joining);
    }
    
    //Ch16, p.181
    @Test public void holdsSnipersInAdditionOrder() {
        context.checking(new Expectations() {{
            ignoring(listener);
        }});    	
        
        model.addSniper(SniperSnapshot.joining("item 0"));
        model.addSniper(SniperSnapshot.joining("item 1"));
        
        assertEquals("item 0", cellValue(0, Column.ITEM_IDENTIFIER));
        assertEquals("item 1", cellValue(1, Column.ITEM_IDENTIFIER));
    }
        
    //Copy assertRowMatchesSnapshot(), cellValue(), anyInsertionEvent(), anInsertionAtRow() and aChangeInRow()
    //from https://github.com/sf105/goos-code/blob/master/test/unit/test/auctionsniper/ui/SnipersTableModelTest.java
    private void assertRowMatchesSnapshot(int row, SniperSnapshot snapshot) {
        assertEquals(snapshot.itemId, cellValue(row, Column.ITEM_IDENTIFIER));
        assertEquals(snapshot.lastPrice, cellValue(row, Column.LAST_PRICE));
        assertEquals(snapshot.lastBid, cellValue(row, Column.LAST_BID));
        assertEquals(SnipersTableModel.textFor(snapshot.state), cellValue(row, Column.SNIPER_STATE));
      }

    
    private Object cellValue(int rowIndex, Column column) {
        return model.getValueAt(rowIndex, column.ordinal());
    }
    
    Matcher<TableModelEvent> anyInsertionEvent() {
        return hasProperty("type", equalTo(TableModelEvent.INSERT));
    }
    
    Matcher<TableModelEvent> anInsertionAtRow(final int row) {
        return samePropertyValuesAs(new TableModelEvent(model, row, row, TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT));
    }
    
    private Matcher<TableModelEvent> aChangeInRow(int row) { 
        return samePropertyValuesAs(new TableModelEvent(model, row)); 
    } 
}
