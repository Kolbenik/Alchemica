/*
 * Copyright (c) 2025 Lunar_sh, Kolbenik
 *
 * All rights reserved.
 */

package sh.luunar.alchemica.event;

import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import sh.luunar.alchemica.effect.ModEffects;
import sh.luunar.alchemica.item.ModItems;

public class AllowChatMessageHandler implements ClientSendMessageEvents.AllowChat, ClientSendMessageEvents.AllowCommand {

    @Override
    public boolean allowSendChatMessage(String message) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return true;

        // 1. Antenna Check
        if (!client.player.getInventory().contains(ModItems.ANTENNA.getDefaultStack())) {
            client.player.sendMessage(Text.translatable("chat.alchemica.antenna_needed"), true);
            return false;
        }

        // 2. Drunk Check
        if (client.player.hasStatusEffect(ModEffects.DRUNK)) {
            if (client.player.getStatusEffect(ModEffects.DRUNK).getAmplifier() >= 2) {
                client.player.sendMessage(Text.literal("You are too drunk to speak properly..."), true);
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean allowSendCommandMessage(String command) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return false;
        if (command.startsWith("say") || command.startsWith("me") || command.startsWith("msg") || command.startsWith("tellraw") || command.startsWith("tell")) {
            if(!client.player.getInventory().contains(ModItems.ANTENNA.getDefaultStack())) {
                client.player.sendMessage(Text.translatable("chat.alchemica.antenna_needed"), true);
                return false;
            }
            if (client.player.hasStatusEffect(ModEffects.DRUNK)) {
                if (client.player.getStatusEffect(ModEffects.DRUNK).getAmplifier() >= 2) {
                    client.player.sendMessage(Text.literal("You are too drunk to speak properly..."), true);
                    return false;
                }
            }
        }

        return true;
    }
}
