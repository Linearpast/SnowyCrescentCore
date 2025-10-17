package com.linearpast.sccore.test;

import com.linearpast.sccore.capability.CapabilityUtils;
import com.linearpast.sccore.capability.data.entity.EntityCapabilityRegistry;
import com.linearpast.sccore.capability.network.CapabilityChannel;
import com.linearpast.sccore.network.Channel;
import com.linearpast.sccore.test.cap.ISheepData;
import com.linearpast.sccore.test.cap.SheepDataCapability;
import com.linearpast.sccore.test.event.PlayerAttackEvent;
import com.linearpast.sccore.test.network.SheepCapabilityPacket;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.eventbus.api.IEventBus;

public class ModCaps {
    public static void register(){
        CapabilityChannel channel = CapabilityUtils.createChannel();
        int cid = Channel.getCid();
        CapabilityUtils.registerEntityCapabilityWithNetwork(
                SheepDataCapability.key,
                new EntityCapabilityRegistry.CapabilityRecord<>(
                        SheepDataCapability.class,
                        CapabilityManager.get(new CapabilityToken<>() {}),
                        ISheepData.class,
                        Sheep.class
                ),
                channel, SheepCapabilityPacket.class, cid++,
                SheepCapabilityPacket::new,
                SheepCapabilityPacket::encode,
                SheepCapabilityPacket::handle
        );
        Channel.setCid(cid);
    }

    public static void addListenerToEvent(IEventBus forgeBus){
        forgeBus.addListener(PlayerAttackEvent::onPlayerAttack);
    }
}
