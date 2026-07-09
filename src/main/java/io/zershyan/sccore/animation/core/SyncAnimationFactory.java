package io.zershyan.sccore.animation.core;

import com.mojang.serialization.Codec;
import io.zershyan.sccore.animation.data.ClientAnimation;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class SyncAnimationFactory {
    public static final Codec<HashMap<ResourceLocation, Integer>> LAYER_CODEC = Codec.unboundedMap(
            ResourceLocation.CODEC, Codec.INT
    ).xmap(HashMap::new, Function.identity());
    private static final Map<ResourceLocation, Integer> Layers = new HashMap<>();
    private static final Map<ResourceLocation, ClientAnimation> Animations = new HashMap<>();

    public static void reloadLayers(Map<ResourceLocation, Integer> layers) {
        ClientAnimationRegistry.registerLayers(layers);
        Layers.clear();
        Layers.putAll(layers);
    }

    public static void reloadAnimations(Map<ResourceLocation, ClientAnimation> layers) {
        Animations.clear();
        Animations.putAll(layers);
    }

    @Nullable
    public static ClientAnimation getAnimation(ResourceLocation location) {
        return Animations.getOrDefault(location, null);
    }

    public static Map<ResourceLocation, Integer> getLayers() {
        return Map.copyOf(Layers);
    }

    public static Map<ResourceLocation, ClientAnimation> getAnimations() {
        return Map.copyOf(Animations);
    }
}
