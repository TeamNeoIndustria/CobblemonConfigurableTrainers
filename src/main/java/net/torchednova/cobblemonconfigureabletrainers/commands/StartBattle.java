package net.torchednova.cobblemonconfigureabletrainers.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

import net.torchednova.cobblemonconfigureabletrainers.internalbattlehandler.Battles;

import java.util.UUID;

import static com.mojang.text2speech.Narrator.LOGGER;

public class StartBattle {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("neofight")
            .requires(source -> source.hasPermission(2))
                .then(Commands.argument("battletype", StringArgumentType.string())
                    .then(Commands.argument("ainame", StringArgumentType.string())
                        .then(Commands.argument("trainer", StringArgumentType.string())
                            .then(Commands.argument("playername", StringArgumentType.greedyString())
                                .executes(context -> {

                                    CommandSourceStack source = context.getSource();

                                        String bt = StringArgumentType.getString(context, "battletype");
                                        String ai = StringArgumentType.getString(context, "ainame");
                                        String trainer = StringArgumentType.getString(context, "trainer");
                                        UUID aiuuid = UUID.fromString(ai);
                                        Battles.addBattle(aiuuid);
                                        //LOGGER.info("Battle Start UUID: " + aiuuid.toString());
                                        String pl = StringArgumentType.getString(context, "playername");

                                        source.sendSuccess(
                                            () -> Component.literal("Starting fight..."),
                                    false
                                        );
                                        var disp = source.getServer().getCommands().getDispatcher();
                                        ///tbcs attach tbcs:hiker_1_40 bcd8f2a0-0bf0-4738-a7ce-d11e57d40c38
                                        ParseResults<CommandSourceStack> parse = disp.parse("tbcs attach " + trainer + " " + aiuuid.toString(), source);

                                        source.getServer().getCommands().performCommand(parse, "");

                                        parse = disp.parse("tbcs battle " + bt + " " + pl + " vs " + aiuuid.toString(), source);

                                        source.getServer().getCommands().performCommand(parse, "");
                                        disp = null;
                                        aiuuid = null;

                                        return 1;
                                    })
                                )
                        )
                    )
                )
        );
    }
}
