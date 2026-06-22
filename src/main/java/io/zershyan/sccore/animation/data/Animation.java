package io.zershyan.sccore.animation.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.zershyan.sccore.animation.data.util.JSONSerializable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.LogicalSide;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class Animation implements JSONSerializable<JsonObject> {
    @NotNull
    protected ResourceLocation key;
    @Nullable
    private String name;
    private int priority;
    @Nullable
    private RideData rideData;
    @NotNull
    private LogicalSide side;

    protected Animation(@NotNull ResourceLocation key, @NotNull LogicalSide side) {
        this.key = key;
        this.side = side;
    }

    public @NotNull ResourceLocation getKey() {
        return key;
    }

    public @Nullable String getName() {
        return name;
    }

    public void setName(@NotNull String name) {
        this.name = name;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public @Nullable RideData getRideData() {
        return rideData;
    }

    public void setRideData(@Nullable RideData rideData) {
        this.rideData = rideData;
    }

    public @NotNull LogicalSide getSide() {
        return side;
    }

    @Override
    public JsonObject serialize() {
        JsonObject object = new JsonObject();
        object.addProperty("key", key.toString());
        object.addProperty("priority", priority);
        object.addProperty("side", side.ordinal());
        if(name != null) {
            object.addProperty("name", name);
        }
        if(rideData != null) {
            object.add("ride", rideData.serialize());
        }
        return object;
    }

    @Override
    public void deserialize(JsonObject json) {
        if(json.has("key")) {
            key = ResourceLocation.parse(json.get("key").getAsString());
        }
        if(json.has("name")) {
            name = json.get("name").getAsString();
        }
        if(json.has("priority")) {
            priority = json.get("priority").getAsInt();
        }
        if(json.has("side")) {
            side = LogicalSide.values()[json.get("side").getAsInt()];
        }
        if(json.has("ride")) {
            JsonElement element = json.get("ride");
            if(element.isJsonObject()) {
                rideData = new RideData(){{deserialize(element.getAsJsonObject());}};
            }
        }
    }

    public static class RideData implements JSONSerializable<JsonObject> {
        private final List<ResourceLocation> componentAnimations = new ArrayList<>();
        private Vec3 offset = new Vec3(0.0D, 0.0D, 0.0D);
        private int existTick;
        private float xRot;
        private float yRot;

        protected RideData() {}

        public void setOffset(Vec3 offset) {
            this.offset = offset;
        }

        public void setExistTick(int existTick) {
            this.existTick = existTick;
        }

        public void setXRot(float xRot) {
            this.xRot = xRot;
        }

        public void setYRot(float yRot) {
            this.yRot = yRot;
        }

        public void addComponentAnimation(ResourceLocation animation) {
            this.componentAnimations.add(animation);
        }

        public float getXRot() {
            return xRot;
        }

        public float getYRot() {
            return yRot;
        }

        public Vec3 getOffset() {
            return offset;
        }

        public int getExistTick() {
            return existTick;
        }

        public List<ResourceLocation> getComponentAnimations() {
            return componentAnimations;
        }

        @Override
        public JsonObject serialize() {
            JsonObject object = new JsonObject();
            object.addProperty("existTick", existTick);
            object.addProperty("xRot", xRot);
            object.addProperty("yRot", yRot);
            if(!offset.equals(Vec3.ZERO)) {
                JsonObject offsetObject = new JsonObject();
                offsetObject.addProperty("x", offset.x());
                offsetObject.addProperty("y", offset.y());
                offsetObject.addProperty("z", offset.z());
                object.add("offset", offsetObject);
            }
            JsonArray element = new JsonArray();
            componentAnimations.stream().map(ResourceLocation::toString).forEach(element::add);
            if(!element.isEmpty()) object.add("componentAnimations", element);
            return object;
        }

        @Override
        public void deserialize(JsonObject json) {
            if(json.has("existTick")) {
                existTick = json.get("existTick").getAsInt();
            }
            if(json.has("xRot")) {
                xRot = json.get("xRot").getAsFloat();
            }
            if(json.has("yRot")) {
                yRot = json.get("yRot").getAsFloat();
            }
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
            if(json.has("componentAnimations")) {
                JsonElement element = json.get("componentAnimations");
                if(element.isJsonArray()) {
                    componentAnimations.clear();
                    for (JsonElement elem : element.getAsJsonArray()) {
                        String locStr = elem.getAsString();
                        componentAnimations.add(ResourceLocation.parse(locStr));
                    }
                }
            }
        }
    }
}
