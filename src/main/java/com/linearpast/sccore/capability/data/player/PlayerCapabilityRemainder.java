package com.linearpast.sccore.capability.data;

import com.linearpast.sccore.capability.PlayerCapabilityRegistry;
import com.linearpast.sccore.capability.PlayerCapabilityUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;

import java.util.Optional;

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
                ICapabilitySync originData = PlayerCapabilityUtils.getPlayerCapability(original, key, ICapabilitySync.class);
                ICapabilitySync newData = PlayerCapabilityUtils.getPlayerCapability(newPlayer, key, ICapabilitySync.class);
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
                ICapabilitySync data = PlayerCapabilityUtils.getPlayerCapability(newPlayer, key, ICapabilitySync.class);
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
                ICapabilitySync data = PlayerCapabilityUtils.getPlayerCapability(target, key, ICapabilitySync.class);
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
                ICapabilitySync data = PlayerCapabilityUtils.getPlayerCapability(event.player, key, ICapabilitySync.class);
                if(data == null) return;
                if(data.isDirty()) {
                    data.setDirty(false);
                    data.sendToClient();
                }
            });
        }
    }

    /**
     * 玩家登录事件<br>
     */
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        if(!(player instanceof ServerPlayer serverPlayer)) return;
        PlayerCapabilityRegistry.getCapabilityMap().forEach((key, value) -> {
            ICapabilitySync data = PlayerCapabilityUtils.getPlayerCapability(player, key, ICapabilitySync.class);
            if(data == null) return;
            data.setOwnerUUID(serverPlayer.getUUID());
            data.setDirty(false);
            data.sendToClient();
        });
        Optional.ofNullable(serverPlayer.getServer()).map(MinecraftServer::getPlayerList).map(PlayerList::getPlayers).ifPresent(
                serverPlayers -> serverPlayers.forEach(p -> {
                    if(!p.getUUID().equals(serverPlayer.getUUID())) {
                        PlayerCapabilityRegistry.getCapabilityMap().forEach((key, value) -> {
                            ICapabilitySync data = PlayerCapabilityUtils.getPlayerCapability(player, key, ICapabilitySync.class);
                            if(data == null) return;
                            data.sendToClient(serverPlayer);
                        });
                    }
                })
        );
    }
}
