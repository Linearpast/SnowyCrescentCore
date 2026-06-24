package io.zershyan.sccore.animation.registry;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.zershyan.sccore.animation.command.JsonCommand;
import io.zershyan.sccore.common.registry.SCCCommands;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import static net.minecraft.commands.Commands.literal;

public class AnimationCommands {
    public static void commonCommandRegister(RegisterCommandsEvent event) {
        SCCCommands.getAnimationCommand().forEach(string -> {
            LiteralArgumentBuilder<CommandSourceStack> builder = literal(string);
            CommandBuildContext buildContext = event.getBuildContext();
            JsonCommand.register(builder);
            event.getDispatcher().register(builder);
        });
    }

    public static void clientCommandRegister(RegisterClientCommandsEvent event) {
        SCCCommands.getAnimationCommand().forEach(string -> {
            LiteralArgumentBuilder<CommandSourceStack> builder = literal(string);

            event.getDispatcher().register(builder);
        });
    }
}
