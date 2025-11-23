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

import java.util.List;

@Mixin(Item.class)
public class ItemMixin {

    @Inject(method = "appendTooltip", at = @At("HEAD"))
    private void alchemica$addRotTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context, CallbackInfo ci) {
        // 1. Safety Checks
        if (world == null || !stack.isFood() || stack.isOf(Items.ROTTEN_FLESH)) return;

        // 2. Calculate Data
        float percent = RotUtils.getRotPercentage(stack, world);

        // 3. Add Tooltip Lines
        tooltip.add(Text.empty()); // Spacer
        tooltip.add(RotUtils.getRotStatus(percent)); // "Fresh", "Stale", etc.
        tooltip.add(RotUtils.getTimeRemaining(stack, world)); // "Rot in: 25s"
    }

    @Inject(method = "inventoryTick", at = @At("HEAD"))
    private void alchemica$tickRot(ItemStack stack, World world, Entity entity, int slot, boolean selected, CallbackInfo ci) {
        if (world.isClient) return;

        // If tickRot returns TRUE, the item dies
        if (RotUtils.tickRot(stack, world, entity)) {

            // Create Rotten Flesh with same count
            ItemStack rot = new ItemStack(Items.ROTTEN_FLESH, stack.getCount());

            // Replace in inventory
            if (entity instanceof PlayerEntity player) {
                player.getInventory().setStack(slot, rot);

                // Squish sound (only 10% chance to not be annoying)
                if (world.random.nextFloat() < 0.1f) {
                    world.playSound(null, player.getX(), player.getY(), player.getZ(),
                            SoundEvents.ENTITY_SLIME_SQUISH, SoundCategory.PLAYERS, 0.5f, 1.0f);
                }
            }
        }
    }
}