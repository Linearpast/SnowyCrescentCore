package com.linearpast.sccore.capability.data.player;

import com.linearpast.sccore.capability.CapabilityUtils;
import com.linearpast.sccore.capability.data.ICapabilitySync;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;

import java.util.Optional;

/**
 * 用于维护数据同步
 */
public class PlayerCapabilityRemainder {
    /**
     * 玩家跨越维度/死亡时应该转移数据到新身体上
     * @param event Clone事件
     */
    public static void onPlayerClone(PlayerEvent.Clone event) {
        Player entity = event.getEntity();
        if(entity instanceof ServerPlayer newPlayer) {
            Player original = event.getOriginal();
            original.reviveCaps();
            PlayerCapabilityRegistry.getCapabilityMap().forEach((key, value) -> {
                ICapabilitySync<?> originData = CapabilityUtils.getCapability(original, key);
                ICapabilitySync<?> newData = CapabilityUtils.getCapability(newPlayer, key);
                if(originData != null && newData != null) {
                    newData.copyFrom(originData, true);
                    newData.sendToClient();
                }
            });
            original.invalidateCaps();
        }
    }

    /**
     * 玩家重生时应该更新自己的capability
     * @param event 重生事件实例
     */
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if(event.getEntity() instanceof ServerPlayer newPlayer){
            PlayerCapabilityRegistry.getCapabilityMap().forEach((key, value) -> {
                ICapabilitySync<?> data = CapabilityUtils.getCapability(newPlayer, key);
                if(data == null) return;
                data.sendToClient(newPlayer);
            });
        }
    }

    /**
     * 玩家追踪实体事件<br>
     * 当有其他玩家被加载时，客户端需要对方的capability，该事件可以主动发送<br>
     * 会调用{@link ICapabilitySync#sendToClient(ServerPlayer)}
     * @param event 追踪事件实例
     */
    public static void onEntityBeTracked(PlayerEvent.StartTracking event) {
        if (event.getTarget() instanceof Player target && event.getEntity() instanceof ServerPlayer attacker) {
            PlayerCapabilityRegistry.getCapabilityMap().forEach((key, value) -> {
                ICapabilitySync<?> data = CapabilityUtils.getCapability(target, key);
                if(data == null) return;
                data.sendToClient(attacker);
            });
        }
    }

    /**
     * 玩家Tick事件<br>
     * 如果capability是dirty的，就会调用{@link ICapabilitySync#sendToClient()}
     * @param event 事件实例
     */
    public static void capabilitySync(TickEvent.PlayerTickEvent event) {
        if(!event.player.level().isClientSide){
            PlayerCapabilityRegistry.getCapabilityMap().forEach((key, value) -> {
                ICapabilitySync<?> data = CapabilityUtils.getCapability(event.player, key);
                if(data == null) return;
                if(data.isDirty()) {
                    data.setDirty(false);
                    data.sendToClient();
                }
            });
        }
    }

    /**
     * 玩家登录事件 <br>
     * 重初始化登录玩家的cap <br>
     * 将服务端所有玩家的cap发送给该玩家以初始化该玩家的客户端侧的RemotePlayer数据<br>
     * 上一行的这个行为可能会导致卡顿，它的必要性还未知，可以发pr或issue提议删除它
     */
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        if(!(player instanceof ServerPlayer serverPlayer)) return;
        PlayerCapabilityRegistry.getCapabilityMap().forEach((key, value) -> {
            ICapabilitySync<Player> data = CapabilityUtils.getPlayerCapability(player, key, null);
            if(data == null) return;
            if(data instanceof SimplePlayerCapabilitySync capabilitySync) {
                capabilitySync.setOwnerUUID(serverPlayer.getUUID());
            }
            data.attachInit(serverPlayer);
            data.setDirty(false);
            data.sendToClient();
        });
        Optional.ofNullable(serverPlayer.getServer()).map(MinecraftServer::getPlayerList).map(PlayerList::getPlayers).ifPresent(
                serverPlayers -> serverPlayers.forEach(p -> {
                    if(!p.getUUID().equals(serverPlayer.getUUID())) {
                        PlayerCapabilityRegistry.getCapabilityMap().forEach((key, value) -> {
                            ICapabilitySync<?> data = CapabilityUtils.getCapability(player, key);
                            if(data == null) return;
                            data.sendToClient(serverPlayer);
                        });
                    }
                })
        );
    }
}
