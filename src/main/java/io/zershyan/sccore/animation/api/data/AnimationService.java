package io.zershyan.sccore.animation.api.data;

import io.zershyan.sccore.animation.registry.AnimationAttachments;
import io.zershyan.sccore.animation.registry.attachment.PlayerAnimations;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.attachment.AttachmentType;

import java.util.function.Supplier;

/**
 * 服务端动画服务实现类。
 * <p>
 * 负责在服务端管理玩家的动画数据，通过 Attachment 机制存储和读取动画数据。
 */
public class AnimationService implements IAnimationService {
    private final ServerPlayer player;

    /**
     * 构造函数。
     *
     * @param player 服务端玩家实例
     */
    protected AnimationService(ServerPlayer player) {
        this.player = player;
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
     * 设置玩家的动画数据。
     *
     * @param data 玩家动画数据对象
     */
    @Override
    public void setData(PlayerAnimations data) {
        player.setData(type(), data);
    }

    /**
     * 获取动画数据的 Attachment 类型。
     *
     * @return Attachment 类型的供应商
     */
    private static Supplier<AttachmentType<PlayerAnimations>> type() {
        return AnimationAttachments.PLAYER_ANIMATIONS;
    }
}