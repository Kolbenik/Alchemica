package sh.luunar.alchemica.compat.rei;

import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.List;

public class BioAlchemyDisplay extends BasicDisplay {

    public BioAlchemyDisplay(Item input, Item output) {
        // Inputs: The Item + A "Stick" (to show interaction)
        super(List.of(
                EntryIngredients.of(input),
                EntryIngredients.of(net.minecraft.item.Items.STICK)
        ), List.of(
                EntryIngredients.of(output)
        ));
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return AlchemicaREIPlugin.BIO_ALCHEMY;
    }
}