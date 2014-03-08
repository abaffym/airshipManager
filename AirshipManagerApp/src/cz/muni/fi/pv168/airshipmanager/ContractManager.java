/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.muni.fi.pv168.airshipmanager;

import java.util.Collection;
import java.util.Date;

/**
 *
 * @author Michal Štefánik 422237
 */
public interface ContractManager {
    public boolean addContract(Contract c);
    public boolean removeContract(Contract c);
    public Contract getContractById(long id);
    public Contract getContractByAirship(Airship a);
    public Collection<Contract> getAll();
    public Collection<Contract> getActive();
    public double getPrice(Contract c);
    public Date getEndDate(Contract c);
}
