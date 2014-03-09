package cz.muni.fi.pv168.airshipmanager;

import java.sql.SQLException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 * @author Marek Abaffy
 */
public class AirshipManagerImplTest {

    private AirshipManagerImpl manager;

    @Before
    public void setUp() throws SQLException {
        manager = new AirshipManagerImpl();
    }

    @Test
    public void addAirship() {
        Airship airship = newAirship("AirshipOne", 140, 50);
        manager.addAirship(airship);

        Long airshipId = airship.getId();
        assertNotNull(airshipId);
        Airship result = manager.getAirshipById(airship.getId());
        assertEquals(airship, result);
        assertNotSame(airship, result);
        assertDeepEquals(airship, result);
    }
    
    @Test
    public void addAirshipWithWrongAtributes() {
        try {
            manager.addAirship(null);
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }
        
        Airship airship = newAirship("AirshipOne", 140, 50);
        airship.setId(1L);
        try {
            manager.addAirship(airship);
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }
        
        airship = newAirship("AirshipOne", -1, 50);
        try {
            manager.addAirship(airship);
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }
        
        airship = newAirship("AirshipOne", 140, 0);
        try {
            manager.addAirship(airship);
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }
            
    }

    private void assertDeepEquals(Airship expected, Airship actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getCapacity(), actual.getCapacity());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getPricePerDay(), actual.getPricePerDay());
    }

    private static Airship newAirship(String name, double pricePerDay, int capacity) {
        Airship airship = new Airship();
        airship.setName(name);
        airship.setPricePerDay(pricePerDay);
        airship.setCapacity(capacity);
        return airship;
    }
}
