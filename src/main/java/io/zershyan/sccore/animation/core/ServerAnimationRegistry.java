package io.zershyan.sccore.animation.core;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import io.zershyan.sccore.SCCore;
import io.zershyan.sccore.animation.api.events.AnimationRegisterEvent;
import io.zershyan.sccore.animation.api.events.LayerRegisterEvent;
import io.zershyan.sccore.animation.data.ServerAnimation;
import io.zershyan.sccore.animation.network.data.RegisterAnimationData;
import io.zershyan.sccore.animation.network.data.RegisterLayerData;
import io.zershyan.sccore.api.events.client.ServerReloadEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.io.BufferedReader;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ServerAnimationRegistry {
    private static final Map<ResourceLocation, Integer> Layers = new HashMap<>();
    private static final Map<ResourceLocation, ServerAnimation> Animations = new HashMap<>();
    public static final String LAYER_DIR = "animation/layer/";
    public static final String ANIMATION_DIR = "animation/animation/";
    public static final Codec<HashMap<ResourceLocation, Integer>> LAYER_CODEC = Codec.unboundedMap(
            ResourceLocation.CODEC, Codec.INT
    ).xmap(HashMap::new, Function.identity());

    @SubscribeEvent
    public static void serverInit(ServerAboutToStartEvent event) {
        reloadData(event.getServer());
    }

    @SubscribeEvent
    public static void initPlayer(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        sendDataToPlayer(player);
    }

    @SubscribeEvent
    public static void serverReload(ServerReloadEvent.Post event) {
        MinecraftServer server = event.getServer();
        reloadData(server);
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            sendDataToPlayer(player);
        }
    }

    private static void reloadData(MinecraftServer server) {
        Layers.clear();
        Animations.clear();

        // 事件驱动的注册Layer
        Layers.putAll(NeoForge.EVENT_BUS.post(new LayerRegisterEvent.Server()).getLayers());
        // 资源包驱动的注册Layer
        ResourceManager resourceManager = server.getResourceManager();
        Map<ResourceLocation, Resource> layerResourceMap = resourceManager.listResources(SCCore.MODID, location ->
                location.getNamespace().equals(LAYER_DIR) && location.getPath().endsWith(".json")
        );
        for (Resource value : layerResourceMap.values()) {
            try (BufferedReader reader = value.openAsReader()){
                JsonElement element = JsonParser.parseReader(reader);
                Layers.putAll(LAYER_CODEC.parse(JsonOps.INSTANCE, element).getOrThrow());
            } catch (Exception e) {
                SCCore.log.error(e.getMessage());
            }
        }

        // 事件驱动的注册Animation
        Animations.putAll(NeoForge.EVENT_BUS.post(new AnimationRegisterEvent.Server()).getAnimations());
        // 资源包驱动的注册Animation
        Map<ResourceLocation, Resource> animationResourceMap = resourceManager.listResources(SCCore.MODID, location ->
                location.getNamespace().equals(ANIMATION_DIR) && location.getPath().endsWith(".json")
        );
        animationResourceMap.forEach((location, resource) -> {
            try (BufferedReader reader = resource.openAsReader()){
                JsonElement element = JsonParser.parseReader(reader);
                Animations.put(location, ServerAnimation.CODEC.parse(JsonOps.INSTANCE, element).getOrThrow());
            } catch (Exception e) {
                SCCore.log.error(e.getMessage());
            }
        });
    }

    private static void sendDataToPlayer(ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, new RegisterLayerData(new HashMap<>(Layers)));
        PacketDistributor.sendToPlayer(player, new RegisterAnimationData(new HashMap<>(Animations)));
    }

    public static Map<ResourceLocation, Integer> getLayers() {
        return Map.copyOf(Layers);
    }

    public static Map<ResourceLocation, ServerAnimation> getAnimations() {
        return Map.copyOf(Animations);
    }
}
