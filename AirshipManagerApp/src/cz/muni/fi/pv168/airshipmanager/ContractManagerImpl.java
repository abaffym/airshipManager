package cz.muni.fi.pv168.airshipmanager;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
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
    public void addContract(Contract c) {
        c.isValid();

        try (PreparedStatement st = connection.prepareStatement("INSERT INTO CONTRACT ("
                + "startDate, length, nameOfClient, airshipId, discount, paymentMethod)"
                + "VALUES (?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {

            st.setLong(1, c.getStartDate());
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
        c.isValid();
        
        try (PreparedStatement st = connection.prepareStatement("DELETE FROM CONTRACT WHERE id= ?")) {
            st.setLong(1, c.getId());
            st.execute();
            
        } catch (SQLException ex) {
            Logger.getLogger(ContractManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public Contract getContractById(Long id) {
        Contract c = null;
        try (PreparedStatement st = connection.prepareStatement("SELECT * FROM CONTRACT WHERE id= ?")) {
            st.setLong(1, id);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                c = buildContract(rs);
            }

        } catch (SQLException ex) {
            Logger.getLogger(ContractManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return c;
    }

    @Override
    public Contract getContractByAirship(Airship a) {
        Contract c = null;
        try (PreparedStatement st = connection.prepareStatement("SELECT * FROM CONTRACT WHERE airshipId= ?")) {
            st.setLong(1, a.getId());
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                c = buildContract(rs);
            }

        } catch (SQLException ex) {
            Logger.getLogger(ContractManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return c;
    }

    @Override
    public List<Contract> getAllContracts() {
        List<Contract> out = new ArrayList<>();
        try (PreparedStatement st = connection.prepareStatement("SELECT * FROM CONTRACT")) {
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                //System.out.println("ResultSet output: "+rs.getString("nameOfClient"));
                out.add(buildContract(rs));
            }
        } catch (SQLException ex) {
            Logger.getLogger(ContractManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return out;
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

    @Override
    public Contract getActiveByAirship(Airship a) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private Contract buildContract(ResultSet rs) throws SQLException {
        Contract c = new Contract();
        c.setId(rs.getLong("id")).setDiscount(rs.getFloat("discount")).setLength(rs.getInt("length"));
        c.setNameOfClient(rs.getString("nameOfClient")).setPaymentMethod(PaymentMethod.valueOf(rs.getString("paymentMethod")));
        c.setStartDate(rs.getLong("startDate"));

        return c;
    }
}
