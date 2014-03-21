package cz.muni.fi.pv168.airshipmanager;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements ComtractManager.
 *
 * @author Michal Štefánik 422237
 * @author Marek Abaffy 422572
 */
public class ContractManagerImpl implements ContractManager {

    private Connection connection;

    public ContractManagerImpl(Connection connection) {
        this.connection = connection;
    }
    
    @Override
    public void addContract(Contract c){
        c.isValid();
        
        try(PreparedStatement st = connection.prepareStatement("INSERT INTO CONTRACT ("+
                "startDate, length, nameOfClient, airship, discount, paymentMethod)"+
                "VALUES (?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)){
            
            st.setString(1, c.getStartDate().toString());
            st.setInt(2, c.getLength());
            st.setString(3, c.getNameOfClient());
            st.setLong(4, c.getAirship().getId());
            st.setFloat(5, c.getDiscount());
            st.setString(6, c.getPaymentMethod().toString());
            st.execute();
            
            //set SQL-generated ID for inserted contract 
            ResultSet rs = st.getGeneratedKeys();
            rs.next();
            c.setId(rs.getLong(1));
            
        } catch (SQLException ex) {
            Logger.getLogger(ContractManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }
    
    @Override
    public void editContract(Contract c) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removeContract(Contract c) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Contract getContractById(Long id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Contract getContractByAirship(Airship a) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Contract> getAllContracts() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Contract> getActiveContracts() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public BigDecimal getPrice(Contract c) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Date getEndDate(Contract c) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Contract> getAllByAirship(Airship a) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

//    @Override
    public Contract getActiveByAirship(Airship a) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
}
