package com.linearpast.sccore.capability.data.player;

import com.linearpast.sccore.capability.data.ICapabilitySync;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

/**
 * 实现时建议手动添加：<br>
 * key  —— 作为cap的唯一标识 <br>
 * getCapability(Player player) —— 获取cap的简化方法<br>
 * 例：
 * <pre>
 * {@code
 *     public static final ResourceLocation key =
 *          new ResourceLocation(MyMod.MODID, "my_data");
 *     public static Optional<MyDataCapability> getCapability(Player player){
 *         return Optional.ofNullable(CapabilityUtils.getPlayerCapability(
 *             player, MyDataCapability.key, MyDataCapability.class
 *         ));
 *     }
 * }
 * </pre>
 *
 */
public abstract class SimplePlayerCapabilitySync implements ICapabilitySync<Player> {
    public static final String OwnerUUID = "OwnerUUID";

    private boolean dirty;
    private UUID ownerUUID;

    @Override
    public boolean isDirty() {
        return dirty;
    }

    @Override
    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public UUID getOwnerUUID() {
        return ownerUUID;
    }

    public void setOwnerUUID(UUID ownerUUID) {
        this.ownerUUID = ownerUUID;
        setDirty(true);
    }

    /**
     * 从参数实例中复制数据到当前实例 <br>
     * 你不应该重写它，你应该实现 {@link SimplePlayerCapabilitySync#copyFrom(ICapabilitySync)}
     * @param oldData 旧数据
     * @param listenDone 最后是否执行完成方法 {@link ICapabilitySync#onCopyDone()}
     */
    @Override
    public void copyFrom(ICapabilitySync<?> oldData, boolean listenDone) {
        SimplePlayerCapabilitySync data = (SimplePlayerCapabilitySync) oldData;
        this.setOwnerUUID(data.getOwnerUUID());
        copyFrom(data);
        ICapabilitySync.super.copyFrom(oldData, listenDone);
    }

    /**
     * 触发数据复制时会执行的方法
     * @param oldData 从这个数据中复制到当前实例
     */
    public abstract void copyFrom(ICapabilitySync<?> oldData);

    /**
     * 序列化为tag <br>
     * 你不应该重写它，你应该实现{@link ICapabilitySync#toTag(CompoundTag)}
     * @return tag
     */
    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        if(ownerUUID != null) tag.putUUID(OwnerUUID, ownerUUID);
        tag = toTag(tag);
        return tag;
    }

    /**
     * 反序列化为实例对象 <br>
     * 你不应该重写它，你应该实现{@link ICapabilitySync#fromTag(CompoundTag)} )}
     * @param nbt nbt
     */
    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.ownerUUID = null;
        if(nbt.contains(OwnerUUID)) this.ownerUUID = nbt.getUUID(OwnerUUID);
        fromTag(nbt);
    }
}
