package auctionsniper.ui;

import javax.swing.table.AbstractTableModel;

import auctionsniper.SniperState;

//Ch15, p.151
//Ch15, p.158, revised 
public class SnipersTableModel extends AbstractTableModel{   
    private final static SniperState STARTING_UP = new SniperState("", 0, 0);   
    private String statusText = MainWindow.STATUS_JOINING;
    private SniperState sniperState = STARTING_UP;
    
    public int getColumnCount() {
        return Column.values().length;
    }
    
    public int getRowCount() {
        return 1;
    }    
    
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch(Column.at(columnIndex)) {
        case ITEM_IDENTIFIER:
            return sniperState.itemId;
        case LAST_PRICE:
            return sniperState.lastPrice;
        case LAST_BID:
            return sniperState.lastBid;
        case SNIPER_STATUS:
            return statusText;
        default:
            throw new IllegalArgumentException("No column at " + columnIndex);
        }
    }
    
    public void setStatusText(String newStatusText) {
        statusText = newStatusText;
        fireTableRowsUpdated(0, 0);
    }

    public void sniperStatusChanged(SniperState newSniperState, String newStatusText) {
        sniperState = newSniperState;
        statusText = newStatusText;
        fireTableRowsUpdated(0, 0);
    }
}
