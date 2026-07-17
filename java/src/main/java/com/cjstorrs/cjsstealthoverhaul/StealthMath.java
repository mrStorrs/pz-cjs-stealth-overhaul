package com.cjstorrs.cjsstealthoverhaul;

public final class StealthMath {
    private StealthMath() {}

    public static float clamp(float value, float minimum, float maximum) {
        return Math.max(minimum, Math.min(maximum, value));
    }

    public static float distanceRamp(float distance, float minimumDistance, float fullEffectDistance) {
        if (distance <= minimumDistance) {
            return 0.0F;
        }
        if (distance >= fullEffectDistance) {
            return 1.0F;
        }
        return (distance - minimumDistance) / (fullEffectDistance - minimumDistance);
    }

    public static float darknessMultiplier(float lightLevel, float maximumReduction, float distanceRamp) {
        float light = clamp(lightLevel, 0.0F, 1.0F);
        float target = 1.0F - clamp(maximumReduction, 0.0F, 1.0F) * (1.0F - light);
        return interpolateFromOne(target, distanceRamp);
    }

    public static float coverMultiplier(float lightLevel, float maximumReduction, float distanceRamp) {
        return darknessMultiplier(lightLevel, maximumReduction, distanceRamp);
    }

    private static float interpolateFromOne(float target, float amount) {
        float clampedAmount = clamp(amount, 0.0F, 1.0F);
        return 1.0F + (target - 1.0F) * clampedAmount;
    }
}

