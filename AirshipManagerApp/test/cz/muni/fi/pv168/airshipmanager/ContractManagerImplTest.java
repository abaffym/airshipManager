/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.muni.fi.pv168.airshipmanager;

import cz.muni.fi.pv168.common.DBUtils;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Calendar;
//import java.sql.Date;
import java.util.Date;
import java.util.List;
import javax.sql.DataSource;
import org.apache.commons.dbcp.BasicDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Michal Štefánik 422237
 */
public class ContractManagerImplTest {
    private ContractManagerImpl contracts;
    private AirshipManagerImpl airships;
    private static final double APPX = 0.0001;
    private java.sql.Date usedDate;
    private Connection conn;
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
        
        contracts = new ContractManagerImpl();
        airships = new AirshipManagerImpl();
        
        contracts.setDataSource(ds);
        airships.setDataSource(ds);
        
        usedDate = java.sql.Date.valueOf("2014-01-01");
        
    }
    
    @After
    public void tearDown() throws SQLException {
        DBUtils.executeSqlScript(ds, AirshipManager.class.getResource("dropTables.sql"));
    }

    /**
     * Test of addContract method, of class ContractManagerImpl.
     */
    @Test
    public void testAddContract() {
        System.out.println("addContract test run");
        
        Contract expected = buildContract(1);
        int prevSize = contracts.getAllContracts().size();
        contracts.addContract(expected);
        
        Contract result = contracts.getContractById(expected.getId());
        
        assertEquals(expected, result);
        
        }

    /**
     * Test of removeContract method, of class ContractManagerImpl.
     */
    @Test
    public void testRemoveContract() {
        System.out.println("removeContract test run");
        Contract expected = buildContract(1);
        Contract c2 = buildContract(2);
        
        contracts.addContract(expected);
        int prevSize = contracts.getAllContracts().size();
        
        contracts.removeContract(expected);
        try{
            contracts.removeContract(c2);
        } catch (IllegalArgumentException e){
            //OK
        }
        assertEquals(null, contracts.getContractById(expected.getId()));
        assertEquals(prevSize-1, contracts.getAllContracts().size());
    }

    /**
     * Test of getPrice method, of class ContractManagerImpl.
     */
    @Test
    public void testGetPrice() {
        System.out.println("getPrice test run");
        
        Airship a = new Airship();
        a.setName("Testship");
        a.setPricePerDay(BigDecimal.valueOf(10));

        Contract c1 = new Contract();
        c1.setAirship(a);
        c1.setDiscount(0.85f);
        double expResult = 8.5;
        assertEquals( (c1.getDiscount()) * (c1.getAirship().getPricePerDay().doubleValue()) , expResult, APPX);
    }
    @Test
    public void testEditContract() {
        System.out.println("editContract test run");
        
        Contract contract = new Contract();
        Airship a = new Airship().setName("Testship").setPricePerDay(BigDecimal.valueOf(25)).setCapacity(12);
        airships.addAirship(a);
        
        contract.setAirship(a)
                .setDiscount(1.0f)
                .setLength(10)
                .setNameOfClient("Peter")
                .setPaymentMethod(PaymentMethod.CASH)
                .setStartDate(usedDate);
        
        contracts.addContract(contract);
                
        Contract updated = contracts.getContractById(contract.getId());
        
        updated.setDiscount(1.8f)
               .setLength(11)
               .setNameOfClient("Pavol")
               .setPaymentMethod(PaymentMethod.CREDIT_CARD);
        
        contracts.editContract(updated);
        assertDeepEquals(updated, contracts.getContractById(updated.getId()));
        
    }
    @Test
    public void testGetActiveByAirship(){
        System.out.println("testGetActiveByAirship run");
        Date current = new Date(System.currentTimeMillis());
        
        Contract c1 = buildContract(1);
        c1.setStartDate(getSqlDate(current, -1));
        Contract c2 = buildContract(2);
        c2.setStartDate(getSqlDate(current, +1));
        c2.setAirship(c1.getAirship());
        
        contracts.addContract(c1);
        contracts.addContract(c2);
        
        
        Contract actual = contracts.getActiveByAirship(c1.getAirship());
        
        assertDeepEquals(c1, actual);
    }
    @Test
    public void testGetActiveContracts(){
        Date current = new Date(System.currentTimeMillis());
        
        Contract c1 = buildContract(1);
        c1.setStartDate(getSqlDate(current, 1));
        
        Contract c2 = buildContract(2);
        c2.setStartDate(getSqlDate(current, -1));
        c2.setAirship(c1.getAirship());
        
        contracts.addContract(c1);
        contracts.addContract(c2);
        List<Contract> actual = contracts.getActiveContracts();
        
        assertEquals(1, actual.size());
        assertDeepEquals(actual.get(0), c2);
    }
    @Test
    public void testGetAllByAirship(){
        Contract c1 = buildContract(1);
        Contract c2 = buildContract(2);
        
        contracts.addContract(c1);
        contracts.addContract(c2);
        List<Contract> actual = contracts.getAllByAirship(c1.getAirship());
        
        assertDeepEquals(c1, actual.get(0));
        assertEquals(1, actual.size());
    }
    
    @Test
    public void testGetContractById(){
        Contract c1 = buildContract(1);
        contracts.addContract(c1);
        
        Contract expected = contracts.getContractById(c1.getId());
        assertDeepEquals(c1, expected);
    }
    
    @Test
    public void testGetAllContracts(){
        Contract c1 = buildContract(1);
        Contract c2 = buildContract(2);
        
        contracts.addContract(c1);
        contracts.addContract(c2);
        List<Contract> all = contracts.getAllContracts();
        
        assertEquals(2, all.size());
    }
    
    @Test
    public void testGetEndDate(){
        Contract c1 = buildContract(1);
        contracts.addContract(c1);
        c1.setStartDate(usedDate);
        
        assertTrue(contracts.getEndDate(c1).getTime() > usedDate.getTime());
    }
    
    private void assertDeepEquals(Contract expected, Contract actual){
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getAirship(), actual.getAirship());
        assertEquals(expected.getDiscount(), actual.getDiscount(), 0.0001);
        assertEquals(expected.getLength(), actual.getLength());
        assertEquals(expected.getNameOfClient(), actual.getNameOfClient());
        assertEquals(expected.getPaymentMethod(), actual.getPaymentMethod());
        assertEquals(expected.getStartDate().toString(), actual.getStartDate().toString());
    }
    
    private void assertDeepEquals(Airship expected, Airship actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getCapacity(), actual.getCapacity());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getPricePerDay(), actual.getPricePerDay());
    }
    
    private Contract buildContract(int diff){
        Contract out = new Contract();
        out.setAirship(buildAirship(diff)).setDiscount(1f*(1-(diff/10)));
        out.setLength(7*diff).setNameOfClient("Zeppelin "+Integer.toString(diff));
        out.setPaymentMethod(PaymentMethod.CASH).setStartDate(usedDate);
         
        return out;
    }
    private Airship buildAirship(int diff){
        Airship a = new Airship()
                .setName("Testship "+Integer.toString(diff))
                .setPricePerDay(BigDecimal.valueOf(diff*5))
                .setCapacity(diff*4);
        airships.addAirship(a);
        return a;
    }
    
    private java.sql.Date getSqlDate(java.util.Date date, int daysMove){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, daysMove);
        
        return new java.sql.Date(cal.getTime().getTime());
    }
}
