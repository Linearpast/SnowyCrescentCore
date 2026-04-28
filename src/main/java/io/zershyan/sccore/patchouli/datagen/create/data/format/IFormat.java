package io.zershyan.sccore.patchouli.datagen.create.data.format;

import com.google.gson.JsonObject;

public interface IFormat {
    default String parse() {
        return "";
    }
    default JsonObject serialize(){
        return new JsonObject();
    }
}
