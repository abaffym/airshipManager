package cz.muni.fi.pv168.airshipmanager;

/**
 * @author Michal Štefánik 422237
 * @author Marek Abaffy 422572
 */
public class AirshipManagerApp {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
    }

}

/*
 Differences to UML:

 A) AirshipManager
 1. add Airship returned value changed from void to boolean
 2. rentAnrship method removed
 3. getAirshipById attribute changed to long from int
 4. editAirship method removed

 B) ContractManager
 1. addContract returned value changed from void to boolean
 2. editContract method removed
 3. getEndDate method got a new attribute Contract c
 4. getContractById method introduced


 */
