package io.zershyan.sccore.animation.api.events;

import io.zershyan.sccore.animation.data.ClientAnimation;
import io.zershyan.sccore.animation.data.ServerAnimation;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.Event;

import java.util.HashMap;
import java.util.Map;

public abstract class AnimationRegisterEvent extends Event {
    public static class Client extends AnimationRegisterEvent {
        private final Map<ResourceLocation, ClientAnimation> animations = new HashMap<>();

        public Map<ResourceLocation, ClientAnimation> getAnimations() {
            return new HashMap<>(animations);
        }

        public void registerAnimation(ResourceLocation location, ClientAnimation animation) {
            animations.put(location, animation);
        }
    }

    public static class Server extends AnimationRegisterEvent {
        private final Map<ResourceLocation, ServerAnimation> animations = new HashMap<>();

        public Map<ResourceLocation, ServerAnimation> getAnimations() {
            return new HashMap<>(animations);
        }

        public void registerAnimation(ResourceLocation location, ServerAnimation animation) {
            animations.put(location, animation);
        }
    }
}
