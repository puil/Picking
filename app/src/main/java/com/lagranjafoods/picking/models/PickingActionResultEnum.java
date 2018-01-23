package com.lagranjafoods.picking.models;

/**
 * Created by joang on 19/01/2018.
 */

public enum PickingActionResultEnum {
    Success(0),
    SaleOrderNotFound(1),
    PalletNotFound(2),
    PickingNotFound(3),
    PickingIsAlreadyConfirmed(4),
    CantDeletePalletBecauseItContainsSomeLines(5),
    PalletIsAlreadyConfirmed(6),
    CantConfirmPalletBecauseItContainsSomeLines(7),
    UserNotFound(8),
    CantConfirmPalletBecauseItHasNoLines(9, "El palet está vacío"),
    RequestIsNull(10),
    SaleOrderLineNotFound(11),
    ProductNotFound(12),
    LotMustBeEmptyBecauseProductIsCommercial(13),
    ExpirationDateMustBeNullBecauseProductIsCommercial(14),
    LotCantBeEmpty(15),
    ExpirationDateCantBeNull(16),
    AmoutMustBeGreaterThanZero(17),
    SourceWarehouseNotFound(18),
    ThereIsNotEnoughStockInSourceWarehouseForTheGivenProductLotAndExpirationDate(19),
    PickedUnitsPlusRequestedAmountIsHigherThanSaleOrderLineUnits(20),
    ProductInSaleOrderLineDoesntMatchTheProductInRequest(21),
    SaleOrderNumberInPickingHeaderDoesntMatchSaleOrderNumberInSaleOrderLineRequest(22),
    PalletLineNotFound(23),
    ThereAreNoProductsWithTheGivenBarcodeNumber(24),
    ProductsFoundWithTheBarcodeNumberAreNotPresentInTheSaleOrder(25),
    CantConfirmPickingBecauseThereAreUnconfirmedPallets(26, "Hay palets en curso"),
    CantUndoPickingConfirmationBecauseItIsNotConfirmed(27),
    PalletStateIsNotCorrect(28, "El estado del palet no es el correcto"),
    PalletBarcodeIsNotCorrect(29);

    private int value;
    private String message;

    PickingActionResultEnum(int value)
    {
        this.value = value;
    }

    PickingActionResultEnum(int value, String message){
        this.value = value;
        this.message = message;
    }

    public static PickingActionResultEnum findByAbbr(int value)
    {
        for (PickingActionResultEnum currEnum : PickingActionResultEnum.values())
        {
            if (currEnum.value == value)
            {
                return currEnum;
            }
        }

        return null;
    }

    public int getValue()
    {
        return value;
    }

    @Override
    public String toString() {
        if (this.message != null)
            return this.message;
        else
            return super.toString();
    }
}
