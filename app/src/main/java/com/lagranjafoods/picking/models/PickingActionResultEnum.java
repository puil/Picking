package com.lagranjafoods.picking.models;

/**
 * Created by joang on 19/01/2018.
 */

public enum PickingActionResultEnum {
    Success(0),
    SaleOrderNotFound(1, "Pedido no encontrado"),
    PalletNotFound(2, "Palet no encontrado"),
    PickingNotFound(3, "Picking no encontrado"),
    PickingIsAlreadyConfirmed(4, "El picking ya está confirmado"),
    CantDeletePalletBecauseItContainsSomeLines(5, "El palet contiene líneas"),
    PalletIsAlreadyConfirmed(6, "El palet ya está confirmado"),
    CantConfirmPalletBecauseItContainsSomeLines(7),
    UserNotFound(8, "Usuario no encontrado"),
    CantConfirmPalletBecauseItHasNoLines(9, "El palet está vacío"),
    RequestIsNull(10),
    SaleOrderLineNotFound(11, "Línea del pedido no encontrada"),
    ProductNotFound(12, "El artículo no existe"),
    LotMustBeEmptyBecauseProductIsCommercial(13, "El lote debe estar vacío porque el artículo es de comercial"),
    ExpirationDateMustBeNullBecauseProductIsCommercial(14, "La fecha de caducidad debe estar vacía porque el artículo es de comercial"),
    LotCantBeEmpty(15, "El lote está vacío"),
    ExpirationDateCantBeNull(16, "La fecha de caducidad está vacía"),
    AmoutMustBeGreaterThanZero(17, "La cantidad debe ser mayor que 0"),
    SourceWarehouseNotFound(18, "El almacén de origen no se ha encontrado"),
    ThereIsNotEnoughStockInSourceWarehouseForTheGivenProductLotAndExpirationDate(19, "No hay suficiente cantidad de stock en la ubicación elegida"),
    PickedUnitsPlusRequestedAmountIsHigherThanSaleOrderLineUnits(20, "La cantidad informada supera la cantidad pendiente para añadir al picking"),
    ProductInSaleOrderLineDoesntMatchTheProductInRequest(21, "El artículo de la línea del pedido no concuerda con el artículo de la request"),
    SaleOrderNumberInPickingHeaderDoesntMatchSaleOrderNumberInSaleOrderLineRequest(22),
    PalletLineNotFound(23, "Línea de palet no encontrada"),
    ThereAreNoProductsWithTheGivenBarcodeNumber(24, "No hay artículos con el código de barras leído"),
    ProductsFoundWithTheBarcodeNumberAreNotPresentInTheSaleOrder(25, "El pedido no contiene los artículos vinculados al código de barras, o bien, ya no queda cantidad pendiente de añadir"),
    CantConfirmPickingBecauseThereAreUnconfirmedPallets(26, "Hay palets en curso"),
    CantUndoPickingConfirmationBecauseItIsNotConfirmed(27, "El picking no está confirmado"),
    PalletStateIsNotCorrect(28, "El estado del palet no es el correcto"),
    PalletBarcodeIsNotCorrect(29, "El código de barras del palet no es correcto"),
    ProductFoundWithProductCodeIsNotPresentInTheSaleOrder(30, "El artículo no se encuentra en el pedido o todas sus unidades ya se han añadido al picking"),
    AmountCantBeDifferentThanAssortmentStock(31, "Cuando el artículo es un surtido, la cantidad introducida debe ser la cantidad total del lote seleccionado");

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
