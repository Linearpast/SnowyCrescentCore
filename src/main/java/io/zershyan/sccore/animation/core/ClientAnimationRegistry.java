package io.zershyan.sccore.animation.core;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import dev.kosmx.playerAnim.api.layered.AnimationStack;
import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.KeyframeAnimationPlayer;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.api.layered.modifier.AbstractFadeModifier;
import dev.kosmx.playerAnim.core.data.KeyframeAnimation;
import dev.kosmx.playerAnim.core.util.Ease;
import dev.kosmx.playerAnim.core.util.Pair;
import dev.kosmx.playerAnim.impl.animation.AnimationApplier;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationAccess;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationFactory;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationRegistry;
import io.zershyan.sccore.SCCore;
import io.zershyan.sccore.animation.api.events.AnimationRegisterEvent;
import io.zershyan.sccore.animation.api.events.LayerRegisterEvent;
import io.zershyan.sccore.animation.data.ClientAnimation;
import io.zershyan.sccore.animation.imixin.IMixinFactoryHolder;
import io.zershyan.sccore.animation.registry.attachment.PlayerAnimations;
import io.zershyan.sccore.api.events.client.ResourceLoadEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;

public class ClientAnimationRegistry {
    private static final Map<ResourceLocation, Integer> Layers = new HashMap<>();
    private static final Map<ResourceLocation, ClientAnimation> Animations = new HashMap<>();
    private static final Map<UUID, Map<ResourceLocation, IAnimation>> cacheLayers = new HashMap<>();
    public static final String LAYER_DIR = "animation/layer/";
    public static final String ANIMATION_DIR = "animation/animation/";
    public static final Codec<HashMap<ResourceLocation, Integer>> LAYER_CODEC = Codec.unboundedMap(
            ResourceLocation.CODEC, Codec.INT
    ).xmap(HashMap::new, Function.identity());

    @SubscribeEvent
    public static void clientReload(ResourceLoadEvent.Post event) {
        reloadData();
    }

    @SubscribeEvent
    public static void clientInit(ClientPlayerNetworkEvent.LoggingIn event) {
        reloadData();
    }

    private static void reloadData() {
        Layers.clear();
        Animations.clear();
        // 事件驱动的注册Layer
        Layers.putAll(NeoForge.EVENT_BUS.post(new LayerRegisterEvent.Client()).getLayers());
        // 资源包驱动的注册Layer
        ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
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
        Animations.putAll(NeoForge.EVENT_BUS.post(new AnimationRegisterEvent.Client()).getAnimations());
        // 资源包驱动的注册Animation
        Map<ResourceLocation, Resource> animationResourceMap = resourceManager.listResources(SCCore.MODID, location ->
                location.getNamespace().equals(ANIMATION_DIR) && location.getPath().endsWith(".json")
        );
        animationResourceMap.forEach((location, resource) -> {
            try (BufferedReader reader = resource.openAsReader()){
                JsonElement element = JsonParser.parseReader(reader);
                Animations.put(location, ClientAnimation.CODEC.parse(JsonOps.INSTANCE, element).getOrThrow());
            } catch (Exception e) {
                SCCore.log.error(e.getMessage());
            }
        });

        // 深度注册Layer数据
        registerLayers(Layers);
    }

    /**
     * 深度注册Layer，将Layer映射到player animator的底层数据中
     * @param layers 需要注册的Layer层
     */
    public static void registerLayers(Map<ResourceLocation, Integer> layers) {
        ((IMixinFactoryHolder)(PlayerAnimationFactory.ANIMATION_DATA_FACTORY)).sccore$clearAnimations(layers.keySet());
        Minecraft instance = Minecraft.getInstance();
        layers.forEach((key, value) -> PlayerAnimationFactory.ANIMATION_DATA_FACTORY.registerFactory(
                key, value, player -> {
                    if(instance.player == null) return registerPlayerAnimation(player);
                    Map<ResourceLocation, IAnimation> animationMap = cacheLayers.getOrDefault(player.getUUID(), new HashMap<>());
                    if(animationMap.containsKey(key)) return animationMap.get(key);
                    IAnimation iAnimation = registerPlayerAnimation(player);
                    animationMap.put(key, iAnimation);
                    cacheLayers.put(player.getUUID(), animationMap);
                    return iAnimation;
                })
        );
        if(instance.level == null) return;
        try {
            instance.level.players().forEach(ClientAnimationRegistry::reflectAnimationCore);
        } catch (Exception e) {
            SCCore.log.error("{} reflect error: {}", ClientAnimationRegistry.class.getName(), e.getMessage());
        }
    }

    private static IAnimation registerPlayerAnimation(AbstractClientPlayer player) {
        return new ModifierLayer<>();
    }

    public static Map<ResourceLocation, Integer> getLayers() {
        return Map.copyOf(Layers);
    }

    public static Map<ResourceLocation, ClientAnimation> getAnimations() {
        return Map.copyOf(Animations);
    }

