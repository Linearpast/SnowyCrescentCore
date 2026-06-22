package io.zershyan.sccore.animation.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.zershyan.sccore.animation.data.util.JSONSerializable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.LogicalSide;

import java.util.TreeMap;

public class ServerAnimation extends Animation {
    private final TreeMap<Integer, AABBData> aabbMovement = new TreeMap<>();
    private float jumpModifier = 1.0f;

    protected ServerAnimation(ResourceLocation key) {
        super(key, LogicalSide.SERVER);
    }

    public TreeMap<Integer, AABBData> getAabbMovement() {
        return aabbMovement;
    }

    public float getJumpModifier() {
        return jumpModifier;
    }

    public void setJumpModifier(float jumpModifier) {
        this.jumpModifier = jumpModifier;
    }

    @Override
    public JsonObject serialize() {
        JsonObject object = super.serialize();
        if(jumpModifier != 1.0f) {
            object.addProperty("jumpModifier", jumpModifier);
        }
        if(!aabbMovement.isEmpty()) {
            JsonArray array = new JsonArray();
            aabbMovement.values().forEach(data -> array.add(data.serialize()));
            object.add("aabbMovement", array);
        }
        return object;
    }

    @Override
    public void deserialize(JsonObject json) {
        super.deserialize(json);
        if(json.has("jumpModifier")) {
            jumpModifier = json.get("jumpModifier").getAsFloat();
        }
        if(json.has("aabbMovement")) {
            JsonElement element = json.get("aabbMovement");
            if(element.isJsonArray()) {
                aabbMovement.clear();
                JsonArray array = element.getAsJsonArray();
                for (JsonElement jsonElement : array) {
                    if(jsonElement.isJsonObject()) {
                        JsonObject dataObj = jsonElement.getAsJsonObject();
                        AABBData aabbData = AABBData.deserializeStatic(dataObj);
                        aabbMovement.put(aabbData.tick(), aabbData);
                    }
                }
            }
        }
    }

    public record AABBData(int tick, AABB aabb) implements JSONSerializable<JsonObject> {

        @Override
        public JsonObject serialize() {
            JsonObject object = new JsonObject();
            object.addProperty("tick", tick);
            JsonObject aabbObj = new JsonObject();
            aabbObj.addProperty("minX", aabb.minX);
            aabbObj.addProperty("maxX", aabb.maxX);
            aabbObj.addProperty("minY", aabb.minY);
            aabbObj.addProperty("maxY", aabb.maxY);
            aabbObj.addProperty("minZ", aabb.minZ);
            aabbObj.addProperty("maxZ", aabb.maxZ);
            object.add("aabb", aabbObj);
            return object;
        }

        @Override
        public void deserialize(JsonObject json) { }

        public static AABBData deserializeStatic(JsonObject json) {
            int tick = json.has("tick") ? json.get("tick").getAsInt() : -1;
            AABB aabb = new AABB(Vec3.ZERO, Vec3.ZERO);
            if(json.has("aabb")) {
                JsonElement element = json.get("aabb");
                if(element.isJsonObject()) {
                    JsonObject object = element.getAsJsonObject();
                    aabb.setMinX(object.has("minX") ? object.get("minX").getAsDouble() : 0.0);
                    aabb.setMinY(object.has("minY") ? object.get("minY").getAsDouble() : 0.0);
                    aabb.setMinZ(object.has("minZ") ? object.get("minZ").getAsDouble() : 0.0);
                    aabb.setMaxX(object.has("maxX") ? object.get("maxX").getAsDouble() : 0.0);
                    aabb.setMaxY(object.has("maxY") ? object.get("maxY").getAsDouble() : 0.0);
                    aabb.setMaxZ(object.has("maxZ") ? object.get("maxZ").getAsDouble() : 0.0);
                }
            }
            return new AABBData(tick, aabb);
        }
    }
}
