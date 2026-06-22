package io.zershyan.sccore.animation.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.kosmx.playerAnim.core.util.Vec3f;
import io.zershyan.sccore.animation.data.util.JSONSerializable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.LogicalSide;

import java.util.TreeMap;

public class ClientAnimation extends Animation {
    private final CameraChange firstPersonCameraChange = new CameraChange(true);
    private final CameraChange cameraChange = new CameraChange(false);

    protected ClientAnimation(ResourceLocation key) {
        super(key, LogicalSide.CLIENT);
    }

    public CameraChange getFirstPersonCameraChange() {
        return firstPersonCameraChange;
    }

    public CameraChange getCameraChange() {
        return cameraChange;
    }

    @Override
    public JsonObject serialize() {
        JsonObject object = super.serialize();
        JsonObject fpObj = firstPersonCameraChange.serialize();
        if(!fpObj.isEmpty()) {
            object.add("firstPersonCameraChange", fpObj);
        }
        JsonObject obj = cameraChange.serialize();
        if(!obj.isEmpty()) {
            object.add("cameraChange", obj);
        }
        return object;
    }

    @Override
    public void deserialize(JsonObject json) {
        super.deserialize(json);
        if(json.has("firstPersonCameraChange")) {
            JsonElement element = json.get("firstPersonCameraChange");
            if(element.isJsonObject()) {
                CameraChange change = new CameraChange(true);
                change.deserialize(element.getAsJsonObject());
                firstPersonCameraChange.relative = change.relative;
                firstPersonCameraChange.movement.clear();
                firstPersonCameraChange.movement.putAll(change.movement);
            }
        }
        if(json.has("cameraChange")) {
            JsonElement element = json.get("cameraChange");
            if(element.isJsonObject()) {
                CameraChange change = new CameraChange(true);
                change.deserialize(element.getAsJsonObject());
                cameraChange.relative = change.relative;
                cameraChange.movement.clear();
                cameraChange.movement.putAll(change.movement);
            }
        }
    }

    public static class CameraChange implements JSONSerializable<JsonObject> {
        private boolean relative;
        private final TreeMap<Integer, CameraData> movement = new TreeMap<>();

        private CameraChange(boolean relative) {
            this.relative = relative;
        }

        public boolean isRelative() {
            return relative;
        }

        public void setRelative(boolean relative) {
            this.relative = relative;
        }

        public TreeMap<Integer, CameraData> getMovement() {
            return movement;
        }

        @Override
        public JsonObject serialize() {
            JsonObject object = new JsonObject();
            if(!movement.isEmpty()) {
                object.addProperty("relative", relative);
                JsonArray movementArray = new JsonArray();
                for (CameraData data : movement.values()) {
                    movementArray.add(data.serialize());
                }
                object.add("movement", movementArray);
            }
            return object;
        }

        @Override
        public void deserialize(JsonObject json) {
            if(json.has("relative")) {
                relative = json.get("relative").getAsBoolean();
            }
            if(json.has("movement")) {
                JsonElement element = json.get("movement");
                if(element.isJsonArray()) {
                    movement.clear();
                    JsonArray array = element.getAsJsonArray();
                    for (JsonElement jsonElement : array) {
                        if(jsonElement.isJsonObject()) {
                            JsonObject dataObj = jsonElement.getAsJsonObject();
                            CameraData cameraData = CameraData.deserializeStatic(dataObj);
                            movement.put(cameraData.tick(), cameraData);
                        }
                    }
                }
            }
        }
    }
    public record CameraData(int tick, Vec3 offset, Vec3f camEulerAngles) implements JSONSerializable<JsonObject> {

        @Override
        public JsonObject serialize() {
            JsonObject object = new JsonObject();
            object.addProperty("tick", tick);
            if(!offset.equals(Vec3.ZERO)) {
                JsonObject offsetObject = new JsonObject();
                offsetObject.addProperty("x", offset.x());
                offsetObject.addProperty("y", offset.y());
                offsetObject.addProperty("z", offset.z());
                object.add("offset", offsetObject);
            }
            if(!camEulerAngles.equals(Vec3f.ZERO)) {
                JsonObject camEulerObject = new JsonObject();
                camEulerObject.addProperty("x", camEulerAngles.getX());
                camEulerObject.addProperty("y", camEulerAngles.getY());
                camEulerObject.addProperty("z", camEulerAngles.getZ());
                object.add("camEulerAngles", camEulerObject);
            }
            return object;
        }

        @Override
        public void deserialize(JsonObject json) { }

        public static CameraData deserializeStatic(JsonObject json) {
            int tick = json.has("tick") ? json.get("tick").getAsInt() : -1;
            Vec3 offset = Vec3.ZERO;
            Vec3f camEulerAngles = Vec3f.ZERO;
            if(json.has("offset")) {
                JsonElement element = json.get("offset");
                if(element.isJsonObject()) {
                    JsonObject offsetObj = element.getAsJsonObject();
                    double x = offsetObj.has("x") ? offsetObj.get("x").getAsDouble() : 0.0;
                    double y = offsetObj.has("y") ? offsetObj.get("y").getAsDouble() : 0.0;
                    double z = offsetObj.has("z") ? offsetObj.get("z").getAsDouble() : 0.0;
                    offset = new Vec3(x, y, z);
                }
            }
            if(json.has("camEulerAngles")) {
                JsonElement element = json.get("camEulerAngles");
                if(element.isJsonObject()) {
                    JsonObject cameraEulerObj = element.getAsJsonObject();
                    float x = cameraEulerObj.has("x") ? cameraEulerObj.get("x").getAsFloat() : 0.0f;
                    float y = cameraEulerObj.has("y") ? cameraEulerObj.get("y").getAsFloat() : 0.0f;
                    float z = cameraEulerObj.has("z") ? cameraEulerObj.get("z").getAsFloat() : 0.0f;
                    camEulerAngles = new Vec3f(x, y, z);
                }
            }
            return new CameraData(tick, offset, camEulerAngles);
        }
    }
}
