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

        matrices.translate(0.5f, 0.5f, 0.5f);
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180));

        renderNoFlickerStack(matrices, vertexConsumers, texture, light, overlay, 0.0625f, 24);

        matrices.pop();
    }

    private void renderNoFlickerStack(MatrixStack matrices, VertexConsumerProvider consumers, Identifier texture, int light, int overlay, float thickness, int layerCount) {
        VertexConsumer buffer = consumers.getBuffer(RenderLayer.getEntityCutout(texture));

        MatrixStack.Entry entry = matrices.peek();
        Matrix4f pos = entry.getPositionMatrix();
        Matrix3f normal = entry.getNormalMatrix();

        float size = 0.5f;
        float startZ = -thickness / 2;
        float step = thickness / layerCount;

        for (int i = 0; i <= layerCount; i++) {
            float z = startZ + (i * step);
            float uLeft = 1.0f; float uRight = 0.0f;

            vertex(buffer, pos, normal, -size, -size, z, uLeft, 0.0f, light, overlay, 0, 0, 1.0f);
            vertex(buffer, pos, normal, size, -size, z, uRight, 0.0f, light, overlay, 0, 0, 1.0f);
            vertex(buffer, pos, normal, size, size, z, uRight, 1.0f, light, overlay, 0, 0, 1.0f);
            vertex(buffer, pos, normal, -size, size, z, uLeft, 1.0f, light, overlay, 0, 0, 1.0f);
        }

        for (int i = 0; i <= layerCount; i++) {
            float z = startZ + (i * step);
            float uLeft = 1.0f; float uRight = 0.0f;

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