package io.zershyan.sccore.animation.api.data;

import io.zershyan.sccore.animation.registry.attachment.PlayerAnimations;

public interface IAnimationService {
    PlayerAnimations getData();
    void setData(PlayerAnimations data);
}
