package sh.luunar.alchemica.item.custom;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import sh.luunar.alchemica.effect.ModEffects;

import java.util.List;

public class AlcoholItem extends Item {
    public AlcoholItem(Settings settings) {
        super(settings);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (user instanceof ServerPlayerEntity serverPlayer) {
            Criteria.CONSUME_ITEM.trigger(serverPlayer, stack);
            serverPlayer.incrementStat(Stats.USED.getOrCreateStat(this));
        }

        if (!world.isClient) {
            // --- ALCOHOL STAGE LOGIC ---
            int currentAmp = -1;
            int duration = 1200; // 60 Seconds per drink

            // 1. Check if already under the influence
            if (user.hasStatusEffect(ModEffects.DRUNK)) {
                StatusEffectInstance currentEffect = user.getStatusEffect(ModEffects.DRUNK);
                if (currentEffect != null) {
                    currentAmp = currentEffect.getAmplifier();
                }
            }

            // 2. Increase Stage (Cap at 3, which is fatal anyway)
            int nextAmp = Math.min(3, currentAmp + 1);

            // 3. Apply the new level (Overwrites the old one)
            // Stage 0 = Tipsy
            // Stage 1 = Drunk
            // Stage 2 = Wasted
            // Stage 3 = Fatal
            user.addStatusEffect(new StatusEffectInstance(ModEffects.DRUNK, duration, nextAmp));

            // Optional: Play a "Burp" or "Hiccup" sound occasionally?
            // For now, the effect handles the gameplay.
        }

        if (user instanceof PlayerEntity player && !player.getAbilities().creativeMode) {
            stack.decrement(1);
        }

        return stack;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.DRINK;
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 32;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        return ItemUsage.consumeHeldItem(world, user, hand);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.empty());
        tooltip.add(Text.literal("Industrial Grade.").formatted(Formatting.GRAY));
        tooltip.add(Text.literal("Warning: Stage 4 is fatal.").formatted(Formatting.DARK_RED).formatted(Formatting.ITALIC));
    }
}