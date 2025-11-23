package sh.luunar.alchemica.recipe;

import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import sh.luunar.alchemica.item.ModItems;
import sh.luunar.alchemica.util.RotUtils; // Import your Rot Logic

public class PreservationRecipe extends SpecialCraftingRecipe {
    public PreservationRecipe(Identifier id, CraftingRecipeCategory category) {
        super(id, category);
    }

    @Override
    public boolean matches(RecipeInputInventory inventory, World world) {
        boolean hasAlcohol = false;
        boolean hasFood = false;

        // Loop through the 3x3 grid (Shapeless logic)
        for (int i = 0; i < inventory.size(); ++i) {
            ItemStack stack = inventory.getStack(i);
            if (stack.isEmpty()) continue;

            if (stack.isOf(ModItems.ALCOHOL)) {
                if (hasAlcohol) return false; // Only 1 alcohol allowed
                hasAlcohol = true;
            } else if (stack.isFood() && !stack.isOf(Items.ROTTEN_FLESH)) {
                // 1. Check if already preserved
                if (stack.hasNbt() && stack.getNbt().getBoolean("alchemica_preserved")) {
                    return false;
                }

                // 2. CHECK FRESHNESS (New Requirement)
                // If it is 100% rotten (or close to it), you can't save it.
                // We use 0.9f as a buffer, or 1.0f if you want "until the very last second".
                if (RotUtils.getRotPercentage(stack, world) >= 1.0f) {
                    return false; // Too late! It's rotten.
                }

                if (hasFood) return false; // Only 1 food allowed
                hasFood = true;
            } else {
                return false; // No extra items allowed
            }
        }

        return hasAlcohol && hasFood;
    }

    @Override
    public ItemStack craft(RecipeInputInventory inventory, DynamicRegistryManager registryManager) {
        ItemStack foodStack = ItemStack.EMPTY;

        // Find the food item to copy
        for (int i = 0; i < inventory.size(); ++i) {
            ItemStack stack = inventory.getStack(i);
            if (stack.isFood() && !stack.isOf(ModItems.ALCOHOL)) {
                foodStack = stack.copy();
                break;
            }
        }

        if (foodStack.isEmpty()) return ItemStack.EMPTY;

        // Apply the Preservation Tag
        foodStack.setCount(1);
        foodStack.getOrCreateNbt().putBoolean("alchemica_preserved", true);

        // Optional: Reset the Rot Timer so it looks "Clean"
        if(foodStack.getNbt().contains(RotUtils.ROT_KEY)) {
            foodStack.getNbt().remove(RotUtils.ROT_KEY);
        }

        return foodStack;
    }

    @Override
    public boolean fits(int width, int height) {
        // As long as the grid has at least 2 slots (Alcohol + Food), it fits.
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.PRESERVATION_SERIALIZER;
    }
}