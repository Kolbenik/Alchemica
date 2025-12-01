package sh.luunar.alchemica.entity;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import sh.luunar.alchemica.Alchemica;
import sh.luunar.alchemica.entity.custom.ThrownMilkEntity;

public class ModEntities {
    public static final EntityType<ThrownMilkEntity> SPLASH_MILK_ENTITY = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(Alchemica.MOD_ID, "splash_milk"),
            FabricEntityTypeBuilder.<ThrownMilkEntity>create(SpawnGroup.MISC, ThrownMilkEntity::new)
                    .dimensions(EntityDimensions.fixed(0.25f, 0.25f))
                    .trackRangeBlocks(4).trackedUpdateRate(10)
                    .build()
    );

    public static void registerModEntities() {
        Alchemica.LOGGER.info("Registering Entities for " + Alchemica.MOD_ID);
    }
}