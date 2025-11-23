package sh.luunar.alchemica.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class DynamicTextureManager {
    private static final Map<String, Identifier> CACHE = new HashMap<>();
    private static final Identifier FALLBACK = new Identifier("minecraft", "textures/item/barrier.png");

    public static Identifier getTexture(String url) {
        if (url == null || url.isEmpty()) return FALLBACK;

        if (CACHE.containsKey(url)) {
            return CACHE.get(url);
        }
        //CACHE.put(url, FALLBACK);

        CompletableFuture.runAsync(() -> {
            try {
                URL imageUrl = new URL(url);
                try (InputStream stream = imageUrl.openStream()) {
                    NativeImage image = NativeImage.read(stream);

                    MinecraftClient.getInstance().execute(() -> {

                        NativeImageBackedTexture dynamicTexture = new NativeImageBackedTexture(image);
                        dynamicTexture.setFilter(false, false);
                        Identifier id = new Identifier("alchemica", "dynamic/" + url.hashCode());
                        MinecraftClient.getInstance().getTextureManager().registerTexture(id, dynamicTexture);
                        CACHE.put(url, id);
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        return FALLBACK;
    }
}