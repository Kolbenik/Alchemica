package sh.luunar.alchemica.client;

import net.minecraft.item.ItemStack;
import sh.luunar.alchemica.item.ModItems;

public class ItemTextureLogic {

    public static String getUrl(ItemStack stack) {

        // Logik für Item 1
        if (stack.getItem() == ModItems.CREDIT_KOLBENIK) {
            //if (stack.getDamage() > 10) {
            //    return "https://raw.githubusercontent.com/MyUser/MyRepo/main/broken_sword.png";
            //}
            return "http://192.168.178.34:83/files/credit_kolbenik.png";
        }

        //// Logik für Item 2
        //if (stack.getItem() == ModItems.ITEM_EINS) {
        //    if (stack.hasCustomName() && stack.getName().getString().equals("Geheim")) {
        //        return "https://raw.githubusercontent.com/MyUser/MyRepo/main/secret.png";
        //    }
        //    return "https://raw.githubusercontent.com/MyUser/MyRepo/main/default.png";
        //}

        return null;
    }
}