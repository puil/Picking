package com.lagranjafoods.picking.models;

/**
 * Created by joang on 19/01/2018.
 */

public enum PalletStateEnum {
    Picking(0),
    Confirmed(1),
    Palletized(2),
    Shipped(3);

    private int value;

    PalletStateEnum(int value)
    {
        this.value = value;
    }

    public static PalletStateEnum findByAbbr(int value)
    {
        for (PalletStateEnum currEnum : PalletStateEnum.values())
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
    public String toString(){
        switch (this){
            case Confirmed:
                return "confirmado";
            case Palletized:
                return "paletizado";
            case Shipped:
                return "expedido";
            default:
                return "en curso";
        }
    }
}
