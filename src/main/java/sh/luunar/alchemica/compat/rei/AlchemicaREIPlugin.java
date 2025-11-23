package sh.luunar.alchemica.compat.rei;

import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import sh.luunar.alchemica.Alchemica;
import sh.luunar.alchemica.block.ModBlocks;
import sh.luunar.alchemica.item.ModItems;
import sh.luunar.alchemica.recipe.AlchemyRecipe;
import sh.luunar.alchemica.recipe.ModRecipes;

import java.util.List;
import java.util.stream.Collectors; // <-- Required for the List<Text> type fix

public class AlchemicaREIPlugin implements REIClientPlugin {

    public static final CategoryIdentifier<AlchemyDisplay> ALCHEMY = CategoryIdentifier.of(Alchemica.MOD_ID, "alchemy");
    public static final CategoryIdentifier<BioAlchemyDisplay> BIO_ALCHEMY = CategoryIdentifier.of(Alchemica.MOD_ID, "bio_alchemy");

    @Override
    public void registerCategories(CategoryRegistry registry) {
        registry.add(new AlchemyCategory());
        registry.addWorkstations(ALCHEMY, EntryIngredients.of(Blocks.CAULDRON), EntryIngredients.of(Blocks.WATER_CAULDRON));

        registry.add(new BioAlchemyCategory());
        registry.addWorkstations(BIO_ALCHEMY, EntryIngredients.of(Blocks.CAULDRON), EntryIngredients.of(Blocks.WATER_CAULDRON));
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        registry.registerRecipeFiller(AlchemyRecipe.class, ModRecipes.ALCHEMY_TYPE, AlchemyDisplay::new);

        registry.add(new BioAlchemyDisplay(ModItems.ALCHEMICAL_ASH, ModItems.SOUL_ASH));
        registry.add(new BioAlchemyDisplay(Items.EMERALD, Items.TOTEM_OF_UNDYING));

        // --- THE FINAL ATTEMPT: Using registerEntryInfo and stream mapping to enforce List<Text> type ---

        // 1. Alchemical Ash
        registry.registerEntryInfo( // <-- The last known method name for this API version
                EntryIngredients.of(ModItems.ALCHEMICAL_ASH),
                Text.literal("Alchemical Ash"),
                // Enforce List<Text> type
                List.of(
                        Text.literal("Obtained as a byproduct of failed Alchemy.").formatted(Formatting.GRAY),
                        Text.empty(),
                        Text.literal("Any transmutation has a 50% chance to fail,").formatted(Formatting.RED),
                        Text.literal("leaving only this ash behind.")
                ).stream().map(text -> (Text) text).collect(Collectors.toList())
        );

        // 2. Rusted Iron Block
        registry.registerEntryInfo( // <-- The last known method name for this API version
                EntryIngredients.of(ModBlocks.RUSTED_IRON_BLOCK),
                Text.literal("Rusted Iron"),
                // Enforce List<Text> type
                List.of(
                        Text.literal("Created by chemically corroding an Iron Block.").formatted(Formatting.GRAY),
                        Text.empty(),
                        Text.literal("Right-click an Iron Block with"),
                        Text.literal("Alchemical Ash").formatted(Formatting.GOLD),
                        Text.literal("to trigger the reaction.")
                ).stream().map(text -> (Text) text).collect(Collectors.toList())
        );

        // 3. Copper Ingot
        registry.registerEntryInfo( // <-- The last known method name for this API version
                EntryIngredients.of(Items.COPPER_INGOT),
                Text.literal("Oxidation"),
                // Enforce List<Text> type
                List.of(
                        Text.literal("Iron Ingots are unstable in nature."),
                        Text.empty(),
                        Text.literal("Drop an Iron Ingot into water").formatted(Formatting.AQUA),
                        Text.literal("and wait 60 seconds to convert it"),
                        Text.literal("into Copper.")
                ).stream().map(text -> (Text) text).collect(Collectors.toList())
        );

        // 4. Vinegar Cauldron
        registry.registerEntryInfo( // <-- The last known method name for this API version
                EntryIngredients.of(ModItems.VINEGAR),
                Text.literal("Preservation"),
                // Enforce List<Text> type
                List.of(
                        Text.literal("Used to pickle food."),
                        Text.empty(),
                        Text.literal("Right-click a Water Cauldron").formatted(Formatting.YELLOW),
                        Text.literal("with Vinegar to prepare it."),
                        Text.literal("Then drop raw food inside.")
                ).stream().map(text -> (Text) text).collect(Collectors.toList())
        );
    }
}