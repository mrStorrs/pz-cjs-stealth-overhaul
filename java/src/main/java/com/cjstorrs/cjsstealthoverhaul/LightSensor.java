package com.cjstorrs.cjsstealthoverhaul;

import java.util.Stack;
import zombie.characters.IsoPlayer;
import zombie.core.textures.ColorInfo;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoLightSource;
import zombie.iso.IsoWorld;
import zombie.iso.LightingJNI;

final class LightSensor {
    private static final long CACHE_NANOS = 100_000_000L;
    private static final LightSample[] SAMPLES = {
        new LightSample(), new LightSample(), new LightSample(), new LightSample()
    };
    private static volatile IsoLightSource trackedCatseyeLight;

    private LightSensor() {}

    static float getRenderedLightLevel(IsoPlayer player) {
        if (player == null || player.getCurrentSquare() == null) {
            return 1.0F;
        }
        int playerIndex = Math.max(0, Math.min(SAMPLES.length - 1, player.getIndex()));
        ColorInfo rendered = player.getCurrentSquare().lighting[playerIndex].lightInfo();
        return StealthMath.clamp((rendered.r + rendered.g + rendered.b) / 3.0F, 0.0F, 1.0F);
    }

    static float getCorrectedLightLevel(IsoPlayer player) {
        if (player == null || player.getCurrentSquare() == null) {
            return 1.0F;
        }

        int playerIndex = Math.max(0, Math.min(SAMPLES.length - 1, player.getIndex()));
        IsoGridSquare square = player.getCurrentSquare();
        long now = System.nanoTime();
        LightSample cached = SAMPLES[playerIndex];
        if (cached.matches(square, now)) {
            return cached.level;
        }

        ColorInfo physical = new LightingJNI.JNILighting(-1, square).lightInfo();
        float red = physical.r;
        float green = physical.g;
        float blue = physical.b;

        if (StealthConfig.correctCatseyeLight) {
            IsoLightSource catseye = findCatseyeLight(player);
            if (catseye != null) {
                float attenuation = attenuationAt(catseye, square);
                red -= catseye.getR() * 2.0F * attenuation;
                green -= catseye.getG() * 2.0F * attenuation;
                blue -= catseye.getB() * 2.0F * attenuation;
            }
        }

        float level = StealthMath.clamp(
            (Math.max(0.0F, red) + Math.max(0.0F, green) + Math.max(0.0F, blue)) / 3.0F,
            0.0F,
            1.0F
        );
        cached.update(square, now, level);
        return level;
    }

    static void observeAddedLight(IsoLightSource light) {
        if (looksLikeCatseye(light)) {
            trackedCatseyeLight = light;
            invalidate();
        }
    }

    static void observeRemovedLight(IsoLightSource light) {
        if (trackedCatseyeLight == light) {
            trackedCatseyeLight = null;
            invalidate();
        }
    }

    private static IsoLightSource findCatseyeLight(IsoPlayer player) {
        IsoLightSource tracked = trackedCatseyeLight;
        if (isUsableCatseyeForPlayer(tracked, player)) {
            return tracked;
        }

        trackedCatseyeLight = null;
        if (IsoWorld.instance == null || IsoWorld.instance.currentCell == null) {
            return null;
        }

        Stack<IsoLightSource> lights = IsoWorld.instance.currentCell.getLamppostPositions();
        for (int index = 0; index < lights.size(); index++) {
            IsoLightSource candidate = lights.get(index);
            if (isUsableCatseyeForPlayer(candidate, player)) {
                trackedCatseyeLight = candidate;
                return candidate;
            }
        }
        return null;
    }

    private static boolean isUsableCatseyeForPlayer(IsoLightSource light, IsoPlayer player) {
        if (!looksLikeCatseye(light) || !light.isActive() || light.life == 0) {
            return false;
        }
        return light.getZ() == player.getCurrentSquare().getZ()
            && attenuationAt(light, player.getCurrentSquare()) > 0.0F;
    }

    private static boolean looksLikeCatseye(IsoLightSource light) {
        if (light == null || (light.getRadius() != 4 && light.getRadius() != 12)) {
            return false;
        }
        return closeTo(light.getR(), 0.1F)
            && closeTo(light.getG(), 0.1F)
            && closeTo(light.getB(), 0.2F)
            && light.localToBuilding == null;
    }

    private static boolean closeTo(float value, float expected) {
        return Math.abs(value - expected) < 0.002F;
    }

    private static float attenuationAt(IsoLightSource light, IsoGridSquare square) {
        float xDistance = light.getX() - square.getX();
        float yDistance = light.getY() - square.getY();
        float distance = (float)Math.sqrt(xDistance * xDistance + yDistance * yDistance);
        if (distance >= light.getRadius()) {
            return 0.0F;
        }
        float remaining = 1.0F - distance / (float)light.getRadius();
        return remaining * remaining;
    }

    private static void invalidate() {
        for (LightSample sample : SAMPLES) {
            sample.timestamp = 0L;
        }
    }

    private static final class LightSample {
        private IsoGridSquare square;
        private long timestamp;
        private float level;

        boolean matches(IsoGridSquare candidate, long now) {
            return square == candidate && now - timestamp <= CACHE_NANOS;
        }

        void update(IsoGridSquare newSquare, long now, float newLevel) {
            square = newSquare;
            timestamp = now;
            level = newLevel;
        }
    }
}
