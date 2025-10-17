package com.linearpast.sccore.capability.data.entity;

import com.linearpast.sccore.capability.data.ICapabilitySync;
import net.minecraft.nbt.CompoundTag;

/**
 * 实现时建议手动添加一个字段以及方法，例如：
 * <pre>
 * {@code
 *     public static final ResourceLocation key =
 *          new ResourceLocation(MyMod.MODID, "my_data");
 *     public static Optional<MyDataCapability> getCapability(Player player){
 *         return Optional.ofNullable(EntityCapabilityHandler.getPlayerCapability(
 *             player, MyDataCapability.key, MyDataCapability.class
 *         ));
 *     }
 * }
 * </pre>
 *
 */
public abstract class SimpleEntityCapabilitySync implements ICapabilitySync {
    public static final String Id = "Id";

    private boolean dirty;
    private Integer id;

    @Override
    public boolean isDirty() {
        return dirty;
    }

    @Override
    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
        setDirty(true);
    }

    /**
     * 从参数实例中复制数据到当前实例 <br>
     * 你不应该重写它，你应该实现 {@link SimpleEntityCapabilitySync#copyFrom(ICapabilitySync)}
     * @param oldData 旧数据
     * @param listenDone 最后是否执行完成方法 {@link ICapabilitySync#onCopyDone()}
     */
    @Override
    public void copyFrom(ICapabilitySync oldData, boolean listenDone) {
        SimpleEntityCapabilitySync data = (SimpleEntityCapabilitySync) oldData;
        this.setId(data.getId());
        copyFrom(data);
        ICapabilitySync.super.copyFrom(oldData, listenDone);
    }

    /**
     * 触发数据复制时会执行的方法
     * @param oldData 从这个数据中复制到当前实例
     */
    public abstract void copyFrom(ICapabilitySync oldData);

    /**
     * 序列化为tag <br>
     * 你不应该重写它，你应该实现{@link ICapabilitySync#toTag(CompoundTag)}
     * @return tag
     */
    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        if(id != null) tag.putInt(Id, id);
        tag = toTag(tag);
        return tag;
    }

    /**
     * 反序列化为实例对象 <br>
     * 你应该不需要重写它，你应该实现{@link ICapabilitySync#fromTag(CompoundTag)}
     * @param nbt nbt
     */
    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.id = null;
        if(nbt.contains(Id)) this.id = nbt.getInt(Id);
        fromTag(nbt);
    }
}
