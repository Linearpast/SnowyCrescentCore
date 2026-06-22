package io.zershyan.sccore.animation.data;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.function.UnaryOperator;

public class AnimationBuilder<T extends AnimationBuilder<T>> {
    private static final ResourceLocation EMPTY_KEY = ResourceLocation.fromNamespaceAndPath("", "");
    T builder;
    protected final Animation animation;
    private AnimationBuilder(Animation animation){
        this.animation = animation;
    }
    public static Client client(ResourceLocation key) {
        return new Client(key);
    }

    public static Server server(ResourceLocation key) {
        return new Server(key);
    }

    public static ClientAnimation clientJson(JsonObject json) {
        ClientAnimation anim = new ClientAnimation(EMPTY_KEY);
        anim.deserialize(json);
        return anim;
    }

    public static ServerAnimation serverJson(JsonObject json) {
        ServerAnimation anim = new ServerAnimation(EMPTY_KEY);
        anim.deserialize(json);
        return anim;
    }

    public T name(@NotNull String name) {
        builder.animation.setName(name);
        return builder;
    }

    public T priority(int priority) {
        builder.animation.setPriority(priority);
        return builder;
    }

    public T rideData(UnaryOperator<Ride> operator) {
        builder.animation.setRideData(operator.apply(new Ride()).build());
        return builder;
    }

    public static class Client extends AnimationBuilder<Client> {
        private final ClientAnimation anim;
        private Client(ResourceLocation key) {
            super(new ClientAnimation(key));
            this.builder = this;
            this.anim = (ClientAnimation) this.animation;
        }

        public Client firstPersonRelative(boolean relative) {
            anim.getFirstPersonCameraChange().setRelative(relative);
            return this;
        }

        public Client relative(boolean relative) {
            anim.getCameraChange().setRelative(relative);
            return this;
        }

        public Client addFirstPersonCameraData(ClientAnimation.CameraData data) {
            anim.getFirstPersonCameraChange().getMovement().put(data.tick(), data);
            return this;
        }

        public Client addCameraData(ClientAnimation.CameraData data) {
            anim.getCameraChange().getMovement().put(data.tick(), data);
            return this;
        }

        public ClientAnimation build() {
            return anim;
        }
    }

    public static class Server extends AnimationBuilder<Server> {
        private final ServerAnimation anim;
        private Server(ResourceLocation key) {
            super(new ServerAnimation(key));
            this.builder = this;
            this.anim = (ServerAnimation) this.animation;
        }

        public Server jumpModifier(float jumpModifier) {
            anim.setJumpModifier(jumpModifier);
            return this;
        }

        public Server addAABBData(ServerAnimation.AABBData data) {
            anim.getAabbMovement().put(data.tick(), data);
            return this;
        }

        public ServerAnimation build() {
            return anim;
        }
    }

    public static class Ride {
        private final Animation.RideData rideData;
        private Ride() {
            this.rideData = new Animation.RideData();
        }

        public Ride addComponentAnimation(ResourceLocation... locations) {
            for (ResourceLocation location : locations) {
                rideData.addComponentAnimation(location);
            }
            return this;
        }

        public Ride offset(Vec3 offset) {
            rideData.setOffset(offset);
            return this;
        }

        public Ride xRot(float xRot) {
            rideData.setXRot(xRot);
            return this;
        }

        public Ride yRot(float yRot) {
            rideData.setYRot(yRot);
            return this;
        }

        public Ride existTick(int existTick) {
            rideData.setExistTick(existTick);
            return this;
        }

        public Animation.RideData build() {
            return rideData;
        }
    }

}
