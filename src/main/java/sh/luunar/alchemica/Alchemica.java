/*
 * Copyright (c) 2025 Lunar_sh, Kolbenik
 *
 * All rights reserved.
 */

package sh.luunar.alchemica;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.advancement.Advancement; // <--- FIXED IMPORT
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeveledCauldronBlock;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.GameMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sh.luunar.alchemica.block.ModBlocks;
import sh.luunar.alchemica.event.AllowChatMessageHandler;
import sh.luunar.alchemica.event.AntennaServerHandler;
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

        // Register Antenna Handler
        ServerTickEvents.END_SERVER_TICK.register(new AntennaServerHandler());

        // --- INTERACTION HUB ---
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (world.isClient) return ActionResult.PASS;

            ItemStack handStack = player.getStackInHand(hand);
            BlockPos pos = hitResult.getBlockPos();
            BlockState state = world.getBlockState(pos);

            // 1. VINEGAR CAULDRON CREATION
            if (handStack.isOf(ModItems.VINEGAR) && state.isOf(Blocks.WATER_CAULDRON)) {

                world.setBlockState(pos, ModBlocks.VINEGAR_CAULDRON.getDefaultState());

                if (!player.getAbilities().creativeMode) {
                    handStack.decrement(1);
                    if (handStack.isEmpty()) {
                        player.setStackInHand(hand, new ItemStack(Items.GLASS_BOTTLE));
                    } else {
                        player.getInventory().offerOrDrop(new ItemStack(Items.GLASS_BOTTLE));
                    }
                }

                world.playSound(null, pos, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 1f, 1f);
                ((ServerWorld) world).spawnParticles(ParticleTypes.HAPPY_VILLAGER, pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5, 15, 0.3, 0.3, 0.3, 0.1);

                // --- FORCE GRANT ADVANCEMENT (FIXED FOR 1.20.1) ---
                if (player instanceof ServerPlayerEntity serverPlayer) {
                    MinecraftServer server = serverPlayer.getServer();
                    if (server != null) {
                        Identifier advId = new Identifier(MOD_ID, "preservation/create_vat");

                        // FIX: Use 'Advancement' instead of 'AdvancementEntry'
                        Advancement entry = server.getAdvancementLoader().get(advId);

                        if (entry != null) {
                            serverPlayer.getAdvancementTracker().grantCriterion(entry, "use_vinegar");
                        }
                    }
                }
                // --------------------------------------------------

                return ActionResult.SUCCESS;
            }

            // 2. STICK INTERACTIONS (Alchemy)
            if (handStack.isOf(Items.STICK)) {
                if (state.isOf(Blocks.WATER_CAULDRON) && state.get(LeveledCauldronBlock.LEVEL) >= 1) {

                    BlockPos below = pos.down();
                    BlockState belowState = world.getBlockState(below);
                    boolean isHeated = belowState.isOf(Blocks.CAMPFIRE) || belowState.isOf(Blocks.SOUL_CAMPFIRE) || belowState.isOf(Blocks.LAVA) || belowState.isOf(Blocks.MAGMA_BLOCK);

                    Box cauldronBox = new Box(pos).contract(0.1, 0.2, 0.1);
                    List<ItemEntity> entities = world.getEntitiesByClass(ItemEntity.class, cauldronBox, e -> true);
                    if (entities.isEmpty()) return ActionResult.PASS;

                    // A. FORBIDDEN RITUALS
                    if (isHeated) {
                        for (ItemEntity entity : entities) {
                            ItemStack stack = entity.getStack();

                            // Soul Ash
                            if (stack.isOf(ModItems.ALCHEMICAL_ASH)) {
                                if (player.damage(world.getDamageSources().magic(), 8.0f)) {
                                    stack.decrement(1);
                                    if (stack.isEmpty()) entity.discard();

                                    world.spawnEntity(new ItemEntity(world, pos.getX()+0.5, pos.getY()+1, pos.getZ()+0.5, new ItemStack(ModItems.SOUL_ASH)));
                                    world.playSound(null, pos, SoundEvents.PARTICLE_SOUL_ESCAPE, SoundCategory.PLAYERS, 1f, 1f);
                                    player.sendMessage(Text.literal("The ash drinks your life...").formatted(Formatting.DARK_AQUA), true);

                                    decrementWater(world, pos, state);
                                    return ActionResult.SUCCESS;
                                }
                            }

                            // Banishment
                            if (stack.isOf(Items.EMERALD)) {
                                stack.decrement(1);
                                if (stack.isEmpty()) entity.discard();

                                world.spawnEntity(new ItemEntity(world, pos.getX()+0.5, pos.getY()+1, pos.getZ()+0.5, new ItemStack(Items.TOTEM_OF_UNDYING)));

                                if (player instanceof ServerPlayerEntity serverPlayer) {
                                    serverPlayer.changeGameMode(GameMode.SPECTATOR);
                                    serverPlayer.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 999999, 0, false, false));
                                    serverPlayer.sendMessage(Text.literal("You have been banished to the Ethereal Plane.").formatted(Formatting.RED).formatted(Formatting.BOLD), false);
                                }

                                world.playSound(null, pos, SoundEvents.BLOCK_END_PORTAL_SPAWN, SoundCategory.PLAYERS, 1f, 0.5f);
                                decrementWater(world, pos, state);
                                return ActionResult.SUCCESS;
                            }
                        }
                    }

                    // B. STANDARD RECIPES
                    SimpleInventory inventory = new SimpleInventory(entities.size());
                    for (int i = 0; i < entities.size(); i++) inventory.setStack(i, entities.get(i).getStack());

                    Optional<AlchemyRecipe> match = world.getRecipeManager().getFirstMatch(ModRecipes.ALCHEMY_TYPE, inventory, world);

                    if (match.isPresent()) {
                        AlchemyRecipe recipe = match.get();

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

                        boolean success = world.random.nextFloat() < 0.5f;

                        if (success) {
                            ItemStack result = recipe.getOutput(world.getRegistryManager()).copy();
                            world.spawnEntity(new ItemEntity(world, pos.getX()+0.5, pos.getY()+1.0, pos.getZ()+0.5, result));
                            world.playSound(null, pos, SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME, SoundCategory.BLOCKS, 1f, 1f);
                            ((ServerWorld) world).spawnParticles(ParticleTypes.END_ROD, pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5, 10, 0.2, 0.2, 0.2, 0.1);
                        } else {
                            ItemStack result = new ItemStack(ModItems.ALCHEMICAL_ASH);
                            world.spawnEntity(new ItemEntity(world, pos.getX()+0.5, pos.getY()+1.0, pos.getZ()+0.5, result));
                            world.playSound(null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 1f, 1f);
                            ((ServerWorld) world).spawnParticles(ParticleTypes.SMOKE, pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5, 10, 0.2, 0.2, 0.2, 0.1);
                        }

                        decrementWater(world, pos, state);
                        return ActionResult.SUCCESS;
                    }
                }
            }
            return ActionResult.PASS;
        });

        // Chat
        AllowChatMessageHandler handler = new AllowChatMessageHandler();
        ClientSendMessageEvents.ALLOW_CHAT.register(handler);
        ClientSendMessageEvents.ALLOW_COMMAND.register(handler);

        LOGGER.info("Alchemica initialized! Rituals Armed.");
    }

    private void decrementWater(net.minecraft.world.World world, BlockPos pos, BlockState state) {
        int currentLevel = state.get(LeveledCauldronBlock.LEVEL);
        if (currentLevel > 1) {
            world.setBlockState(pos, state.with(LeveledCauldronBlock.LEVEL, currentLevel - 1));
        } else {
            world.setBlockState(pos, Blocks.CAULDRON.getDefaultState());
        }
    }
}