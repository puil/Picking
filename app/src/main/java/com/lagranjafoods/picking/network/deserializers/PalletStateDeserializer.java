package com.lagranjafoods.picking.network.deserializers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.lagranjafoods.picking.models.PalletStateEnum;

import java.lang.reflect.Type;

/**
 * Created by joang on 19/01/2018.
 */

public class PalletStateDeserializer implements JsonDeserializer<PalletStateEnum> {

    @Override
    public PalletStateEnum deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        int typeInt = json.getAsInt();
        return PalletStateEnum.findByAbbr(typeInt);
    }
}
