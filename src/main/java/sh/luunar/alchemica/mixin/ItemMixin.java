/*
 * Copyright (c) 2025 Lunar_sh, Kolbenik
 *
 * All rights reserved.
 */

package sh.luunar.alchemica.mixin;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sh.luunar.alchemica.util.RotUtils;

import java.util.List;

@Mixin(Item.class)
public class ItemMixin {

    // --- VISUALS: ONLY FOOD ROT ---
    @Inject(method = "appendTooltip", at = @At("HEAD"))
    private void alchemica$addTooltips(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context, CallbackInfo ci) {
        if (world == null) return;

        // ROT TOOLTIP
        if (stack.isFood() && !stack.isOf(Items.ROTTEN_FLESH)) {

            // 1. CHECK IF PRESERVED
            if (stack.hasNbt() && stack.getNbt().getBoolean("alchemica_preserved")) {
               // tooltip.add(Text.empty());
                tooltip.add(Text.literal("Preserved in Alcohol").formatted(Formatting.AQUA));
                return; // Skip the rot timer!
            }

            // 2. Otherwise show Rot
            float percent = RotUtils.getRotPercentage(stack, world);
            //tooltip.add(Text.empty());
            tooltip.add(RotUtils.getRotStatus(percent));
            tooltip.add(RotUtils.getTimeRemaining(stack, world));
        }
    }

    // --- LOGIC: ONLY FOOD ROT ---
    @Inject(method = "inventoryTick", at = @At("HEAD"))
    private void alchemicaInventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected, CallbackInfo ci) {
        if (world.isClient) return;

        if (RotUtils.tickRot(stack, world, entity)) {
            ItemStack rot = new ItemStack(Items.ROTTEN_FLESH, stack.getCount());
            if (entity instanceof PlayerEntity player) {
                player.getInventory().setStack(slot, rot);
                if (world.random.nextFloat() < 0.1f) {
                    world.playSound(null, player.getX(), player.getY(), player.getZ(),
                            SoundEvents.ENTITY_SLIME_SQUISH, SoundCategory.PLAYERS, 0.5f, 1.0f);
                }
            }
        }
    }
}
