package com.linearpast.sccore.capability.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 你应该在你的Channel中，使用：<br>
 * PlayerCapabilityChannel.create(你的Channel).add(索引, 网络包的new方法) <br>
 * 所添加的网络包必须实现了ICapabilityPacket接口
 */
public class PlayerCapabilityChannel {
    private final SimpleChannel channel;
    public PlayerCapabilityChannel(SimpleChannel channel) {
        this.channel = channel;
    }

    /**
     * 通过此方法预设添加一个网络包，等待实例register
     * @param clazz 网络包类
     * @param cid 索引
     * @param decoder 解码器
     * @param encoder 编码器
     * @param handler 句柄
     * @param <T> 网络包接口
     */
    public <T extends ICapabilityPacket> void register(
            Class<T> clazz,
            int cid,
            Function<FriendlyByteBuf, T> decoder,
            BiConsumer<T, FriendlyByteBuf> encoder,
            BiConsumer<T, Supplier<NetworkEvent.Context>> handler
    ) {
        channel.messageBuilder(clazz, cid, NetworkDirection.PLAY_TO_CLIENT)
                .decoder(decoder)
                .encoder(encoder)
                .consumerMainThread(handler)
                .add();
    }
}
