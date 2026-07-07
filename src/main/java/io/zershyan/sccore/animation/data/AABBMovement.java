package io.zershyan.sccore.animation.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class AABBMovement {
    public static final Codec<AABBMovement> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.INT.fieldOf("beginTick").forGetter(AABBMovement::getBeginTick),
            Codec.INT.fieldOf("endTick").forGetter(AABBMovement::getEndTick),
            Codec.INT.fieldOf("stopTick").forGetter(AABBMovement::getStopTick),
            Codec.INT.fieldOf("returnTick").forGetter(AABBMovement::getReturnTick)
    ).apply(i, AABBMovement::new));
    private final int beginTick;
    private final int endTick;
    private final int stopTick;
    private final int returnTick;
    private boolean stopped = false;
    private int currentTick = 0;

    public AABBMovement(int beginTick, int endTick, int stopTick, int returnTick) {
        this.beginTick = beginTick;
        this.endTick = endTick;
        this.stopTick = stopTick;
        this.returnTick = returnTick;
    }

    public void tick() {
        this.currentTick++;
        if (this.currentTick > this.endTick) {
            this.currentTick = this.returnTick;
        }
        if (this.currentTick >= this.stopTick) {
            stopped = true;
        }
    }

    public int getBeginTick() {
        return beginTick;
    }

    public int getEndTick() {
        return endTick;
    }

    public int getStopTick() {
        return stopTick;
    }

    public int getReturnTick() {
        return returnTick;
    }

    public int getCurrentTick() {
        return currentTick;
    }

    public boolean isStopped() {
        return stopped;
    }
}
