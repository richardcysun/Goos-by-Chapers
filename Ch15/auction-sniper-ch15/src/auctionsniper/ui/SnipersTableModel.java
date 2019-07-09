package auctionsniper.ui;

import javax.swing.table.AbstractTableModel;

import auctionsniper.SniperSnapshot;

//Ch15, p.151
//Ch15, p.158, revised 
public class SnipersTableModel extends AbstractTableModel{   
    private final static SniperSnapshot STARTING_UP = new SniperSnapshot("", 0, 0);   
    private String statusText = MainWindow.STATUS_JOINING;
    private SniperSnapshot sniperSnapshot = STARTING_UP;
    
    public int getColumnCount() {
        return Column.values().length;
    }
    
    public int getRowCount() {
        return 1;
    }    
    
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch(Column.at(columnIndex)) {
        case ITEM_IDENTIFIER:
            return sniperSnapshot.itemId;
        case LAST_PRICE:
            return sniperSnapshot.lastPrice;
        case LAST_BID:
            return sniperSnapshot.lastBid;
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

    public void sniperStatusChanged(SniperSnapshot newSniperSnapshot, String newStatusText) {
        sniperSnapshot = newSniperSnapshot;
        statusText = newStatusText;
        fireTableRowsUpdated(0, 0);
    }
}
