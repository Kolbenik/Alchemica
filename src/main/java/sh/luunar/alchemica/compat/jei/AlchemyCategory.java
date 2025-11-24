package sh.luunar.alchemica.compat.jei;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import sh.luunar.alchemica.Alchemica;
import sh.luunar.alchemica.recipe.AlchemyRecipe;

public class AlchemyCategory implements IRecipeCategory<AlchemyRecipe> {

    public static final RecipeType<AlchemyRecipe> TYPE = RecipeType.create(Alchemica.MOD_ID, "alchemy", AlchemyRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;

    public AlchemyCategory(IGuiHelper helper) {
        // A standard grey background (width 120, height 40)
        this.background = helper.createDrawable(new Identifier("jei", "textures/gui/gui_vanilla.png"), 0, 220, 120, 40);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(Blocks.CAULDRON));
    }

    @Override
    public RecipeType<AlchemyRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public Text getTitle() {
        return Text.literal("Cauldron Alchemy");
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, AlchemyRecipe recipe, IFocusGroup focuses) {
        // Input 1
        if (recipe.getIngredients().size() > 0) {
            builder.addSlot(RecipeIngredientRole.INPUT, 10, 11)
                    .addIngredients(recipe.getIngredients().get(0));
        }

        // Input 2
        if (recipe.getIngredients().size() > 1) {
            builder.addSlot(RecipeIngredientRole.INPUT, 30, 11)
                    .addIngredients(recipe.getIngredients().get(1));
        }

        // Output
        builder.addSlot(RecipeIngredientRole.OUTPUT, 90, 11)
                .addItemStack(recipe.getOutput(null));
    }
}