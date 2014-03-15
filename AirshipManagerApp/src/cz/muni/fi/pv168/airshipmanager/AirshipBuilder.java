/*
 * Unused demontration of Airship class using Builder pattern
 * instead of ordinairy setters.
 * 
 * See http://en.wikipedia.org/wiki/Builder_pattern for tedious model description
 */

package cz.muni.fi.pv168.airshipmanager;

import java.math.BigDecimal;


public class AirshipBuilder {
    private Long id;
    private String name;
    private BigDecimal pricePerDay;
    private int capacity;

    public AirshipBuilder setId(Long id) {
        this.id = id;
        return this;
    }

    public AirshipBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public AirshipBuilder setPricePerDay(BigDecimal pricePerDay) {
        this.pricePerDay = pricePerDay;
        return this;
    }

    public AirshipBuilder setCapacity(int capacity) {
        this.capacity = capacity;
        return this;
    }  

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
        return this.id == other.getId();
    }
}
