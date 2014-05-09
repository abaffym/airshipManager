/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pv168.airshipmanagergui;

import cz.muni.fi.pv168.airshipmanager.Airship;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

public class AirshipsTableModel extends AbstractTableModel {

    private List<Airship> airships = new ArrayList<>();

    @Override
    public int getRowCount() {
        return airships.size();
    }

    @Override
    public int getColumnCount() {
        return 4;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Airship airship = airships.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return airship.getId();
            case 1:
                return airship.getName();
            case 2:
                return airship.getCapacity();
            case 3:
                return airship.getPricePerDay();
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return Long.class;
            case 1:
                return String.class;
            case 2:
                return Integer.class;
            case 3:
                return BigDecimal.class;
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }

   public void addAirship(Airship airship) {
        airships.add(airship);
        int lastRow = airships.size() - 1;
        fireTableRowsInserted(lastRow, lastRow);
    }
    
    public Airship getAirship(int rowIndex){
        Airship airship = airships.get(rowIndex);
        return airship;
    }
    
    public void removeAirship(Airship airship) {
        airships.remove(airship);
        int lastRow = airships.size() - 1;
        fireTableRowsDeleted(lastRow, lastRow);
    }
    
    public void removeAllAirship(){
         airships.clear();
         fireTableDataChanged();
    }


    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        Airship airship = airships.get(rowIndex);
        switch (columnIndex) {
            case 0:
                airship.setId((Long) aValue);
                break;
            case 1:
                airship.setName((String) aValue);
                break;
            case 2:
                airship.setCapacity((Integer) aValue);
                break;
            case 3:
                airship.setPricePerDay((BigDecimal) aValue);
                break;
            default:
                throw new IllegalArgumentException("columnIndex");
        }
        fireTableCellUpdated(rowIndex, columnIndex);
    }

    @Override
    public String getColumnName(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return java.util.ResourceBundle.getBundle("cz.muni.fi.pv168.airshipmanagergui/localization").getString("id");
            case 1:
                return java.util.ResourceBundle.getBundle("cz.muni.fi.pv168.airshipmanagergui/localization").getString("name");
            case 2:
                return java.util.ResourceBundle.getBundle("cz.muni.fi.pv168.airshipmanagergui/localization").getString("capacity");
            case 3:
                return java.util.ResourceBundle.getBundle("cz.muni.fi.pv168.airshipmanagergui/localization").getString("pricePerDay");
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }
}