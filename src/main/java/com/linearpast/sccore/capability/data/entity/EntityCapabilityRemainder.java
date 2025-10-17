package com.linearpast.sccore.capability.data.entity;

import com.linearpast.sccore.capability.CapabilityUtils;
import com.linearpast.sccore.capability.data.ICapabilitySync;
import com.linearpast.sccore.capability.data.player.PlayerCapabilityRegistry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class EntityCapabilityRemainder {
    /**
     * 玩家追踪实体事件<br>
     * 当有其他实体被加载时，客户端需要对方的capability，该事件可以主动发送<br>
     * 会调用{@link ICapabilitySync#sendToClient(ServerPlayer)}
     * @param event 追踪事件实例
     */
    public static void onEntityBeTracked(PlayerEvent.StartTracking event) {
        if (event.getEntity() instanceof ServerPlayer attacker) {
            PlayerCapabilityRegistry.getCapabilityMap().forEach((key, value) -> {
                ICapabilitySync data = CapabilityUtils.getEntityCapability(event.getTarget(), key, ICapabilitySync.class);
                if(data == null) return;
                data.sendToClient(attacker);
            });
        }
    }

    /**
     * 实体Tick事件<br>
     * 如果capability是dirty的，就会调用{@link ICapabilitySync#sendToClient()} <br>
     * 为了性能，每秒才触发一次同步
     * @param event 事件实例
     */
    public static void capabilitySync(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        if(!entity.level().isClientSide){
            if (entity.tickCount % 20 == 0) {
                PlayerCapabilityRegistry.getCapabilityMap().forEach((key, value) -> {
                    ICapabilitySync data = CapabilityUtils.getEntityCapability(entity, key, ICapabilitySync.class);
                    if(data == null) return;
                    if(data.isDirty()) {
                        data.setDirty(false);
                        data.sendToClient();
                    }
                });
            }
        }
    }

    /**
     * 实体加入level的事件，初始化
     * @param event 实体加入事件
     */
    public static void onEntityJoin(EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();
        if(entity.level().isClientSide) return;
        EntityCapabilityRegistry.getCapabilityMap().forEach((key, value) -> {
            ICapabilitySync data = CapabilityUtils.getEntityCapability(entity, key, ICapabilitySync.class);
            if(data == null) return;
            if(data instanceof SimpleEntityCapabilitySync capabilitySync){
                capabilitySync.setId(entity.getId());
            }
            data.setDirty(false);
            data.sendToClient();
        });
    }
}
