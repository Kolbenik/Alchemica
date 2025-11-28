package sh.luunar.alchemica.client;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import sh.luunar.alchemica.item.ModItems;

import java.util.Random;

public class AntennaSignalHandler implements ClientTickEvents.EndTick {

    private final Random random = new Random();
    private int cooldown = 0;

    private static final String[] MESSAGES = {
            "THEY ARE WATCHING",
            "DO NOT SLEEP",
            "THE ASH IS ALIVE",
            "SIGNAL LOST...",
            "IT HURTS",
            "NULL POINTER IN REALITY",
            "THE SKY IS LEAKING",
            "01001000 01000101 01001100 01010000" // "HELP" in binary
    };

    @Override
    public void onEndTick(MinecraftClient client) {
        if (client.player == null || client.world == null) return;

        // 1. COOLDOWN (Don't spam chat)
        if (cooldown > 0) {
            cooldown--;
            return;
        }

        // 2. CHECK CONDITIONS
        // Must hold Antenna
        boolean hasAntenna = client.player.getMainHandStack().isOf(ModItems.ANTENNA) ||
                client.player.getOffHandStack().isOf(ModItems.ANTENNA);

        // Must be Thundering (High Energy)
        boolean isStorm = client.world.isThundering();

        if (hasAntenna && isStorm) {
            // 3. ROLL DICE (1 in 200 chance per tick = roughly every 10 seconds)
            if (random.nextInt(200) == 0) {
                receiveSignal(client);
                cooldown = 100; // 5 second buffer
            }
        }
    }

    private void receiveSignal(MinecraftClient client) {
        // 4. GENERATE CONTENT
        // A. Creepy Text
        String message = MESSAGES[random.nextInt(MESSAGES.length)];

        // B. Coordinates (An anomaly nearby?)
        // Pick a random spot within 100 blocks
        int x = (int) client.player.getX() + (random.nextInt(200) - 100);
        int z = (int) client.player.getZ() + (random.nextInt(200) - 100);
        int y = client.world.getSeaLevel() - random.nextInt(40); // Deep underground

        // Format: "⏚ [X, Y, Z] :: MESSAGE"
        Text chatMessage = Text.literal("⏚ ")
                .formatted(Formatting.DARK_AQUA)
                .append(Text.literal("[" + x + ", " + y + ", " + z + "] ").formatted(Formatting.YELLOW))
                .append(Text.literal(":: ").formatted(Formatting.DARK_GRAY))
                .append(Text.literal(message).formatted(Formatting.GRAY, Formatting.ITALIC)); // Obfuscated? .formatted(Formatting.OBFUSCATED) is too hard to read usually

        // 5. PLAY OUTPUT
        client.player.sendMessage(chatMessage, false);

        // Static Sound (Tripwire click pitch-shifted sounds like radio static)
        client.player.playSound(SoundEvents.BLOCK_TRIPWIRE_CLICK_OFF, SoundCategory.AMBIENT, 0.5f, 2.0f);
        client.player.playSound(SoundEvents.BLOCK_BEACON_AMBIENT, SoundCategory.AMBIENT, 0.2f, 0.5f);
    }
}