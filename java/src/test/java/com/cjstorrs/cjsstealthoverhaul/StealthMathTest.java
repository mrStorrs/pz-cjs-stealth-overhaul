package com.cjstorrs.cjsstealthoverhaul;

public final class StealthMathTest {
    private static final float EPSILON = 0.0001F;

    private StealthMathTest() {}

    public static void main(String[] args) {
        assertNear(0.0F, StealthMath.distanceRamp(3.0F, 3.0F, 6.0F), "no close-range bonus");
        assertNear(0.5F, StealthMath.distanceRamp(4.5F, 3.0F, 6.0F), "distance fade");
        assertNear(1.0F, StealthMath.distanceRamp(6.0F, 3.0F, 6.0F), "full distance bonus");

        assertNear(0.50F, StealthMath.darknessMultiplier(0.0F, 0.50F, 1.0F), "pitch darkness cap");
        assertNear(0.75F, StealthMath.darknessMultiplier(0.5F, 0.50F, 1.0F), "mid-light bonus");
        assertNear(1.00F, StealthMath.darknessMultiplier(1.0F, 0.50F, 1.0F), "no bright-light bonus");
        assertNear(0.75F, StealthMath.darknessMultiplier(0.0F, 0.50F, 0.5F), "close-range fade");

        float combined = StealthMath.darknessMultiplier(0.0F, 0.50F, 1.0F)
            * StealthMath.coverMultiplier(0.0F, 0.15F, 1.0F);
        assertNear(0.425F, combined, "darkness and cover remain bounded");

        assertEquals(50, StealthMath.relativeBonusPercent(0.50F), "half spotting chance");
        assertEquals(0, StealthMath.relativeBonusPercent(1.0F), "neutral spotting chance");
        assertEquals(-20, StealthMath.relativeBonusPercent(1.20F), "spotting penalty");
        assertEquals(99, StealthMath.relativeBonusPercent(0.0F), "display cap");

        System.out.println("StealthMathTest: all assertions passed");
    }

    private static void assertNear(float expected, float actual, String label) {
        if (Math.abs(expected - actual) > EPSILON) {
            throw new AssertionError(label + ": expected " + expected + ", got " + actual);
        }
    }

    private static void assertEquals(int expected, int actual, String label) {
        if (expected != actual) {
            throw new AssertionError(label + ": expected " + expected + ", got " + actual);
        }
    }
}
