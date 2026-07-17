package com.cjstorrs.cjsstealthoverhaul;

import me.zed_0xff.zombie_buddy.Patch;
import zombie.characters.IsoGameCharacter;
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
            StealthRuntime.beginSpotting(zombie, other, forced);
        }

        @Patch.OnExit(onThrowable = Throwable.class)
        public static void exit() {
            StealthRuntime.endSpotting();
        }
    }

    @Patch(className = "zombie.characters.IsoGameCharacter", methodName = "getSneakSpotMod", warmUp = true)
    public static class AdjustActualSpottingChance {
        @Patch.OnExit
        public static void exit(
            @Patch.This IsoGameCharacter character,
            @Patch.Return(readOnly = false) float returnValue
        ) {
            returnValue = StealthRuntime.adjustSneakSpotModifier(character, returnValue);
        }
    }

    @Patch(className = "zombie.characters.IsoZombie", methodName = "getObstacleMod", warmUp = true)
    public static class StrengthenDirectionalCover {
        @Patch.OnExit
        public static void exit(
            @Patch.This IsoZombie zombie,
            @Patch.Return(readOnly = false) float returnValue
        ) {
            returnValue = StealthRuntime.adjustDirectionalCover(zombie, returnValue);
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
            StealthRuntime.observeAddedLight(light);
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
            StealthRuntime.observeRemovedLight(light);
        }
    }
}
