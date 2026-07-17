package com.cjstorrs.cjsstealthoverhaul;

final class StealthConfig {
    static volatile float darknessReduction = 0.50F;
    static volatile float coverReduction = 0.15F;
    static volatile float minimumDistance = 3.0F;
    static volatile float fullEffectDistance = 6.0F;
    static volatile boolean correctCatseyeLight = true;

    private StealthConfig() {}

    static void configure(
        double darknessReductionPercent,
        double coverReductionPercent,
        double minimumDistanceValue,
        double fullEffectDistanceValue,
        boolean correctCatseye
    ) {
        darknessReduction = StealthMath.clamp((float)darknessReductionPercent / 100.0F, 0.0F, 0.75F);
        coverReduction = StealthMath.clamp((float)coverReductionPercent / 100.0F, 0.0F, 0.30F);
        minimumDistance = StealthMath.clamp((float)minimumDistanceValue, 1.0F, 6.0F);
        fullEffectDistance = StealthMath.clamp(
            (float)fullEffectDistanceValue,
            minimumDistance + 0.5F,
            12.0F
        );
        correctCatseyeLight = correctCatseye;
    }
}

