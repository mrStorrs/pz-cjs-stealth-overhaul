package com.cjstorrs.cjsstealthoverhaul;

import me.zed_0xff.zombie_buddy.Patch;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.iso.IsoLightSource;
import zombie.iso.IsoMovingObject;

public final class StealthPatches {
    private StealthPatches() {}

    @Patch(className = "zombie.characters.IsoZombie", methodName = "spottedNew", warmUp = true)
    public static class TrackSpottingRoll {
        @Patch.OnEnter
        public static void enter(
            @Patch.This IsoZombie zombie,
            @Patch.Argument(0) IsoMovingObject other,
            @Patch.Argument(1) boolean forced
        ) {
            SpottingContext.begin(zombie, other instanceof IsoPlayer ? (IsoPlayer)other : null, forced);
        }

        @Patch.OnExit(onThrowable = Throwable.class)
        public static void exit() {
            SpottingContext.clear();
        }
    }

    @Patch(className = "zombie.characters.IsoGameCharacter", methodName = "getSneakSpotMod", warmUp = true)
    public static class AdjustActualSpottingChance {
        @Patch.OnExit
        public static void exit(
            @Patch.This IsoGameCharacter character,
            @Patch.Return(readOnly = false) float returnValue
        ) {
            SpottingContext context = SpottingContext.current();
            if (context == null || context.player != character) {
                return;
            }
            returnValue *= StealthMath.darknessMultiplier(
                context.lightLevel(),
                StealthConfig.darknessReduction,
                context.distanceRamp
            );
        }
    }

    @Patch(className = "zombie.characters.IsoZombie", methodName = "getObstacleMod", warmUp = true)
    public static class StrengthenDirectionalCover {
        @Patch.OnExit
        public static void exit(
            @Patch.This IsoZombie zombie,
            @Patch.Return(readOnly = false) float returnValue
        ) {
            SpottingContext context = SpottingContext.current();
            if (context == null || context.zombie != zombie || returnValue >= 0.999F) {
                return;
            }
            returnValue *= StealthMath.coverMultiplier(
                context.lightLevel(),
                StealthConfig.coverReduction,
                context.distanceRamp
            );
        }
    }

    @Patch(
        className = "zombie.iso.IsoCell",
        methodName = "addLamppost",
        warmUp = true,
        strictMatch = true
    )
    public static class TrackAddedLight {
        @Patch.OnEnter
        public static void enter(@Patch.Argument(0) IsoLightSource light) {
            LightSensor.observeAddedLight(light);
        }
    }

    @Patch(
        className = "zombie.iso.IsoCell",
        methodName = "removeLamppost",
        warmUp = true,
        strictMatch = true
    )
    public static class TrackRemovedLight {
        @Patch.OnEnter
        public static void enter(@Patch.Argument(0) IsoLightSource light) {
            LightSensor.observeRemovedLight(light);
        }
    }
}

