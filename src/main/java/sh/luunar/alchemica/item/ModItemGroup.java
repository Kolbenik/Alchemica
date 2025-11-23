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

    // TAB 1: ITEMS (Ingredients, Tools, Magic)
    public static final ItemGroup ALCHEMICA_ITEMS = Registry.register(Registries.ITEM_GROUP,
            new Identifier(Alchemica.MOD_ID, "alchemica_items"),
            FabricItemGroup.builder()
                    .displayName(Text.translatable("itemgroup.alchemica.items"))
                    .icon(() -> new ItemStack(ModItems.ICON_ITEM)) // Or maybe Vinegar?
                    .entries((displayContext, entries) -> {
                        entries.add(ModItems.ANTENNA);
                        entries.add(ModItems.VINEGAR);
                        entries.add(ModItems.ALCOHOL);
                        entries.add(ModItems.OXALIC_ACID);
                        entries.add(ModItems.ALCHEMICAL_ASH);
                        entries.add(ModItems.SOUL_ASH);
                        entries.add(ModItems.SOUL_TOTEM);
                        // Add other pure items here
                    }).build());

    // TAB 2: BLOCKS (Cauldrons, Building Blocks)
    public static final ItemGroup ALCHEMICA_BLOCKS = Registry.register(Registries.ITEM_GROUP,
            new Identifier(Alchemica.MOD_ID, "alchemica_blocks"),
            FabricItemGroup.builder()
                    .displayName(Text.translatable("itemgroup.alchemica.blocks"))
                    .icon(() -> new ItemStack(ModBlocks.RUSTED_IRON_BLOCK)) // Use a block as icon
                    .entries((displayContext, entries) -> {
                       // entries.add(ModBlocks.TEST_BLOCK);
                        entries.add(ModBlocks.RUSTED_IRON_BLOCK);
                        // entries.add(ModBlocks.VINEGAR_CAULDRON); // If you want the block item available directly
                    }).build());

    // TAB 3: CREDITS (Dev items)
    public static final ItemGroup ALCHEMICA_CREDITS = Registry.register(Registries.ITEM_GROUP,
            new Identifier(Alchemica.MOD_ID, "alchemica_credits"),
            FabricItemGroup.builder()
                    .displayName(Text.translatable("itemgroup.alchemica.credits"))
                    .icon(() -> new ItemStack(ModItems.CREDIT_LUNAR)) // Use your face/item
                    .entries((displayContext, entries) -> {
                        entries.add(ModItems.CREDIT_KOLBENIK);
                        entries.add(ModItems.CREDIT_LUNAR);
                        // Add any other "Dev" or "Debug" items here
                    }).build());

    public static void registerModItemGroups() {
        Alchemica.LOGGER.info("Registering Mod Item Groups for " + Alchemica.MOD_ID);
    }
}