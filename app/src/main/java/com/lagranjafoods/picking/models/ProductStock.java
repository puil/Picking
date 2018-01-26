package com.lagranjafoods.picking.models;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by joang on 24/01/2018.
 */

public class ProductStock implements Serializable{
    private String lot;
    private Date expirationDate;
    private double amount;
    private boolean isRotas;
    private int warehouseId;
    private int productId;
    private String productDescription;

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

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public boolean isRotas() {
        return isRotas;
    }

    public void setRotas(boolean rotas) {
        isRotas = rotas;
    }

    public int getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(int warehouseId) {
        this.warehouseId = warehouseId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }
}
