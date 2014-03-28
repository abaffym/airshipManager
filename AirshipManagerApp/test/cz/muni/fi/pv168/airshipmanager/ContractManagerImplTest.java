/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.muni.fi.pv168.airshipmanager;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Date;
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
    Long usedDate;
    Connection conn;
    
    //edit format setter by: http://kore.fi.muni.cz/wiki/index.php/PV168/P%C5%99%C3%ADklad_objektov%C3%A9ho_n%C3%A1vrhu
    
    public ContractManagerImplTest() {
    }

    @Before
    public void setUp() throws SQLException {
        //SQL
        //!! Another table needs to be added to return Airship Id 
        
        conn = DriverManager.getConnection("jdbc:derby:memory:ContractManagerImplTest;create=true");
        conn.prepareStatement("CREATE TABLE CONTRACT("
                + "id BIGINT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY,"
                + "startDate BIGINT,"
                + "nameOfClient VARCHAR(50),"
                + "paymentMethod VARCHAR(12),"
                + "airshipId BIGINT,"
                + "discount DECIMAL,"
                + "length INTEGER)").executeUpdate();
        conn.prepareStatement("CREATE TABLE AIRSHIP("
                + "ID BIGINT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY,"
                + "NAME VARCHAR(50),"
                + "CAPACITY INTEGER,"
                + "PRICE DECIMAL)").executeUpdate();
        
        contracts = new ContractManagerImpl(conn);
        airships = new AirshipManagerImpl(conn);
        //test date set
        usedDate = Date.UTC(2014, 1, 1, 0, 0, 0);
        
    }
    @After
    public void tearDown() throws SQLException {
        conn.prepareStatement("DROP TABLE CONTRACT").executeUpdate();
        conn.prepareStatement("DROP TABLE AIRSHIP").executeUpdate();
        conn.close();
        
    }

    /**
     * Test of addContract method, of class ContractManagerImpl.
     */
    @Test
    public void testAddContract() {
        System.out.println("addContract test run");
        
        Contract expected = new Contract();
        expected.setAirship(new Airship().setName("Testship").setId(1L)).setDiscount(1f).setLength(10).setNameOfClient("Zeppelin");
        expected.setPaymentMethod(PaymentMethod.CASH).setStartDate(usedDate);
        int prevSize = contracts.getAllContracts().size();
        contracts.addContract(expected);
        
        Contract result = contracts.getContractById(1L);
        
        assertEquals(expected, result);
        
        }

    /**
     * Test of removeContract method, of class ContractManagerImpl.
     */
    @Test
    public void testRemoveContract() {
        System.out.println("removeContract test run");
        Contract expected = new Contract();
        expected.setAirship(new Airship().setName("Testship").setId(1L)).setDiscount(1f).setLength(10).setNameOfClient("Zeppelin");
        expected.setPaymentMethod(PaymentMethod.CASH).setStartDate(usedDate);
        Contract c2 = new Contract();
        contracts.addContract(expected);
        int prevSize = contracts.getAllContracts().size();
        contracts.removeContract(expected);
        try{
            contracts.removeContract(c2);
            fail("Contract to have been removed was not found");
        } catch(IllegalArgumentException e) {
            //OK
        }
        assertEquals(null ,contracts.getContractById(expected.getId()));
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
        
        Contract expected = new Contract();
        Airship a = new Airship().setName("Testship").setPricePerDay(BigDecimal.valueOf(25)).setCapacity(12);
        airships.addAirship(a);
        expected.setAirship(a).setDiscount(1.0f).setLength(10).setNameOfClient("Peter");
        expected.setPaymentMethod(PaymentMethod.CASH).setStartDate(usedDate);
        contracts.addContract(expected);
        
        
        Contract c2 = new Contract();
        c2.setAirship(a).setDiscount(0.8f).setLength(11).setNameOfClient("Pavol");
        c2.setPaymentMethod(PaymentMethod.CREDIT_CARD).setStartDate(usedDate);
        contracts.editContract(c2);
        
        assertDeepEquals(expected, contracts.getContractById(expected.getId()));
        
    }
    
    private void assertDeepEquals(Contract expected, Contract actual){
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getAirship(), actual.getAirship());
        assertEquals(expected.getDiscount(), actual.getDiscount(), 0.0001);
        assertEquals(expected.getLength(), actual.getLength());
        assertEquals(expected.getNameOfClient(), actual.getNameOfClient());
        assertEquals(expected.getPaymentMethod(), actual.getPaymentMethod());
        assertEquals(expected.getStartDate(), actual.getStartDate());
    }
}
