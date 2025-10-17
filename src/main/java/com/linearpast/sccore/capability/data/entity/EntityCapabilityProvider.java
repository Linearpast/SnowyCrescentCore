package com.linearpast.sccore.capability.data.entity;

import com.linearpast.sccore.capability.data.ICapabilitySync;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * cap的最终 序列化、反序列化、获取方法
 * @param <C> 继承 {@link ICapabilitySync}
 */
@AutoRegisterCapability
public class EntityCapabilityProvider<C extends ICapabilitySync> implements ICapabilitySerializable<CompoundTag> {
    private final C instance;
    private final ResourceLocation resourceLocation;
    public EntityCapabilityProvider(ResourceLocation resourceLocation, C instance) {
        this.resourceLocation = resourceLocation;
        this.instance = instance;
    }

    @SuppressWarnings("unchecked")
    @Override
    public @NotNull <R> LazyOptional<R> getCapability(@NotNull Capability<R> cap, @Nullable Direction side) {
        Capability<C> iCapabilitySyncCapability = (Capability<C>) EntityCapabilityRegistry.getCapabilityRecord(resourceLocation).capability();
        return iCapabilitySyncCapability.orEmpty(cap, LazyOptional.of(() -> instance));
    }

    @Override
    public CompoundTag serializeNBT() {
        return instance.serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        instance.deserializeNBT(nbt);
    }
}
