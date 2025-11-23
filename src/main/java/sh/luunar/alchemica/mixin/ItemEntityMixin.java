package sh.luunar.alchemica.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity {

    public ItemEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Shadow public abstract ItemStack getStack();
    @Shadow public abstract void setStack(ItemStack stack);

    @Inject(method = "tick", at = @At("TAIL"))
    private void alchemica$rustInWater(CallbackInfo ci) {
        if (this.getWorld().isClient) return;

        ItemStack stack = this.getStack();

        // 1. Must be Iron Ingot
        if (!stack.isOf(Items.IRON_INGOT)) return;

        // 2. Must be In Water
        if (!this.isSubmergedInWater()) return;

        // 3. Logic: Increment Timer
        NbtCompound nbt = stack.getOrCreateNbt();
        int rustTime = nbt.getInt("alchemica_water_rust");
        rustTime++;
        nbt.putInt("alchemica_water_rust", rustTime);

        // Optional: Particles every second (20 ticks) to show it's working
        if (rustTime % 20 == 0) {
            // Spawn bubble particle at item location
            // (Need to send packet or use world event, but for simplicity we rely on splash sounds usually)
        }

        // 4. Transform after 60 seconds (1200 ticks)
        if (rustTime >= 1200) {
            // Create Copper
            ItemStack copper = new ItemStack(Items.COPPER_INGOT, stack.getCount());

            // Apply!
            this.setStack(copper);

            // Effects
            this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(),
                    SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.AMBIENT, 0.5f, 1.0f);

            // Reset timer (just in case)
            nbt.remove("alchemica_water_rust");
        }
    }
}