# CJS Stealth Overhaul

A Project Zomboid Build 42 stealth overhaul by CJ Storrs.

This mod changes the real vanilla visual-spotting roll through ZombieBuddy. It does not scan the global zombie list, disable zombie AI, or alter hearing.

Default balance:

- Darkness reduces the remaining visual spotting chance by up to 50%.
- Vanilla-confirmed directional cover adds up to 15% more reduction in darkness.
- No bonus applies within 3 tiles; the effect fades to full strength at 6 tiles.
- An already-acquired zombie target receives no bonus.
- Cat's Eye in the Dark's personal blue light is removed from this mod's environmental light reading. Flashlights and ordinary world lighting still count.

The top-screen status icon shows an approximate, full-distance relative stealth bonus. It combines rendered light (including flashlights), movement, Sneak skill, Conspicuous/Inconspicuous, the mod's corrected darkness bonus, and nearby cover potential. Green is a strong bonus, amber is moderate, and red is weak or negative. Zombie-specific facing, exact distance, and cover direction are intentionally excluded so the indicator stays lightweight and never scans the zombie list.

## Build

Run `scripts/build.sh`. B42.19 needs a Java 25-aware compiler; the script uses an ignored local Eclipse compiler at `.tools/ecj.jar` ([ECJ 3.46.0](https://repo1.maven.org/maven2/org/eclipse/jdt/ecj/3.46.0/ecj-3.46.0.jar)). Override `ECJ_JAR`, `PZ_JAR`, or `ZOMBIE_BUDDY_JAR` when those paths differ.
