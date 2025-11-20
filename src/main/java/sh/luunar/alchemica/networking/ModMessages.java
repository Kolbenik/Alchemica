package sh.luunar.alchemica.networking;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.util.Identifier;
import sh.luunar.alchemica.Alchemica;
import sh.luunar.alchemica.networking.paket.TestS2CPacket;

public class ModMessages {

    public static final Identifier TEST_ID = new Identifier("alchemica", "test");

    public static void registerC2SMessages() {
        Alchemica.LOGGER.info("Registering C2S Messages for " + Alchemica.MOD_ID);

    }

    public static void registerS2CMessages() {
        Alchemica.LOGGER.info("Registering S2C Messages for " + Alchemica.MOD_ID);

        ClientPlayNetworking.registerGlobalReceiver(TEST_ID, TestS2CPacket::receive);
    }
}
