package sh.luunar.alchemica.compat.rei;

import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.block.Blocks;
import net.minecraft.util.Identifier;
import sh.luunar.alchemica.Alchemica;
import sh.luunar.alchemica.recipe.AlchemyRecipe;
import sh.luunar.alchemica.recipe.ModRecipes;
import sh.luunar.alchemica.compat.rei.AlchemyCategory;

public class AlchemicaREIPlugin implements REIClientPlugin {

    // Unique ID for our category
    public static final CategoryIdentifier<AlchemyDisplay> ALCHEMY = CategoryIdentifier.of(Alchemica.MOD_ID, "alchemy");

    @Override
    public void registerCategories(CategoryRegistry registry) {
        // Register the UI
        registry.add(new AlchemyCategory());

        // Associate the Cauldron item with this category (so clicking 'U' on a cauldron shows these recipes)
        registry.addWorkstations(ALCHEMY, EntryIngredients.of(Blocks.CAULDRON));
        registry.addWorkstations(ALCHEMY, EntryIngredients.of(Blocks.WATER_CAULDRON));
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        // Automatically find all our JSON recipes and add them to REI
        registry.registerRecipeFiller(AlchemyRecipe.class, ModRecipes.ALCHEMY_TYPE, AlchemyDisplay::new);
    }
}