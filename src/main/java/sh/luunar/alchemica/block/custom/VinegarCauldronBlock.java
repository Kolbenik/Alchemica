package sh.luunar.alchemica.block.custom;

import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import sh.luunar.alchemica.util.RotUtils;

public class VinegarCauldronBlock extends Block {
    // Custom Level Property: 1 to 4
    public static final IntProperty LEVEL = IntProperty.of("level", 1, 4);

    // Standard Cauldron Shape
    private static final VoxelShape RAYCAST_SHAPE = createCuboidShape(2.0, 4.0, 2.0, 14.0, 16.0, 14.0);
    private static final VoxelShape OUTLINE_SHAPE = VoxelShapes.combineAndSimplify(VoxelShapes.fullCube(), VoxelShapes.union(createCuboidShape(0.0, 0.0, 4.0, 16.0, 3.0, 12.0), createCuboidShape(4.0, 0.0, 0.0, 12.0, 3.0, 16.0), createCuboidShape(2.0, 0.0, 2.0, 14.0, 3.0, 14.0), RAYCAST_SHAPE), BooleanBiFunction.ONLY_FIRST);

    public VinegarCauldronBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(LEVEL, 4));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(LEVEL);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return OUTLINE_SHAPE;
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (world.isClient) return;

        if (entity instanceof ItemEntity itemEntity) {
            ItemStack stack = itemEntity.getStack();

            // 1. Validate Item: Must be Food, Not Rotten Flesh, Not Already Preserved
            if (!stack.isFood() || stack.isOf(Items.ROTTEN_FLESH)) return;
            if (stack.hasNbt() && stack.getNbt().getBoolean("alchemica_preserved")) return;

            // 2. Logic: Process ONE item at a time
            // We split 1 item off the stack to preserve it.
            ItemStack preservedStack = stack.split(1);

            // Apply Preservation Tag
            preservedStack.getOrCreateNbt().putBoolean("alchemica_preserved", true);

            // Reset Rot Timer (Clean it)
            if(preservedStack.getNbt().contains(RotUtils.ROT_KEY)) {
                preservedStack.getNbt().remove(RotUtils.ROT_KEY);
            }

            // 3. Spawn the new Preserved Item
            ItemEntity newEntity = new ItemEntity(world, entity.getX(), entity.getY(), entity.getZ(), preservedStack);
            newEntity.setVelocity(entity.getVelocity()); // Keep momentum
            world.spawnEntity(newEntity);

            // 4. Update the Cauldron Level
            int currentLevel = state.get(LEVEL);
            if (currentLevel > 1) {
                // Lower Level
                world.setBlockState(pos, state.with(LEVEL, currentLevel - 1));
            } else {
                // Empty! Revert to Vanilla Cauldron
                world.setBlockState(pos, Blocks.CAULDRON.getDefaultState());
                world.playSound(null, pos, SoundEvents.BLOCK_BREWING_STAND_BREW, SoundCategory.BLOCKS, 1f, 0.5f);
            }

            // Effects
            world.playSound(null, pos, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 1f, 1f);

            // If original stack is empty, kill the old entity
            if (stack.isEmpty()) {
                itemEntity.discard();
            }
        }
    }
}