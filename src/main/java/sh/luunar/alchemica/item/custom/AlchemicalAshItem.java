package sh.luunar.alchemica.item.custom;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Fertilizable;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class AlchemicalAshItem extends Item {
    public AlchemicalAshItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();

        // Check if the block is a plant that accepts Bone Meal (Fertilizable)
        if (block instanceof Fertilizable fertilizable) {

            if (fertilizable.isFertilizable(world, pos, state, world.isClient)) {
                if (world.isClient) {
                    // Add a little particle effect on click
                    for(int i = 0; i < 5; i++) {
                        world.addParticle(ParticleTypes.SMOKE, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 0, 0, 0);
                    }
                    return ActionResult.SUCCESS;
                }

                ServerWorld serverWorld = (ServerWorld) world;

                // --- THE GAMBLE (50/50) ---
                if (serverWorld.random.nextFloat() < 0.5f) {
                    // Outcome A: SUCCESS (Bone Meal Effect)
                    if (fertilizable.canGrow(world, world.random, pos, state)) {
                        fertilizable.grow(serverWorld, world.random, pos, state);

                        // Play Bone Meal Sound
                        world.playSound(null, pos, SoundEvents.ITEM_BONE_MEAL_USE, SoundCategory.BLOCKS, 1.0f, 1.0f);
                        // Green Particles
                        serverWorld.syncWorldEvent(2005, pos, 0);
                    }
                } else {
                    // Outcome B: FAILURE (Death)
                    // "false" means do NOT drop items/seeds. It just vanishes.
                    serverWorld.breakBlock(pos, false);

                    // Play a harsh "wither" or "burn" sound
                    world.playSound(null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5f, 2.0f); // High pitch hiss
                    serverWorld.spawnParticles(ParticleTypes.SMOKE, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 10, 0.1, 0.1, 0.1, 0.05);
                }

                // Consume the Ash
                context.getStack().decrement(1);

                return ActionResult.CONSUME;
            }
        }

        return ActionResult.PASS;
    }
}