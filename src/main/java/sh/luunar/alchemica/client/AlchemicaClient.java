/*
 * Copyright (c) 2025 Lunar_sh, Kolbenik
 *
 * All rights reserved.
 */

package sh.luunar.alchemica.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class AlchemicaClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // 1. Drunk Shader
        ClientTickEvents.END_CLIENT_TICK.register(new DrunkShaderHandler());

        // 2. Antenna Broadcast (NEW)
        ClientTickEvents.END_CLIENT_TICK.register(new AntennaSignalHandler());
    }
}
