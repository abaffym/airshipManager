/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.muni.fi.pv168.airshipmanager;

import java.util.Collection;

/**
 *
 * @author Michal Štefánik 422237
 */
public interface AirshipManager {
    public boolean addAirship(Airship a);
    public boolean removeAirship(Airship a);
    public Airship getAirshipById(long id);
    public Collection<Airship> getAirshipByCapacity(int capacity);
    public Collection<Airship> getFreeAirships();
    public boolean isRented(Airship a);
}
