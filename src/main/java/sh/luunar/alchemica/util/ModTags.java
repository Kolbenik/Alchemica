package sh.luunar.alchemica.util;

import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import sh.luunar.alchemica.Alchemica;

public class ModTags {
    public static class Blocks {
        public static final TagKey<Block> IMMUNE_TO_RUST = createTag("immune_to_rust");
        public static  final TagKey<Block> RUSTING = createTag("rusting");

        private static TagKey<Block> createTag(String name) {
            return TagKey.of(RegistryKeys.BLOCK, new Identifier(Alchemica.MOD_ID, name));
        }
    }
}