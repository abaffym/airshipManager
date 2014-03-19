package cz.muni.fi.pv168.airshipmanager;

import java.awt.print.Book;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

    private String url = "jdbc:mysql://localhost:1527/airshipDB?useUnicode=true";
    private final String USER = "root";
    private final String PASSWD = "root";
    
    @Override
    public void addAirship(Airship airship) {
        if (airship == null) {
            throw new IllegalArgumentException("Airship is null");
        }
        if (airship.getId() != null) {
            throw new IllegalArgumentException("Id is not null");
        }
        if (airship.getPricePerDay() == null || airship.getPricePerDay().doubleValue() <= 0) {
            throw new IllegalArgumentException("Price is invalid");
        }
        if (airship.getCapacity() <= 0) {
            throw new IllegalArgumentException("Capacity is negative or zero");
        }
        
        try (Connection conn = DriverManager.getConnection(url, USER, PASSWD)) {
            try (PreparedStatement st = conn.prepareStatement("INSERT INTO AIRSHIPTABLE ("
                    + "id, name, price, capacity) VALUES ( null, ?, ?, ?);")) {
                st.setString(1, airship.getName());
                st.setBigDecimal(2, airship.getPricePerDay());
                st.setInt(3, airship.getCapacity());
                
                st.execute();
            }
            
            
        } catch (SQLException ex) {
            Logger.getLogger(AirshipManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    @Override
    public void editAirship(Airship airship) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removeAirship(Airship airship) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Airship getAirshipById(Long id) throws SQLException {
        if(id==null){
            throw new IllegalArgumentException("id is null");
        }
        
        try (Connection conn = DriverManager.getConnection(url, USER, PASSWD)) {
            try (PreparedStatement st = conn.prepareStatement("SELECT * FROM AIRSHIPTABLE WHERE ID = ?;")) {
                st.setLong(1, id);
                ResultSet rs = st.executeQuery();
                if(rs.next()){
                    return resultSetToAirship(rs);
                } else {
                    return null;
                }
                
            }
        } catch(SQLException ex) {
            Logger.getLogger(AirshipManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }
        
    }

    @Override
    public List<Airship> getAllAirships() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.

    }

    @Override
    public List<Airship> getAllAirshipsByCapacity(int capacity) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
        Airship a = new Airship();
        a.setId(rs.getLong("id")).setCapacity(rs.getInt("capacity")).setName(rs.getString(
                "name")).setPricePerDay(rs.getBigDecimal("price"));
        return a;
    }
}
