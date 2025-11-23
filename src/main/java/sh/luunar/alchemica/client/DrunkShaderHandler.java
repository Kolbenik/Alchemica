package sh.luunar.alchemica.client;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.Perspective;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.Identifier;
import sh.luunar.alchemica.mixin.GameRendererAccessor;

public class DrunkShaderHandler implements ClientTickEvents.EndTick {

    // "blobs2" is the trippiest vanilla shader.
    // It shifts colors and deforms the screen like a lava lamp.
    private static final Identifier DRUNK_SHADER = new Identifier("shaders/post/blobs2.json");

    private boolean wasDrunk = false;

    @Override
    public void onEndTick(MinecraftClient client) {
        if (client.player == null || client.world == null) return;

        boolean isDrunk = client.player.hasStatusEffect(StatusEffects.NAUSEA);

        // --- 1. FORCE FIRST PERSON ---
        // If drunk, banish 3rd Person View.
        if (isDrunk) {
            if (!client.options.getPerspective().isFirstPerson()) {
                client.options.setPerspective(Perspective.FIRST_PERSON);
            }
        }

        // --- 2. SHADER LOGIC ---
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
            // Using our Accessor Mixin to load the shader
            ((GameRendererAccessor) client.gameRenderer).alchemica$loadPostProcessor(DRUNK_SHADER);
        }
    }

    private void unloadShader(MinecraftClient client) {
        if (client.gameRenderer != null) {
            client.gameRenderer.disablePostProcessor();
        }
    }
}