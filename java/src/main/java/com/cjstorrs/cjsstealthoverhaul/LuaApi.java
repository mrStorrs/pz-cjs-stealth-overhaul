package com.cjstorrs.cjsstealthoverhaul;

import me.zed_0xff.zombie_buddy.Exposer;
import zombie.characters.IsoPlayer;
import zombie.scripting.objects.CharacterTrait;

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

    public static int getRelativeStealthBonusPercent(IsoPlayer player) {
        if (player == null || player.getCurrentSquare() == null) {
            return 0;
        }

        float multiplier = LightSensor.getRenderedLightLevel(player);
        multiplier *= movementModifier(player);

        float traitModifier = 1.0F;
        if (player.hasTrait(CharacterTrait.INCONSPICUOUS)) {
            traitModifier = 0.8F;
        }
        if (player.hasTrait(CharacterTrait.CONSPICUOUS)) {
            traitModifier = 1.2F;
        }
        multiplier *= traitModifier;

        if (player.isSneaking()) {
            float correctedLight = LightSensor.getCorrectedLightLevel(player);
            multiplier *= player.getSneakSpotMod();
            multiplier *= StealthMath.darknessMultiplier(
                correctedLight,
                StealthConfig.darknessReduction,
                1.0F
            );
            if (player.checkIsNearWall() > 0.0F) {
                multiplier *= StealthMath.coverMultiplier(
                    correctedLight,
                    StealthConfig.coverReduction,
                    1.0F
                );
            }
        }

        return StealthMath.relativeBonusPercent(multiplier);
    }

    private static float movementModifier(IsoPlayer player) {
        float movement = player.getMovementLastFrame().getLength();
        if (movement == 0.5F) {
            return 1.0F;
        }
        if (movement == 1.0F) {
            return 1.5F;
        }
        if (movement == 1.5F) {
            return 2.0F;
        }
        return 0.8F;
    }
}
