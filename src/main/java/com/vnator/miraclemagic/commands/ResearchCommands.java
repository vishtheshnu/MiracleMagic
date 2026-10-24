package com.vnator.miraclemagic.commands;

import com.vnator.miraclemagic.MiracleMagic;
import com.vnator.miraclemagic.attachment.ModDataAttachments;

import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;

import java.util.HashSet;

@EventBusSubscriber(modid = MiracleMagic.MODID)
public class ResearchCommands {

    @SubscribeEvent // on the game event bus
    public static void registerCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("miraclemagic")
                .then(Commands.literal("research")
                        .then(Commands.literal("get")
                                .executes(context -> {
                                    ServerPlayer player = context.getSource().getPlayer();
                                    if (player == null) {
                                        return 0;
                                    }

                                    String stages = String.join("\n",
                                            player.getData(ModDataAttachments.PLAYER_RESEARCH_STAGES));
                                    player.sendSystemMessage(Component.literal("Unlocked research:\n" + stages));
                                    return Command.SINGLE_SUCCESS;
                                }))
                        .then(Commands.literal("add")
                                .requires(source -> source.hasPermission(2))
                                .then(Commands.argument("researchName", StringArgumentType.word()).executes(context -> {
                                    ServerPlayer player = context.getSource().getPlayer();
                                    if (player == null) {
                                        return 0;
                                    }

                                    String research = context.getArgument("researchName", String.class);

                                    HashSet<String> playerResearch = player
                                            .getData(ModDataAttachments.PLAYER_RESEARCH_STAGES);
                                    boolean success = playerResearch.add(research);
                                    player.setData(ModDataAttachments.PLAYER_RESEARCH_STAGES, playerResearch);

                                    String message = success ? "Added research: " + research :
                                            "Player already has research " + research;
                                    player.sendSystemMessage(Component.literal(message));
                                    return Command.SINGLE_SUCCESS;
                                })))
                        .then(Commands.literal("remove")
                                .requires(source -> source.hasPermission(2))
                                .then(Commands.argument("researchName", StringArgumentType.word()).executes(context -> {
                                    ServerPlayer player = context.getSource().getPlayer();
                                    if (player == null) {
                                        return 0;
                                    }

                                    String research = context.getArgument("researchName", String.class);

                                    HashSet<String> playerResearch = player
                                            .getData(ModDataAttachments.PLAYER_RESEARCH_STAGES);
                                    boolean success = playerResearch.remove(research);
                                    player.setData(ModDataAttachments.PLAYER_RESEARCH_STAGES, playerResearch);

                                    String message = success ? "Removed research: " + research :
                                            "Player does not have research: " + research;
                                    player.sendSystemMessage(Component.literal(message));
                                    return Command.SINGLE_SUCCESS;
                                })))));
    }
}
