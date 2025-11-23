package sh.luunar.alchemica.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class AlchemicaClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Register the Drunk Shader Logic
        ClientTickEvents.END_CLIENT_TICK.register(new DrunkShaderHandler());
    }
}