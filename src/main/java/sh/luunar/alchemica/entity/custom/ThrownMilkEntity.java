/*
 * Copyright (c) 2025 Lunar_sh, Kolbenik
 *
 * All rights reserved.
 */

package sh.luunar.alchemica.entity.custom;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import sh.luunar.alchemica.entity.ModEntities;
import sh.luunar.alchemica.item.ModItems;

public class ThrownMilkEntity extends ThrownItemEntity {

    public ThrownMilkEntity(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }

    public ThrownMilkEntity(World world, LivingEntity owner) {
        super(ModEntities.SPLASH_MILK_ENTITY, owner, world);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.SPLASH_MILK;
    }

    // FIX 1: ADJUST GRAVITY (0.05F = Potion, 0.03F = Snowball)
    @Override
    protected float getGravity() {
        return 0.08F;
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        if (!this.getWorld().isClient) {

            // 1. Clear Effects
            this.getWorld().getEntitiesByClass(LivingEntity.class, this.getBoundingBox().expand(4.0, 2.0, 4.0), entity -> true)
                    .forEach(LivingEntity::clearStatusEffects);

            // FIX 2: WHITE PARTICLES
            // Event 2002 (POTION_SPLASH) takes a Color Integer as the data.
            // 0xFFFFFF = White.
            this.getWorld().syncWorldEvent(2002, this.getBlockPos(), 0xFFFFFF);

            this.discard();
        }
    }
}