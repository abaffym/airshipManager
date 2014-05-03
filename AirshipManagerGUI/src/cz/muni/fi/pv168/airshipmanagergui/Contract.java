/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.muni.fi.pv168.airshipmanagergui;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 *
 * @author Marek
 */
@Entity
@Table(name = "CONTRACT", catalog = "", schema = "ROOT")
@NamedQueries({
    @NamedQuery(name = "Contract.findAll", query = "SELECT c FROM Contract c"),
    @NamedQuery(name = "Contract.findById", query = "SELECT c FROM Contract c WHERE c.id = :id"),
    @NamedQuery(name = "Contract.findByStartdate", query = "SELECT c FROM Contract c WHERE c.startdate = :startdate"),
    @NamedQuery(name = "Contract.findByNameofclient", query = "SELECT c FROM Contract c WHERE c.nameofclient = :nameofclient"),
    @NamedQuery(name = "Contract.findByPaymentmethod", query = "SELECT c FROM Contract c WHERE c.paymentmethod = :paymentmethod"),
    @NamedQuery(name = "Contract.findByAirshipid", query = "SELECT c FROM Contract c WHERE c.airshipid = :airshipid"),
    @NamedQuery(name = "Contract.findByDiscount", query = "SELECT c FROM Contract c WHERE c.discount = :discount"),
    @NamedQuery(name = "Contract.findByLength", query = "SELECT c FROM Contract c WHERE c.length = :length")})
public class Contract implements Serializable {
    @Transient
    private PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Long id;
    @Column(name = "STARTDATE")
    @Temporal(TemporalType.DATE)
    private Date startdate;
    @Column(name = "NAMEOFCLIENT")
    private String nameofclient;
    @Column(name = "PAYMENTMETHOD")
    private String paymentmethod;
    @Column(name = "AIRSHIPID")
    private BigInteger airshipid;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "DISCOUNT")
    private Double discount;
    @Column(name = "LENGTH")
    private Integer length;

    public Contract() {
    }

    public Contract(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        Long oldId = this.id;
        this.id = id;
        changeSupport.firePropertyChange("id", oldId, id);
    }

    public Date getStartdate() {
        return startdate;
    }

    public void setStartdate(Date startdate) {
        Date oldStartdate = this.startdate;
        this.startdate = startdate;
        changeSupport.firePropertyChange("startdate", oldStartdate, startdate);
    }

    public String getNameofclient() {
        return nameofclient;
    }

    public void setNameofclient(String nameofclient) {
        String oldNameofclient = this.nameofclient;
        this.nameofclient = nameofclient;
        changeSupport.firePropertyChange("nameofclient", oldNameofclient, nameofclient);
    }

    public String getPaymentmethod() {
        return paymentmethod;
    }

    public void setPaymentmethod(String paymentmethod) {
        String oldPaymentmethod = this.paymentmethod;
        this.paymentmethod = paymentmethod;
        changeSupport.firePropertyChange("paymentmethod", oldPaymentmethod, paymentmethod);
    }

    public BigInteger getAirshipid() {
        return airshipid;
    }

    public void setAirshipid(BigInteger airshipid) {
        BigInteger oldAirshipid = this.airshipid;
        this.airshipid = airshipid;
        changeSupport.firePropertyChange("airshipid", oldAirshipid, airshipid);
    }

    public Double getDiscount() {
        return discount;
    }

    public void setDiscount(Double discount) {
        Double oldDiscount = this.discount;
        this.discount = discount;
        changeSupport.firePropertyChange("discount", oldDiscount, discount);
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        Integer oldLength = this.length;
        this.length = length;
        changeSupport.firePropertyChange("length", oldLength, length);
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Contract)) {
            return false;
        }
        Contract other = (Contract) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "airshipmanagergui.Contract[ id=" + id + " ]";
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }
    
}
