package auctionsniper.ui;

import javax.swing.table.AbstractTableModel;

import auctionsniper.SniperListener;
import auctionsniper.SniperSnapshot;
import auctionsniper.SniperState;

//Ch15, p.151
//Ch15, p.158, revised 
public class SnipersTableModel extends AbstractTableModel implements SniperListener {   
    private final static SniperSnapshot STARTING_UP = new SniperSnapshot("", 0, 0, SniperState.JOINING);   
    private String state = MainWindow.STATUS_JOINING;
    private SniperSnapshot snapshot = STARTING_UP;
    private static String[] STATUS_TEXT = {
            "Joining", "Bidding", "Winning", "Lost", "Won"
    };
    
    public int getColumnCount() {
        return Column.values().length;
    }
    
    public int getRowCount() {
        return 1;
    }    

    //Ch15, p.170
    @Override public String getColumnName(int column) {
        return Column.at(column).name;
    }
    
    //Ch15, p.161 revised
    //Ch15, p.167 revised
    public Object getValueAt(int rowIndex, int columnIndex) {
        return Column.at(columnIndex).valueIn(snapshot);
    }

    public void setStatusText(String newStatusText) {
        state = newStatusText;
        fireTableRowsUpdated(0, 0);
    }

    public void sniperLost() {};
    //Ch15, p.161, 166 revised
    public void sniperStateChanged(SniperSnapshot newSnapshot) {
        this.snapshot = newSnapshot;
        //this.state = STATUS_TEXT[newSnapshot.state.ordinal()];
        fireTableRowsUpdated(0, 0);
    }
    
    public static String textFor(SniperState state) {
        return STATUS_TEXT[state.ordinal()];
    }
}
