package sh.luunar.alchemica.mixin;

import net.minecraft.block.AbstractCauldronBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sh.luunar.alchemica.item.ModItems;

import java.util.List;

// CHANGED: Target AbstractCauldronBlock because LeveledCauldronBlock doesn't override onEntityCollision
@Mixin(AbstractCauldronBlock.class)
public class BioAlchemyMixin {

    @Inject(method = "onEntityCollision", at = @At("HEAD"))
    private void alchemica$bioAlchemy(BlockState state, World world, BlockPos pos, Entity entity, CallbackInfo ci) {
        if (world.isClient) return;

        // --- CHECK 1: IS THE WATER BOILING? ---
        // Must be Water Cauldron
        if (!state.isOf(Blocks.WATER_CAULDRON)) return;

        // Must have heat below
        BlockPos below = pos.down();
        BlockState belowState = world.getBlockState(below);
        boolean isHeated = belowState.isOf(Blocks.CAMPFIRE) || belowState.isOf(Blocks.SOUL_CAMPFIRE) || belowState.isOf(Blocks.LAVA) || belowState.isOf(Blocks.MAGMA_BLOCK);

        if (!isHeated) return;

        // --- CHECK 2: PREVENT PICKUP (Fix for your issue) ---
        if (entity instanceof ItemEntity item) {
            // If an item falls into a boiling cauldron, make it un-pickupable for a bit
            // This gives the player time to jump in without grabbing it accidentally.
            if (item.getStack().isOf(ModItems.ALCHEMICAL_ASH) || item.getStack().isOf(Items.EMERALD)) {
                item.setPickupDelay(20); // 1 second delay constantly applied while inside
            }
            return;
        }

        // --- CHECK 3: THE SACRIFICE (Player Logic) ---
        if (entity instanceof ServerPlayerEntity player) {

            // Scan for those items we just made un-pickupable
            Box cauldronBox = new Box(pos).contract(0.1, 0.1, 0.1);
            List<ItemEntity> items = world.getEntitiesByClass(ItemEntity.class, cauldronBox, e -> true);

            for (ItemEntity itemEntity : items) {
                ItemStack stack = itemEntity.getStack();

                // RITUAL A: SOUL ASH (Ash + Player Damage)
                if (stack.isOf(ModItems.ALCHEMICAL_ASH)) {
                    // Require at least 4 hearts health to be safe, or kill them?
                    // Let's do damage.
                    if (player.damage(world.getDamageSources().magic(), 8.0f)) {

                        stack.decrement(1);
                        if (stack.isEmpty()) itemEntity.discard();

                        // Spawn Soul Ash (Pop it up)
                        ItemEntity product = new ItemEntity(world, pos.getX()+0.5, pos.getY()+1, pos.getZ()+0.5, new ItemStack(ModItems.SOUL_ASH));
                        product.setVelocity(0, 0.3, 0);
                        world.spawnEntity(product);

                        world.playSound(null, pos, SoundEvents.PARTICLE_SOUL_ESCAPE, SoundCategory.PLAYERS, 1f, 1f);
                        return; // Done
                    }
                }

                // RITUAL B: BANISHMENT (Emerald + Player Life)
                if (stack.isOf(Items.EMERALD)) {
                    stack.decrement(1);
                    if (stack.isEmpty()) itemEntity.discard();

                    // Drop the Totem
                    ItemEntity totem = new ItemEntity(world, pos.getX()+0.5, pos.getY()+1, pos.getZ()+0.5, new ItemStack(Items.TOTEM_OF_UNDYING));
                    totem.setVelocity(0, 0.3, 0);
                    world.spawnEntity(totem);

                    // BANISH PLAYER
                    player.changeGameMode(GameMode.SPECTATOR);
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 999999, 0, false, false));
                    player.sendMessage(Text.literal("You have been banished.").formatted(Formatting.RED), true);

                    world.playSound(null, pos, SoundEvents.BLOCK_END_PORTAL_SPAWN, SoundCategory.PLAYERS, 1f, 0.5f);
                    return;
                }
            }
        }
    }
}