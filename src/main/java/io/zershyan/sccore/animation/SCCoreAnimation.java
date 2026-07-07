package io.zershyan.sccore.animation;

import io.zershyan.sccore.animation.core.ClientAnimationRegistry;
import io.zershyan.sccore.animation.core.ServerAnimationRegistry;
import io.zershyan.sccore.animation.network.data.*;
import io.zershyan.sccore.animation.network.handler.ClientPayloadHandler;
import io.zershyan.sccore.animation.network.handler.CommonPayloadHandler;
import io.zershyan.sccore.animation.network.handler.ServerPayloadHandler;
import io.zershyan.sccore.animation.registry.AnimationAttachments;
import io.zershyan.sccore.animation.registry.AnimationCommands;
import io.zershyan.sccore.animation.registry.AnimationEntities;
import io.zershyan.sccore.animation.registry.AnimationEntityDataSerializers;
import io.zershyan.sccore.api.ICompatUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class SCCoreAnimation extends ICompatUtils {
    public static final String MODID = "playeranimator";
    public SCCoreAnimation() {
        super(MODID);
    }

    @Override
    protected void addClientListener(IEventBus forgeBus, IEventBus modBus) {
        forgeBus.register(ClientAnimationRegistry.class);
        forgeBus.addListener(AnimationCommands::clientCommandRegister);
    }

    @Override
    protected void addCommonListener(IEventBus forgeBus, IEventBus modBus) {
        forgeBus.register(ServerAnimationRegistry.class);
        forgeBus.addListener(AnimationCommands::commonCommandRegister);

        AnimationEntities.register(modBus);
        AnimationAttachments.register(modBus);
        AnimationEntityDataSerializers.register(modBus);
    }

    @Override
    public void registerNetwork(PayloadRegistrar registrar) {
        //common
        registrar.playBidirectional(MovementAnimationTickData.TYPE, MovementAnimationTickData.STREAM_CODEC, CommonPayloadHandler::movementAnimationTick);

        //server
        registrar.playToServer(UpdateAnimationData.TYPE, UpdateAnimationData.STREAM_CODEC, ServerPayloadHandler::updateAnimMap);
        registrar.playToServer(UpdateRideAnimationData.TYPE, UpdateRideAnimationData.STREAM_CODEC, ServerPayloadHandler::updateRideAnim);

        //client
        registrar.playToClient(RegisterLayerData.TYPE, RegisterLayerData.STREAM_CODEC, ClientPayloadHandler::registerLayers);
        registrar.playToClient(RegisterAnimationData.TYPE, RegisterAnimationData.STREAM_CODEC, ClientPayloadHandler::registerAnimations);
        registrar.playToClient(SyncAnimationData.TYPE, SyncAnimationData.STREAM_CODEC, ClientPayloadHandler::syncAnimation);
        registrar.playToClient(TurnThirdPersonData.TYPE, TurnThirdPersonData.STREAM_CODEC, ClientPayloadHandler::turnThirdPerson);
    }
}
