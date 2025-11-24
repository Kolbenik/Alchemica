package sh.luunar.alchemica.compat.jei;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import sh.luunar.alchemica.Alchemica;
import sh.luunar.alchemica.recipe.AlchemyRecipe;

public class AlchemyCategory implements IRecipeCategory<AlchemyRecipe> {

    public static final RecipeType<AlchemyRecipe> TYPE = RecipeType.create(Alchemica.MOD_ID, "alchemy", AlchemyRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;

    public AlchemyCategory(IGuiHelper helper) {
        // USE VANILLA TEXTURE (Safe & Thematic)
        // We use the Brewing Stand GUI texture.
        // U:0, V:0, Width:150, Height:60 (Arbitrary slice of the grey background)
        this.background = helper.createDrawable(new Identifier("minecraft", "textures/gui/container/brewing_stand.png"), 10, 10, 120, 50);
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
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public int getWidth() {
        return background.getWidth();
    }

    @Override
    public int getHeight() {
        return background.getHeight();
    }

    @Override
    public void draw(AlchemyRecipe recipe, IRecipeSlotsView recipeSlotsView, DrawContext context, double mouseX, double mouseY) {
        background.draw(context, 0, 0);

        // Draw "Stir" hint
        context.drawText(net.minecraft.client.MinecraftClient.getInstance().textRenderer,
                Text.literal("Stir").formatted(Formatting.DARK_GRAY), 48, 35, 0xFF404040, false);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, AlchemyRecipe recipe, IFocusGroup focuses) {
        // Adjust coordinates to fit new background

        // Input 1
        if (recipe.getIngredients().size() > 0) {
            builder.addSlot(RecipeIngredientRole.INPUT, 10, 10)
                    .addIngredients(recipe.getIngredients().get(0));
        }
        // Input 2
        if (recipe.getIngredients().size() > 1) {
            builder.addSlot(RecipeIngredientRole.INPUT, 30, 10)
                    .addIngredients(recipe.getIngredients().get(1));
        }

        // Stick Icon
        builder.addSlot(RecipeIngredientRole.CATALYST, 52, 10)
                .addItemStack(new ItemStack(Items.STICK));

        // Output
        builder.addSlot(RecipeIngredientRole.OUTPUT, 90, 10)
                .addItemStack(recipe.getOutput(null));
    }
}