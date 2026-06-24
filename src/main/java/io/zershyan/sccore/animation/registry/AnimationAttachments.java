package io.zershyan.sccore.animation.registry;

import io.zershyan.sccore.SCCore;
import io.zershyan.sccore.animation.registry.attachment.PlayerAnimations;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class AnimationAttachments {
    private static final DeferredRegister<AttachmentType<?>> REGISTRY = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, SCCore.MODID);

    public static final Supplier<AttachmentType<PlayerAnimations>> PLAYER_ANIMATIONS = REGISTRY.register(
            "player_animations", () -> AttachmentType.builder(PlayerAnimations::new)
                    .serialize(PlayerAnimations.CODEC)
                    .sync(PlayerAnimations.SYNC_HANDLER)
                    .copyOnDeath()
                    .build());

    public static void register(IEventBus modEventBus) {
        REGISTRY.register(modEventBus);
    }

}
