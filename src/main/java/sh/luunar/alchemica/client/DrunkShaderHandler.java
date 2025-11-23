package sh.luunar.alchemica.client;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.Identifier;
import sh.luunar.alchemica.mixin.GameRendererAccessor; // <--- Import this!

public class DrunkShaderHandler implements ClientTickEvents.EndTick {

    private static final Identifier DRUNK_SHADER = new Identifier("shaders/post/wobble.json");
    private boolean wasDrunk = false;

    @Override
    public void onEndTick(MinecraftClient client) {
        if (client.player == null || client.world == null) return;

        boolean isDrunk = client.player.hasStatusEffect(StatusEffects.NAUSEA);

        if (isDrunk && !wasDrunk) {
            loadShader(client);
        }
        else if (!isDrunk && wasDrunk) {
            unloadShader(client);
        }

        wasDrunk = isDrunk;
    }

    private void loadShader(MinecraftClient client) {
        if (client.gameRenderer != null) {
            // --- THE FIX ---
            // Cast to the Accessor and call the Invoker method
            ((GameRendererAccessor) client.gameRenderer).alchemica$loadPostProcessor(DRUNK_SHADER);
        }
    }

    private void unloadShader(MinecraftClient client) {
        if (client.gameRenderer != null) {
            client.gameRenderer.disablePostProcessor();
        }
    }
}