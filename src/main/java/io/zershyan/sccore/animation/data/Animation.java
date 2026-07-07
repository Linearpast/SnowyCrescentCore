package io.zershyan.sccore.animation.data;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;

import java.util.Optional;
import java.util.TreeMap;

public abstract class Animation {
    protected final ResourceLocation animationLocation;
    protected final Optional<String> name;
    protected final int priority;
    protected final Optional<RideData> rideData;
    protected final boolean defaultThirdPerson;
    protected final TreeMap<Integer, AABB> aabbMovement;

    public Animation(ResourceLocation animationLocation, Optional<String> name, int priority, Optional<RideData> rideData, boolean defaultThirdPerson, TreeMap<Integer, AABB> aabbMovement) {
        this.animationLocation = animationLocation;
        this.name = name;
        this.priority = priority;
        this.rideData = rideData;
        this.defaultThirdPerson = defaultThirdPerson;
        this.aabbMovement = aabbMovement;
    }

    public Animation(ResourceLocation animationLocation, Optional<String> name, int priority, Optional<RideData> rideData) {
        this(animationLocation, name, priority, rideData, false, new TreeMap<>());
    }

    public int priority() {
        return priority;
    }

    public ResourceLocation animationLocation() {
        return animationLocation;
    }

    public Optional<String> name() {
        return name;
    }

    public Optional<RideData> rideData() {
        return rideData;
    }

    public boolean defaultThirdPerson() {
        return defaultThirdPerson;
    }

    public TreeMap<Integer, AABB> aabbMovement() {
        return aabbMovement;
    }
}
