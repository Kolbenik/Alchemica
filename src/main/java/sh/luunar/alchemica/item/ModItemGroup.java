package sh.luunar.alchemica.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import sh.luunar.alchemica.Alchemica;
import sh.luunar.alchemica.block.ModBlocks;

public class ModItemGroup {

    public static final ItemGroup ALCHEMICA = Registry.register(Registries.ITEM_GROUP,
            new Identifier(Alchemica.MOD_ID, "alchemica"),
            FabricItemGroup.builder().displayName(Text.translatable("itemgroup.alchemica"))
                    .icon(() -> new ItemStack(ModItems.ICON_ITEM))
                    .entries((displayContext, entries) -> {
                                entries.add(ModItems.ANTENNA);
                                entries.add(ModItems.CREDIT_KOLBENIK);
                                entries.add(ModItems.CREDIT_LUNAR);
                                entries.add(ModItems.TEST_ITEM);
                                entries.add(ModBlocks.TEST_BLOCK);
                    }).build());

    public static void registerModItemGroups() {
        Alchemica.LOGGER.info("Registering Mod Item Groups for " + Alchemica.MOD_ID);
    }
}
