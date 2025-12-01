package sh.luunar.alchemica.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.StonecutterBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(StonecutterBlock.class)
public class StonecutterBlockMixin extends Block {

    public StonecutterBlockMixin(Settings settings) {
        super(settings);
    }

    // Override the "Stepped On" method to deal damage
    @Override
    public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
        if (!world.isClient && entity instanceof LivingEntity) {
            // Check if they are sneaking (Safety mechanism)
            if (!entity.isSneaking()) {
                // Deal 2 damage (1 Heart) per tick they are moving on it
                // Using "cactus" source for generic physical damage
                entity.damage(world.getDamageSources().cactus(), 2.0F);
            }
        }
        super.onSteppedOn(world, pos, state, entity);
    }
}