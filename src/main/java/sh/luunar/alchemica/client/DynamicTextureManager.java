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

        // Platzhalter setzen
        //CACHE.put(url, FALLBACK);

        CompletableFuture.runAsync(() -> {
            try {
                URL imageUrl = new URL(url);
                try (InputStream stream = imageUrl.openStream()) {
                    NativeImage image = NativeImage.read(stream);

                    // Alles was Texturen registriert, MUSS auf dem Main-Thread passieren
                    MinecraftClient.getInstance().execute(() -> {

                        // 1. Die Textur erstellen
                        NativeImageBackedTexture dynamicTexture = new NativeImageBackedTexture(image);

                        // 2. DEN FILTER AUSSCHALTEN!
                        // false = kein Blur (Bilinear Filter aus)
                        // false = keine Mipmaps (kleinere Versionen aus)
                        // Das sorgt für den "Pixel-Art" Look und macht die Kanten massiv.
                        dynamicTexture.setFilter(false, false); // <--- HIER IST DAS WICHTIGSTE!

                        // 3. Registrieren
                        Identifier id = new Identifier("alchemica", "dynamic/" + url.hashCode());
                        MinecraftClient.getInstance().getTextureManager().registerTexture(id, dynamicTexture);

                        // 4. Cache updaten
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