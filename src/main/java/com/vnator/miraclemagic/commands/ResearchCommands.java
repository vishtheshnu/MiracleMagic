package com.vnator.miraclemagic.commands;

import com.vnator.miraclemagic.MiracleMagic;
import com.vnator.miraclemagic.attachment.ModDataAttachments;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import java.util.HashSet;

@EventBusSubscriber(modid = MiracleMagic.MODID)
public class ResearchCommands {

    private static final String RESEARCH_NAME_ARG = "researchName";
    private static final String PLAYER_ARG = "player";
    private static final int RESULT_UNSUCCESSFUL = 0;

    @SubscribeEvent // on the game event bus
    public static void registerCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("miraclemagic")
                .then(Commands.literal("research")
                        .then(Commands.literal("get")
                                .then(Commands.argument(PLAYER_ARG, EntityArgument.player())
                                        .executes(ResearchCommands::commandGetResearch)))
                        .then(Commands.literal("add")
                                .requires(source -> source.hasPermission(2))
                                .then(Commands.argument(RESEARCH_NAME_ARG, StringArgumentType.word())
                                        .then(Commands.argument(PLAYER_ARG, EntityArgument.player())
                                                .executes(ResearchCommands::commandAddResearch))))
                        .then(Commands.literal("remove")
                                .requires(source -> source.hasPermission(2))
                                .then(Commands.argument(RESEARCH_NAME_ARG, StringArgumentType.word())
                                        .then(Commands.argument(PLAYER_ARG, EntityArgument.player())
                                                .executes(ResearchCommands::commandRemoveResearch))))));
    }

    public static int commandGetResearch(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(context, PLAYER_ARG);

        String stages = String.join("\n",
                player.getData(ModDataAttachments.PLAYER_RESEARCH_STAGES));
        player.sendSystemMessage(Component.literal("Unlocked research:\n" + stages));
        return Command.SINGLE_SUCCESS;
    }

    public static int commandAddResearch(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(context, PLAYER_ARG);

        String research = context.getArgument(RESEARCH_NAME_ARG, String.class);

        HashSet<String> playerResearch = player
                .getData(ModDataAttachments.PLAYER_RESEARCH_STAGES);
        boolean success = playerResearch.add(research);
        player.setData(ModDataAttachments.PLAYER_RESEARCH_STAGES, playerResearch);

        String message = success ? "Added research: " + research :
                "Player already has research " + research;
        player.sendSystemMessage(Component.literal(message));
        return success ? Command.SINGLE_SUCCESS : RESULT_UNSUCCESSFUL;
    }

    public static int commandRemoveResearch(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(context, PLAYER_ARG);

        String research = context.getArgument(RESEARCH_NAME_ARG, String.class);

        HashSet<String> playerResearch = player
                .getData(ModDataAttachments.PLAYER_RESEARCH_STAGES);
        boolean success = playerResearch.remove(research);
        player.setData(ModDataAttachments.PLAYER_RESEARCH_STAGES, playerResearch);

        String message = success ? "Removed research: " + research :
                "Player does not have research: " + research;
        player.sendSystemMessage(Component.literal(message));
        return success ? Command.SINGLE_SUCCESS : RESULT_UNSUCCESSFUL;
    }
}
