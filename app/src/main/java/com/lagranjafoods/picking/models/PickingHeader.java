package com.lagranjafoods.picking.models;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by joang on 19/01/2018.
 */

public class PickingHeader implements Serializable{
    private int id;
    private int saleOrderNumber;
    private Date saleOrderDate;
    private String customerName;
    private boolean isConfirmed;
    private List<PickingPallet> pallets;

    public PickingHeader(int id, int saleOrderNumber, Date saleOrderDate, String customerName, boolean isConfirmed, List<PickingPallet> pallets) {
        this.id = id;
        this.saleOrderNumber = saleOrderNumber;
        this.saleOrderDate = saleOrderDate;
        this.customerName = customerName;
        this.isConfirmed = isConfirmed;
        this.setPallets(pallets);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSaleOrderNumber() {
        return saleOrderNumber;
    }

    public void setSaleOrderNumber(int saleOrderNumber) {
        this.saleOrderNumber = saleOrderNumber;
    }

    public Date getSaleOrderDate() {
        return saleOrderDate;
    }

    public void setSaleOrderDate(Date saleOrderDate) {
        this.saleOrderDate = saleOrderDate;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public boolean isConfirmed() {
        return isConfirmed;
    }

    public void setConfirmed(boolean confirmed) {
        isConfirmed = confirmed;
    }

    public List<PickingPallet> getPallets() {
        return pallets;
    }

    public void setPallets(List<PickingPallet> pallets) {
        this.pallets = pallets;
    }
}
