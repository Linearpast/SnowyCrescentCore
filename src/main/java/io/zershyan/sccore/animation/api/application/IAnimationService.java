package io.zershyan.sccore.animation.api.application;

import io.zershyan.sccore.animation.registry.attachment.AnimationData;

public interface IAnimationService {
    AnimationData getData();
    void setData(AnimationData data);
}
