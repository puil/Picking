package com.lagranjafoods.picking.models;

/**
 * Created by joang on 25/01/2018.
 */

public class Warehouses {
    public static final int ManufacturingArea = 1;
    public static final int Warehouse = 2;
    public static final int Shop = 3;

    public static String getDescription(int warehouseId){
        switch (warehouseId){
            case ManufacturingArea:
                return "Area fabricación";
            case Warehouse:
                return "Almacén";
            case Shop:
                return "Tienda";
            default:
                return "ERROR";
        }
    }
}
