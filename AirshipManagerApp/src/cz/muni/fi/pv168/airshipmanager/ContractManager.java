package cz.muni.fi.pv168.airshipmanager;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Michal Štefánik 422237
 * @author Marek Abaffy 422572
 */
public interface ContractManager {

    /**
     * Adds new contract with automatically generated id.
     *
     * @param contract contract to be created
     *
     * @throws IllegalArgumentException when contract is null or has already
     * assigned id.
     */
    public void addContract(Contract contract);
    
    /**
     * Edits contract.
     *
     * @param contract contract to be created
     *
     * @throws IllegalArgumentException when contract is null or has already
     * assigned id.
     */
    public void editContract(Contract contract);

    /**
     * Removes given contract.
     *
     * @param contract updated contract
     * @throws IllegalArgumentException when contract or its id is null
     */
    public void removeContract(Contract contract);

    /**
     * Gets contract with given id.
     *
     * @param id id of requested contract
     * @return contract with given id or null if contract does not exist
     * @throws IllegalArgumentException when given id is null
     */
    public Contract getContractById(Long id);

    /**
     * Gets contract of given airship.
     *
     * @param airship airship of requested contract
     * @return contract with given airship or null if contract does not exist
     * @throws IllegalArgumentException when given airship is null
     */
    public Contract getContractByAirship(Airship airship);

    /**
     * Gets all contracts.
     *
     * @return collection of all contracts
     */
    public List<Contract> getAllContracts();

    /**
     * Gets all active contracts.
     *
     * @return collection of all active contracts
     */
    public List<Contract> getActiveContracts();

    /**
     * Gets price of given contract.
     *
     * @param contract contract to get price from
     * @return price of given contract
     */
    public BigDecimal getPrice(Contract contract);

    /**
     * Gets end date of given contract.
     *
     * @param contract contract to get price from
     * @return end date of given contract
     */
    public Date getEndDate(Contract contract);
    
    public List<Contract> getAllByAirship(Airship a);
    
    Contract getActiveByAirship(Airship a) 
}
