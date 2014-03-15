package cz.muni.fi.pv168.airshipmanager;

import java.math.BigDecimal;

/**
 * This entity class represents Airship. Airship has specific id, some name,
 * price and capacity.
 *
 * @author Michal Štefánik 422237
 * @author Marek Abaffy 422572
 */
public class Airship {

    private Long id;
    private String name;
    private BigDecimal pricePerDay;
    private int capacity;
    
    //setters
    public Airship setId(Long id) {
        this.id = id;
        return this;
    }

    public Airship setName(String name) {
        this.name = name;
        return this;
    }

    public Airship setPricePerDay(BigDecimal pricePerDay) {
        this.pricePerDay = pricePerDay;
        return this;
    }

    public Airship setCapacity(int capacity) {
        this.capacity = capacity;
        return this;
    }
    
    //getters
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPricePerDay() {
        return pricePerDay;
    }

    public int getCapacity() {
        return capacity;
    }

    @Override
    public String toString() {
        return "Airship{" + "id=" + id + ", name=" + name + '}';
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + (int) (this.id ^ (this.id >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Airship other = (Airship) obj;
        return this.id == other.id;
    }

}
