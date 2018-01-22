package com.lagranjafoods.picking.models;

import java.util.List;

/**
 * Created by joang on 19/01/2018.
 */

public class PickingResponse {
    private PickingActionResultEnum actionResult;
    private String actionResultMessage;
    private int newEntityId;
    private PickingHeader pickingHeader;
    private List<PickingPalletLine> pickingPalletLines;
    private List<PickingProduct> pickingProducts;
    private List<PickingPallet> pickingPallets;

    public PickingActionResultEnum getActionResult() {
        return actionResult;
    }

    public void setActionResult(PickingActionResultEnum actionResult) {
        this.actionResult = actionResult;
    }

    public String getActionResultMessage() {
        return actionResultMessage;
    }

    public void setActionResultMessage(String actionResultMessage) {
        this.actionResultMessage = actionResultMessage;
    }

    public int getNewEntityId() {
        return newEntityId;
    }

    public void setNewEntityId(int newEntityId) {
        this.newEntityId = newEntityId;
    }

    public PickingHeader getPickingHeader() {
        return pickingHeader;
    }

    public void setPickingHeader(PickingHeader pickingHeader) {
        this.pickingHeader = pickingHeader;
    }

    public List<PickingPalletLine> getPickingPalletLines() {
        return pickingPalletLines;
    }

    public void setPickingPalletLines(List<PickingPalletLine> pickingPalletLines) {
        this.pickingPalletLines = pickingPalletLines;
    }

    public List<PickingProduct> getPickingProducts() {
        return pickingProducts;
    }

    public void setPickingProducts(List<PickingProduct> pickingProducts) {
        this.pickingProducts = pickingProducts;
    }

    public List<PickingPallet> getPickingPallets() {
        return pickingPallets;
    }

    public void setPickingPallets(List<PickingPallet> pickingPallets) {
        this.pickingPallets = pickingPallets;
    }

    public boolean isSuccess(){
        return this.actionResult.equals(PickingActionResultEnum.Success);
    }

    public String getErrorMessage(){
        return this.actionResult.toString();
    }
}
