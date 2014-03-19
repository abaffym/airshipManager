package cz.muni.fi.pv168.airshipmanager;

import java.util.List;
/**
 * @author Michal Štefánik 422237
 * @author Marek Abaffy 422572
 */
public interface AirshipManager {

    /**
     * Adds new airship with automatically generated id.
     *
     * @param airship airship to be created
     * @throws IllegalArgumentException when airship is null or has already
     * assigned id.
     */
    public void addAirship(Airship airship);

    /**
     * Edits airship.
     *
     * @param airship updated airship
     * @throws IllegalArgumentException when airship is null, or it has null id.
     */    
    public void editAirship(Airship airship);

    /**
     * Removes given airship.
     *
     * @param airship airship to be removed
     * @throws IllegalArgumentException when airship or its id is null
     */    
    public void removeAirship(Airship airship);

    /**
     * Gets airship with given id.
     *
     * @param id if of requested airship
     * @return airship with given id or null if airship does not exist
     * @throws IllegalArgumentException when given id is null
     * @throws java.sql.SQLException if data werent retrieved correctly
     */
    public Airship getAirshipById(Long id);
    
    /**
     * Gets all airships.
     *
     * @return collection of all airships
     */
    public List<Airship> getAllAirships();
    
    /**
     * Gets all airships with higher capacity.
     *
     * @param capacity minimal capacity of airship
     * @return collection of airships with higher capacity than is given
     * @throws IllegalArgumentException if given capacity is less than 0
     */
    public List<Airship> getAllAirshipsByCapacity(int capacity);

    /**
     * Gets all avaliable airships
     *
     * @return collection of airships which are currently avaliable
     */
    public List<Airship> getFreeAirships();

    /**
     * Returns whether given airship is rented or not
     *
     * @param airship
     * @return true if airship is rented else false
     */
    public boolean isRented(Airship airship);
}
