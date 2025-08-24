package sh.lunar.alchemica.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import sh.lunar.alchemica.Alchemica;

public class ModItems {

    public static final Item TEST_ITEM = registerItem("test",
            new Item(new FabricItemSettings()));

    public static final Item ANTENNA = registerItem("antenna",
            new Item(new FabricItemSettings()));



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
