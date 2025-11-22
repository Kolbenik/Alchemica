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

    @Inject(method = "clientTick", at = @At("TAIL"))
    private static void alchemica$renderRitualParticles(World world, BlockPos pos, BlockState state, CampfireBlockEntity blockEntity, CallbackInfo ci) {
        // 1. Check if the Campfire is actually lit. If it's unlit, do nothing.
        if (!state.get(net.minecraft.block.CampfireBlock.LIT)) {
            return;
        }

        // 2. Check the block immediately ABOVE the campfire.
        BlockPos abovePos = pos.up();
        BlockState aboveState = world.getBlockState(abovePos);

        // 3. Is it a Cauldron? (Checks for empty, water, or lava cauldrons)
        // You can restrict this to just Blocks.WATER_CAULDRON if you want it to require water.
        if (aboveState.isOf(Blocks.CAULDRON) || aboveState.isOf(Blocks.WATER_CAULDRON)) {

            // 4. Math for the particles.
            // We want them to spiral or float around the cauldron.
            Random random = world.random;

            // Only spawn particles occasionally (e.g., 30% chance per tick) so it's not too messy
            if (random.nextFloat() < 0.3f) {
                double x = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.6;
                double y = pos.getY() + 1.2; // Start inside the cauldron logic
                double z = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.6;

                // Velocity (moving slowly upwards)
                double vX = (random.nextDouble() - 0.5) * 0.05;
                double vY = 0.05;
                double vZ = (random.nextDouble() - 0.5) * 0.05;

                // I chose SOUL_FIRE_FLAME for a "Dark/Magic" blue look, or WITCH particles.
                // Change this to whatever fits Alchemica best.
                world.addParticle(ParticleTypes.SOUL_FIRE_FLAME, x, y, z, vX, vY, vZ);

                // Optional: Add a secondary particle for "bubbling"
                world.addParticle(ParticleTypes.ENCHANT, x, y + 0.5, z, 0, 0.1, 0);
            }
        }
    }
}