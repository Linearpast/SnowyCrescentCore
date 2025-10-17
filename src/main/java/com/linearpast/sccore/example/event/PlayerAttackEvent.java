package com.linearpast.sccore.test.event;

import com.linearpast.sccore.test.cap.SheepDataCapability;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.AttackEntityEvent;

public class PlayerAttackEvent {
    public static void onPlayerAttack(AttackEntityEvent event) {
        Entity target = event.getTarget();
        Player entity = event.getEntity();
        if(entity instanceof ServerPlayer player) {
            if(target instanceof Sheep sheep){
                SheepDataCapability iSheepData = SheepDataCapability.getCapability(sheep).orElse(null);
                if(iSheepData == null) return;
                Integer value = iSheepData.getValue();
                if(value == null) value = 0;
                value++;
                iSheepData.setValue(value);
                Integer id = iSheepData.getId();
                player.sendSystemMessage(Component.literal(
                        "第" + value + "攻击了id为\"" + id + "\"的羊"
                ));
            }
        }
    }
}
