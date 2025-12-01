/*
 * Copyright (c) 2025 Lunar_sh, Kolbenik
 *
 * All rights reserved.
 */

package sh.luunar.alchemica.effect;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import sh.luunar.alchemica.Alchemica;

public class ModEffects {
    public static final StatusEffect DRUNK = new DrunkStatusEffect();

    public static void registerEffects() {
        Registry.register(Registries.STATUS_EFFECT, new Identifier(Alchemica.MOD_ID, "drunk"), DRUNK);
    }
}
