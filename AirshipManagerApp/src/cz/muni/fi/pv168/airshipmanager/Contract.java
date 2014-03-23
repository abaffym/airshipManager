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

    private Long id;
    private Long startDate;
    private int length;
    private String nameOfClient;
    private Airship airship;
    private float discount;
    private PaymentMethod paymentMethod;

    public Contract setId(Long id) {
        this.id = id;
        return this;
    }

    public Contract setStartDate(Long startDate) {
        this.startDate = startDate;
        return this;
    }

    public Contract setLength(int length) {
        this.length = length;
        return this;
    }

    public Contract setNameOfClient(String nameOfClient) {
        this.nameOfClient = nameOfClient;
        return this;
    }

    public Contract setAirship(Airship airship) {
        this.airship = airship;
        return this;
    }

    public Contract setDiscount(float discount) {
        this.discount = discount;
        return this;
    }

    public Contract setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
        return this;
    }
    
    public Long getId() {
        return id;
    }
    
    public Long getStartDate() {
        return startDate;
    }

    public int getLength() {
        return length;
    }

    public String getNameOfClient() {
        return nameOfClient;
    }

    public float getDiscount() {
        return discount;
    }

    public Airship getAirship() {
        return airship;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void isValid() throws IllegalArgumentException  {
        if(this == null){
            throw new IllegalArgumentException("contract is null");
        }
        if(this.id == null){
            throw new IllegalArgumentException("ID is null");
        }
        if(this.id <= 0){
            throw new IllegalArgumentException("ID is negative or zero");
        }
        if(this.airship == null){
            throw new IllegalArgumentException("Airship is null");
        }
        if(this.length <= 0){
            throw new IllegalArgumentException("Contract length is negative or zero");
        }
        if(this.paymentMethod == null){
            throw new IllegalArgumentException("Payment method is null");
        }
        if(this.startDate == null){
            throw new IllegalArgumentException("Start Date is null");
        }
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
