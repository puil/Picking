package com.lagranjafoods.picking.models;

import java.io.Serializable;

/**
 * Created by joang on 19/01/2018.
 */

public class PickingPallet implements Serializable {
    private int id;
    private int pickingHeaderId;
    private int palletNumber;
    private PalletStateEnum state;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPickingHeaderId() {
        return pickingHeaderId;
    }

    public void setPickingHeaderId(int pickingHeaderId) {
        this.pickingHeaderId = pickingHeaderId;
    }

    public int getPalletNumber() {
        return palletNumber;
    }

    public void setPalletNumber(int palletNumber) {
        this.palletNumber = palletNumber;
    }

    public PalletStateEnum getState() {
        return state;
    }

    public void setState(PalletStateEnum state) {
        this.state = state;
    }
}
