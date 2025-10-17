package com.linearpast.sccore.capability.network;

import com.linearpast.sccore.capability.data.ICapabilitySync;
import com.linearpast.sccore.capability.data.entity.SimpleEntityCapabilitySync;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

public abstract class SimpleCapabilityPacket<T extends Entity> implements ICapabilityPacket<T> {
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

    @SuppressWarnings("unchecked")
    @Override
    public void handler(NetworkEvent.Context context) {
        context.setPacketHandled(true);
        Minecraft instance = Minecraft.getInstance();
        ClientLevel level = instance.level;
        if (level == null) return;
        CompoundTag nbt = getData();
        Entity entity = level.getEntity(nbt.getInt(SimpleEntityCapabilitySync.Id));
        try {
            ICapabilitySync data = getCapability((T) entity);
            syncData(nbt, data);
        }catch (Exception ignored) {}
    }

    @Override
    public CompoundTag getData() {
        return data;
    }
}
