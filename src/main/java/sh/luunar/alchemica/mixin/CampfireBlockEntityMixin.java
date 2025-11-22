package sh.luunar.alchemica.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeveledCauldronBlock;
import net.minecraft.block.entity.CampfireBlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.random.Random; // specific import for 1.20.1
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sh.luunar.alchemica.item.ModItems; // Import your ModItems

import java.util.List;

@Mixin(CampfireBlockEntity.class)
public class CampfireBlockEntityMixin {

    // CLIENT SIDE: Visuals (Particles)
    @Inject(method = "clientTick", at = @At("TAIL"))
    private static void alchemica$renderRitualParticles(World world, BlockPos pos, BlockState state, CampfireBlockEntity blockEntity, CallbackInfo ci) {
        if (!state.get(net.minecraft.block.CampfireBlock.LIT)) return;

        BlockPos abovePos = pos.up();
        BlockState aboveState = world.getBlockState(abovePos);

        if (aboveState.isOf(Blocks.WATER_CAULDRON) || aboveState.isOf(Blocks.CAULDRON)) {
            Random random = world.random;
            if (random.nextFloat() < 0.2f) { // 20% chance per tick
                double x = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.6;
                double y = pos.getY() + 1.2;
                double z = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.6;
                world.addParticle(ParticleTypes.SOUL_FIRE_FLAME, x, y, z, 0, 0.05, 0);
            }
        }
    }

    // SERVER SIDE: Logic (Transformation)
    @Inject(method = "litServerTick", at = @At("TAIL"))
    private static void alchemica$alchemyLogic(World world, BlockPos pos, BlockState state, CampfireBlockEntity blockEntity, CallbackInfo ci) {
        BlockPos abovePos = pos.up();
        BlockState aboveState = world.getBlockState(abovePos);

        // Requirement: Must be a FULL Water Cauldron (Level 3)
        if (!aboveState.isOf(Blocks.WATER_CAULDRON) || aboveState.get(LeveledCauldronBlock.LEVEL) != 3) {
            return;
        }

        Box cauldronBox = new Box(abovePos).contract(0.1, 0.2, 0.1);
        List<ItemEntity> entities = world.getEntitiesByClass(ItemEntity.class, cauldronBox, (entity) -> true);

        for (ItemEntity itemEntity : entities) {
            ItemStack stack = itemEntity.getStack();

            // RECIPE: Iron Ingot -> Gold Nugget OR Ash
            if (stack.isOf(Items.IRON_INGOT)) {

                boolean success = world.random.nextFloat() < 0.5f; // 50% Chance
                ItemStack resultStack;

                if (world instanceof ServerWorld serverWorld) {
                    if (success) {
                        // SUCCESS
                        resultStack = new ItemStack(Items.GOLD_NUGGET, stack.getCount());

                        serverWorld.spawnParticles(ParticleTypes.END_ROD, itemEntity.getX(), itemEntity.getY() + 0.5, itemEntity.getZ(), 10, 0.1, 0.1, 0.1, 0.05);
                        world.playSound(null, abovePos, SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME, SoundCategory.BLOCKS, 1.0f, 1.0f);
                    } else {
                        // FAILURE
                        resultStack = new ItemStack(ModItems.ALCHEMICAL_ASH, stack.getCount());

                        serverWorld.spawnParticles(ParticleTypes.SMOKE, itemEntity.getX(), itemEntity.getY() + 0.5, itemEntity.getZ(), 10, 0.1, 0.1, 0.1, 0.05);
                        world.playSound(null, abovePos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 1.0f, 0.5f);
                    }

                    // Spawn the result
                    ItemEntity resultEntity = new ItemEntity(world, itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(), resultStack);
                    resultEntity.setVelocity(0, 0.2, 0);
                    world.spawnEntity(resultEntity);

                    // Remove input
                    itemEntity.discard();
                    break; // Process one stack per tick
                }
            }
        }
    }
}