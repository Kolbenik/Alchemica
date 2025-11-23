package sh.luunar.alchemica;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
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
        ModItemGroup.registerModItemGroups();
        ModItems.registerModItems();
        ModBlocks.registerModBlocks();
        ModRecipes.registerRecipes();
        ModMessages.registerS2CMessages();

        sh.luunar.alchemica.effect.ModEffects.registerEffects();

        // --- THE INTERACTION HUB ---
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (world.isClient) return ActionResult.PASS;

            ItemStack handStack = player.getStackInHand(hand);
            BlockPos pos = hitResult.getBlockPos();
            BlockState state = world.getBlockState(pos);

            // -----------------------------------------------------------
            // INTERACTION 1: VINEGAR + WATER CAULDRON (Create Vinegar Cauldron)
            // -----------------------------------------------------------
            if (handStack.isOf(ModItems.VINEGAR) && state.isOf(Blocks.WATER_CAULDRON)) {

                // 1. Transform the Block
                world.setBlockState(pos, ModBlocks.VINEGAR_CAULDRON.getDefaultState());

                // 2. Consume Vinegar / Return Bottle
                if (!player.getAbilities().creativeMode) {
                    handStack.decrement(1);
                    if (handStack.isEmpty()) {
                        player.setStackInHand(hand, new ItemStack(Items.GLASS_BOTTLE));
                    } else {
                        player.getInventory().offerOrDrop(new ItemStack(Items.GLASS_BOTTLE));
                    }
                }

                // 3. Effects (Sound + Green Particles)
                world.playSound(null, pos, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 1f, 1f);
                ((ServerWorld) world).spawnParticles(ParticleTypes.HAPPY_VILLAGER, pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5, 15, 0.3, 0.3, 0.3, 0.1);

                return ActionResult.SUCCESS;
            }

            // -----------------------------------------------------------
            // INTERACTION 2: STICK + WATER CAULDRON (The Alchemy Ritual)
            // -----------------------------------------------------------
            if (handStack.isOf(Items.STICK)) {
                // Must be a Water Cauldron with at least Level 1
                if (state.isOf(Blocks.WATER_CAULDRON) && state.get(LeveledCauldronBlock.LEVEL) >= 1) {

                    // A. Scan for items
                    Box cauldronBox = new Box(pos).contract(0.1, 0.2, 0.1);
                    List<ItemEntity> entities = world.getEntitiesByClass(ItemEntity.class, cauldronBox, e -> true);

                    if (entities.isEmpty()) return ActionResult.PASS;

                    // B. Build Inventory
                    SimpleInventory inventory = new SimpleInventory(entities.size());
                    for (int i = 0; i < entities.size(); i++) {
                        inventory.setStack(i, entities.get(i).getStack());
                    }

                    // C. Check Recipes
                    Optional<AlchemyRecipe> match = world.getRecipeManager()
                            .getFirstMatch(ModRecipes.ALCHEMY_TYPE, inventory, world);

                    if (match.isPresent()) {
                        AlchemyRecipe recipe = match.get();

                        // D. Consume Ingredients
                        List<ItemEntity> toDiscard = new ArrayList<>();
                        for (int i = 0; i < recipe.getIngredients().size(); i++) {
                            for(ItemEntity entity : entities) {
                                if(recipe.getIngredients().get(i).test(entity.getStack())) {
                                    entity.getStack().decrement(1);
                                    if(entity.getStack().isEmpty()) toDiscard.add(entity);
                                    break;
                                }
                            }
                        }
                        for(ItemEntity e : toDiscard) e.discard();

                        // E. 50/50 Gamble
                        boolean success = world.random.nextFloat() < 0.5f;

                        if (success) {
                            // SUCCESS
                            ItemStack result = recipe.getOutput(world.getRegistryManager()).copy();
                            ItemEntity resultEntity = new ItemEntity(world, pos.getX()+0.5, pos.getY()+1.0, pos.getZ()+0.5, result);
                            world.spawnEntity(resultEntity);

                            world.playSound(null, pos, SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME, SoundCategory.BLOCKS, 1f, 1f);
                            ((ServerWorld) world).spawnParticles(ParticleTypes.END_ROD, pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5, 10, 0.2, 0.2, 0.2, 0.1);
                        } else {
                            // FAILURE
                            ItemStack result = new ItemStack(ModItems.ALCHEMICAL_ASH);
                            ItemEntity resultEntity = new ItemEntity(world, pos.getX()+0.5, pos.getY()+1.0, pos.getZ()+0.5, result);
                            world.spawnEntity(resultEntity);

                            world.playSound(null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 1f, 1f);
                            ((ServerWorld) world).spawnParticles(ParticleTypes.SMOKE, pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5, 10, 0.2, 0.2, 0.2, 0.1);
                        }

                        // F. Consume Water
                        int currentLevel = state.get(LeveledCauldronBlock.LEVEL);
                        if (currentLevel > 1) {
                            world.setBlockState(pos, state.with(LeveledCauldronBlock.LEVEL, currentLevel - 1));
                        } else {
                            world.setBlockState(pos, Blocks.CAULDRON.getDefaultState());
                        }

                        return ActionResult.SUCCESS;
                    }
                }
            }

            return ActionResult.PASS;
        });

        // Register Chat/Antenna Logic
        AllowChatMessageHandler handler = new AllowChatMessageHandler();
       // ServerMessageEvents.ALLOW_CHAT_MESSAGE.register(handler);
        ClientSendMessageEvents.ALLOW_CHAT.register(handler);     // <--- NEW CLIENT LINE
        ClientSendMessageEvents.ALLOW_COMMAND.register(handler);

        // Logs
        LOGGER.info("Alchemica initialized!");
        LOGGER.info("I ADDED THE MULTIBLOCK BITCH!!!");
        LOGGER.info("NOW WITH JSON!!!");
        LOGGER.info("HEY BITCH!!! I ADDED ROT MECHANIC!!");
        LOGGER.info("I give a fuck about Performance rn!");
        LOGGER.info("I'm a bit tired rn.");
        LOGGER.info("Uh... now Vinegar?");
        LOGGER.info("YOU CAN GET DRUNK!!! YIPPAAA!!");
    }
}