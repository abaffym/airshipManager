/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.muni.fi.pv168.airshipmanager;

import java.math.BigDecimal;
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
    
    public ContractManagerImplTest() {
    }

    @Before
    public void setUp() {
        contracts = new ContractManagerImpl();
        Contract c = new Contract();
        c.setId(123l);
        c.setNameOfClient("Peter");
        contracts.addContract(c);
    }


    /**
     * Test of addContract method, of class ContractManagerImpl.
     */
    @Test
    public void testAddContract() {
        System.out.println("addContract test run");
        int prevSize = contracts.getAllContracts().size();
        contracts.addContract(new Contract());
        contracts.addContract(new Contract());
        
        assertEquals(contracts.getAllContracts().size()+2, contracts.getAllContracts().size());
        // TODO review the generated test code and remove the default call to fail.
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
       // TODO review the generated test code and remove the default call to fail.
    }

}
