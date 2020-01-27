/*
 This class provides the entity mapping data structure for database in this project. 
 Comments added by Ali Malik - 26.01.2020
 */
package com.currencies.entities;

import java.io.Serializable;
import java.util.Date;


public class ExchangeRates implements Serializable {
    private double rate;
    private static final long serialVersionUID = 1L;
    private Integer exchangeRateId;
    private String fromCurrency;
    private String toCurrency;
    private Date validityStartDate;
    private Date validityEndDate;

    public ExchangeRates() {
    }

    public ExchangeRates(Integer exchangeRateId) {
        this.exchangeRateId = exchangeRateId;
    }

    public ExchangeRates(Integer exchangeRateId, String fromCurrency, String toCurrency, Date validityStartDate, Date validityEndDate) {
        this.exchangeRateId = exchangeRateId;
        this.fromCurrency = fromCurrency;
        this.toCurrency = toCurrency;
        this.validityStartDate = validityStartDate;
        this.validityEndDate = validityEndDate;
    }

    public Integer getExchangeRateId() {
        return exchangeRateId;
    }

    public void setExchangeRateId(Integer exchangeRateId) {
        this.exchangeRateId = exchangeRateId;
    }

    public String getFromCurrency() {
        return fromCurrency;
    }

    public void setFromCurrency(String fromCurrency) {
        this.fromCurrency = fromCurrency;
    }

    public String getToCurrency() {
        return toCurrency;
    }

    public void setToCurrency(String toCurrency) {
        this.toCurrency = toCurrency;
    }

    public Date getValidityStartDate() {
        return validityStartDate;
    }

    public void setValidityStartDate(Date validityStartDate) {
        this.validityStartDate = validityStartDate;
    }

    public Date getValidityEndDate() {
        return validityEndDate;
    }

    public void setValidityEndDate(Date validityEndDate) {
        this.validityEndDate = validityEndDate;
    }
/*
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (exchangeRateId != null ? exchangeRateId.hashCode() : 0);
        return hash;
    }
*/
    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ExchangeRates)) {
            return false;
        }
        ExchangeRates other = (ExchangeRates) object;
        if ((this.exchangeRateId == null && other.exchangeRateId != null) || (this.exchangeRateId != null && !this.exchangeRateId.equals(other.exchangeRateId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "fromCurrency:" + fromCurrency + ", toCurrency:"+toCurrency+ ", rate:"+rate;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }
    
}
