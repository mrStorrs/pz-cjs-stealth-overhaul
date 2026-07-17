package com.cjstorrs.cjsstealthoverhaul;

import me.zed_0xff.zombie_buddy.Exposer;

public final class Main {
    private Main() {}

    public static void main(String[] args) {
        Exposer.exposeClass(LuaApi.class);
        System.out.println("[CJS Stealth Overhaul] Loaded real-roll stealth patches");
    }
}

