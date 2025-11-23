package sh.luunar.alchemica.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import sh.luunar.alchemica.Alchemica;
import sh.luunar.alchemica.item.custom.AlchemicalAshItem;
import sh.luunar.alchemica.item.custom.CreditItem;

public class ModItems {

    public static final Item ICON_ITEM = registerItem("icon",
            new Item(new FabricItemSettings().maxCount(1)));

    public static final Item CREDIT_KOLBENIK = registerItem("credit_kolbenik",
            new CreditItem(new FabricItemSettings().maxCount(1)));

    public static final Item CREDIT_LUNAR = registerItem("credit_lunar",
            new CreditItem(new FabricItemSettings()));

    public static final Item TEST_ITEM = registerItem("test",
            new Item(new FabricItemSettings()));


    public static final Item ANTENNA = registerItem("antenna",
            new Item(new FabricItemSettings()));

    public static final Item ALCHEMICAL_ASH = registerItem("alchemical_ash",
            new AlchemicalAshItem(new FabricItemSettings()));

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
