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
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sh.luunar.alchemica.util.RotUtils;
import sh.luunar.alchemica.util.RustUtils;

import java.util.List;

@Mixin(Item.class)
public class ItemMixin {

    /**
     * VISUALS: Adds tooltips to rotting items.
     */
    @Inject(method = "appendTooltip", at = @At("HEAD"))
    private void alchemica$addRotTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context, CallbackInfo ci) {
        // Safety Checks: World must exist, must be food, must not already be rotten flesh
        if (world == null || !stack.isFood() || stack.isOf(Items.ROTTEN_FLESH)) return;

        // Calculate Data
        float percent = RotUtils.getRotPercentage(stack, world);

        // Add Tooltip Lines
        tooltip.add(Text.empty()); // Spacer line
        tooltip.add(RotUtils.getRotStatus(percent)); // "Fresh", "Stale", etc.
        tooltip.add(RotUtils.getTimeRemaining(stack, world)); // "Rot in: 25s"
    }

    /**
     * LOGIC: Runs every tick while an item is in an inventory.
     * Handles both Rotting (Food -> Flesh) and Rusting (Iron -> Orange).
     */
    @Inject(method = "inventoryTick", at = @At("HEAD"))
    private void alchemica$inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected, CallbackInfo ci) {
        // We only run logic on the server to prevent desync/ghost items
        if (world.isClient) return;

        // --- 1. ROT LOGIC (Food) ---
        // If tickRot returns TRUE, it means the item has fully rotted.
        if (RotUtils.tickRot(stack, world, entity)) {

            // Create Rotten Flesh with same stack count
            ItemStack rot = new ItemStack(Items.ROTTEN_FLESH, stack.getCount());

            // Replace the item in the player's inventory
            if (entity instanceof PlayerEntity player) {
                player.getInventory().setStack(slot, rot);

                // Play a squish sound (10% chance)
                if (world.random.nextFloat() < 0.1f) {
                    world.playSound(null, player.getX(), player.getY(), player.getZ(),
                            SoundEvents.ENTITY_SLIME_SQUISH, SoundCategory.PLAYERS, 0.5f, 1.0f);
                }
            }
        }

        // --- 2. RUST LOGIC (Iron) ---
        // This simply updates the NBT tag so the client knows what color to render.
        RustUtils.tickRust(stack, world);

    }
}