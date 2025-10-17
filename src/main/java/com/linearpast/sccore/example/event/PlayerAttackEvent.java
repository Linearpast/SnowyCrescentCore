package com.linearpast.sccore.example.event;

import com.linearpast.sccore.example.cap.SheepDataCapability;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.AttackEntityEvent;

public class PlayerAttackEvent {
    //简单的测试一下cap是否生效
    public static void onPlayerAttack(AttackEntityEvent event) {
        Entity target = event.getTarget();
        Player entity = event.getEntity();
        if(entity instanceof ServerPlayer player) {
            if(target instanceof Sheep sheep){
                SheepDataCapability.getCapability(sheep).ifPresent(data -> {
                    Integer value = data.getValue();
                    if(value == null) value = 0;
                    value++;
                    data.setValue(value);
                    Integer id = data.getId();
                    player.sendSystemMessage(Component.literal(
                            "第" + value + "攻击了id为\"" + id + "\"的羊"
                    ));
                });
            }
        }
    }
}
