package cz.muni.fi.pv168.airshipmanager;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
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
    private Connection conn;

    @Before
    public void setUp() throws SQLException {
        conn = DriverManager.getConnection("jdbc:derby:memory:AirshipManagerImplTest;create=true");
        conn.prepareStatement("CREATE TABLE AIRSHIP("
                + "ID BIGINT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY,"
                + "NAME VARCHAR(50),"
                + "CAPACITY INTEGER,"
                + "PRICE DECIMAL)").executeUpdate();

        manager = new AirshipManagerImpl(conn);
    }

    @After
    public void tearDown() throws SQLException {
        conn.prepareStatement("DROP TABLE AIRSHIP").executeUpdate();
        conn.close();
    }

    /**
     * Test of addAirship method, of class AirshipManagerImpl.
     *
     * @throws java.sql.SQLException
     */
    @Test
    public void testAddAirship() throws SQLException {
        Airship airship = newAirship("AirshipOne", BigDecimal.valueOf(140), 50);
        manager.addAirship(airship);

        Long airshipId = airship.getId();
        Airship result = manager.getAirshipById(airship.getId());
        assertNotNull(airshipId);
        assertEquals(airship, result);
        assertNotSame(airship, result);
        assertDeepEquals(airship, result);
    }

    @Test
    public void testAddAirshipNullName() throws SQLException {
        Airship airship = newAirship(null, BigDecimal.valueOf(140), 50);
        manager.addAirship(airship);
        Long airshipId = airship.getId();
        Airship result = manager.getAirshipById(airshipId);
        assertNotNull(result);
        assertNull(result.getName());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddAirshipNullAirship() {
        manager.addAirship(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddAirshipNotNullId() {
        Airship airship = newAirship("AirshipOne", BigDecimal.valueOf(140), 50);
        airship.setId(1L);
        manager.addAirship(airship);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddAirshipZeroPrice() {
        Airship airship = newAirship("AirshipOne", BigDecimal.valueOf(0), 50);
        manager.addAirship(airship);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddAirshipNegativePrice() {
        Airship airship = newAirship("AirshipOne", BigDecimal.valueOf(-1), 50);
        manager.addAirship(airship);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddAirshipZeroCapacity() {
        Airship airship = newAirship("AirshipOne", BigDecimal.valueOf(1), 0);
        manager.addAirship(airship);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddAirshipNegativeCapacity() {
        Airship airship = newAirship("AirshipOne", BigDecimal.valueOf(1), -1);
        manager.addAirship(airship);
    }

    @Test
    public void testEditAirship() throws SQLException {
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
        assertEquals(50, airship.getCapacity());

        airship = manager.getAirshipById(airshipId);
        airship.setName("AirshipX");
        manager.editAirship(airship);
        assertEquals("AirshipX", airship.getName());
        assertEquals(BigDecimal.valueOf(140), airship.getPricePerDay());
        assertEquals(50, airship.getCapacity());

        // Check if updates didn't affected other records
        assertDeepEquals(airship2, manager.getAirshipById(airship2.getId()));
    }

    @Test
    public void testEditAirshipWithWrongAttributes() throws SQLException {

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
        }

//        try {
//            airship = manager.getAirshipById(airshipId);
//            airship.setId(airshipId - 1);
//            manager.editAirship(airship);
//            fail();
//        } catch (IllegalArgumentException ex) {
//            //OK
//        }

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
     *
     * @throws java.sql.SQLException
     */
    @Test
    public void testRemoveAirship() throws SQLException {
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
    
     @Test(expected = IllegalArgumentException.class)
    public void testRemoveAirshipNullAirship() {
        manager.removeAirship(null);
    }

//    @Test(expected = IllegalArgumentException.class)
//    public void testRemoveAirshipWrongId() {
//        Airship airship = newAirship("AirshipOne", BigDecimal.valueOf(140), 50);
//        airship.setId(1L);
//        manager.removeAirship(airship);
//    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testRemoveAirshipNullId() {
        Airship airship = newAirship("AirshipOne", BigDecimal.valueOf(140), 50);
        airship.setId(null);
        manager.removeAirship(airship);
    }

    /**
     * Test of getAirshipById method, of class AirshipManagerImpl.
     * @throws java.sql.SQLException
     */
    @Test
    public void testGetAirshipById() throws SQLException {
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
        Airship airship = newAirship("AirshipOne", BigDecimal.valueOf(140), 50);
        Airship airship2 = newAirship("AirshipTwo", BigDecimal.valueOf(120), 30);
        Airship airship3 = newAirship("AirshipThre", BigDecimal.valueOf(120), 10);
        
        manager.addAirship(airship);
        manager.addAirship(airship2);
        manager.addAirship(airship3);
        
        List<Airship> expected = Arrays.asList(airship, airship2);
        List<Airship> actual = manager.getAllAirshipsByCapacity(30);
        
        Collections.sort(actual, idComparator);
        Collections.sort(expected, idComparator);
        
        assertEquals(expected, actual);
        assertDeepEquals(expected, actual);
    }

    @Test
    public void testGetAllAirships() {
        assertTrue(manager.getAllAirships().isEmpty());

        Airship airship = newAirship("AirshipOne", BigDecimal.valueOf(140), 50);
        Airship airship2 = newAirship("AirshipTwo", BigDecimal.valueOf(120), 30);

        manager.addAirship(airship);
        manager.addAirship(airship2);

        List<Airship> expected = Arrays.asList(airship, airship2);
        List<Airship> actual = manager.getAllAirships();
        Collections.sort(actual, idComparator);
        Collections.sort(expected, idComparator);

        assertEquals(expected, actual);
        assertDeepEquals(expected, actual);
    }

    /**
     * Test of getFreeAirships method, of class AirshipManagerImpl.
     */
    @Test
    public void testGetFreeAirships() {
        fail();
    }

    /**
     * Test of isRented method, of class AirshipManagerImpl.
     */
    @Test
    public void testIsRented() {
        fail();
    }

    private void assertDeepEquals(Airship expected, Airship actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getCapacity(), actual.getCapacity());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getPricePerDay(), actual.getPricePerDay());
    }

    private void assertDeepEquals(List<Airship> expectedList, List<Airship> actualList) {
        for (int i = 0; i < expectedList.size(); i++) {
            Airship expected = expectedList.get(i);
            Airship actual = actualList.get(i);
            assertDeepEquals(expected, actual);
        }
    }

    private static Airship newAirship(String name, BigDecimal pricePerDay, int capacity) {
        Airship airship = new Airship();
        airship.setName(name);
        airship.setPricePerDay(pricePerDay);
        airship.setCapacity(capacity);
        return airship;
    }

    private static Comparator<Airship> idComparator = new Comparator<Airship>() {

        @Override
        public int compare(Airship o1, Airship o2) {
            return Long.valueOf(o1.getId()).compareTo(Long.valueOf(o2.getId()));
        }

    };

}
