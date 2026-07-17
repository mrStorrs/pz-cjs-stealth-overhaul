package com.cjstorrs.cjsstealthoverhaul;

import me.zed_0xff.zombie_buddy.Exposer;
import zombie.characters.IsoPlayer;

@Exposer.LuaClass(name = "CjsStealthOverhaul")
public final class LuaApi {
    private LuaApi() {}

    public static void configure(
        double darknessReductionPercent,
        double coverReductionPercent,
        double minimumDistance,
        double fullEffectDistance,
        boolean correctCatseyeLight
    ) {
        StealthConfig.configure(
            darknessReductionPercent,
            coverReductionPercent,
            minimumDistance,
            fullEffectDistance,
            correctCatseyeLight
        );
    }

    public static float getLightLevel(IsoPlayer player) {
        return LightSensor.getCorrectedLightLevel(player);
    }

    public static int getDarknessReductionPercent(IsoPlayer player) {
        float multiplier = StealthMath.darknessMultiplier(
            LightSensor.getCorrectedLightLevel(player),
            StealthConfig.darknessReduction,
            1.0F
        );
        return Math.round((1.0F - multiplier) * 100.0F);
    }
}

