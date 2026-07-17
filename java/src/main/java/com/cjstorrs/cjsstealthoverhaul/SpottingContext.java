package com.cjstorrs.cjsstealthoverhaul;

import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;

final class SpottingContext {
    private static final ThreadLocal<SpottingContext> CURRENT = new ThreadLocal<>();

    final IsoZombie zombie;
    final IsoPlayer player;
    final float distance;
    final float distanceRamp;
    private float lightLevel = Float.NaN;

    private SpottingContext(IsoZombie zombie, IsoPlayer player, float distance, float distanceRamp) {
        this.zombie = zombie;
        this.player = player;
        this.distance = distance;
        this.distanceRamp = distanceRamp;
    }

    static void begin(IsoZombie zombie, IsoPlayer player, boolean forced) {
        clear();
        if (forced
            || player == null
            || player.isDead()
            || !player.isSneaking()
            || player.getVehicle() != null
            || zombie.getTarget() == player) {
            return;
        }

        float xDistance = zombie.getX() - player.getX();
        float yDistance = zombie.getY() - player.getY();
        float distance = (float)Math.sqrt(xDistance * xDistance + yDistance * yDistance);
        float ramp = StealthMath.distanceRamp(
            distance,
            StealthConfig.minimumDistance,
            StealthConfig.fullEffectDistance
        );
        if (ramp <= 0.0F) {
            return;
        }
        CURRENT.set(new SpottingContext(zombie, player, distance, ramp));
    }

    static SpottingContext current() {
        return CURRENT.get();
    }

    static void clear() {
        CURRENT.remove();
    }

    float lightLevel() {
        if (Float.isNaN(lightLevel)) {
            lightLevel = LightSensor.getCorrectedLightLevel(player);
        }
        return lightLevel;
    }
}

