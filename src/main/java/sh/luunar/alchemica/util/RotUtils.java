package sh.luunar.alchemica.util;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

public class RotUtils {

    // --- CONFIGURATION START---
    public static final long ROT_TIME = 72000;
    // --- CONFIGURATION END---


    public static final String ROT_KEY = "alchemica_rot_start";

    public static float getRotPercentage(ItemStack stack, World world) {
        if (!stack.hasNbt() || !stack.getNbt().contains(ROT_KEY)) {
            return 0.0f;
        }
        long startTime = stack.getNbt().getLong(ROT_KEY);
        long currentTime = world.getTime();
        long age = currentTime - startTime;

        return Math.min(1.0f, Math.max(0.0f, (float) age / (float) ROT_TIME));
    }

    public static boolean tickRot(ItemStack stack, World world, Entity entity) {
        if (!stack.isFood() || stack.isOf(Items.ROTTEN_FLESH)) return false;

        NbtCompound nbt = stack.getOrCreateNbt();

        // Initialize time if missing
        if (!nbt.contains(ROT_KEY)) {
            nbt.putLong(ROT_KEY, world.getTime());
            return false;
        }

        // Check if time is up
        if (getRotPercentage(stack, world) >= 1.0f) {
            return true;
        }
        return false;
    }

    public static Text getRotStatus(float percent) {
        if (percent < 0.2) return Text.literal("Fresh").formatted(Formatting.GREEN);
        if (percent < 0.5) return Text.literal("Stale").formatted(Formatting.YELLOW);
        if (percent < 0.8) return Text.literal("Decaying").formatted(Formatting.GOLD);
        return Text.literal("PUTRID").formatted(Formatting.RED).formatted(Formatting.BOLD);
    }

    // --- NEW: Time Indicator ---
    public static Text getTimeRemaining(ItemStack stack, World world) {
        if (!stack.hasNbt() || !stack.getNbt().contains(ROT_KEY)) {
            return Text.literal("Calculating...").formatted(Formatting.GRAY);
        }

        long startTime = stack.getNbt().getLong(ROT_KEY);
        long currentTime = world.getTime();
        long passed = currentTime - startTime;
        long remainingTicks = ROT_TIME - passed;

        if (remainingTicks <= 0) return Text.literal("Rotting...").formatted(Formatting.RED);

        long seconds = remainingTicks / 20;
        long minutes = seconds / 60;

        // Format: "Rot in: 1m 30s"
        String timeString = String.format("%dm %ds", minutes, seconds % 60);
        return Text.literal("Rot in: " + timeString).formatted(Formatting.DARK_GRAY).formatted(Formatting.ITALIC);
    }
}