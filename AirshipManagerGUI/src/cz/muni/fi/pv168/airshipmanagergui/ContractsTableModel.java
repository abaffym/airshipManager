/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pv168.airshipmanagergui;

import cz.muni.fi.pv168.airshipmanager.Contract;
import cz.muni.fi.pv168.airshipmanager.PaymentMethod;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.table.AbstractTableModel;

public class ContractsTableModel extends AbstractTableModel {

    private List<Contract> contracts = new ArrayList<>();

    @Override
    public int getRowCount() {
        return contracts.size();
    }

    @Override
    public int getColumnCount() {
        return 7;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Contract contract = contracts.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return contract.getId();
            case 1:
                return contract.getNameOfClient();
            case 2:
                return contract.getAirship().getName();
            case 3:
                return contract.getStartDate();
            case 4:
                return contract.getLength();
            case 5:
                return contract.getDiscount();
            case 6:
                return contract.getPaymentMethod();
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
                return String.class;
            case 3:
                return Date.class;
            case 4:
                return Integer.class;
            case 5:
                return Float.class;
            case 6:
                return PaymentMethod.class;
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }

   public void addContract(Contract contract) {
        contracts.add(contract);
        int lastRow = contracts.size() - 1;
        fireTableRowsInserted(lastRow, lastRow);
    }
    
    public Contract getContract(int rowIndex){
        Contract contract = contracts.get(rowIndex);
        return contract;
    }
    
    public void removeContract(Contract contract) {
        contracts.remove(contract);
        int lastRow = contracts.size() - 1;
        fireTableRowsDeleted(lastRow, lastRow);
    }
    
    public void removeAllContract(){
         contracts.clear();
         fireTableDataChanged();
    }


    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        Contract contract = contracts.get(rowIndex);
        switch (columnIndex) {
            case 0:
                contract.setId((Long) aValue);
                break;
            case 1:
                contract.setNameOfClient((String) aValue);
                break;
            case 2:
               // contract.set;
                break;
            case 3:
                contract.setStartDate((java.sql.Date) aValue);
                break;
            case 4:
                contract.setLength((int) aValue);
                break;
            case 5:
                contract.setDiscount((float) aValue);
                break;
            case 6:
                contract.setPaymentMethod((PaymentMethod) aValue);
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
                return java.util.ResourceBundle.getBundle("cz.muni.fi.pv168.airshipmanagergui/localization").getString("nameOfClient");
            case 2:
                return java.util.ResourceBundle.getBundle("cz.muni.fi.pv168.airshipmanagergui/localization").getString("name");
            case 3:
                return java.util.ResourceBundle.getBundle("cz.muni.fi.pv168.airshipmanagergui/localization").getString("startDate");
            case 4:
                return java.util.ResourceBundle.getBundle("cz.muni.fi.pv168.airshipmanagergui/localization").getString("length");
            case 5:
                return java.util.ResourceBundle.getBundle("cz.muni.fi.pv168.airshipmanagergui/localization").getString("discount");
            case 6:
                return java.util.ResourceBundle.getBundle("cz.muni.fi.pv168.airshipmanagergui/localization").getString("paymentMethod");
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }
}