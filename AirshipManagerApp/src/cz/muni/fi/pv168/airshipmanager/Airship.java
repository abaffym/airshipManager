package cz.muni.fi.pv168.airshipmanager;

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
    private double pricePerDay;
    private int capacity;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPricePerDay() {
        return pricePerDay;
    }

    public void setPricePerDay(double pricePerDay) {
        this.pricePerDay = pricePerDay;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
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
        if (this.id != other.id) {
            return false;
        }
        return true;
    }

}
