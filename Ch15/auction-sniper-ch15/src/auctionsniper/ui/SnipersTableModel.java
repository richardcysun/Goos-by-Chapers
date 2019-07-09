package auctionsniper.ui;

import javax.swing.table.AbstractTableModel;

//Ch15, p.151
public class SniperTableModel extends AbstractTableModel{    
    private String statusText = "Joining";
    
    public int getColumnCount() {
        return 1;
    }
    
    public int getRowCount() {
        return 1;
    }    
    
    public Object getValueAt(int rowIndex, int ColumnIndex) {
        return statusText;
    }
    
    public void setStatusText(String newStatusText) {
        statusText = newStatusText;
        fireTableRowsUpdated(0, 0);
    }
}
