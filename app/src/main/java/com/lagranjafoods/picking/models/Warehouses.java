package com.lagranjafoods.picking.models;

/**
 * Created by joang on 25/01/2018.
 */

public class Warehouses {
    public static final int MANUFACTURING_AREA = 1;
    public static final int WAREHOUSE = 2;
    public static final int SHOP = 3;

    public static String getDescription(int warehouseId){
        switch (warehouseId){
            case MANUFACTURING_AREA:
                return "Area fabricación";
            case WAREHOUSE:
                return "Almacén";
            case SHOP:
                return "Tienda";
            default:
                return "ERROR";
        }
    }
}
