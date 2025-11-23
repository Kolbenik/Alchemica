package sh.luunar.alchemica.client;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.option.Perspective;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.Identifier;
import sh.luunar.alchemica.mixin.GameRendererAccessor;

public class DrunkShaderHandler implements ClientTickEvents.EndTick {

    // "blobs2" = The LSD Shader (Colors + Wobbly deformation)
    private static final Identifier DRUNK_SHADER = new Identifier("shaders/post/blobs2.json");

    @Override
    public void onEndTick(MinecraftClient client) {
        if (client.player == null || client.world == null || client.gameRenderer == null) return;

        boolean isDrunk = client.player.hasStatusEffect(StatusEffects.NAUSEA);

        // Get the current active shader (using our new Accessor)
        PostEffectProcessor activeShader = ((GameRendererAccessor) client.gameRenderer).alchemica$getPostProcessor();

        if (isDrunk) {
            // 1. FORCE FIRST PERSON
            // If they try to F5, snap them back immediately.
            if (!client.options.getPerspective().isFirstPerson()) {
                client.options.setPerspective(Perspective.FIRST_PERSON);
            }

            // 2. FORCE SHADER
            // If the shader is missing (null), LOAD IT.
            // This fixes the "F5 disables shader" bug because the next tick will see it's null and reload it.
            if (activeShader == null) {
                ((GameRendererAccessor) client.gameRenderer).alchemica$loadPostProcessor(DRUNK_SHADER);
            }
        }
        else {
            // SOBER CLEANUP
            // If we are sober but the shader is still ON, turn it off.
            // (We check if it's not null to avoid spamming disable)
            if (activeShader != null) {
                // Note: This turns off ANY shader. If you use other shaders (like glowing entities),
                // we might need a more complex check, but for now, this ensures the trip ends.
                client.gameRenderer.disablePostProcessor();
            }
        }
    }
}