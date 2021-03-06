package com.lagranjafoods.picking.models;

import java.io.Serializable;

/**
 * Created by joang on 19/01/2018.
 */

public class PickingProduct implements Serializable{
    private int productId;
    private String productDescription;
    private boolean productIsCommercial;
    private int saleOrderLineId;
    private int saleOrderLineNumber;
    private int saleOrderLinePickedUnits;
    private int saleOrderLineUnits;


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

    public boolean isProductIsCommercial() {
        return productIsCommercial;
    }

    public void setProductIsCommercial(boolean productIsCommercial) {
        this.productIsCommercial = productIsCommercial;
    }

    public int getSaleOrderLineId() {
        return saleOrderLineId;
    }

    public void setSaleOrderLineId(int saleOrderLineId) {
        this.saleOrderLineId = saleOrderLineId;
    }

    public int getSaleOrderLineNumber() {
        return saleOrderLineNumber;
    }

    public void setSaleOrderLineNumber(int saleOrderLineNumber) {
        this.saleOrderLineNumber = saleOrderLineNumber;
    }

    public int getSaleOrderLinePickedUnits() {
        return saleOrderLinePickedUnits;
    }

    public void setSaleOrderLinePickedUnits(int saleOrderLinePickedUnits) {
        this.saleOrderLinePickedUnits = saleOrderLinePickedUnits;
    }

    public int getSaleOrderLineUnits() {
        return saleOrderLineUnits;
    }

    public void setSaleOrderLineUnits(int saleOrderLineUnits) {
        this.saleOrderLineUnits = saleOrderLineUnits;
    }
}
