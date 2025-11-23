package sh.luunar.alchemica.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sh.luunar.alchemica.effect.ModEffects;

@Mixin(Camera.class)
public abstract class CameraMixin {

    @Shadow protected abstract void setRotation(float yaw, float pitch);
    @Shadow public abstract float getYaw();
    @Shadow public abstract float getPitch();

    @Inject(method = "update", at = @At("TAIL"))
    private void alchemica$drunkShake(BlockView area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();

        // 1. Check if it's the player and they are drunk
        if (client.player != null && focusedEntity == client.player) {
            if (client.player.hasStatusEffect(ModEffects.DRUNK)) {

                int amp = client.player.getStatusEffect(ModEffects.DRUNK).getAmplifier();

                // --- THE FIX: HIGHER INTENSITY ---
                // Stage 1 (Amp 0) = 2.0 multiplier
                // Stage 4 (Amp 3) = 8.0 multiplier (Unplayable)
                float intensity = (amp + 1) * 2.0f;

                // Random jitter
                // nextFloat() is 0.0 to 1.0. Minus 0.5 gives -0.5 to 0.5.
                // Multiplied by intensity gives a range of e.g. -4 to +4 degrees.
                float randomYaw = (client.player.getRandom().nextFloat() - 0.5f) * intensity;
                float randomPitch = (client.player.getRandom().nextFloat() - 0.5f) * intensity;

                // Apply Shake
                this.setRotation(this.getYaw() + randomYaw, this.getPitch() + randomPitch);
            }
        }
    }
}