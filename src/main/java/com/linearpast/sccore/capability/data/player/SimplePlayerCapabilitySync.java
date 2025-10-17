package com.linearpast.sccore.capability.data;

import net.minecraft.nbt.CompoundTag;

import java.util.UUID;

/**
 * 实现时建议手动添加一个字段以及方法，例如：
 * <pre>
 * {@code
 *     public static final ResourceLocation key =
 *          new ResourceLocation(MyMod.MODID, "my_data");
 *     public static Optional<MyDataCapability> getCapability(Player player){
 *         return Optional.ofNullable(PlayerCapabilityHandler.getPlayerCapability(
 *             player, MyDataCapability.key, MyDataCapability.class
 *         ));
 *     }
 * }
 * </pre>
 *
 */
public abstract class SimpleCapabilitySync implements ICapabilitySync {
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

    @Override
    public UUID getOwnerUUID() {
        return ownerUUID;
    }

    @Override
    public void setOwnerUUID(UUID ownerUUID) {
        this.ownerUUID = ownerUUID;
        setDirty(true);
    }

    /**
     * 序列化为tag
     * @return tag
     */
    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        if(ownerUUID != null) tag.putUUID(OwnerUUID, ownerUUID);
        return tag;
    }

    /**
     * 反序列化为实例对象
     * @param nbt nbt
     */
    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.ownerUUID = null;
        if(nbt.contains(OwnerUUID)) this.ownerUUID = nbt.getUUID(OwnerUUID);
    }
}
