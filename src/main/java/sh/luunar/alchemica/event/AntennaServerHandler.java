package sh.luunar.alchemica.event;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList; // <--- FIXED IMPORT
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.structure.Structure;
import sh.luunar.alchemica.Alchemica;
import sh.luunar.alchemica.item.ModItems;

import java.util.Objects;
import java.util.Optional;
import java.util.Random;

public class AntennaServerHandler implements ServerTickEvents.EndTick {

    private final Random random = new Random();
    private int ticker = 0;

    @Override
    public void onEndTick(MinecraftServer server) {
        // Run logic every 5 seconds (100 ticks)
        ticker++;
        if (ticker < 100) return;
        ticker = 0;

        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            checkPlayer(player);
        }
    }

    private void checkPlayer(ServerPlayerEntity player) {
        // 1. Conditions: Antenna + Thunder
        boolean hasAntenna = player.getMainHandStack().isOf(ModItems.ANTENNA) ||
                player.getOffHandStack().isOf(ModItems.ANTENNA);

        if (!hasAntenna || !player.getWorld().isThundering()) return;

        // 2. Roll Chance (20% chance every 5 seconds)
        if (random.nextFloat() > 0.2f) return;

        // 3. LOCATE REAL STRUCTURE
        ServerWorld world = player.getServerWorld();
        BlockPos playerPos = player.getBlockPos();

        // Get the structure registry
        var structureRegistry = world.getRegistryManager().get(RegistryKeys.STRUCTURE);

        // Find our specific structure by ID
        Identifier structId = new Identifier(Alchemica.MOD_ID, "anomaly_chest");
        Optional<RegistryEntry.Reference<Structure>> structureEntry = structureRegistry.getEntry(net.minecraft.registry.RegistryKey.of(RegistryKeys.STRUCTURE, structId));

        if (structureEntry.isPresent()) {
            // Locate it! (Radius 100 chunks)
            // FIXED: Used RegistryEntryList.of() instead of HolderSet.createDirect()
            BlockPos targetPos = Objects.requireNonNull(world.getChunkManager().getChunkGenerator()
                    .locateStructure(world, RegistryEntryList.of(structureEntry.get()), playerPos, 100, false)).getFirst();

            if (targetPos != null) {
                sendSignal(player, targetPos);
            }
        }
    }

    private void sendSignal(ServerPlayerEntity player, BlockPos target) {
        String[] messages = {
                "ANOMALY DETECTED", "SIGNAL LOCK", "THEY ARE HERE", "CACHE FOUND"
        };
        String msg = messages[random.nextInt(messages.length)];
        int dist = (int) Math.sqrt(player.getBlockPos().getSquaredDistance(target));

        Text chatMessage = Text.literal("⏚ ")
                .formatted(Formatting.DARK_AQUA)
                .append(Text.literal("[" + target.getX() + ", " + target.getZ() + "]").formatted(Formatting.YELLOW))
                .append(Text.literal(" (" + dist + "m) :: ").formatted(Formatting.DARK_GRAY))
                .append(Text.literal(msg).formatted(Formatting.GRAY, Formatting.ITALIC));

        player.sendMessage(chatMessage, false);
        player.playSound(SoundEvents.BLOCK_BEACON_AMBIENT, SoundCategory.AMBIENT, 1.0f, 0.5f);
    }
}