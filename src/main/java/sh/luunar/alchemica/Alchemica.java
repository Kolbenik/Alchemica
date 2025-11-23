package sh.luunar.alchemica;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeveledCauldronBlock;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sh.luunar.alchemica.block.ModBlocks;
import sh.luunar.alchemica.event.AllowChatMessageHandler;
import sh.luunar.alchemica.item.ModItemGroup;
import sh.luunar.alchemica.item.ModItems;
import sh.luunar.alchemica.networking.ModMessages;

import java.util.List;

public class Alchemica implements ModInitializer {
    public static final String MOD_ID = "alchemica";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ModItemGroup.registerModItemGroups();
        ModItems.registerModItems(); // Make sure this is called!
        ModBlocks.registerModBlocks();
        ModMessages.registerS2CMessages();

        // --- THE STIRRING RITUAL LOGIC ---
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            // 1. Check Server Side Only (Logic happens here)
            if (world.isClient) return ActionResult.PASS;

            // 2. Check if holding a Stick
            ItemStack handStack = player.getStackInHand(hand);
            if (!handStack.isOf(Items.STICK)) return ActionResult.PASS;

            // 3. Check if clicking a Water Cauldron
            BlockPos pos = hitResult.getBlockPos();
            BlockState state = world.getBlockState(pos);

            if (state.isOf(Blocks.WATER_CAULDRON) && state.get(LeveledCauldronBlock.LEVEL) == 3) {

                // 4. Look for floating items inside the Cauldron
                Box cauldronBox = new Box(pos).contract(0.1, 0.2, 0.1); // Inside the walls
                List<ItemEntity> entities = world.getEntitiesByClass(ItemEntity.class, cauldronBox, e -> true);

                ItemEntity ironEntity = null;
                ItemEntity goldNuggetEntity = null;

                // Scan the items to find our ingredients
                for (ItemEntity entity : entities) {
                    if (entity.getStack().isOf(Items.IRON_INGOT)) ironEntity = entity;
                    if (entity.getStack().isOf(Items.GOLD_NUGGET)) goldNuggetEntity = entity;
                }

                // 5. If we found BOTH ingredients
                if (ironEntity != null && goldNuggetEntity != null) {

                    // --- ALCHEMY TIME ---

                    // Consume 1 of each
                    ItemStack ironStack = ironEntity.getStack();
                    ItemStack goldStack = goldNuggetEntity.getStack();

                    ironStack.decrement(1);
                    goldStack.decrement(1);

                    // Update or Kill the entities if stack is empty
                    if (ironStack.isEmpty()) ironEntity.discard();
                    if (goldStack.isEmpty()) goldNuggetEntity.discard();

                    // 50/50 Chance Calculation
                    boolean success = world.random.nextFloat() < 0.5f;

                    if (success) {
                        // SUCCESS: Spawn Gold Ingot
                        ItemStack result = new ItemStack(Items.GOLD_INGOT);
                        ItemEntity resultEntity = new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, result);
                        world.spawnEntity(resultEntity);

                        // Effects
                        world.playSound(null, pos, SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME, SoundCategory.BLOCKS, 1f, 1f);
                        ((ServerWorld) world).spawnParticles(ParticleTypes.END_ROD, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 10, 0.2, 0.2, 0.2, 0.1);
                    } else {
                        // FAILURE: Spawn Ash
                        ItemStack result = new ItemStack(ModItems.ALCHEMICAL_ASH);
                        ItemEntity resultEntity = new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, result);
                        world.spawnEntity(resultEntity);

                        // Effects
                        world.playSound(null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 1f, 1f);
                        ((ServerWorld) world).spawnParticles(ParticleTypes.SMOKE, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 10, 0.2, 0.2, 0.2, 0.1);
                    }

                    return ActionResult.SUCCESS; // Swing the hand
                }
            }

            return ActionResult.PASS;
        });

        LOGGER.info("Alchemica loaded! Rituals armed.");
        LOGGER.info("I ADDED THE MULTIBLOCK BITCH!!!");
    }
}