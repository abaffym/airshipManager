package cz.muni.fi.pv168.airshipmanager;

import cz.muni.fi.pv168.common.DBUtils;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import javax.sql.DataSource;
import org.apache.commons.dbcp.BasicDataSource;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Marek Abaffy 422572
 */
public class AirshipManagerImplTest {

    private AirshipManagerImpl manager;
    private DataSource ds;

    private static DataSource prepareDataSource() throws SQLException {
        BasicDataSource ds = new BasicDataSource();
        ds.setUrl("jdbc:derby:memory:AirshipManagerImplTest;create=true");
        return ds;
    }

    @Before
    public void setUp() throws SQLException {
        ds = prepareDataSource();
        DBUtils.executeSqlScript(ds, AirshipManager.class.getResource("createTables.sql"));
        manager = new AirshipManagerImpl();
        manager.setDataSource(ds);
    }

    @After
    public void tearDown() throws SQLException {
       DBUtils.executeSqlScript(ds, AirshipManager.class.getResource("dropTables.sql"));
    }

    private java.sql.Date date(String date) {
        return java.sql.Date.valueOf(date);
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
        airship = manager.getAirshipById(airshipId);
        assertEquals("AirshipOne", airship.getName());
        assertEquals(BigDecimal.valueOf(140), airship.getPricePerDay());
        assertEquals(1, airship.getCapacity());

        airship = manager.getAirshipById(airshipId);
        airship.setPricePerDay(BigDecimal.valueOf(1));
        manager.editAirship(airship);
        airship = manager.getAirshipById(airshipId);
        assertEquals("AirshipOne", airship.getName());
        assertEquals(BigDecimal.valueOf(1), airship.getPricePerDay());
        assertEquals(1, airship.getCapacity());

        airship = manager.getAirshipById(airshipId);
        airship.setName("AirshipX");
        manager.editAirship(airship);
        airship = manager.getAirshipById(airshipId);
        assertEquals("AirshipX", airship.getName());
        assertEquals(BigDecimal.valueOf(1), airship.getPricePerDay());
        assertEquals(1, airship.getCapacity());

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
     *
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
     *
     * @throws java.sql.SQLException
     */
    @Test
    public void testGetFreeAirships() throws SQLException {
        ContractManagerImpl cManager = new ContractManagerImpl();
        cManager.setDataSource(ds);

        Airship airship = newAirship("AirshipOne", BigDecimal.valueOf(140), 50);
        Airship airship2 = newAirship("AirshipTwo", BigDecimal.valueOf(120), 30);
        Airship airship3 = newAirship("AirshipThre", BigDecimal.valueOf(120), 10);

        manager.addAirship(airship);
        manager.addAirship(airship2);
        manager.addAirship(airship3);
        Date current = new Date(System.currentTimeMillis());

        Contract contract = new Contract();
        contract.setAirship(airship2)
                .setLength(1)
                .setNameOfClient("Client")
                .setPaymentMethod(PaymentMethod.CASH)
                .setStartDate(getSqlDate(current, 0));
        cManager.addContract(contract);

        List<Airship> expected = Arrays.asList(airship, airship3);
        List<Airship> actual = manager.getFreeAirships();

        assertEquals(expected, actual);
        assertDeepEquals(expected, actual);

        Contract contract2 = new Contract();
        contract2.setAirship(airship)
                .setLength(1)
                .setNameOfClient("Client")
                .setPaymentMethod(PaymentMethod.CASH)
                .setStartDate(getSqlDate(current, 0));
        cManager.addContract(contract2);

        Contract contract3 = new Contract();
        contract3.setAirship(airship3)
                .setLength(1)
                .setNameOfClient("Client")
                .setPaymentMethod(PaymentMethod.CASH)
                .setStartDate(getSqlDate(current, 0));
        cManager.addContract(contract3);

        actual = manager.getFreeAirships();

        assertNotNull(actual);
        assertEquals(Arrays.asList(), actual);
    }

    /**
     * Test of isRented method, of class AirshipManagerImpl.
     *
     * @throws java.sql.SQLException
     */
    @Test
    public void testIsRented() throws SQLException {
        ContractManagerImpl cManager = new ContractManagerImpl();
        cManager.setDataSource(ds);

        Airship airship = newAirship("AirshipOne", BigDecimal.valueOf(140), 50);
        Airship airship2 = newAirship("AirshipTwo", BigDecimal.valueOf(120), 30);
        manager.addAirship(airship);
        manager.addAirship(airship2);
        Date current = new Date(System.currentTimeMillis());
        Contract contract = new Contract();
        contract.setAirship(airship)
                .setLength(1)
                .setNameOfClient("Client")
                .setPaymentMethod(PaymentMethod.CASH)
                .setStartDate(getSqlDate(current, 0));
        cManager.addContract(contract);

        assertTrue(manager.isRented(airship));
        assertFalse(manager.isRented(airship2));
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

    private static final Comparator<Airship> idComparator = new Comparator<Airship>() {

        @Override
        public int compare(Airship o1, Airship o2) {
            return o1.getId().compareTo(o2.getId());
        }
    };
    
    private java.sql.Date getSqlDate(java.util.Date date, int daysMove){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, daysMove);
        
        return new java.sql.Date(cal.getTime().getTime());
    }

}
