package sh.luunar.alchemica;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeveledCauldronBlock;
import net.minecraft.entity.ItemEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sh.luunar.alchemica.block.ModBlocks;
import sh.luunar.alchemica.event.AllowChatMessageHandler;
import sh.luunar.alchemica.item.ModItemGroup;
import sh.luunar.alchemica.item.ModItems;
import sh.luunar.alchemica.networking.ModMessages;
import sh.luunar.alchemica.recipe.AlchemyRecipe;
import sh.luunar.alchemica.recipe.ModRecipes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Alchemica implements ModInitializer {
    public static final String MOD_ID = "alchemica";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        // 1. Register all your content
        ModItemGroup.registerModItemGroups();
        ModItems.registerModItems();
        ModBlocks.registerModBlocks();
        ModRecipes.registerRecipes(); // <--- CRITICAL: Registers the "alchemy" recipe type
        ModMessages.registerS2CMessages();



        // 2. The Dynamic "Stirring" Ritual
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (world.isClient) return ActionResult.PASS;

            // Must hold a Stick
            ItemStack handStack = player.getStackInHand(hand);
            if (!handStack.isOf(Items.STICK)) return ActionResult.PASS;

            BlockPos pos = hitResult.getBlockPos();
            BlockState state = world.getBlockState(pos);

            // Must be a Full Water Cauldron (Level 3)
            if (state.isOf(Blocks.WATER_CAULDRON) && state.get(LeveledCauldronBlock.LEVEL) == 3) {

                // A. Scan for items inside the cauldron
                Box cauldronBox = new Box(pos).contract(0.1, 0.2, 0.1);
                List<ItemEntity> entities = world.getEntitiesByClass(ItemEntity.class, cauldronBox, e -> true);

                if (entities.isEmpty()) return ActionResult.PASS;

                // B. Convert Entities to a Temporary Inventory so RecipeManager can understand them
                SimpleInventory inventory = new SimpleInventory(entities.size());
                for (int i = 0; i < entities.size(); i++) {
                    inventory.setStack(i, entities.get(i).getStack());
                }

                // C. Ask Minecraft: "Do these items match any JSON recipe in 'alchemica:alchemy'?"
                Optional<AlchemyRecipe> match = world.getRecipeManager()
                        .getFirstMatch(ModRecipes.ALCHEMY_TYPE, inventory, world);

                if (match.isPresent()) {
                    AlchemyRecipe recipe = match.get();

                    // D. Consume Ingredients
                    // (Iterates through recipe ingredients and removes 1 matching item from the cauldron for each)
                    List<ItemEntity> toDiscard = new ArrayList<>();

                    for (int i = 0; i < recipe.getIngredients().size(); i++) {
                        for(ItemEntity entity : entities) {
                            if(recipe.getIngredients().get(i).test(entity.getStack())) {
                                entity.getStack().decrement(1);
                                if(entity.getStack().isEmpty()) toDiscard.add(entity);
                                break; // Found this ingredient, move to the next requirement
                            }
                        }
                    }

                    // Clear out empty item entities
                    for(ItemEntity e : toDiscard) e.discard();

                    // E. The 50/50 Gamble
                    boolean success = world.random.nextFloat() < 0.5f;

                    if (success) {
                        // --- SUCCESS ---
                        // Get the output defined in the JSON file
                        ItemStack result = recipe.getOutput(world.getRegistryManager()).copy();

                        ItemEntity resultEntity = new ItemEntity(world, pos.getX()+0.5, pos.getY()+1.0, pos.getZ()+0.5, result);
                        world.spawnEntity(resultEntity);

                        // Magic chime sound & Sparkles
                        world.playSound(null, pos, SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME, SoundCategory.BLOCKS, 1f, 1f);
                        ((ServerWorld) world).spawnParticles(ParticleTypes.END_ROD, pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5, 10, 0.2, 0.2, 0.2, 0.1);
                    } else {
                        // --- FAILURE ---
                        // Always creates Ash
                        ItemStack result = new ItemStack(ModItems.ALCHEMICAL_ASH);

                        ItemEntity resultEntity = new ItemEntity(world, pos.getX()+0.5, pos.getY()+1.0, pos.getZ()+0.5, result);
                        world.spawnEntity(resultEntity);

                        // Fizzle sound & Smoke
                        world.playSound(null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 1f, 1f);
                        ((ServerWorld) world).spawnParticles(ParticleTypes.SMOKE, pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5, 10, 0.2, 0.2, 0.2, 0.1);
                    }

                    return ActionResult.SUCCESS;
                }
            }
            return ActionResult.PASS;
        });

        // 3. Register Chat/Antenna Logic
        LOGGER.info("Registering Message Events for " + Alchemica.MOD_ID);
        AllowChatMessageHandler handler = new AllowChatMessageHandler();
        ServerMessageEvents.ALLOW_CHAT_MESSAGE.register(handler);
        ClientSendMessageEvents.ALLOW_COMMAND.register(handler);

        // 4. The Grand Finale
        LOGGER.info("Alchemica initialized!");
        LOGGER.info("I ADDED THE MULTIBLOCK BITCH!!!");
        LOGGER.info("NOW WITH JSON!!!");
    }
}