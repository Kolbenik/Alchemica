/*
 * Copyright (c) 2025 Lunar_sh, Kolbenik
 *
 * All rights reserved.
 */

package sh.luunar.alchemica.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import sh.luunar.alchemica.entity.ModEntities;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import sh.luunar.alchemica.item.ModItems;

public class AlchemicaClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // 1. Drunk Shader
        ClientTickEvents.END_CLIENT_TICK.register(new DrunkShaderHandler());

        // 2. Antenna Broadcast (NEW)
        ClientTickEvents.END_CLIENT_TICK.register(new AntennaSignalHandler());

        EntityRendererRegistry.register(ModEntities.SPLASH_MILK_ENTITY, FlyingItemEntityRenderer::new);

        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> {
            // Tint index 1 is the liquid overlay. Return WHITE (0xFFFFFF).
            // Tint index 0 is the bottle glass. Return -1 (No Tint).
            return tintIndex == 1 ? 0xFFFFFF : -1;
        }, ModItems.SPLASH_MILK);

        // 2. DUST COLORS (Layer 0)
        // Copper Dust -> Orange
        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> 0xB87333, ModItems.COPPER_DUST);

        // Iron Dust -> Light Grey/Iron color
        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> 0xD8D8D8, ModItems.IRON_DUST);

        // Diamond Dust -> Cyan
        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> 0x4AEDD9, ModItems.DIAMOND_DUST);
    }
}
