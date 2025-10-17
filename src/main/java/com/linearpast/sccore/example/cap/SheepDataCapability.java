package com.linearpast.sccore.test.cap;

import com.linearpast.sccore.SnowyCrescentCore;
import com.linearpast.sccore.capability.CapabilityUtils;
import com.linearpast.sccore.capability.data.ICapabilitySync;
import com.linearpast.sccore.capability.data.entity.SimpleEntityCapabilitySync;
import com.linearpast.sccore.capability.network.SimpleCapabilityPacket;
import com.linearpast.sccore.test.network.SheepCapabilityPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Sheep;

import java.util.Optional;

public class SheepDataCapability extends SimpleEntityCapabilitySync implements ISheepData {
    public static final ResourceLocation key = new ResourceLocation(SnowyCrescentCore.MODID, "sheep_data");

    public static final String Value = "Value";

    private Integer value;

    @Override
    public Integer getValue() {
        return value;
    }

    @Override
    public void setValue(Integer value) {
        this.value = value;
        setDirty(true);
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        if(value != null) tag.putInt(Value, value);
        return tag;
    }

    @Override
    public void fromTag(CompoundTag tag) {
        this.value = null;
        if(tag.contains(Value)) this.value = tag.getInt(Value);
    }

    @Override
    public void copyFrom(ICapabilitySync oldData) {
        SheepDataCapability data = (SheepDataCapability) oldData;
        this.value = data.getValue();
    }

    @Override
    public SimpleCapabilityPacket<Sheep> getDefaultPacket() {
        return new SheepCapabilityPacket(serializeNBT());
    }

    public static Optional<SheepDataCapability> getCapability(Sheep sheep){
        return Optional.ofNullable(CapabilityUtils.getEntityCapability(
                sheep, SheepDataCapability.key, SheepDataCapability.class
        ));
    }
}
