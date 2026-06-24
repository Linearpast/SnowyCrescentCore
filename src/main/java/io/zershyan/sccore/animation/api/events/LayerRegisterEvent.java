package io.zershyan.sccore.animation.api.events;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.Event;

import java.util.HashMap;
import java.util.Map;

public abstract class LayerRegisterEvent extends Event {
    private final Map<ResourceLocation, Integer> layers = new HashMap<>();

    public Map<ResourceLocation, Integer> getLayers() {
        return layers;
    }

    public void registerLayer(ResourceLocation key, Integer value) {
        layers.put(key, value);
    }

    public static class Client extends LayerRegisterEvent { }

    public static class Server extends LayerRegisterEvent { }
}
