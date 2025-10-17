package com.linearpast.sccore.capability.data.player;

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

@AutoRegisterCapability
public class PlayerCapabilityProvider<C extends ICapabilitySync> implements ICapabilitySerializable<CompoundTag> {
    private final C instance;
    private final ResourceLocation resourceLocation;
    public PlayerCapabilityProvider(ResourceLocation resourceLocation, C instance) {
        this.resourceLocation = resourceLocation;
        this.instance = instance;
    }

    @SuppressWarnings("unchecked")
    @Override
    public @NotNull <R> LazyOptional<R> getCapability(@NotNull Capability<R> cap, @Nullable Direction side) {
        Capability<C> iCapabilitySyncCapability = (Capability<C>) PlayerCapabilityRegistry.getCapabilityRecord(resourceLocation).capability();
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
