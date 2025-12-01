/*
 * Copyright (c) 2025 Lunar_sh, Kolbenik
 *
 * All rights reserved.
 */

package sh.luunar.alchemica.util;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class RustUtils {

    // 1200 ticks = 1 minute of rain exposure
    public static final long RUST_MAX_AGE = 1200L;

    /**
     * Logic: Tick the Rusting Process
     * Returns TRUE if the item fully rusted.
     */
    public static boolean tickRust(ItemStack stack, World world, Entity entity) {
        if (world.isClient) return false;
        if (!stack.isOf(Items.IRON_INGOT)) return false;

        // 1. Environment Checks
        if (!world.isRaining()) return false;

        // 2. Biome Check
        Biome biome = world.getBiome(entity.getBlockPos()).value();
        if (biome.getPrecipitation(entity.getBlockPos()) != Biome.Precipitation.RAIN) {
            return false;
        }

        NbtCompound nbt = stack.getOrCreateNbt();

        // 3. Increment Rust Counter
        long currentAge = nbt.getLong("rust_age");
        currentAge++;
        nbt.putLong("rust_age", currentAge);

        // 4. Update Visual Progress for Tooltip
        float progress = Math.min(1.0f, (float) currentAge / (float) RUST_MAX_AGE);
        nbt.putFloat("rust_progress", progress);

        // 5. Done?
        return progress >= 1.0f;
    }

    /**
     * Visuals: Tooltip Text
     */
    public static Text getRustTooltip(ItemStack stack) {
        if (!stack.hasNbt() || !stack.getNbt().contains("rust_progress")) {
            return null; // Don't show anything if it hasn't started rusting
        }

        float progress = stack.getNbt().getFloat("rust_progress");
        int percentage = (int) (progress * 100);

        if (percentage <= 0) return null;

        // Color coding: Gray -> Orange -> Red
        Formatting color = Formatting.GRAY;
        if (percentage > 50) color = Formatting.GOLD;
        if (percentage > 90) color = Formatting.RED;

        return Text.literal("Corrosion: " + percentage + "%").formatted(color);
    }
}
