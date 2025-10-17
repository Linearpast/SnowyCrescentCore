package com.linearpast.sccore.capability.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 在Mod主类构造方法逻辑中调用createChannel，有两种：<br>
 * <pre>
 * 1. {@link com.linearpast.sccore.capability.CapabilityUtils#createChannel(SimpleChannel)}
 * 若如此做，则必须重写Cap实体类中的所有sendToPlayer方法，并在重写中调用使用你的Channel
 * </pre>
 * <pre>
 * 2. {@link com.linearpast.sccore.capability.CapabilityUtils#createChannel()}
 * 若如此做，则网络包会以SnowyCrescentCore的Channel注册
 * </pre>
 * 所添加的网络包必须实现ICapabilityPacket接口
 */
public class CapabilityChannel {
    private final SimpleChannel channel;
    public CapabilityChannel(SimpleChannel channel) {
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
    public <T extends ICapabilityPacket<?>> void register(
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
