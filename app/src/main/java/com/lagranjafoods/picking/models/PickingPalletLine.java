package com.lagranjafoods.picking.models;

import java.util.Date;

/**
 * Created by joang on 19/01/2018.
 */

public class PickingPalletLine {
    private int id;
    private int pickingPalletId;
    private int saleOrderLineId;
    private int productId;
    private String lot;
    private Date expirationDate;
    private int warehouseSourceId;
    private double amount;
    private String productDescription;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPickingPalletId() {
        return pickingPalletId;
    }

    public void setPickingPalletId(int pickingPalletId) {
        this.pickingPalletId = pickingPalletId;
    }

    public int getSaleOrderLineId() {
        return saleOrderLineId;
    }

    public void setSaleOrderLineId(int saleOrderLineId) {
        this.saleOrderLineId = saleOrderLineId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getLot() {
        return lot;
    }

    public void setLot(String lot) {
        this.lot = lot;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public int getWarehouseSourceId() {
        return warehouseSourceId;
    }

    public void setWarehouseSourceId(int warehouseSourceId) {
        this.warehouseSourceId = warehouseSourceId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }
}
