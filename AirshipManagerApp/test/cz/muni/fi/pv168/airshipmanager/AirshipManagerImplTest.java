package cz.muni.fi.pv168.airshipmanager;

import java.math.BigDecimal;
import java.sql.SQLException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Marek Abaffy 422572
 */
public class AirshipManagerImplTest {

    private AirshipManagerImpl manager;

    @Before
    public void setUp() throws SQLException {
        manager = new AirshipManagerImpl();
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of addAirship method, of class AirshipManagerImpl.
     */
    @Test
    public void testAddAirship() {
        Airship airship = newAirship("AirshipOne", BigDecimal.valueOf(140), 50);
        manager.addAirship(airship);

        Long airshipId = airship.getId();
        assertNotNull(airshipId);
        Airship result = manager.getAirshipById(airship.getId());
        assertEquals(airship, result);
        assertNotSame(airship, result);
        assertDeepEquals(airship, result);
    }

    @Test
    public void testAddAirshipWithWrongAtributes() {
        try {
            manager.addAirship(null);
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }

        Airship airship = newAirship("AirshipOne", BigDecimal.valueOf(140), 50);
        airship.setId(1L);
        try {
            manager.addAirship(airship);
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }

        airship = newAirship("AirshipOne", BigDecimal.valueOf(0), 50);
        try {
            manager.addAirship(airship);
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }

        airship = newAirship("AirshipOne", BigDecimal.valueOf(140), 0);
        try {
            manager.addAirship(airship);
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }

        //OK variant
        airship = newAirship(null, BigDecimal.valueOf(140), 50);
        manager.addAirship(airship);
        Airship result = manager.getAirshipById(airship.getId());
        assertNotNull(result);
        assertNull(result.getName());

    }

    @Test
    public void testEditAirship() {
        Airship airship = newAirship("AirshipOne", BigDecimal.valueOf(140), 50);
        Airship airship2 = newAirship("AirshipTwo", BigDecimal.valueOf(120), 30);
        manager.addAirship(airship);
        manager.addAirship(airship2);
        Long airshipId = airship.getId();

        airship = manager.getAirshipById(airshipId);
        airship.setCapacity(1);
        manager.editAirship(airship);
        assertEquals("AirshipOne", airship.getName());
        assertEquals(BigDecimal.valueOf(140), airship.getPricePerDay());
        assertEquals(1, airship.getCapacity());

        airship = manager.getAirshipById(airshipId);
        airship.setPricePerDay(BigDecimal.valueOf(1));
        manager.editAirship(airship);
        assertEquals("AirshipOne", airship.getName());
        assertEquals(BigDecimal.valueOf(1), airship.getPricePerDay());
        assertEquals(1, airship.getCapacity());

        airship = manager.getAirshipById(airshipId);
        airship.setName("AirshipX");
        manager.editAirship(airship);
        assertEquals("AirshipX", airship.getName());
        assertEquals(BigDecimal.valueOf(1), airship.getPricePerDay());
        assertEquals(1, airship.getCapacity());

        // Check if updates didn't affected other records
        assertDeepEquals(airship2, manager.getAirshipById(airship2.getId()));
    }

    @Test
    public void testEditAirshipWithWrongAttributes() {

        Airship airship = newAirship("AirshipOne", BigDecimal.valueOf(140), 50);
        manager.addAirship(airship);
        Long airshipId = airship.getId();

        try {
            manager.editAirship(null);
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }

        try {
            airship = manager.getAirshipById(airshipId);
            airship.setId(null);
            manager.editAirship(airship);
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }

        try {
            airship = manager.getAirshipById(airshipId);
            airship.setId(airshipId - 1);
            manager.editAirship(airship);
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }

        try {
            airship = manager.getAirshipById(airshipId);
            airship.setCapacity(0);
            manager.editAirship(airship);
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }

        try {
            airship = manager.getAirshipById(airshipId);
            airship.setPricePerDay(BigDecimal.ZERO);
            manager.editAirship(airship);
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }
    }

    /**
     * Test of removeAirship method, of class AirshipManagerImpl.
     */
    @Test
    public void testRemoveAirship() {

        Airship airship1 = newAirship("AirshipOne", BigDecimal.valueOf(140), 50);
        Airship airship2 = newAirship("AirshipTwo", BigDecimal.valueOf(120), 30);
        manager.addAirship(airship1);
        manager.addAirship(airship2);

        assertNotNull(manager.getAirshipById(airship1.getId()));
        assertNotNull(manager.getAirshipById(airship1.getId()));

        manager.removeAirship(airship1);

        assertNull(manager.getAirshipById(airship1.getId()));
        assertNotNull(manager.getAirshipById(airship2.getId()));
    }

    @Test
    public void testRemoveAirshipWithWrongAttributes() {

        Airship airship = newAirship("AirshipOne", BigDecimal.valueOf(140), 50);

        try {
            manager.removeAirship(null);
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }

        try {
            airship.setId(null);
            manager.removeAirship(airship);
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }

        try {
            airship.setId(1L);
            manager.removeAirship(airship);
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }

    }

    /**
     * Test of getAirshipById method, of class AirshipManagerImpl.
     */
    @Test
    public void testGetAirshipById() {
        assertNull(manager.getAirshipById(1L));

        Airship airship = newAirship("AirshipOne", BigDecimal.valueOf(140), 50);
        manager.addAirship(airship);
        Long airshipId = airship.getId();

        Airship result = manager.getAirshipById(airshipId);
        assertEquals(airship, result);
        assertDeepEquals(airship, result);
    }

    /**
     * Test of getAirshipByCapacity method, of class AirshipManagerImpl.
     */
    @Test
    public void testGetAirshipByCapacity() {
    }

    /**
     * Test of getFreeAirships method, of class AirshipManagerImpl.
     */
    @Test
    public void testGetFreeAirships() {
    }

    /**
     * Test of isRented method, of class AirshipManagerImpl.
     */
    @Test
    public void testIsRented() {
    }

    private void assertDeepEquals(Airship expected, Airship actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getCapacity(), actual.getCapacity());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getPricePerDay(), actual.getPricePerDay());
    }

    private static Airship newAirship(String name, BigDecimal pricePerDay, int capacity) {
        Airship airship = new Airship();
        airship.setName(name);
        airship.setPricePerDay(pricePerDay);
        airship.setCapacity(capacity);
        return airship;
    }

}
