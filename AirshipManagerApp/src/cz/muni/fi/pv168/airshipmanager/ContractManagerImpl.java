package cz.muni.fi.pv168.airshipmanager;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;

/**
 * This class implements ComtractManager.
 *
 * @author Michal Štefánik 422237
 * @author Marek Abaffy 422572
 */
public class ContractManagerImpl implements ContractManager {
    
    private final AirshipManagerImpl airships;
    private Connection conn;
    
    public ContractManagerImpl(){
        airships = new AirshipManagerImpl();
    }
    
    public ContractManagerImpl(Connection conn){
        this.conn = conn;
        airships = new AirshipManagerImpl();
    }
    
    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
        airships.setDataSource(dataSource);
    }

    private void checkDataSource() {
        if (dataSource == null) {
            throw new IllegalStateException("DataSource is not set");
        }
    }

    @Override
    public void addContract(Contract contract) {
        contract.isValid();
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement st = connection.prepareStatement("INSERT INTO CONTRACT ("
                    + "startDate, length, nameOfClient, airshipId, discount, paymentMethod)"
                    + "VALUES (?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {

                st.setDate(1, contract.getStartDate());
                st.setInt(2, contract.getLength());
                st.setString(3, contract.getNameOfClient());
                st.setLong(4, contract.getAirship().getId());
                st.setFloat(5, contract.getDiscount());
                st.setString(6, contract.getPaymentMethod().name());
                st.execute();

                ResultSet keyRS = st.getGeneratedKeys();
                keyRS.next();
                contract.setId(keyRS.getLong(1));
            }
        } catch (SQLException ex) {
            Logger.getLogger(ContractManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void editContract(Contract contract) {
        if (contract == null) {
            throw new IllegalArgumentException("Contract in editAirship() is null");
        } else {
            contract.isValid();
        }
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement st = connection.prepareStatement("UPDATE CONTRACT SET"
                    + " startDate = ?, length = ?, nameOfClient = ?, airshipId = ?, discount = ?, paymentMethod = ?"
                    + " WHERE ID = ?")) {
                st.setDate(1, contract.getStartDate());
                st.setInt(2, contract.getLength());
                st.setString(3, contract.getNameOfClient());
                st.setLong(4, contract.getAirship().getId());
                st.setFloat(5, contract.getDiscount());
                st.setString(6, contract.getPaymentMethod().name());

                st.executeUpdate();
            }
        } catch (SQLException ex) {
            Logger.getLogger(ContractManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public void removeContract(Contract contract) {
        if (contract == null) {
            throw new IllegalArgumentException("Contract in removeAirship() is null");
        } else if(contract.getId() == null) {
            throw new IllegalArgumentException("Contract ID in removeAirship() is null");
        } else {
            contract.isValid();
        }
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement st = connection.prepareStatement("DELETE FROM CONTRACT WHERE Id= ?")) {
                st.setLong(1, contract.getId());
                st.execute();
            }
        } catch (SQLException ex) {
            Logger.getLogger(ContractManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public Contract getContractById(Long id) {
        Contract contract = null;
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement st = connection.prepareStatement("SELECT * FROM CONTRACT WHERE id= ?")) {
                st.setLong(1, id);
                ResultSet rs = st.executeQuery();
                if (rs.next()) {
                    contract = resultSetToContract(rs);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ContractManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return contract;
    }

    @Override
    public Contract getActiveByAirship(Airship airship) {
        List<Contract> all = getAllByAirship(airship);
        for (Contract c : all) {
            if(isActive(c)){
                return c;
            }
        }
        return null;
    }

    @Override
    public List<Contract> getAllContracts() {
        List<Contract> contracts = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement st = connection.prepareStatement("SELECT * FROM CONTRACT")) {
                ResultSet rs = st.executeQuery();
                while (rs.next()) {
                    contracts.add(resultSetToContract(rs));
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ContractManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return contracts;
    }

    @Override
    public List<Contract> getActiveContracts() {
        List<Contract> active = new ArrayList<>();
        for (Contract c : getAllContracts()) {
            if (isActive(c)) {
                active.add(c);
            }
        }
        return active;
    }

    @Override
    public BigDecimal getPrice(Contract contract) {
        return contract.getAirship().getPricePerDay().multiply(BigDecimal.valueOf(contract.getDiscount() * contract.getLength()));
        //return BigDecimal.valueOf(contract.getLength() * contract.getAirship().getPricePerDay().longValue());
    }

    @Override
    public Date getEndDate(Contract contract) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(contract.getStartDate());
        cal.add(Calendar.DATE, contract.getLength());
        return cal.getTime();
    }

    /**
     * Determines whether the contract is still active
     *
     * @param contract given contract
     * @return true if contract is still active, otherwise false
     */
    public static boolean isActive(Contract contract) {
        if(contract.getStartDate().after(Calendar.getInstance(Locale.ENGLISH).getTime())){
            return false;
        }
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(contract.getStartDate());
        cal.add(Calendar.DATE, contract.getLength());
        
        if(cal.before(cal.getTime())){
            return false;
        }
        return true;
    }

    @Override
    public List<Contract> getAllByAirship(Airship airship) {
        if (airship == null) {
            throw new IllegalArgumentException("Airship is null");
        }
        if (airship.getId() == null) {
            throw new IllegalArgumentException("Id is null");
        }
        if (airship.getId() < 1) {
            throw new IllegalArgumentException("Id is negative or zero");
        }

        List<Contract> out = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement st = connection.prepareStatement("SELECT * FROM CONTRACT WHERE airshipId = ?")) {
                st.setLong(1, airship.getId());
                ResultSet rs = st.executeQuery();

                while (rs.next()) {
                    out.add(resultSetToContract(rs));
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ContractManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        return out;
    }

    private Contract resultSetToContract(ResultSet rs) throws SQLException {
        Contract contract = new Contract();
        contract.setId(rs.getLong("id"))
                .setDiscount(rs.getFloat("discount"))
                .setLength(rs.getInt("length"))
                .setNameOfClient(rs.getString("nameOfClient"))
                .setPaymentMethod(PaymentMethod.valueOf(rs.getString("paymentMethod")))
                .setStartDate(rs.getDate("startDate"))
                .setAirship(airships.getAirshipById(rs.getLong("airshipId")));

        return contract;
    }

}
