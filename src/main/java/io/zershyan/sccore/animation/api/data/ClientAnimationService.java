package io.zershyan.sccore.animation.api.data;

import io.zershyan.sccore.animation.network.data.UpdateAnimationData;
import io.zershyan.sccore.animation.registry.attachment.PlayerAnimations;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;

@OnlyIn(Dist.CLIENT)
public class ClientAnimationService implements IAnimationService {
    private final AbstractClientPlayer player;

    protected ClientAnimationService(Player player) {
        this.player = (AbstractClientPlayer) player;
    }

    @Override
    public PlayerAnimations getData() {
        return PlayerAnimations.getData(player);
    }

    @Override
    public void setData(PlayerAnimations data) {
        PacketDistributor.sendToServer(new UpdateAnimationData(data.clientAnimMap(), false));
        PacketDistributor.sendToServer(new UpdateAnimationData(data.serverAnimMap(), true));
    }
}
