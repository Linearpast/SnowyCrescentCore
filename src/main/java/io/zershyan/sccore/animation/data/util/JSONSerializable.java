package io.zershyan.sccore.animation.data.util;

import com.google.gson.JsonElement;

public interface JSONSerializable<T extends JsonElement> {
    T serialize();
    void deserialize(T json);
}
