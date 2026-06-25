package io.zershyan.sccore.animation.imixin;

import dev.kosmx.playerAnim.api.layered.IAnimation;
import org.spongepowered.asm.mixin.Unique;

public interface IMixinKeyframeAnimationPlayer {
    @Unique
    void sccore$setCurrentTick(int tick);

    static IMixinKeyframeAnimationPlayer of(IAnimation player) {
        try { return (IMixinKeyframeAnimationPlayer) player; }
        catch (Exception ignored) { return null; }
    }
}
