package sh.lunar.alchemica.event;

import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import sh.lunar.alchemica.item.ModItems;

public class AllowChatMessageHandler implements ServerMessageEvents.AllowChatMessage,
                                                ClientSendMessageEvents.AllowCommand {

    @Override
    public boolean allowChatMessage(SignedMessage message, ServerPlayerEntity sender, MessageType.Parameters params) {
        if (sender instanceof ServerPlayerEntity) {
            boolean allow = sender.getInventory().contains(ModItems.ANTENNA.getDefaultStack());
            if (allow) return true;
            sender.sendMessage(Text.translatable("chat.alchemica.antenna_needed"), true);
        }
        return false;
    }

    @Override
    public boolean allowSendCommandMessage(String command) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return false;
        if (command.startsWith("say") || command.startsWith("me") ||command.startsWith("msg")) {
            if(!client.player.getInventory().contains(ModItems.ANTENNA.getDefaultStack())) {
                client.player.sendMessage(Text.translatable("chat.alchemica.antenna_needed"), true);
                return false;
            }
        }
        return true;
    }
}
