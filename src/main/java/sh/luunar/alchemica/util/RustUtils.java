package sh.luunar.alchemica.util;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.ColorHelper; // Use Minecraft's Helper!
import net.minecraft.world.World;

public class RustUtils {

    public static final long RUST_TIME = 24000L;
    public static final String RUST_KEY = "alchemica_rust_start";

    // Colors (ARGB format)
    private static final int COLOR_FRESH = 0xFFFFFFFF;  // White
    private static final int COLOR_RUSTED = 0xFFB87333; // Copper Orange

    public static float getRustPercentage(ItemStack stack, long worldTime) {
        if (!stack.hasNbt() || !stack.getNbt().contains(RUST_KEY)) return 0.0f;

        long startTime = stack.getNbt().getLong(RUST_KEY);
        long age = worldTime - startTime;
        return Math.min(1.0f, Math.max(0.0f, (float) age / (float) RUST_TIME));
    }

    public static int getItemColor(ItemStack stack, int tintIndex) {
        if (tintIndex != 0) return 0xFFFFFFFF;

        // Debug: If no NBT, return strict White (visible)
        if (!stack.hasNbt() || !stack.getNbt().contains("rust_progress")) {
            return 0xFFFFFFFF;
        }

        float progress = stack.getNbt().getFloat("rust_progress");

        // Use Minecraft's built-in Color Lerp
        return ColorHelper.Argb.lerp(progress, COLOR_FRESH, COLOR_RUSTED);
    }

    public static boolean isImmune(ItemStack stack) {
        return stack.hasNbt() && stack.getNbt().getBoolean("AlchemicaWaxed");
    }

    public static void tickRust(ItemStack stack, World world) {
        if (world.isClient) return;
        if (!stack.isOf(Items.IRON_INGOT) && !stack.isOf(Items.IRON_BLOCK)) return;

        // If Waxed/Immune, stop here.
        if (isImmune(stack)) return;

        NbtCompound nbt = stack.getOrCreateNbt();

        if (!nbt.contains(RUST_KEY)) {
            nbt.putLong(RUST_KEY, world.getTime());
            nbt.putFloat("rust_progress", 0f);
            return;
        }

        float percent = getRustPercentage(stack, world.getTime());
        nbt.putFloat("rust_progress", percent);
    }


}