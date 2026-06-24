package io.zershyan.sccore.animation.api.data;

import io.zershyan.sccore.animation.network.data.UpdateAnimationData;
import io.zershyan.sccore.animation.registry.attachment.PlayerAnimations;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * 客户端动画服务实现类。
 * <p>
 * 负责在客户端管理玩家的动画数据，通过网络包将数据同步到服务端。
 */
@OnlyIn(Dist.CLIENT)
public class ClientAnimationService implements IAnimationService {
    private final AbstractClientPlayer player;

    /**
     * 构造函数。
     *
     * @param player 客户端玩家实例
     */
    protected ClientAnimationService(Player player) {
        this.player = (AbstractClientPlayer) player;
    }

    /**
     * 获取玩家的动画数据。
     *
     * @return 玩家动画数据对象
     */
    @Override
    public PlayerAnimations getData() {
        return PlayerAnimations.getData(player);
    }

    /**
     * 设置玩家的动画数据并同步到服务端。
     * <p>
     * 通过发送网络包将客户端和服务端的动画数据分别同步。
     *
     * @param data 玩家动画数据对象
     */
    @Override
    public void setData(PlayerAnimations data) {
        PacketDistributor.sendToServer(new UpdateAnimationData(data.clientAnimMap(), false));
        PacketDistributor.sendToServer(new UpdateAnimationData(data.serverAnimMap(), true));
    }
}