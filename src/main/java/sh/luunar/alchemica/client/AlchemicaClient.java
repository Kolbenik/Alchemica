/*
 * Copyright (c) 2025 Lunar_sh, Kolbenik
 *
 * All rights reserved.
 */

package sh.luunar.alchemica.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import sh.luunar.alchemica.entity.ModEntities;
import sh.luunar.alchemica.event.AllowChatMessageHandler;
import sh.luunar.alchemica.item.ModItems;
import sh.luunar.alchemica.networking.ModMessages;

public class AlchemicaClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // 1. Register Client Networking (S2C Packets)
        // This was the cause of your crash! It belongs here.
        ModMessages.registerS2CMessages();

        // 2. Register Chat Handlers (Antenna/Drunk blocking)
        AllowChatMessageHandler chatHandler = new AllowChatMessageHandler();
        ClientSendMessageEvents.ALLOW_CHAT.register(chatHandler);
        ClientSendMessageEvents.ALLOW_COMMAND.register(chatHandler);

        // 3. Visuals (Shaders, Renderers, Colors)
        ClientTickEvents.END_CLIENT_TICK.register(new DrunkShaderHandler());
        EntityRendererRegistry.register(ModEntities.SPLASH_MILK_ENTITY, FlyingItemEntityRenderer::new);

        // Colors
        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> tintIndex == 1 ? 0xFFFFFF : -1, ModItems.SPLASH_MILK);
        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> 0xB87333, ModItems.COPPER_DUST);
        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> 0xD8D8D8, ModItems.IRON_DUST);
        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> 0x4AEDD9, ModItems.DIAMOND_DUST);
    }
}