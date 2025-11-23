package sh.luunar.alchemica.compat.rei;

import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.block.Blocks;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.LinkedList;
import java.util.List;

public class BioAlchemyCategory implements DisplayCategory<BioAlchemyDisplay> {

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(Blocks.SOUL_CAMPFIRE);
    }

    @Override
    public Text getTitle() {
        return Text.literal("Forbidden Rituals").formatted(Formatting.DARK_RED);
    }

    @Override
    public CategoryIdentifier<? extends BioAlchemyDisplay> getCategoryIdentifier() {
        return AlchemicaREIPlugin.BIO_ALCHEMY;
    }

    @Override
    public List<Widget> setupDisplay(BioAlchemyDisplay display, Rectangle bounds) {
        Point startPoint = new Point(bounds.getCenterX() - 41, bounds.getCenterY() - 13);
        List<Widget> widgets = new LinkedList<>();

        // Background
        widgets.add(Widgets.createRecipeBase(bounds));

        // Input (The Ingredient)
        widgets.add(Widgets.createSlot(new Point(startPoint.x, startPoint.y + 5))
                .entries(display.getInputEntries().get(0))
                .markInput());

        // Stick Icon (To show interaction)
        widgets.add(Widgets.createSlot(new Point(startPoint.x, startPoint.y - 14))
                .entries(display.getInputEntries().get(1))
                .disableBackground()
                .markInput());

        // Arrow
        widgets.add(Widgets.createArrow(new Point(startPoint.x + 27, startPoint.y + 4)));

        // Result
        widgets.add(Widgets.createResultSlotBackground(new Point(startPoint.x + 61, startPoint.y + 5)));
        widgets.add(Widgets.createSlot(new Point(startPoint.x + 61, startPoint.y + 5))
                .entries(display.getOutputEntries().get(0))
                .disableBackground()
                .markOutput());

        // WARNING TEXT (The important part)
        widgets.add(Widgets.createLabel(new Point(bounds.getCenterX(), bounds.getMaxY() - 10),
                        Text.literal("Requires Self-Sacrifice").formatted(Formatting.RED))
                .noShadow()
                .color(0xFF8B0000, 0xFF8B0000));

        return widgets;
    }
}