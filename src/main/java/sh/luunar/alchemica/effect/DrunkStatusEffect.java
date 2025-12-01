/*
 * Copyright (c) 2025 Lunar_sh, Kolbenik
 *
 * All rights reserved.
 */

package sh.luunar.alchemica.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import sh.luunar.alchemica.Alchemica;

public class DrunkStatusEffect extends StatusEffect {

    // Key for our custom damage type (defined in JSON later)
    public static final RegistryKey<DamageType> ALCOHOL_POISONING = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier(Alchemica.MOD_ID, "alcohol_poisoning"));

    public DrunkStatusEffect() {
        super(StatusEffectCategory.NEUTRAL, 0x551A8B);

        // ATTRIBUTES
        // Slowness: Gets harder to walk
        this.addAttributeModifier(EntityAttributes.GENERIC_MOVEMENT_SPEED,
                "7107DE5E-7CE8-4030-940E-514C1F160890", -0.10, EntityAttributeModifier.Operation.MULTIPLY_TOTAL);

        // Strength: "Liquid Courage"
        this.addAttributeModifier(EntityAttributes.GENERIC_ATTACK_DAMAGE,
                "648D7064-6A60-4F59-8ABE-C2C23A6DD7A9", 2.0, EntityAttributeModifier.Operation.ADDITION);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        // BENEFIT: Resistance (Numb to pain)
        // Stage 0 = Res 0, Stage 1 = Res 1, etc.
        if (!entity.hasStatusEffect(StatusEffects.RESISTANCE)) {
            entity.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 40, amplifier, false, false, false));
        }

        // SIDE EFFECT: Mining Fatigue (Hard to work) at Stage 2+
        if (amplifier >= 1 && !entity.hasStatusEffect(StatusEffects.MINING_FATIGUE)) {
            entity.addStatusEffect(new StatusEffectInstance(StatusEffects.MINING_FATIGUE, 40, 0, false, false, false));
        }

        // SIDE EFFECT: Blindness (Blacking Out) at Stage 3 (Critical)
        // Flashes blindness occasionally
        if (amplifier >= 2) {
            if (entity.getRandom().nextFloat() < 0.05f) {
                entity.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 60, 0, false, false, false));
            }
        }

        // DEATH: Stage 4 (Amplifier 3)
        if (amplifier >= 3) {
            if (entity.getHealth() > 0) {
                // 1. Create the Damage Source (for the chat message)
                DamageSource source = new DamageSource(
                        entity.getWorld().getRegistryManager()
                                .get(RegistryKeys.DAMAGE_TYPE)
                                .entryOf(ALCOHOL_POISONING)
                );

                // 2. Try normal damage first (for survival players / visuals)
                entity.damage(source, Float.MAX_VALUE);

                // 3. THE FIX: FORCE KILL (Bypasses Creative)
                entity.setHealth(0);
            }
        }
    }
}
