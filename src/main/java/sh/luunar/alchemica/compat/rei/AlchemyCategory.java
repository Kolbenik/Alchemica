package sh.luunar.alchemica.compat.rei;

import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks; // <--- CHANGED IMPORT
import net.minecraft.block.Blocks;
import net.minecraft.text.Text;

import java.util.LinkedList;
import java.util.List;

public class AlchemyCategory implements DisplayCategory<AlchemyDisplay> {

    @Override
    public Renderer getIcon() {
        // FIX: Use EntryStacks.of() for icons/renderers
        return EntryStacks.of(Blocks.CAULDRON);
    }

    @Override
    public Text getTitle() {
        return Text.literal("Cauldron Alchemy");
    }

    @Override
    public CategoryIdentifier<? extends AlchemyDisplay> getCategoryIdentifier() {
        return AlchemicaREIPlugin.ALCHEMY;
    }

    @Override
    public List<Widget> setupDisplay(AlchemyDisplay display, Rectangle bounds) {
        Point startPoint = new Point(bounds.getCenterX() - 41, bounds.getCenterY() - 13);
        List<Widget> widgets = new LinkedList<>();

        // 1. The Background
        widgets.add(Widgets.createRecipeBase(bounds));

        // 2. The Arrow
        widgets.add(Widgets.createArrow(new Point(startPoint.x + 27, startPoint.y + 4)));

        // 3. The Result Slot
        widgets.add(Widgets.createResultSlotBackground(new Point(startPoint.x + 61, startPoint.y + 5)));
        widgets.add(Widgets.createSlot(new Point(startPoint.x + 61, startPoint.y + 5))
                .entries(display.getOutputEntries().get(0))
                .disableBackground()
                .markOutput());

        // 4. The Input Slots
        int i = 0;
        for (var ingredient : display.getInputEntries()) {
            widgets.add(Widgets.createSlot(new Point(startPoint.x + (i * 18), startPoint.y + 5))
                    .entries(ingredient)
                    .markInput());
            i++;
        }

        return widgets;
    }
}