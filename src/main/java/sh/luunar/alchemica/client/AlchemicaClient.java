package sh.luunar.alchemica.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import sh.luunar.alchemica.client.render.WebItemRenderer;
import sh.luunar.alchemica.item.ModItems;

public class AlchemicaClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        WebItemRenderer renderer = new WebItemRenderer();

        // Für beide Items registrieren
        BuiltinItemRendererRegistry.INSTANCE.register(ModItems.CREDIT_KOLBENIK, renderer);
    }
}
