package com.linearpast.sccore.capability.data;

import com.linearpast.sccore.capability.network.SimpleCapabilityPacket;
import com.linearpast.sccore.network.Channel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.UUID;

public interface ICapabilitySync extends INBTSerializable<CompoundTag> {
    void setDirty(boolean dirty);
    boolean isDirty();

    void setOwnerUUID(UUID uuid);
    UUID getOwnerUUID();

    /**
     * 该方法重写时应该在最后调用super方法
     * @param oldData 旧数据
     * @param listenDone 最后是否执行完成方法 {@link ICapabilitySync#onCopyDone()}
     */
    default void copyFrom(ICapabilitySync oldData, boolean listenDone) {
        this.setOwnerUUID(oldData.getOwnerUUID());
        this.setDirty(oldData.isDirty());
        if(listenDone) onCopyDone();
    }

    /**
     * 当copy的时候，如果某些值需要被重定义，你应该重写这个方法
     */
    default void onCopyDone(){}

    /**
     * 一般情况下建议重写，否则会以sccore的Channel发送
     * 服务端给全体玩家发送客户端同步数据
     */
    default void sendToClient(){
        Channel.sendAllPlayer(getDefaultPacket());
    }

    /**
     * 一般情况下建议重写，否则会以sccore的Channel发送
     * 服务端给单个玩家发送客户端同步数据
     * @param player 发送给的目标玩家
     */
    default void sendToClient(ServerPlayer player){
        Channel.sendToPlayer(getDefaultPacket(), player);
    }

    /**
     * 重写该方法为你的Capability设定一个网络包类，目前仅有客户端 <br>
     * 当调用sendToClient方法时会从这里获取网络包直接发送 <br>
     * 一般情况下，你应该extends SimpleCapabilityPacket然后重写该方法返回你的子类
     * @return 网络包类SimpleCapabilityPacket
     */
    SimpleCapabilityPacket getDefaultPacket();
}
