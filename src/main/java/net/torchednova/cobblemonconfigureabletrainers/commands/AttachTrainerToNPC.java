package net.torchednova.cobblemonconfigureabletrainers.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.UuidArgument;
import net.minecraft.network.chat.Component;

import java.util.UUID;

public class AttachTrainerToNPC {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("attachtrainer").requires(source -> source.hasPermission(2))
                .then(Commands.argument("name", StringArgumentType.string())
                        .then(Commands.argument("uuid", UuidArgument.uuid())
                                .executes(context -> {
                                    CommandSourceStack source = context.getSource();

                                    String name = StringArgumentType.getString(context, "name");
                                    UUID uuid = UuidArgument.getUuid(context, "uuid");


                                    var disp = source.getServer().getCommands().getDispatcher();
                                    ParseResults<CommandSourceStack> parse = disp.parse("tbcs attach " + name + " " + uuid, source);

                                    source.getServer().getCommands().performCommand(parse, "");

                                    source.sendSuccess(
                                            () -> Component.literal("Trainer" + name + " has been add to " + uuid.toString()),
                                            false
                                    );
                                    return 1;
                                })
                        )
                )
        );
    }
}
