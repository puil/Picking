package com.lagranjafoods.picking.models;

import java.util.Date;

/**
 * Created by joang on 24/01/2018.
 */

public class AddPickingPalletLineRequest {
    private int pickingPalletId;
    private int saleOrderLineId;
    private int productId;
    private String lot;
    private Date expirationDate;
    private int sourceWarehouseId;
    private double amount;
    private int userId;

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

    public int getSourceWarehouseId() {
        return sourceWarehouseId;
    }

    public void setSourceWarehouseId(int sourceWarehouseId) {
        this.sourceWarehouseId = sourceWarehouseId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
