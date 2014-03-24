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
    private static final double APPX = 0.0001;
    Long usedDate;
    Connection conn;
    
    public ContractManagerImplTest() {
    }

    @Before
    public void setUp() throws SQLException {
        //SQL
        conn = DriverManager.getConnection("jdbc:derby:memory:ContractManagerImplTest;create=true");
        conn.prepareStatement("CREATE TABLE CONTRACT("
                + "id BIGINT NOT NULL PRIMARY KEY,"
                + "startDate BIGINT,"
                + "nameOfClient VARCHAR(50),"
                + "paymentMethod VARCHAR(12),"
                + "airship BIGINT,"
                + "discount DECIMAL,"
                + "length INTEGER)").executeUpdate();
        //
        
        contracts = new ContractManagerImpl(conn);
        //test date set
        usedDate = Date.UTC(2014, 1, 1, 0, 0, 0);
        
    }
    @After
    public void tearDown() throws SQLException {
        conn.prepareStatement("DROP TABLE CONTRACT").executeUpdate();
        conn.close();
        
    }

    /**
     * Test of addContract method, of class ContractManagerImpl.
     */
    @Test
    public void testAddContract() {
        System.out.println("addContract test run");
        
        Contract newC = new Contract();
        newC.setAirship(new Airship().setName("Testship")).setDiscount(1f).setLength(10).setNameOfClient("Zeppelin");
        newC.setPaymentMethod(PaymentMethod.CASH).setStartDate(usedDate);
        int prevSize = contracts.getAllContracts().size();
        contracts.addContract(newC);
        
        assertEquals(prevSize+2, contracts.getAllContracts().size());
        }

    /**
     * Test of removeContract method, of class ContractManagerImpl.
     */
    @Test
    public void testRemoveContract() {
        System.out.println("removeContract test run");
        int prevSize = contracts.getAllContracts().size();
        Contract c1 = new Contract();
        Contract c2 = new Contract();
        contracts.addContract(c1);
        contracts.removeContract(c1);
        try{
            contracts.removeContract(c2);
            fail("Contract to have been removed was not found");
        } catch(IllegalArgumentException e) {
            //OK
        }
        assertEquals(prevSize+1 ,contracts.getAllContracts().size());
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
        
        Contract c1 = new Contract();
        c1.setId(123l).setAirship(new Airship()).setDiscount(1.0f).setLength(10).setNameOfClient("Peter");
        c1.setPaymentMethod(PaymentMethod.CASH).setStartDate(usedDate);
        contracts.addContract(c1);
        
        Contract c2 = new Contract();
        c2.setId(123l).setAirship(new Airship()).setDiscount(0.8f).setLength(11).setNameOfClient("Pavol");
        c2.setPaymentMethod(PaymentMethod.CREDIT_CARD).setStartDate(usedDate);
        contracts.editContract(c2);
        
        assertTrue(contracts.getContractById(123l).equals(c2));
        
    }
}
