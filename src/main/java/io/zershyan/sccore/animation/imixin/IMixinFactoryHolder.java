package io.zershyan.sccore.animation.imixin;

import dev.kosmx.playerAnim.api.layered.IAnimation;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public interface IMixinFactoryHolder {
    record DataHolder(@Nullable ResourceLocation id, int priority, @NotNull IAnimation animation) {}

    void sccore$clearAnimations(Set<ResourceLocation> ids);
}
