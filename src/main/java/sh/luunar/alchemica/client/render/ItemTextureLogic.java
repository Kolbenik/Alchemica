package sh.luunar.alchemica.client.render;

import net.minecraft.item.ItemStack;
import sh.luunar.alchemica.item.ModItems;

public class ItemTextureLogic {

    public static String getUrl(ItemStack stack) {

        if (stack.getItem() == ModItems.CREDIT_KOLBENIK) {
            return "http://192.168.178.34:83/files/credit_kolbenik.png";
        }

        return null;
    }
}