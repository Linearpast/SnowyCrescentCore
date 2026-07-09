package io.zershyan.sccore.animation.api.utils;

import io.zershyan.sccore.animation.data.Animation;
import io.zershyan.sccore.animation.data.ClientAnimation;
import io.zershyan.sccore.animation.data.RideData;
import io.zershyan.sccore.animation.data.ServerAnimation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.UnaryOperator;

public abstract class AnimationBuilder {
    protected final ResourceLocation animationLocation;
    @Nullable
    protected String name = null;
    protected int priority = 0;
    @Nullable
    protected RideData rideData = null;
    protected boolean defaultThirdPerson = false;
    protected final TreeMap<Integer, AABB> aabbMovement = new TreeMap<>();

    protected AnimationBuilder(ResourceLocation animationLocation) {
        this.animationLocation = animationLocation;
    }

    public AnimationBuilder name(@Nullable String name) {
        this.name = name;
        return this;
    }

    public AnimationBuilder priority(int priority) {
        this.priority = priority;
        return this;
    }

    public AnimationBuilder rideData(UnaryOperator<RideDataBuilder> operator) {
        this.rideData = operator.apply(new RideDataBuilder()).build();
        return this;
    }

    public AnimationBuilder defaultThirdPerson(boolean defaultThirdPerson) {
        this.defaultThirdPerson = defaultThirdPerson;
        return this;
    }

    public AnimationBuilder aabbMovement(TreeMap<Integer, AABB> aabbMovement) {
        this.aabbMovement.clear();
        this.aabbMovement.putAll(aabbMovement);
        return this;
    }

    public AnimationBuilder addAABBMovement(int tick, AABB aabb) {
        this.aabbMovement.put(tick, aabb);
        return this;
    }

    protected abstract Animation build();

    public static class Server extends AnimationBuilder {
        private float jumpModifier = 1.0f;

        private Server(ResourceLocation animationLocation) {
            super(animationLocation);
        }

        public static Server builder(ResourceLocation location) {
            return new Server(location);
        }

        public Server jumpModifier(float jumpModifier) {
            this.jumpModifier = jumpModifier;
            return this;
        }

        @Override
        public ServerAnimation build() {
            return new ServerAnimation(
                    animationLocation,
                    Optional.ofNullable(name),
                    priority,
                    Optional.ofNullable(rideData),
                    defaultThirdPerson,
                    aabbMovement,
                    jumpModifier
            );
        }
    }

    public static class Client extends AnimationBuilder {
        @NotNull
        private ClientAnimation.CameraChange firstPersonCameraChange = new ClientAnimation.CameraChange(true);
        @NotNull
        private ClientAnimation.CameraChange cameraChange = new ClientAnimation.CameraChange(false);

        private Client(ResourceLocation animationLocation) {
            super(animationLocation);
        }

        public static Client builder(ResourceLocation location) {
            return new Client(location);
        }

        @Override
        public ClientAnimation build() {
            return new ClientAnimation(
                    animationLocation,
                    Optional.ofNullable(name),
                    priority,
                    Optional.ofNullable(rideData),
                    defaultThirdPerson,
                    firstPersonCameraChange,
                    cameraChange
            );
        }

        public Client firstPersonCameraChange(ClientAnimation.CameraChange firstPersonCameraChange) {
            this.firstPersonCameraChange = firstPersonCameraChange;
            return this;
        }

        public Client cameraChange(ClientAnimation.CameraChange cameraChange) {
            this.cameraChange = cameraChange;
            return this;
        }
    }

    public static class RideDataBuilder {
        private final List<ResourceLocation> componentAnimations = new ArrayList<>();
        private Vec3 offset = Vec3.ZERO;
        private int existTick = -1;
        private float xRot = 0;
        private float yRot = 0;

        public RideDataBuilder offset(Vec3 offset) {
            this.offset = offset;
            return this;
        }

        public RideDataBuilder existTick(int existTick) {
            this.existTick = existTick;
            return this;
        }

        public RideDataBuilder xRot(float xRot) {
            this.xRot = xRot;
            return this;
        }

        public RideDataBuilder yRot(float yRot) {
            this.yRot = yRot;
            return this;
        }

        public RideDataBuilder componentAnimations(ResourceLocation... locations) {
            this.componentAnimations.clear();
            this.componentAnimations.addAll(Arrays.stream(locations).toList());
            return this;
        }

        public RideDataBuilder componentAnimations(Collection<ResourceLocation> locations) {
            this.componentAnimations.clear();
            this.componentAnimations.addAll(locations);
            return this;
        }

        public RideDataBuilder addComponentAnimation(ResourceLocation location) {
            this.componentAnimations.add(location);
            return this;
        }

        public RideData build() {
            return new RideData(
                    componentAnimations,
                    offset,
                    existTick,
                    xRot,
                    yRot
            );
        }
    }
}
