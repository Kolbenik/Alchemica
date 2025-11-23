package sh.luunar.alchemica.item.custom;

import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;

import java.util.List;

public class SoulTotemItem extends Item {
    public SoulTotemItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient) {
            // 1. Get ALL players in the current world (Global Rescue)
            List<? extends PlayerEntity> allPlayers = world.getPlayers();
            boolean savedSomeone = false;

            for (PlayerEntity p : allPlayers) {
                if (p instanceof ServerPlayerEntity player) {

                    // 2. Check criteria: Must be Spectator AND have Blindness (The "Banished" mark)
                    if (player.isSpectator() && player.hasStatusEffect(StatusEffects.BLINDNESS)) {

                        // 3. THE RESCUE
                        player.changeGameMode(GameMode.SURVIVAL);
                        player.removeStatusEffect(StatusEffects.BLINDNESS);

                        // Optional: Teleport them to the rescuer so they aren't lost?
                        player.teleport(user.getX(), user.getY(), user.getZ());

                        player.sendMessage(Text.literal("A Soul Totem has tethered you back to the living.").formatted(Formatting.GOLD).formatted(Formatting.BOLD), true);
                        savedSomeone = true;
                    }
                }
            }

            if (savedSomeone) {
                // Success: Play big sound, consume item
                user.sendMessage(Text.literal("The tether is complete. Souls returned.").formatted(Formatting.AQUA), true);
                world.playSound(null, user.getBlockPos(), SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundCategory.PLAYERS, 1.0f, 1.0f);
                world.playSound(null, user.getBlockPos(), SoundEvents.ENTITY_EVOKER_PREPARE_SUMMON, SoundCategory.PLAYERS, 1.0f, 1.0f);

                user.getStackInHand(hand).decrement(1);
                return TypedActionResult.consume(user.getStackInHand(hand));
            } else {
                // Fail: No one to save
                user.sendMessage(Text.literal("No banished souls found in this realm.").formatted(Formatting.GRAY), true);
                return TypedActionResult.fail(user.getStackInHand(hand));
            }
        }
        return TypedActionResult.pass(user.getStackInHand(hand));
    }

    // Optional: Make it glow like a totem
    @Override
    public boolean hasGlint(ItemStack stack) {
        return true;
    }
}