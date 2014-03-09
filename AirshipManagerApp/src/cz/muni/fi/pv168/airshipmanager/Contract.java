package cz.muni.fi.pv168.airshipmanager;

import java.util.Date;

/**
 * This entity class represents contract. Contract has specific id, some start
 * date, length, it also includes information about name of the client, airship,
 * given discount and method of payment.
 *
 * @author Michal Štefánik 422237
 * @author Marek Abaffy 422572
 */
public class Contract {

    private long id;
    private Date startDate;
    private int length;
    private String nameOfClient;
    private Airship airship;
    private float discount;
    private PaymentMethod paymentMethod;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getNameOfClient() {
        return nameOfClient;
    }

    public void setNameOfClient(String nameOfClient) {
        this.nameOfClient = nameOfClient;
    }

    public float getDiscount() {
        return discount;
    }

    public void setDiscount(float discount) {
        this.discount = discount;
    }

    public Airship getAirship() {
        return airship;
    }

    public void setAirship(Airship airship) {
        this.airship = airship;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + (int) (this.id ^ (this.id >>> 32));
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
        final Contract other = (Contract) obj;
        return this.id == other.id;
    }

    @Override
    public String toString() {
        return "Contract{" + "id=" + id + ", nameOfClient=" + nameOfClient + ", airship=" + airship + '}';
    }

}
