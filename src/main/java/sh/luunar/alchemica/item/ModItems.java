/*
 * Copyright (c) 2025 Lunar_sh, Kolbenik
 *
 * All rights reserved.
 */

package sh.luunar.alchemica.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import sh.luunar.alchemica.Alchemica;
import sh.luunar.alchemica.item.custom.*;

public class ModItems {

    public static final Item ICON_ITEM = registerItem("icon",
            new Item(new FabricItemSettings().maxCount(1)));

    public static final Item CREDIT_KOLBENIK = registerItem("credit_kolbenik",
            new CreditItem(new FabricItemSettings().maxCount(1)));

    public static final Item CREDIT_LUNAR = registerItem("credit_lunar",
            new CreditItem(new FabricItemSettings().maxCount(1)));

    public static final Item ANTENNA = registerItem("antenna",
            new Item(new FabricItemSettings().maxCount(1)));

    public static final Item ALCHEMICAL_ASH = registerItem("alchemical_ash",
            new AlchemicalAshItem(new FabricItemSettings()));

    // 1. Oxalic Acid (For cleaning rust)
    public static final Item OXALIC_ACID = registerItem("oxalic_acid",
            new Item(new FabricItemSettings()));

    // 2. Soul Ash (Infused with player life)
    public static final Item SOUL_ASH = registerItem("soul_ash",
            new Item(new FabricItemSettings()));

    public static final Item SOUL_TOTEM = registerItem("soul_totem",
            new SoulTotemItem(new FabricItemSettings().maxCount(1)));


    public static final Item VINEGAR = registerItem("vinegar",
            new Item(new FabricItemSettings()));

    public static final Item ALCOHOL = registerItem("alcohol",
            new AlcoholItem(new FabricItemSettings().maxCount(16))); // Stack to 16 like potions

    public static final Item COPPER_DUST = registerItem("copper_dust", new Item(new FabricItemSettings()));
    public static final Item IRON_DUST = registerItem("iron_dust", new Item(new FabricItemSettings()));
    public static final Item DIAMOND_DUST = registerItem("diamond_dust", new Item(new FabricItemSettings()));
    public static final Item SPLASH_MILK = registerItem("splash_milk", new SplashMilkItem(new FabricItemSettings().maxCount(16)));

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier(Alchemica.MOD_ID, name), item);
    }

    private static void addItemsToIngredientTabItemGroup(FabricItemGroupEntries entries) {
        //entries.add(PYRITE);
    }

    public static void registerModItems() {
        Alchemica.LOGGER.info("Registering Mod Items for " + Alchemica.MOD_ID);

        //ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS)
        //        .register(ModItems::addItemsToIngredientTabItemGroup);
    }
}
