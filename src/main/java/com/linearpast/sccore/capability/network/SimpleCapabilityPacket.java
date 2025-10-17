package com.linearpast.snowy_crescent_core.capability.network;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

public abstract class SimpleCapabilityPacket implements ICapabilityPacket {
    private final CompoundTag data;

    public SimpleCapabilityPacket(CompoundTag data) {
        this.data = data;
    }

    public SimpleCapabilityPacket(FriendlyByteBuf buf) {
        this.data = buf.readNbt();
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeNbt(data);
    }

    @Override
    public CompoundTag getData() {
        return data;
    }
}
