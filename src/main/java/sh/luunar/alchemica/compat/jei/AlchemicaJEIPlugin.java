/*
 * Copyright (c) 2025 Lunar_sh, Kolbenik
 *
 * All rights reserved.
 */

package sh.luunar.alchemica.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import sh.luunar.alchemica.Alchemica;
import sh.luunar.alchemica.block.ModBlocks;
import sh.luunar.alchemica.item.ModItems;
import sh.luunar.alchemica.recipe.AlchemyRecipe;
import sh.luunar.alchemica.recipe.ModRecipes;

import java.util.List;

@JeiPlugin
public class AlchemicaJEIPlugin implements IModPlugin {

    public static final Identifier PLUGIN_ID = new Identifier(Alchemica.MOD_ID, "jei_plugin");

    @Override
    public Identifier getPluginUid() {
        return PLUGIN_ID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new AlchemyCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new BioAlchemyCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        MinecraftClient client = MinecraftClient.getInstance();

        // 1. JSON Alchemy Recipes
        if (client.world != null) {
            RecipeManager rm = client.world.getRecipeManager();
            List<AlchemyRecipe> alchemyRecipes = rm.listAllOfType(ModRecipes.ALCHEMY_TYPE);
            registration.addRecipes(AlchemyCategory.TYPE, alchemyRecipes);
        }

        // 2. Forbidden Rituals (Hardcoded)
        registration.addRecipes(BioAlchemyCategory.TYPE, List.of(
                new BioAlchemyRecipeWrapper(ModItems.ALCHEMICAL_ASH, ModItems.SOUL_ASH),
                new BioAlchemyRecipeWrapper(Items.EMERALD, Items.TOTEM_OF_UNDYING)
        ));

        // 3. Information Tabs
        addItemInfo(registration, new ItemStack(ModItems.ALCHEMICAL_ASH),
                "Obtained as a byproduct of failed Alchemy.",
                "Any transmutation has a 50% chance to fail, leaving only this ash behind.");

        addItemInfo(registration, new ItemStack(ModBlocks.RUSTED_IRON_BLOCK),
                "Created by chemically corroding an Iron Block.",
                "Right-click an Iron Block with Alchemical Ash to trigger the reaction.");

        addItemInfo(registration, new ItemStack(Items.COPPER_INGOT),
                "Oxidation:",
                "Iron Ingots are unstable. Drop an Iron Ingot into water and wait 60 seconds to convert it into Copper.");

        addItemInfo(registration, new ItemStack(ModItems.VINEGAR),
                "Preservation:",
                "Used to pickle food. Right-click a Water Cauldron with Vinegar to prepare it, then drop raw food inside.");
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(Blocks.CAULDRON), AlchemyCategory.TYPE, BioAlchemyCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(Blocks.WATER_CAULDRON), AlchemyCategory.TYPE, BioAlchemyCategory.TYPE);
    }

    // --- THE FIXED HELPER METHOD ---
    private void addItemInfo(IRecipeRegistration reg, ItemStack stack, String... lines) {
        // We join the lines with a standard newline character
        reg.addIngredientInfo(stack, VanillaTypes.ITEM_STACK,
                Text.literal(String.join("\n", lines)).formatted(Formatting.GRAY));
    }
}