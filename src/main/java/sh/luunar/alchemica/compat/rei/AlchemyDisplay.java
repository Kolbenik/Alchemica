package sh.luunar.alchemica.compat.rei;

import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import sh.luunar.alchemica.recipe.AlchemyRecipe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class AlchemyDisplay extends BasicDisplay {

    public AlchemyDisplay(AlchemyRecipe recipe) {
        super(getInputList(recipe), List.of(EntryIngredients.of(recipe.getOutput(null))), Optional.ofNullable(recipe.getId()));
    }

    // Helper to convert Minecraft Ingredients to REI Ingredients
    private static List<EntryIngredient> getInputList(AlchemyRecipe recipe) {
        if (recipe == null) return Collections.emptyList();
        List<EntryIngredient> list = new ArrayList<>();
        for (Ingredient ingredient : recipe.getIngredients()) {
            list.add(EntryIngredients.ofIngredient(ingredient));
        }
        return list;
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return AlchemicaREIPlugin.ALCHEMY;
    }
}