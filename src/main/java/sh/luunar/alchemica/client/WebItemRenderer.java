package sh.luunar.alchemica.client;

import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.render.*;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class WebItemRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer {

    @Override
    public void render(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {

        String url = ItemTextureLogic.getUrl(stack);
        Identifier texture = DynamicTextureManager.getTexture(url);

        matrices.push();

        // 1. ZENTRIERUNG
        matrices.translate(0.5f, 0.5f, 0.5f);
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180));


        // 24 Layer sind ein guter Mittelweg (Massiv genug, aber nicht zu rechenintensiv).
        // WICHTIG: Damit das massiv aussieht, muss in DynamicTextureManager 'setFilter(false, false)' stehen!
        renderNoFlickerStack(matrices, vertexConsumers, texture, light, overlay, 0.0625f, 24);

        matrices.pop();
    }

    private void renderNoFlickerStack(MatrixStack matrices, VertexConsumerProvider consumers, Identifier texture, int light, int overlay, float thickness, int layerCount) {
        // Wir nutzen Cutout. Transparente Pixel werden gelöscht, der Rest ist solid.
        VertexConsumer buffer = consumers.getBuffer(RenderLayer.getEntityCutout(texture));

        MatrixStack.Entry entry = matrices.peek();
        Matrix4f pos = entry.getPositionMatrix();
        Matrix3f normal = entry.getNormalMatrix();

        float size = 0.5f;
        float startZ = -thickness / 2;
        float step = thickness / layerCount;

        // --- DER ANTI-FLACKER TRICK ---
        // Wir teilen das Rendern in zwei Blöcke auf.

        // BLOCK 1: Wir zeichnen NUR die Rückseiten aller Layer.
        // Die Normale zeigt nach HINTEN (+1).
        for (int i = 0; i <= layerCount; i++) {
            float z = startZ + (i * step);
            // Textur (1->0 für korrekte Ausrichtung)
            float uLeft = 1.0f; float uRight = 0.0f;

            // Rückseite zeichnen (Winding Order beachten!)
            vertex(buffer, pos, normal, -size, -size, z, uLeft, 0.0f, light, overlay, 0, 0, 1.0f);
            vertex(buffer, pos, normal, size, -size, z, uRight, 0.0f, light, overlay, 0, 0, 1.0f);
            vertex(buffer, pos, normal, size, size, z, uRight, 1.0f, light, overlay, 0, 0, 1.0f);
            vertex(buffer, pos, normal, -size, size, z, uLeft, 1.0f, light, overlay, 0, 0, 1.0f);
        }

        // BLOCK 2: Wir zeichnen NUR die Vorderseiten aller Layer.
        // Die Normale zeigt nach VORNE (-1).
        for (int i = 0; i <= layerCount; i++) {
            float z = startZ + (i * step);
            float uLeft = 1.0f; float uRight = 0.0f;

            // Vorderseite zeichnen
            vertex(buffer, pos, normal, -size, -size, z, uLeft, 0.0f, light, overlay, 0, 0, -1.0f);
            vertex(buffer, pos, normal, -size, size, z, uLeft, 1.0f, light, overlay, 0, 0, -1.0f);
            vertex(buffer, pos, normal, size, size, z, uRight, 1.0f, light, overlay, 0, 0, -1.0f);
            vertex(buffer, pos, normal, size, -size, z, uRight, 0.0f, light, overlay, 0, 0, -1.0f);
        }
    }

    private void vertex(VertexConsumer buffer, Matrix4f pos, Matrix3f normal, float x, float y, float z, float u, float v, int light, int overlay, float nx, float ny, float nz) {
        buffer.vertex(pos, x, y, z)
                .color(255, 255, 255, 255)
                .texture(u, v)
                .overlay(overlay)
                .light(light)
                .normal(normal, nx, ny, nz)
                .next();
    }
}