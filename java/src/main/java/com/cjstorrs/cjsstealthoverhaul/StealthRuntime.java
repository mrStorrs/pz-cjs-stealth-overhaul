package com.cjstorrs.cjsstealthoverhaul;

import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.iso.IsoLightSource;
import zombie.iso.IsoMovingObject;

/**
 * Public boundary used by ZombieBuddy advice after it is inlined into vanilla classes.
 */
public final class StealthRuntime {
    private StealthRuntime() {}

    public static void beginSpotting(IsoZombie zombie, IsoMovingObject other, boolean forced) {
        SpottingContext.begin(zombie, other instanceof IsoPlayer ? (IsoPlayer)other : null, forced);
    }

    public static void endSpotting() {
        SpottingContext.clear();
    }

    public static float adjustSneakSpotModifier(IsoGameCharacter character, float vanillaModifier) {
        SpottingContext context = SpottingContext.current();
        if (context == null || context.player != character) {
            return vanillaModifier;
        }
        return vanillaModifier * StealthMath.darknessMultiplier(
            context.lightLevel(),
            StealthConfig.darknessReduction,
            context.distanceRamp
        );
    }

    public static float adjustDirectionalCover(IsoZombie zombie, float vanillaModifier) {
        SpottingContext context = SpottingContext.current();
        if (context == null || context.zombie != zombie || vanillaModifier >= 0.999F) {
            return vanillaModifier;
        }
        return vanillaModifier * StealthMath.coverMultiplier(
            context.lightLevel(),
            StealthConfig.coverReduction,
            context.distanceRamp
        );
    }

    public static void observeAddedLight(IsoLightSource light) {
        LightSensor.observeAddedLight(light);
    }

    public static void observeRemovedLight(IsoLightSource light) {
        LightSensor.observeRemovedLight(light);
    }
}

