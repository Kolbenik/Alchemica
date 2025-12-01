/*
 * Copyright (c) 2025 Lunar_sh, Kolbenik
 *
 * All rights reserved.
 */

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

public class BioAlchemyCategory implements IRecipeCategory<BioAlchemyRecipeWrapper> {

    public static final RecipeType<BioAlchemyRecipeWrapper> TYPE = RecipeType.create(Alchemica.MOD_ID, "bio_alchemy", BioAlchemyRecipeWrapper.class);

    private final IDrawable background;
    private final IDrawable icon;

    public BioAlchemyCategory(IGuiHelper helper) {
        // Same Brewing Stand background (it looks alchemy-like)
        this.background = helper.createDrawable(new Identifier("minecraft", "textures/gui/container/brewing_stand.png"), 10, 10, 120, 50);
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
    public void draw(BioAlchemyRecipeWrapper recipe, IRecipeSlotsView recipeSlotsView, DrawContext context, double mouseX, double mouseY) {
        background.draw(context, 0, 0);

        // Warning Text
        context.drawText(net.minecraft.client.MinecraftClient.getInstance().textRenderer,
                Text.literal("SACRIFICE").formatted(Formatting.RED).formatted(Formatting.BOLD), 35, 35, 0xFF0000, false);
    }

    @Override
    @SuppressWarnings("removal") // <--- Silences the warnings!
    public void setRecipe(IRecipeLayoutBuilder builder, BioAlchemyRecipeWrapper recipe, IFocusGroup focuses) {
        // 1. The Item (Ash or Emerald)
        builder.addSlot(RecipeIngredientRole.INPUT, 10, 10)
                .addItemStack(new ItemStack(recipe.input()));

        // 2. The Player (Represented by a Player Head)
        builder.addSlot(RecipeIngredientRole.INPUT, 30, 10)
                .addItemStack(new ItemStack(Items.PLAYER_HEAD))
                .addTooltipCallback((view, list) -> {
                    list.add(Text.literal("You (The Player)").formatted(Formatting.RED));
                    list.add(Text.literal("Step into the boiling cauldron.").formatted(Formatting.GRAY));
                });

        // 3. The Stick (Stirring)
        builder.addSlot(RecipeIngredientRole.CATALYST, 50, 10)
                .addItemStack(new ItemStack(Items.STICK));

        // 4. The Result
        builder.addSlot(RecipeIngredientRole.OUTPUT, 90, 10)
                .addItemStack(new ItemStack(recipe.output()));
    }
}
