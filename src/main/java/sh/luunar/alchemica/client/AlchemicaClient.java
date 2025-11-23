package sh.luunar.alchemica.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.item.Items;
import sh.luunar.alchemica.util.RustUtils;

public class AlchemicaClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Register Color Provider for Iron Ingot and Iron Block Items
        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> {
            return RustUtils.getItemColor(stack, tintIndex);
        }, Items.IRON_INGOT, Items.IRON_BLOCK);
    }
}