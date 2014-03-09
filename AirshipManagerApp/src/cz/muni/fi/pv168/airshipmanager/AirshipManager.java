package cz.muni.fi.pv168.airshipmanager;

import java.util.Collection;

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
     *
     */
    public void addAirship(Airship airship);

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
     */
    public Airship getAirshipById(Long id);

    /**
     * Gets all airships with higher capacity.
     *
     * @param capacity minimal capacity of airship
     * @return collection of airships with higher capacity than is given
     * @throws IllegalArgumentException if given capacity is less than 0
     */
    public Collection<Airship> getAirshipByCapacity(int capacity);

    /**
     * Gets all avaliable airships
     *
     * @return collection of airships which are currently avaliable
     */
    public Collection<Airship> getFreeAirships();

    /**
     * Returns whether given airship is rented or not
     *
     * @param airship
     * @return true if airship is rented else false
     */
    public boolean isRented(Airship airship);
}
