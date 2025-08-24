package sh.lunar.alchemica;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sh.lunar.alchemica.block.ModBlocks;
import sh.lunar.alchemica.event.AllowChatMessageHandler;
import sh.lunar.alchemica.item.ModItemGroup;
import sh.lunar.alchemica.item.ModItems;
import sh.lunar.alchemica.networking.ModMessages;

public class Alchemica  implements ModInitializer {
    public static final String MOD_ID = "alchemica";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ModItemGroup.registerModItemGroups();
        ModItems.registerModItems();
        ModBlocks.registerModBlocks();
        ModMessages.registerS2CMessages();

        ServerMessageEvents.ALLOW_CHAT_MESSAGE.register(new AllowChatMessageHandler());
        ClientSendMessageEvents.ALLOW_COMMAND.register(new AllowChatMessageHandler());
    }
}
