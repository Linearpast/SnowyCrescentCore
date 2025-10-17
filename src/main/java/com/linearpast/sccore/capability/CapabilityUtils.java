package com.linearpast.sccore.capability;

import com.linearpast.sccore.capability.data.ICapabilitySync;
import com.linearpast.sccore.capability.data.player.PlayerCapabilityHandler;
import com.linearpast.sccore.capability.network.ICapabilityPacket;
import com.linearpast.sccore.capability.network.CapabilityChannel;
import com.linearpast.sccore.network.Channel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.simple.SimpleChannel;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class PlayerCapabilityUtils {

    /**
     * 同时注册capability和对应的网络包
     * @param key capability的唯一name
     * @param capabilityRecord capability的注册数据
     * @param channelRegister 应该提前创建好实例传入，参阅{@link PlayerCapabilityUtils#createChannel}
     * @param cid 网络频道索引
     * @param decoder 网络包的new方法调用
     */
    public static <T extends ICapabilityPacket> void registerCapabilityWithNetwork(
            ResourceLocation key, PlayerCapabilityRegistry.CapabilityRecord<? extends ICapabilitySync> capabilityRecord,
            CapabilityChannel channelRegister,
            Class<T> clazz, int cid,
            Function<FriendlyByteBuf, T> decoder,
            BiConsumer<T, FriendlyByteBuf> encoder,
            BiConsumer<T, Supplier<NetworkEvent.Context>> handler
    ) {
        PlayerCapabilityRegistry.registerCapability(key, capabilityRecord);
        channelRegister.register(clazz, cid, decoder, encoder, handler);
    }

    /**
     * 通过此方法注册capability，仅当 {@link net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent}
     * 事件结束之前有效
     * @param key capability的唯一name
     * @param capabilityRecord 使用record存储了应该注册的capability的各项数据，参阅：{@link PlayerCapabilityRegistry.CapabilityRecord}
     */
    public static <T extends ICapabilitySync> void registerCapability(ResourceLocation key, PlayerCapabilityRegistry.CapabilityRecord<T> capabilityRecord){
        PlayerCapabilityRegistry.registerCapability(key, capabilityRecord);
    }

    /**
     * 通过这个方法返回一个新的PlayerCapabilityChannel实例，一般只有注册不同channel的网络包才会使用
     * @param channel 你自己模组的channel
     * @return 新的实例
     */
    public static CapabilityChannel createChannel(SimpleChannel channel) {
        return new CapabilityChannel(channel);
    }

    /**
     * 通过这个方法返回本模组的Channel的PlayerCapabilityChannel实例
     * @return 新的实例
     */
    public static CapabilityChannel createChannel() {
        return new CapabilityChannel(Channel.INSTANCE);
    }

    /**
     * 通过此方法监听capability事件，从而启用所有功能<br>
     * 重复调用不会发生任何事
     * @param forgeBus forge事件总线
     */
    public static void registerHandler(IEventBus forgeBus){
        PlayerCapabilityHandler.register(forgeBus);
    }

    /**
     * 请通过该方法获取capability
     * @param player 目标玩家
     * @param key capability key
     * @param clazz 应返回的capability类型
     * @return 返回对应的capability
     */
    @Nullable
    public static <T extends ICapabilitySync> T getPlayerCapability(Player player, ResourceLocation key, Class<T> clazz) {
        if(player == null) return null;
        ICapabilitySync playerCapability = player.getCapability(
                PlayerCapabilityRegistry.getCapabilityMap().get(key).capability()
        ).resolve().orElse(null);
        if(playerCapability == null) return null;
        try {
            if(clazz.isInstance(playerCapability))
                return clazz.cast(playerCapability);
            else return null;
        }catch(ClassCastException e){
            return null;
        }
    }
}
