package cz.muni.fi.pv168.airshipmanager;

import java.awt.print.Book;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements AirshipManager
 *
 * @author Michal Štefánik 422237
 * @author Marek Abaffy 422572
 */
public class AirshipManagerImpl implements AirshipManager {

    private Connection connection;

    AirshipManagerImpl(Connection conn) {
        this.connection = conn;
    }

    @Override
    public void addAirship(Airship airship) {
        if (airship == null) {
            throw new IllegalArgumentException("Airship is null");
        }
        if (airship.getId() != null) {
            throw new IllegalArgumentException("Id is not null");
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

        try (PreparedStatement st = connection.prepareStatement("INSERT INTO AIRSHIP ("
                + "name, price, capacity) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {

            st.setString(1, airship.getName());
            st.setBigDecimal(2, airship.getPricePerDay());
            st.setInt(3, airship.getCapacity());
            st.execute();

            ResultSet keyRS = st.getGeneratedKeys();
            keyRS.next();
            airship.setId(keyRS.getLong(1));

        } catch (SQLException ex) {
            Logger.getLogger(AirshipManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
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
        if (airship.getPricePerDay() == null) {
            throw new IllegalArgumentException("Price is null");
        }
        if (airship.getPricePerDay().doubleValue() <= 0) {
            throw new IllegalArgumentException("Price is negative or zero");
        }
        if (airship.getCapacity() <= 0) {
            throw new IllegalArgumentException("Capacity is negative or zero");
        }
        
        try(PreparedStatement st = connection.prepareStatement("UPDATE AIRSHIP SET "
                + "name=?, price=?, capacity=? WHERE ID = ?")) {
            st.setString(1, airship.getName());
            st.setBigDecimal(2, airship.getPricePerDay());
            st.setInt(3, airship.getCapacity());
            st.setLong(4, airship.getId());
            
            st.executeUpdate();
            
        } catch (SQLException ex) {
            Logger.getLogger(AirshipManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
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
        
        try (PreparedStatement st = connection.prepareStatement("DELETE FROM AIRSHIP WHERE ID = ?")) {
            st.setLong(1, airship.getId());
            st.execute();
            
        } catch (SQLException ex) {
            Logger.getLogger(AirshipManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public Airship getAirshipById(Long id) throws SQLException {
        if (id == null) {
            throw new IllegalArgumentException("Id is null");
        }
        Airship airship = null;
        try (PreparedStatement st = connection.prepareStatement("SELECT ID,NAME,PRICE,CAPACITY FROM AIRSHIP WHERE ID = ?")) {
            st.setLong(1, id);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                airship = resultSetToAirship(rs);
            }

        } catch (SQLException ex) {
            Logger.getLogger(AirshipManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return airship;

    }

    @Override
    public List<Airship> getAllAirships() {
        List<Airship> airships = new ArrayList<>();
        try (PreparedStatement st = connection.prepareStatement("SELECT ID,NAME,PRICE,CAPACITY FROM AIRSHIP")) {
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                airships.add(resultSetToAirship(rs));
            }
        } catch (SQLException ex) {
            Logger.getLogger(AirshipManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return airships;

    }

    @Override
    public List<Airship> getAllAirshipsByCapacity(int capacity) {
        List<Airship> airships = new ArrayList<>();
        try (PreparedStatement st = connection.prepareStatement(
                "SELECT ID,NAME,PRICE,CAPACITY FROM AIRSHIP WHERE CAPACITY >= ?")) {
            st.setInt(1, capacity);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                airships.add(resultSetToAirship(rs));
            }
        } catch (SQLException ex) {
            Logger.getLogger(AirshipManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return airships;
    }

    @Override
    public List<Airship> getFreeAirships() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isRented(Airship a) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private Airship resultSetToAirship(ResultSet rs) throws SQLException {
        Airship airship = new Airship();
        airship.setId(rs.getLong("id")).setCapacity(rs.getInt("capacity")).setName(rs.getString(
                "name")).setPricePerDay(rs.getBigDecimal("price"));
        return airship;
    }
}
