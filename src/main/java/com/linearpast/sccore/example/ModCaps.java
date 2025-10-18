package com.linearpast.sccore.example;

import com.linearpast.sccore.capability.CapabilityUtils;
import com.linearpast.sccore.capability.data.entity.EntityCapabilityRegistry;
import com.linearpast.sccore.capability.network.CapabilityChannel;
import com.linearpast.sccore.example.cap.ISheepData;
import com.linearpast.sccore.example.cap.SheepDataCapability;
import com.linearpast.sccore.example.event.PlayerAttackEvent;
import com.linearpast.sccore.network.Channel;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.eventbus.api.IEventBus;

public class ModCaps {
    /**
     * 注册实体capability的示例<br>
     * 请参阅 {@link CapabilityUtils}
     */
    public static void register(){
        //如果你想将网络包注册到你自己的mod中，createChannel(INSTANCE)
        //然后别忘记在capability类里面重写所有的sendToClient方法
        CapabilityChannel channel = CapabilityUtils.createChannel();
        //不可与其他网络包重复的任意整数
        int cid = Channel.getCid();
        //注册实体cap和它的网络包
        //若注册玩家的请用registerPlayerCapabilityWithNetwork
        CapabilityUtils.registerEntityCapabilityWithNetwork(
                //一个resourceLocation，任意命名，不重复即可
                SheepDataCapability.key,
                //需要注册cap的数据
                new EntityCapabilityRegistry.CapabilityRecord<>(
                        //registry将会 new 一个此类的实例，你可以在该类中重写无参构造以让它初始化
                        SheepDataCapability.class,
                        //固定写法，一般情况你无需修改它
                        CapabilityManager.get(new CapabilityToken<>() {}),
                        //第一个参数类的接口，可以为抽象类或不用接口
                        //你可以用它自己: SheepDataCapability.class
                        ISheepData.class,
                        //注册的cap应附加在什么实体上
                        Sheep.class
                ),
                channel,
                //索引使用后+1，防止后续网络频道冲突
                cid++,
                //网络包的class
                SheepDataCapability.SheepCapabilityPacket.class,
                //网络包的decode方法
                SheepDataCapability.SheepCapabilityPacket::new,
                //网络包的encode方法
                SheepDataCapability.SheepCapabilityPacket::encode,
                //网络包的handle方法
                SheepDataCapability.SheepCapabilityPacket::handle
        );
        //这是为了还给Channel一个增加后的cid，以防止后续网络包索引重复
        Channel.setCid(cid);
    }

    //测试cap是否成功添加的监听事件
    public static void addListenerToEvent(IEventBus forgeBus){
        forgeBus.addListener(PlayerAttackEvent::onPlayerAttack);
    }
}
