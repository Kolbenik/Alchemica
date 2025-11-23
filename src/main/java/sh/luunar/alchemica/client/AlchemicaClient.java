package sh.luunar.alchemica.client;

import net.fabricmc.api.ClientModInitializer;

public class AlchemicaClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Empty! We are NOT registering any ColorProviders anymore.
        // This ensures Iron Ingots render normally.
    }
}