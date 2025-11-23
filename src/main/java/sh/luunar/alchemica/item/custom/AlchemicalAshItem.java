package sh.luunar.alchemica.item.custom;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Fertilizable;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import sh.luunar.alchemica.block.ModBlocks;
import sh.luunar.alchemica.util.ModTags;

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

        // --- 1. IRON BLOCK CONVERSION (The only way to make a block rust) ---
        if (state.isOf(Blocks.IRON_BLOCK)) {
            // Check if protected by tag
            if (state.isIn(ModTags.Blocks.IMMUNE_TO_RUST)) {
                if(world.isClient) context.getPlayer().sendMessage(Text.literal("Protected from corrosion."), true);
                return ActionResult.FAIL;
            }

            if (!world.isClient) {
                // Swap to Rusted Block
                world.setBlockState(pos, ModBlocks.RUSTED_IRON_BLOCK.getDefaultState());

                // Effects: Sound of metal corroding
                world.playSound(null, pos, SoundEvents.BLOCK_COPPER_BREAK, SoundCategory.BLOCKS, 1f, 0.5f);
                // Particles
                ((ServerWorld)world).spawnParticles(ParticleTypes.ASH, pos.getX()+0.5, pos.getY()+1, pos.getZ()+0.5, 10, 0.2, 0.2, 0.2, 0.05);

                // Consume Ash
                context.getStack().decrement(1);
            }
            return ActionResult.SUCCESS;
        }

        // --- 2. CROP GAMBLE (Existing Feature) ---
        if (block instanceof Fertilizable fertilizable) {
            if (fertilizable.isFertilizable(world, pos, state, world.isClient)) {
                if (world.isClient) {
                    for(int i = 0; i < 5; i++) world.addParticle(ParticleTypes.SMOKE, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 0, 0, 0);
                    return ActionResult.SUCCESS;
                }

                ServerWorld serverWorld = (ServerWorld) world;
                if (serverWorld.random.nextFloat() < 0.5f) {
                    if (fertilizable.canGrow(world, world.random, pos, state)) {
                        fertilizable.grow(serverWorld, world.random, pos, state);
                        world.playSound(null, pos, SoundEvents.ITEM_BONE_MEAL_USE, SoundCategory.BLOCKS, 1.0f, 1.0f);
                        serverWorld.syncWorldEvent(2005, pos, 0);
                    }
                } else {
                    serverWorld.breakBlock(pos, false);
                    world.playSound(null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5f, 2.0f);
                    serverWorld.spawnParticles(ParticleTypes.SMOKE, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 10, 0.1, 0.1, 0.1, 0.05);
                }
                context.getStack().decrement(1);
                return ActionResult.CONSUME;
            }
        }

        return ActionResult.PASS;
    }
}