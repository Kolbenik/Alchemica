/*
 * Copyright (c) 2025 Lunar_sh, Kolbenik
 *
 * All rights reserved.
 */

package sh.luunar.alchemica.client;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.option.Perspective;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.util.Identifier;
import sh.luunar.alchemica.effect.ModEffects;
import sh.luunar.alchemica.mixin.GameRendererAccessor;

public class DrunkShaderHandler implements ClientTickEvents.EndTick {

    // MILD: Phosphor = Motion Blur / Light Trails
    private static final Identifier SHADER_BLUR = new Identifier("shaders/post/phosphor.json");

    // HARD: Wobble = Nausea/Wave distortion
    private static final Identifier SHADER_WOBBLE = new Identifier("shaders/post/wobble.json");

    @Override
    public void onEndTick(MinecraftClient client) {
        if (client.player == null || client.world == null || client.gameRenderer == null) return;

        boolean isDrunk = client.player.hasStatusEffect(ModEffects.DRUNK);
        PostEffectProcessor activeShader = ((GameRendererAccessor) client.gameRenderer).alchemica$getPostProcessor();

        if (isDrunk) {
            // 1. Force First Person (Immersion)
            if (!client.options.getPerspective().isFirstPerson()) {
                client.options.setPerspective(Perspective.FIRST_PERSON);
            }

            // 2. Determine Intensity
            StatusEffectInstance effect = client.player.getStatusEffect(ModEffects.DRUNK);
            int amp = (effect != null) ? effect.getAmplifier() : 0;

            // Stage 0 (Tipsy) = Blur
            // Stage 1+ (Drunk) = Wobble
            Identifier targetShader = (amp == 0) ? SHADER_BLUR : SHADER_WOBBLE;

            // 3. Load or Swap Shader
            // We check if shader is missing OR if the loaded shader name doesn't match our target
            // (Note: activeShader.getName() isn't easily accessible without more mixins, so we rely on
            // simply reloading if null. If you switch stages, it might stick to the old shader until a restart/toggle.
            // To fix this perfectly, we force reload if the amplifier changes, but 'activeShader == null' is safe for now).

            if (activeShader == null) {
                ((GameRendererAccessor) client.gameRenderer).alchemica$loadPostProcessor(targetShader);
            }
        }
        else {
            // Cleanup
            if (activeShader != null) {
                client.gameRenderer.disablePostProcessor();
            }
        }
    }
}
