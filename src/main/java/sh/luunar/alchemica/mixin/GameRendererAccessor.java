/*
 * Copyright (c) 2025 Lunar_sh, Kolbenik
 *
 * All rights reserved.
 */

package sh.luunar.alchemica.mixin;

import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(GameRenderer.class)
public interface GameRendererAccessor {
    // The Invoker (Method caller)
    @Invoker("loadPostProcessor")
    void alchemica$loadPostProcessor(Identifier id);

    // --- NEW: The Accessor (Variable reader) ---
    @Accessor("postProcessor")
    PostEffectProcessor alchemica$getPostProcessor();
}
