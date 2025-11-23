package sh.luunar.alchemica.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.CampfireBlockEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CampfireBlockEntity.class)
public class CampfireBlockEntityMixin {

    // CLIENT SIDE ONLY: Visual particles for the ritual
    @Inject(method = "clientTick", at = @At("TAIL"))
    private static void alchemica$renderRitualParticles(World world, BlockPos pos, BlockState state, CampfireBlockEntity blockEntity, CallbackInfo ci) {
        // 1. Check if Campfire is Lit
        if (!state.get(net.minecraft.block.CampfireBlock.LIT)) {
            return;
        }

        // 2. Check for Cauldron above
        BlockPos abovePos = pos.up();
        BlockState aboveState = world.getBlockState(abovePos);

        if (aboveState.isOf(Blocks.WATER_CAULDRON) || aboveState.isOf(Blocks.CAULDRON)) {

            // 3. Spawn Magic Particles (Blue Fire)
            Random random = world.random;
            if (random.nextFloat() < 0.2f) { // 20% chance per tick
                double x = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.6;
                double y = pos.getY() + 1.2;
                double z = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.6;

                world.addParticle(ParticleTypes.SOUL_FIRE_FLAME, x, y, z, 0, 0.05, 0);
            }
        }
    }
}