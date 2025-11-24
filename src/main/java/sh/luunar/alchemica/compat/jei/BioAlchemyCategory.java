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
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import sh.luunar.alchemica.Alchemica;

public class BioAlchemyCategory implements IRecipeCategory<BioAlchemyRecipeWrapper> {

    public static final RecipeType<BioAlchemyRecipeWrapper> TYPE = RecipeType.create(Alchemica.MOD_ID, "bio_alchemy", BioAlchemyRecipeWrapper.class);

    private final IDrawable background;
    private final IDrawable icon;

    public BioAlchemyCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(new Identifier("jei", "textures/gui/gui_vanilla.png"), 0, 220, 120, 40);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(Blocks.SOUL_CAMPFIRE));
    }

    @Override
    public RecipeType<BioAlchemyRecipeWrapper> getRecipeType() {
        return TYPE;
    }

    @Override
    public Text getTitle() {
        return Text.literal("Forbidden Rituals").formatted(Formatting.DARK_RED);
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
    public void setRecipe(IRecipeLayoutBuilder builder, BioAlchemyRecipeWrapper recipe, IFocusGroup focuses) {
        // Input Item
        builder.addSlot(RecipeIngredientRole.INPUT, 10, 11)
                .addItemStack(new ItemStack(recipe.input()));

        // Stick (Catalyst)
        builder.addSlot(RecipeIngredientRole.CATALYST, 30, 11)
                .addItemStack(new ItemStack(Items.STICK));

        // Output
        builder.addSlot(RecipeIngredientRole.OUTPUT, 90, 11)
                .addItemStack(new ItemStack(recipe.output()));
    }
}