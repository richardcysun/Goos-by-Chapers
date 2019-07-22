package auctionsniper.ui;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import com.objogate.exception.Defect;

import auctionsniper.SniperListener;
import auctionsniper.SniperSnapshot;
import auctionsniper.SniperState;

//Ch15, p.151
//Ch15, p.158, revised 
public class SnipersTableModel extends AbstractTableModel implements SniperListener {   
    private static String[] STATUS_TEXT = {
            "Joining", "Bidding", "Winning", "Lost", "Won"
    };
       
    //Copy snapshots and revise getRowCount(), getValueAt(), sniperStateChanged() and addSniper()
    //from https://github.com/sf105/goos-code/blob/master/src/auctionsniper/ui/SnipersTableModel.java
    private ArrayList<SniperSnapshot> snapshots = new ArrayList<SniperSnapshot>();
    
    public int getColumnCount() {
        return Column.values().length;
    }
    
    public int getRowCount() {
        return snapshots.size();
    }    

    //Ch15, p.170
    @Override public String getColumnName(int column) {
        return Column.at(column).name;
    }
    
    //Ch15, p.161 revised
    //Ch15, p.167 revised
    public Object getValueAt(int rowIndex, int columnIndex) {
        return Column.at(columnIndex).valueIn(snapshots.get(rowIndex));
    }

    //Ch15, p.161, 166 revised
    public void sniperStateChanged(SniperSnapshot newSnapshot) {
    	for (int i = 0; i < snapshots.size(); i++) {
    		if(newSnapshot.isForSameItemAs(snapshots.get(i))) {
    			snapshots.set(i, newSnapshot);
    			fireTableRowsUpdated(i, i);
    			return;
    		}    			
    	}
    	throw new Defect("No existing Sniper state for " + newSnapshot.itemId);
    }
    
    public static String textFor(SniperState state) {
        return STATUS_TEXT[state.ordinal()];
    }

	public void addSniper(SniperSnapshot newSniper) {
		snapshots.add(newSniper);
		int row = snapshots.size() - 1;
		fireTableRowsInserted(row, row);
	}
}
