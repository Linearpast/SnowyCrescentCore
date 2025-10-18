package com.linearpast.sccore.capability;

import com.linearpast.sccore.capability.data.ICapabilitySync;
import com.linearpast.sccore.capability.data.entity.EntityCapabilityHandler;
import com.linearpast.sccore.capability.data.entity.EntityCapabilityRegistry;
import com.linearpast.sccore.capability.data.player.PlayerCapabilityHandler;
import com.linearpast.sccore.capability.data.player.PlayerCapabilityRegistry;
import com.linearpast.sccore.capability.network.CapabilityChannel;
import com.linearpast.sccore.capability.network.ICapabilityPacket;
import com.linearpast.sccore.network.Channel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.simple.SimpleChannel;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class CapabilityUtils {

    /**
     * 同时注册玩家capability和对应的网络包
     * @param key capability的唯一name
     * @param capabilityRecord capability的注册数据
     * @param channelRegister 应该提前创建好实例传入，参阅{@link CapabilityUtils#createChannel}
     * @param cid 网络频道索引
     * @param clazz 网络包的类
     * @param decoder 网络包的decode
     * @param encoder 网络包的encode
     * @param handler 网络包的handle
     */
    public static <T extends ICapabilityPacket<?>> void registerPlayerCapabilityWithNetwork(
            ResourceLocation key, PlayerCapabilityRegistry.CapabilityRecord<? extends ICapabilitySync<Player>> capabilityRecord,
            CapabilityChannel channelRegister,
            int cid,
            Class<T> clazz,
            Function<FriendlyByteBuf, T> decoder,
            BiConsumer<T, FriendlyByteBuf> encoder,
            BiConsumer<T, Supplier<NetworkEvent.Context>> handler
    ) {
        PlayerCapabilityRegistry.registerCapability(key, capabilityRecord);
        channelRegister.register(clazz, cid, decoder, encoder, handler);
    }

    /**
     * 同时注册实体capability和对应的网络包
     * @param key capability的唯一name
     * @param capabilityRecord capability的注册数据
     * @param channelRegister 应该提前创建好实例传入，参阅{@link CapabilityUtils#createChannel}
     * @param cid 网络频道索引
     * @param clazz 网络包的类
     * @param decoder 网络包的decode
     * @param encoder 网络包的encode
     * @param handler 网络包的handle
     */
    public static <T extends ICapabilityPacket<?>> void registerEntityCapabilityWithNetwork(
            ResourceLocation key, EntityCapabilityRegistry.CapabilityRecord<? extends ICapabilitySync<? extends Entity>> capabilityRecord,
            CapabilityChannel channelRegister,
            int cid,
            Class<T> clazz,
            Function<FriendlyByteBuf, T> decoder,
            BiConsumer<T, FriendlyByteBuf> encoder,
            BiConsumer<T, Supplier<NetworkEvent.Context>> handler
    ) {
        EntityCapabilityRegistry.registerCapability(key, capabilityRecord);
        channelRegister.register(clazz, cid, decoder, encoder, handler);
    }

    /**
     * 通过此方法注册玩家capability，仅当 {@link net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent}
     * 事件结束之前有效
     * @param key capability的唯一name
     * @param capabilityRecord 使用record存储了应该注册的capability的各项数据，参阅：{@link PlayerCapabilityRegistry.CapabilityRecord}
     */
    public static <T extends ICapabilitySync<Player>> void registerPlayerCapability(ResourceLocation key, PlayerCapabilityRegistry.CapabilityRecord<T> capabilityRecord){
        PlayerCapabilityRegistry.registerCapability(key, capabilityRecord);
    }

    /**
     * 通过此方法注册实体capability，仅当 {@link net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent}
     * 事件结束之前有效
     * @param key capability的唯一name
     * @param capabilityRecord 使用record存储了应该注册的capability的各项数据，参阅：{@link PlayerCapabilityRegistry.CapabilityRecord}
     */
    public static <T extends ICapabilitySync<Entity>> void registerEntityCapability(ResourceLocation key, EntityCapabilityRegistry.CapabilityRecord<T> capabilityRecord){
        EntityCapabilityRegistry.registerCapability(key, capabilityRecord);
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
        EntityCapabilityHandler.register(forgeBus);
    }

    /**
     * 请通过该方法获取capability
     * @param entity 目标实体，类型 {@code <E extends Entity>}
     * @param key capability的唯一名
     * @param clazz 应返回的capability类型，若为null则会返回 {@code ICapabilitySync<E>}
     * @return 返回对应的capability
     */
    @SuppressWarnings("unchecked")
    @Nullable
    public static <E extends Entity, T extends ICapabilitySync<E>> T getEntityCapability(E entity, ResourceLocation key, @Nullable Class<T> clazz) {
        try {
            ICapabilitySync<?> capabilitySync = entity.getCapability(
                    EntityCapabilityRegistry.getCapabilityMap().get(key).capability()
            ).resolve().orElse(null);
            if(clazz == null) return (T) capabilitySync;
            if(clazz.isInstance(capabilitySync))
                return clazz.cast(capabilitySync);
            else return null;
        }catch(Exception e){
            return null;
        }
    }

    /**
     * 请通过该方法获取capability
     * @param entity 目标实体，类型 {@code <E extends Entity>}
     * @param key capability的唯一名
     * @param clazz 应返回的capability类型，若为null则会返回 {@code ICapabilitySync<E>}
     * @return 返回对应的capability
     */
    @SuppressWarnings("unchecked")
    @Nullable
    public static <E extends Player, T extends ICapabilitySync<E>> T getPlayerCapability(E entity, ResourceLocation key, @Nullable Class<T> clazz) {
        try {
            ICapabilitySync<?> capabilitySync = entity.getCapability(
                    PlayerCapabilityRegistry.getCapabilityMap().get(key).capability()
            ).resolve().orElse(null);
            if(clazz == null) return (T) capabilitySync;
            if(clazz.isInstance(capabilitySync))
                return clazz.cast(capabilitySync);
            else return null;
        }catch(Exception e){
            return null;
        }
    }

    /**
     * 获取一个未转换类型的Cap
     * @param entity 目标
     * @param key cap的唯一名
     * @return 未转换类型的cap
     */
    @Nullable
    public static ICapabilitySync<?> getCapability(Entity entity, ResourceLocation key) {
        if(entity == null) return null;
        try {
            if(entity instanceof Player) {
                return entity.getCapability(
                        PlayerCapabilityRegistry.getCapabilityMap().get(key).capability()
                ).resolve().orElse(null);
            }
            return entity.getCapability(
                    EntityCapabilityRegistry.getCapabilityMap().get(key).capability()
            ).resolve().orElse(null);
        }catch(Exception e){
            return null;
        }

    }
}
