package sh.luunar.alchemica.networking.paket;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import sh.luunar.alchemica.Alchemica;

public class TestS2CPacket {

    public static void receive(MinecraftClient client,
                               ClientPlayNetworkHandler handler,
                               PacketByteBuf buf,
                               PacketSender sender) {

        int test  = buf.readInt();
        Alchemica.LOGGER.info("Received S2C Packet with test: " + test);
    }
}
