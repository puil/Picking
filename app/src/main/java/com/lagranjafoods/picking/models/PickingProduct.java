package com.lagranjafoods.picking.models;

/**
 * Created by joang on 19/01/2018.
 */

public class PickingProduct {
    private int productId;
    private String productDescription;
    private boolean productIsCommercial;
    private int saleOrderLineId;
    private int SaleOrderLineNumber;
    private int SaleOrderLinePickedUnits;
    private int SaleOrderLineUnits;


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
        return SaleOrderLineNumber;
    }

    public void setSaleOrderLineNumber(int saleOrderLineNumber) {
        SaleOrderLineNumber = saleOrderLineNumber;
    }

    public int getSaleOrderLinePickedUnits() {
        return SaleOrderLinePickedUnits;
    }

    public void setSaleOrderLinePickedUnits(int saleOrderLinePickedUnits) {
        SaleOrderLinePickedUnits = saleOrderLinePickedUnits;
    }

    public int getSaleOrderLineUnits() {
        return SaleOrderLineUnits;
    }

    public void setSaleOrderLineUnits(int saleOrderLineUnits) {
        SaleOrderLineUnits = saleOrderLineUnits;
    }
}
