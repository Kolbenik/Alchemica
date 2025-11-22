package sh.luunar.alchemica.item.custom;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import sh.luunar.alchemica.item.ModItems;

import java.util.List;

public class CreditItem extends Item {
    public CreditItem(Settings settings) {
        super(settings);
    }

    @Override
    public boolean isFireproof() {
        return true;
    }

    @Override
    public Text getName(ItemStack stack) {
        MutableText name = (MutableText) super.getName(stack);
        if (stack.getItem().equals(ModItems.CREDIT_LUNAR)) {
            name.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6A5ACD)).withFormatting(Formatting.BOLD));
        } else if (stack.getItem().equals(ModItems.CREDIT_KOLBENIK)) {
            name.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x9932CC)).withFormatting(Formatting.BOLD));
        }
        return name;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        if (stack.getItem().equals(ModItems.CREDIT_LUNAR)) {
            tooltip.add(Text.translatable("tooltip.credit_lunar"));
            tooltip.add(Text.translatable("tooltip.credit_lunar.2").setStyle(Style.EMPTY.withFormatting(Formatting.ITALIC, Formatting.GRAY)));
        } else if (stack.getItem().equals(ModItems.CREDIT_KOLBENIK)) {
            tooltip.add(Text.translatable("tooltip.credit_kolbenik"));
            tooltip.add(Text.translatable("tooltip.credit_kolbenik.2").setStyle(Style.EMPTY.withFormatting(Formatting.ITALIC, Formatting.GRAY)));
        }
    }
}
