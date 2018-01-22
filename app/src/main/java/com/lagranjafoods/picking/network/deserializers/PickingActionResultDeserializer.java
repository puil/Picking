package com.lagranjafoods.picking.network.deserializers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.lagranjafoods.picking.models.PickingActionResultEnum;

import java.lang.reflect.Type;

/**
 * Created by joang on 19/01/2018.
 */

public class PickingActionResultDeserializer implements JsonDeserializer<PickingActionResultEnum> {

    @Override
    public PickingActionResultEnum deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        int typeInt = json.getAsInt();
        return PickingActionResultEnum.findByAbbr(typeInt);
    }
}
