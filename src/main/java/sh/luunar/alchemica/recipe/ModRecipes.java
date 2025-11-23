package sh.luunar.alchemica.recipe;

import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import sh.luunar.alchemica.Alchemica;

public class ModRecipes {
    public static final RecipeType<AlchemyRecipe> ALCHEMY_TYPE = Registry.register(Registries.RECIPE_TYPE,
            new Identifier(Alchemica.MOD_ID, "alchemy"),
            new RecipeType<AlchemyRecipe>() {
                @Override
                public String toString() {
                    return "alchemy";
                }
            });

    public static final RecipeSerializer<AlchemyRecipe> ALCHEMY_SERIALIZER = Registry.register(Registries.RECIPE_SERIALIZER,
            new Identifier(Alchemica.MOD_ID, "alchemy"),
            new AlchemyRecipe.Serializer());

    public static void registerRecipes() {
        Alchemica.LOGGER.info("Registering Custom Recipes for " + Alchemica.MOD_ID);
    }
}