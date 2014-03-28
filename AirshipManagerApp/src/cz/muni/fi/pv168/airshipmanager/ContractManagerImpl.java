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
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements ComtractManager.
 *
 * @author Michal Štefánik 422237
 * @author Marek Abaffy 422572
 */
public class ContractManagerImpl implements ContractManager {

    private final Connection connection;
    private final AirshipManagerImpl airships;

    public ContractManagerImpl(Connection connection) {
        this.connection = connection;
        airships = new AirshipManagerImpl();
    }

    @Override
    public void addContract(Contract contract) {
        contract.isValid();

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
        } catch (SQLException ex) {
            Logger.getLogger(ContractManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public void removeContract(Contract contract) {
        contract.isValid();

        try (PreparedStatement st = connection.prepareStatement("DELETE FROM CONTRACT WHERE Id= ?")) {
            st.setLong(1, contract.getId());
            st.execute();

        } catch (SQLException ex) {
            Logger.getLogger(ContractManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public Contract getContractById(Long id) {
        Contract contract = null;
        try (PreparedStatement st = connection.prepareStatement("SELECT * FROM CONTRACT WHERE id= ?")) {
            st.setLong(1, id);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                contract = resultSetToContract(rs);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ContractManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return contract;
    }

    @Override
    public Contract getActiveByAirship(Airship airship) {
        Contract contract = null;
        try (PreparedStatement st = connection.prepareStatement("SELECT * FROM CONTRACT WHERE airshipId= ?")) {
            st.setLong(1, airship.getId());
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                contract = resultSetToContract(rs);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ContractManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return contract;
    }

    @Override
    public List<Contract> getAllContracts() {
        List<Contract> contracts = new ArrayList<>();
        try (PreparedStatement st = connection.prepareStatement("SELECT * FROM CONTRACT")) {
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                contracts.add(resultSetToContract(rs));
            }
        } catch (SQLException ex) {
            Logger.getLogger(ContractManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return contracts;
    }

//    @Override
//    public List<Contract> getActiveContracts() {
//        List<Contract> all = getAllContracts();
//        List<Contract> active = new ArrayList<>();
//
//        Date actual = new Date();
//        Long actualDate = actual.getTime();
//
//        for (Contract c : all) {
//            Long endDate = c.getStartDate() + TimeUnit.MICROSECONDS.convert(c.getLength(), TimeUnit.DAYS);
//            if (endDate < actualDate) {
//                active.add(c);
//            }
//        }
//
//        return active;
//    }
    
      @Override
      public List<Contract> getActiveContracts(){
          List<Contract> active = new ArrayList<>();
          for (Contract c: getAllContracts()) {
              if (isActive(c)) {
                  active.add(c);
              }
          }
          return active;
      }

//    @Override
//    public BigDecimal getPrice(Contract contract) {
//        if (contract == null) {
//            throw new IllegalArgumentException("getPrice: input contract is null");
//        } else {
//            contract.isValid();
//        }
//
//        int length = 0;
//        float discount = 0;
//        long airshipId = 0;
//        BigDecimal priceDay = new BigDecimal(0);
//
//        try (PreparedStatement st = connection.prepareStatement("SELECT length, discount, airshipId FROM CONTRACT WHERE id = ?")) {
//            st.setLong(1, contract.getId());
//            ResultSet rs = st.executeQuery();
//
//            while (rs.next()) {
//                length = rs.getInt("length");
//                discount = rs.getFloat("discount");
//                airshipId = rs.getLong("airshipId");
//            }
//            try (PreparedStatement st2 = connection.prepareStatement("SELECT pricePerDay FROM AIRSHIP WHERE ID = ?")) {
//                st2.setLong(1, airshipId);
//                ResultSet rs2 = st2.executeQuery();
//
//                while (rs.next()) {
//                    priceDay = rs.getBigDecimal("pricePerDay");
//                }
//            }
//
//        } catch (SQLException ex) {
//            Logger.getLogger(ContractManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
//        }
//
//        return priceDay.multiply(BigDecimal.valueOf(length * discount));
//    }
    
    @Override
    public BigDecimal getPrice(Contract contract) {
        return BigDecimal.valueOf(contract.getLength() * contract.getAirship().getPricePerDay().longValue());
    }


//    @Override
//    public Date getEndDate(Contract c) {
//        if (c == null) {
//            throw new IllegalArgumentException("getEndDate: input contract is null");
//        } else {
//            c.isValid();
//        }
//        long startDate = 0;
//        int length = 0;
//        Date outDate = new Date();
//
//        try (PreparedStatement st = connection.prepareStatement("SELECT length, startDate FROM CONTRACT WHERE id = ?")) {
//            st.setLong(1, c.getId());
//            ResultSet rs = st.executeQuery();
//
//            while (rs.next()) {
//                startDate = rs.getLong("startDate");
//                length = rs.getInt("length");
//            }
//        } catch (SQLException ex) {
//            Logger.getLogger(ContractManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        outDate.setTime(startDate + TimeUnit.MICROSECONDS.convert(length, TimeUnit.DAYS));
//        return outDate;
//    }
    
    @Override
    public Date getEndDate(Contract contract) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(contract.getStartDate());
        cal.add(Calendar.DATE, contract.getLength());
        return cal.getTime();
    }
    
    public boolean isActive(Contract contract) {
       return contract.getStartDate().after(Calendar.getInstance(Locale.ENGLISH).getTime());
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
        
        try (PreparedStatement st = connection.prepareStatement("SELECT * FROM CONTRACT WHERE airshipId = ?")) {
            st.setLong(1, airship.getId());
            ResultSet rs = st.executeQuery();
            
            while(rs.next()){
                out.add(resultSetToContract(rs));
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
