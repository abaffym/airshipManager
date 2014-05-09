package cz.muni.fi.pv168.airshipmanager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
//import java.util.logging.Level;
//import java.util.logging.Logger;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * This class implements AirshipManager
 *
 * @author Michal Štefánik 422237
 * @author Marek Abaffy 422572
 */
public class AirshipManagerImpl implements AirshipManager {

    private DataSource dataSource;
    private final Logger log = LoggerFactory.getLogger(AirshipManagerImpl.class);

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private void checkDataSource() {
        if (dataSource == null) {
            throw new IllegalStateException("DataSource is not set");
        }
    }

    @Override
    public void addAirship(Airship airship) {
        
        checkDataSource();
        if (airship == null) {
            throw new IllegalArgumentException("Airship is null");
        }
        if (airship.getId() != null) {
            throw new IllegalArgumentException("Id is not null");
        }
        if (airship.getPricePerDay() == null) {
            throw new IllegalArgumentException("Price is null");
        }
        if (airship.getPricePerDay().signum() <= 0) {
            throw new IllegalArgumentException("Price is negative or zero");
        }
        if (airship.getCapacity() <= 0) {
            throw new IllegalArgumentException("Capacity is negative or zero");
        }
        log.info("Attepmt to add airship given: "+airship.toString());
        
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement st = connection.prepareStatement("INSERT INTO AIRSHIP ("
                    + "name, price, capacity) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {

                st.setString(1, airship.getName());
                st.setBigDecimal(2, airship.getPricePerDay());
                st.setInt(3, airship.getCapacity());
                st.execute();

                ResultSet keyRS = st.getGeneratedKeys();
                keyRS.next();
                airship.setId(keyRS.getLong(1));
            }
        } catch (SQLException ex) {
            log.error(ex.toString());
        }
    }

    @Override
    public void editAirship(Airship airship) {
        if (airship == null) {
            throw new IllegalArgumentException("Airship is null");
        }
        if (airship.getId() == null) {
            throw new IllegalArgumentException("Id is null");
        }
        if (airship.getId() < 1) {
            throw new IllegalArgumentException("Id is negative or zero");
        }
        if (airship.getPricePerDay() == null) {
            throw new IllegalArgumentException("Price is null");
        }
        if (airship.getPricePerDay().doubleValue() <= 0) {
            throw new IllegalArgumentException("Price is negative or zero");
        }
        if (airship.getCapacity() <= 0) {
            throw new IllegalArgumentException("Capacity is negative or zero");
        }
        log.info("Attempt to edit airship: "+airship.toString());
        
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement st = connection.prepareStatement("UPDATE AIRSHIP SET "
                    + "name = ?, price = ?, capacity = ? WHERE ID = ?")) {
                st.setString(1, airship.getName());
                st.setBigDecimal(2, airship.getPricePerDay());
                st.setInt(3, airship.getCapacity());
                st.setLong(4, airship.getId());

                st.executeUpdate();
            }
        } catch (SQLException ex) {
            log.error(ex.toString());
        }
    }

    @Override
    public void removeAirship(Airship airship) {
        if (airship == null) {
            throw new IllegalArgumentException("Airship is null");
        }
        if (airship.getId() == null) {
            throw new IllegalArgumentException("Id is null");
        }
        log.info("Attempt to remove airship: "+airship.toString());
        
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement st = connection.prepareStatement("DELETE FROM AIRSHIP WHERE ID = ?")) {
                st.setLong(1, airship.getId());
                st.execute();
            }
        } catch (SQLException ex) {
            log.error(ex.toString());
        }
    }

    @Override
    public Airship getAirshipById(Long id) throws SQLException {
        log.info("Attempt to get airship by id with given id: "+id.toString());
        
        if (id == null) {
            throw new IllegalArgumentException("Id is null");
        }
        Airship airship = null;
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement st = connection.prepareStatement("SELECT ID,NAME,PRICE,CAPACITY FROM AIRSHIP WHERE ID = ?")) {
                st.setLong(1, id);
                ResultSet rs = st.executeQuery();
                if (rs.next()) {
                    airship = resultSetToAirship(rs);
                }
            }
        } catch (SQLException ex) {
            log.error(ex.toString());
        }
        return airship;

    }

    @Override
    public List<Airship> getAllAirships() {
        log.info("Attempt to get all airships");
        
        List<Airship> airships = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement st = connection.prepareStatement("SELECT ID,NAME,PRICE,CAPACITY FROM AIRSHIP")) {
                ResultSet rs = st.executeQuery();
                while (rs.next()) {
                    airships.add(resultSetToAirship(rs));
                }
            }
        } catch (SQLException ex) {
            log.error(ex.toString());
        }
        return airships;

    }

    @Override
    public List<Airship> getAllAirshipsByCapacity(int capacity) {
        log.info("Attempt to get all airships with higher or equal capacity, given: "+capacity);
        
        List<Airship> airships = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement st = connection.prepareStatement(
                    "SELECT ID,NAME,PRICE,CAPACITY FROM AIRSHIP WHERE CAPACITY >= ?")) {
                st.setInt(1, capacity);
                ResultSet rs = st.executeQuery();
                while (rs.next()) {
                    airships.add(resultSetToAirship(rs));
                }
            }
        } catch (SQLException ex) {
            log.error(ex.toString());
        }
        return airships;
    }

    @Override
    public List<Airship> getFreeAirships() {
        log.info("Attempt to get all free airships");
        
        List<Airship> free = new ArrayList<>();
        for (Airship airship : getAllAirships()) {
            if (!isRented(airship)) {
                free.add(airship);
            }
        }
        return free;
    }

    @Override
    public boolean isRented(Airship airship) {
        log.info("Attempt to trigger isRented method, given airship: "+airship.toString());
        
        ContractManagerImpl cManager = null;
        try (Connection connection = dataSource.getConnection()) {
            cManager = new ContractManagerImpl();
            cManager.setDataSource(dataSource);
            
        } catch (SQLException ex) {
            log.error(ex.toString());
        }
        return (cManager.getActiveByAirship(airship) != null);

    }

    private Airship resultSetToAirship(ResultSet rs) throws SQLException {
        Airship airship = new Airship();
        airship.setId(rs.getLong("id"))
                .setCapacity(rs.getInt("capacity"))
                .setName(rs.getString("name"))
                .setPricePerDay(rs.getBigDecimal("price"));

        return airship;
    }
}
