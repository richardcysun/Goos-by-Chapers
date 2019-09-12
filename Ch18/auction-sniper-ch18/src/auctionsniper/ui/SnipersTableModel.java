package auctionsniper.ui;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import com.objogate.exception.Defect;

import auctionsniper.AuctionSniper;
import auctionsniper.PortfolioListener;
import auctionsniper.SniperListener;
import auctionsniper.SniperSnapshot;
import auctionsniper.SniperState;

//Ch15, p.151
//Ch15, p.158, revised
//Ch17, revised, not in the book
public class SnipersTableModel extends AbstractTableModel implements SniperListener, PortfolioListener {
    private static String[] STATUS_TEXT = {
            "Joining", "Bidding", "Winning", "Losing", "Lost", "Won"
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
    //Ch16, p.182 revised
    public void sniperStateChanged(SniperSnapshot newSnapshot) {
    	int row = rowMatching(newSnapshot);
    	snapshots.set(row, newSnapshot);
    	fireTableRowsUpdated(row, row);
    }
    
    //Ch16, p.182
    private int rowMatching(SniperSnapshot snapshot) {
    	for (int i = 0; i < snapshots.size(); i++) {
    		if(snapshot.isForSameItemAs(snapshots.get(i))) {
    			return i;
    		}
    	}
    	
    	throw new Defect("Cannot find match for " + snapshot);
	}

	public static String textFor(SniperState state) {
        return STATUS_TEXT[state.ordinal()];
    }

	//Ch17, p.199, renamed from addSniper() to addSniperSnapshot()
	public void addSniperSnapshot(SniperSnapshot sniperSnapshot) {
		snapshots.add(sniperSnapshot);
		int row = snapshots.size() - 1;
		fireTableRowsInserted(row, row);
	}
	
	//Ch17, p.199 new function, the book misplaced sniperAdded() as addSniper()  
	public void sniperAdded(AuctionSniper sniper) 
	{
	    addSniperSnapshot(sniper.getSnapshot());
	    sniper.addSniperListener(new SwingThreadSniperListener(this));		
	};
}
