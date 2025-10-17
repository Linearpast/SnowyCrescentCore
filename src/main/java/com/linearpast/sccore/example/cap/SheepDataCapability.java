package com.linearpast.sccore.example.cap;

import com.linearpast.sccore.SnowyCrescentCore;
import com.linearpast.sccore.capability.CapabilityUtils;
import com.linearpast.sccore.capability.data.ICapabilitySync;
import com.linearpast.sccore.capability.data.entity.SimpleEntityCapabilitySync;
import com.linearpast.sccore.capability.network.SimpleCapabilityPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Sheep;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * cap的实体类 <br>
 * 继承SimpleEntityCapabilitySync意味着自动托管一个id的同步 <br>
 * 实现的IsheepData仅含有属性value的getter和setter <br>
 */
public class SheepDataCapability extends SimpleEntityCapabilitySync implements ISheepData {
    //代表cap的key，注册、获取时都需要它
    public static final ResourceLocation key = new ResourceLocation(SnowyCrescentCore.MODID, "sheep_data");

    //只是为了统一管理(反)序列化时的keyName
    public static final String Value = "Value";

    //最后附加到实体实例变量
    private Integer value;

    //getter
    @Override
    public Integer getValue() {
        return value;
    }

    //setter
    @Override
    public void setValue(Integer value) {
        this.value = value;
        setDirty(true);
    }

    //在SimpleEntityCapabilitySync的serializeNBT方法中会调用
    //实际上相当于serializeNBT
    @Override
    public CompoundTag toTag(CompoundTag tag) {
        if(value != null) tag.putInt(Value, value);
        return tag;
    }

    //在SimpleEntityCapabilitySync的deserializeNBT方法中会调用
    //实际上相当于deserializeNBT
    @Override
    public void fromTag(CompoundTag tag) {
        this.value = null;
        if(tag.contains(Value)) this.value = tag.getInt(Value);
    }

    //从旧实例中复制数据到新实例的方法
    @Override
    public void copyFrom(ICapabilitySync oldData) {
        SheepDataCapability data = (SheepDataCapability) oldData;
        this.value = data.getValue();
    }

    /**
     * 网络包，你可以在里面重写任意方法，关于方法的作用请参阅<br>
     * {@link com.linearpast.sccore.capability.network.ICapabilityPacket} <br>
     * 可以不写在内部类中，作者是觉得它内容太少，写里面显得更紧凑美观
     */
    public static class SheepCapabilityPacket extends SimpleCapabilityPacket<Sheep> {
        //网络包构造方法
        public SheepCapabilityPacket(CompoundTag data) {
            super(data);
        }

        //这实际上是decoder
        public SheepCapabilityPacket(FriendlyByteBuf buf) {
            super(buf);
        }

        //仅用在网络包内部的getCap
        @Override
        public @Nullable ICapabilitySync getCapability(Sheep entity) {
            return SheepDataCapability.getCapability(entity).orElse(null);
        }
    }

    //获取默认网络包，会在sendToClient的时候调用以发送
    @Override
    public SimpleCapabilityPacket<Sheep> getDefaultPacket() {
        return new SheepCapabilityPacket(serializeNBT());
    }

    //在其他地方需要用到cap的时候调用这个
    //目的是为了简化cap utils的方法
    public static Optional<SheepDataCapability> getCapability(Sheep sheep){
        return Optional.ofNullable(CapabilityUtils.getEntityCapability(
                sheep, SheepDataCapability.key, SheepDataCapability.class
        ));
    }
}
