package com.linearpast.snowy_crescent_core.capability.network;

import com.linearpast.snowy_crescent_core.capability.data.ICapabilitySync;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public interface ICapabilityPacket {
    /**
     * 解码网络包
     * @param buf FriendlyByteBuf
     */
    void encode(FriendlyByteBuf buf);

    /**
     * 网络包处理事件，一般情况下不需要重写它，默认的行为足够使用
     * @param supplier supplier
     */
    default void handle(Supplier<NetworkEvent.Context> supplier){
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> handler(context));
    }

    /**
     * 网络包处理事件应该在这重写
     * @param context NetworkEvent.Context
     */
    void handler(NetworkEvent.Context context);

    /**
     * 在网络包中获取对应的capability，一般在 {@link ICapabilityPacket#syncData}后执行
     * @param player 目标玩家
     * @return 返回Capability
     */
    @Nullable ICapabilitySync getCapability(Player player);

    /**
     * 获取Tag
     * @return Tag
     */
    CompoundTag getData();

    /**
     * 网络包中将tag转换为capability data，默认直接反序列化
     * @param dataTag tag
     * @param data 应被写入数据的data
     */
    default void syncData(CompoundTag dataTag, ICapabilitySync data){
        if(data == null) return;
        data.deserializeNBT(dataTag);
    }
}
