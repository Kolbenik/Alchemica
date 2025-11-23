package sh.luunar.alchemica.block;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import sh.luunar.alchemica.Alchemica;
import sh.luunar.alchemica.block.custom.VinegarCauldronBlock;

public class ModBlocks {

    public static final Block TEST_BLOCK = registerBlock("test_block",
            new Block(FabricBlockSettings.copyOf(Blocks.STONE)));

    // --- NEW BLOCK ---
    // Copies properties of Iron Block (requires pickaxe, sounds like metal)
    public static final Block RUSTED_IRON_BLOCK = registerBlock("rusted_iron_block",
            new Block(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).strength(5.0f, 6.0f)));
    // -----------------

    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, new Identifier(Alchemica.MOD_ID, name), block);
    }

    private static Item registerBlockItem(String name, Block block) {
        return Registry.register(Registries.ITEM, new Identifier(Alchemica.MOD_ID, name),
                new BlockItem(block, new FabricItemSettings()));
    }

    // Register Vinegar Cauldron (No ItemGroup needed, as we transform it in-world)
    public static final Block VINEGAR_CAULDRON = registerBlock("vinegar_cauldron",
            new VinegarCauldronBlock(FabricBlockSettings.copyOf(Blocks.CAULDRON)));


    public static void registerModBlocks() {
        Alchemica.LOGGER.info("Registering Mod Blocks for " + Alchemica.MOD_ID);
    }
}