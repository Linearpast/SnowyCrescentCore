package io.zershyan.sccore.animation.event.client;

import io.zershyan.sccore.animation.AnimationApi;
import io.zershyan.sccore.animation.data.AnimationData;
import io.zershyan.sccore.animation.helper.AnimationHelper;
import io.zershyan.sccore.animation.service.IAnimationService;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.Input;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.MovementInputUpdateEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class ClientPlayerEvent {
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.side.isClient() && event.phase == TickEvent.Phase.START) {
            Player player = event.player;
            if(player.tickCount % 10 != 0) return;
            if (!(player instanceof AbstractClientPlayer clientPlayer)) return;
            AnimationApi.getHelper(clientPlayer).refreshAnimation();
        }
    }

    public static Map<Runnable, Map.Entry<Integer, Integer>> runs = new HashMap<>();
    @SubscribeEvent
    public static void delayRuns(TickEvent.PlayerTickEvent event) {
        if (event.side.isClient() && event.phase == TickEvent.Phase.END) {
            Map.copyOf(runs).forEach((runnable, countMap) -> {
                if(countMap.getValue() >= countMap.getKey()) {
                    runnable.run();
                    runs.remove(runnable);
                } else {
                    countMap.setValue(countMap.getValue() + 1);
                }
            });
        }
    }

    private static AnimationData cacheData = null;
    private static int counter = 0;
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void inputUpdate(MovementInputUpdateEvent event){
        if(counter % 20 == 0) {
            AnimationHelper helper = AnimationApi.getHelper(event.getEntity());
            getCamYaw: {
                for (Map.Entry<IAnimationService<?, ?>, List<ResourceLocation>> entry : helper.getAnimationPlaying()) {
                    for (ResourceLocation resourceLocation : entry.getValue()) {
                        AnimationData animationData = AnimationApi.getDataHelper().getAnimationData(resourceLocation);
                        if(animationData != null) {
                            AnimationData.LyingType dataLyingType = animationData.getLyingType();
                            if(dataLyingType == AnimationData.LyingType.RIGHT || dataLyingType == AnimationData.LyingType.LEFT) {
                                cacheData = animationData;
                                break getCamYaw;
                            }
                            if(animationData.getCamRoll() == 0.0f && animationData.getCamPitch() == 0.0f && animationData.getCamYaw() != 0) {
                                cacheData = animationData;
                                break getCamYaw;
                            }
                        }
                    }
                }
                cacheData = null;
            }
        }
        counter++;
        Input input = event.getInput();
        if(cacheData != null) {
            AnimationData.LyingType lyingType = cacheData.getLyingType();
            if(lyingType != null) {
                switch (lyingType) {
                    case LEFT, RIGHT -> {
                        float forwardImpulse = input.forwardImpulse;
                        if(lyingType == AnimationData.LyingType.RIGHT)
                            forwardImpulse = -forwardImpulse;
                        input.forwardImpulse = 0.0f;
                        input.leftImpulse = forwardImpulse;
                        return;
                    }
                }
            }
            float camYaw = cacheData.getCamYaw();
            double camYawRadian = Math.toRadians(camYaw);
            double forwardCos = Math.cos(camYawRadian) * input.forwardImpulse;
            double forwardSin = Math.sin(camYawRadian) * input.forwardImpulse;
            double LeftCamYawRadian = Math.toRadians(camYaw + 90);
            double leftCos = Math.cos(LeftCamYawRadian) * input.leftImpulse;
            double leftSin = Math.sin(LeftCamYawRadian) * input.leftImpulse;
            input.forwardImpulse = (float) (forwardCos + leftCos);
            input.leftImpulse = (float) (forwardSin + leftSin);
            if(lyingType == AnimationData.LyingType.FRONT) input.forwardImpulse = 0.0f;
        }
    }
}
