package sh.luunar.alchemica.mixin;

import net.minecraft.client.render.GameRenderer;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(GameRenderer.class)
public interface GameRendererAccessor {
    // The "@Invoker" annotation creates a bridge to the private method
    @Invoker("loadPostProcessor")
    void alchemica$loadPostProcessor(Identifier id);
}