    public static Map<ResourceLocation, ClientAnimation> getAllAnimations() {
        return Map.copyOf(new HashMap<>(Animations){{putAll(SyncAnimationFactory.getAnimations());}});
    }

    public static Map<ResourceLocation, Integer> getAllLayers() {
        return Map.copyOf(new HashMap<>(Layers){{putAll(SyncAnimationFactory.getLayers());}});
    }

    private static KeyframeAnimation getAnimationOfPair(Pair<Integer, IAnimation> pair) {
        return Optional.ofNullable((KeyframeAnimationPlayer) ((ModifierLayer<?>) pair.getRight()).getAnimation())
                .map(KeyframeAnimationPlayer::getData).orElse(null);
    }

    @Nullable
    public static ClientAnimation getAnimation(ResourceLocation location) {
        ClientAnimation anim = SyncAnimationFactory.getAnimation(location);
        if(anim == null) anim = Animations.getOrDefault(location, null);
        return anim;
    }

    @Nullable
    public static KeyframeAnimation getKeyframeAnimation(ResourceLocation location) {
        ClientAnimation animation = getAnimation(location);
        if(animation == null) return null;
        return  (KeyframeAnimation) PlayerAnimationRegistry.getAnimation(animation.animationLocation());
    }

    /**
     * 反射修改玩家Layer底层数据 <br>
     * Mixin办不到或过于麻烦
     * @param player 目标玩家
     */
    @SuppressWarnings({"JavaReflectionMemberAccess", "UnstableApiUsage", "unchecked"})
    private static void reflectAnimationCore(AbstractClientPlayer player) {
        try {
            Class<?> playerClass = Player.class;
            Field animationStackField = playerClass.getDeclaredField("animationStack");
            animationStackField.setAccessible(true);
            Method createAnimationStack = playerClass.getDeclaredMethod("createAnimationStack");
            createAnimationStack.setAccessible(true);
            AnimationStack newAnimationStack = (AnimationStack) createAnimationStack.invoke(player);
            AnimationStack oldAnimationStack = (AnimationStack) animationStackField.get(player);
            Field layersField = AnimationStack.class.getDeclaredField("layers");
            layersField.setAccessible(true);
            ArrayList<Pair<Integer, IAnimation>> oldArrayList = new ArrayList<>((ArrayList<Pair<Integer, IAnimation>>) layersField.get(oldAnimationStack));
            ArrayList<Pair<Integer, IAnimation>> newArrayList = new ArrayList<>((ArrayList<Pair<Integer, IAnimation>>) layersField.get(newAnimationStack));
            ArrayList<Pair<Integer, IAnimation>> result = new ArrayList<>();
            for (Pair<Integer, IAnimation> oldAnimationPair : List.copyOf(oldArrayList)) {
                for (Pair<Integer, IAnimation> newAnimationPair : List.copyOf(newArrayList)) {
                    if(Objects.equals(oldAnimationPair.getLeft(), newAnimationPair.getLeft())) {
                        KeyframeAnimation oldData = getAnimationOfPair(oldAnimationPair);
                        KeyframeAnimation newData = getAnimationOfPair(newAnimationPair);
                        if(Objects.equals(oldData, newData)) oldArrayList.remove(oldAnimationPair);
                    }
                }
            }
            result.addAll(oldArrayList);
            result.addAll(newArrayList);

            layersField.set(newAnimationStack, result);
            animationStackField.set(player, newAnimationStack);
            Field animationApplierField = playerClass.getDeclaredField("animationApplier");
            animationApplierField.setAccessible(true);
            animationApplierField.set(player, new AnimationApplier(newAnimationStack));
            PlayerAnimations data = PlayerAnimations.getData(player);
            Map<ResourceLocation, ResourceLocation> dataAnimations = new HashMap<>();
            dataAnimations.putAll(data.clientAnimMap());
            dataAnimations.putAll(data.serverAnimMap());
            data.rideAnim().layer().ifPresent(riderAnimLayer ->
                    dataAnimations.put(riderAnimLayer, data.rideAnim().animation().orElseThrow())
            );
            for (ResourceLocation location : dataAnimations.keySet()) {
                ModifierLayer<IAnimation> modifierLayer = (ModifierLayer<IAnimation>) PlayerAnimationAccess
                        .getPlayerAssociatedData(player).get(location);
                if(modifierLayer == null) continue;
                KeyframeAnimation keyframeAnimation = getKeyframeAnimation(location);
                if(keyframeAnimation == null) continue;
                modifierLayer.replaceAnimationWithFade(
                        AbstractFadeModifier.standardFadeIn(3, Ease.INOUTSINE),
                        new KeyframeAnimationPlayer(keyframeAnimation)
                );
            }
        }catch (Exception e){
            SCCore.log.error("Failed to register on {} animation layer: {}", player, e.getMessage(), e);
        }
    }
}
