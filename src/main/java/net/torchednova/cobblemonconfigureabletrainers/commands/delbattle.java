package net.torchednova.cobblemonconfigureabletrainers.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.torchednova.cobblemonconfigureabletrainers.internalbattlehandler.BattleLadderController;
import net.torchednova.cobblemonconfigureabletrainers.internalbattlehandler.BattleLadders;

public class delbattle {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("delbattle")
                .then(Commands.argument("id", IntegerArgumentType.integer())
                        .then(Commands.argument("name", StringArgumentType.greedyString())
                                .executes(context -> {
                                            String name = StringArgumentType.getString(context, "name");
                                            BattleLadders bl = BattleLadderController.getBattleLadder(name);
                                            if (bl == null)
                                            {
                                                return 1;
                                            }
                                            int id = IntegerArgumentType.getInteger(context, "id");
                                            bl.delBattle(id);
                                            context.getSource().sendSuccess(
                                                    () -> Component.literal("Removed battle id: " + id + " from " + name),
                                                    false
                                            );

                                    return 1;
                                }
                                )
                        )
                )
        );
    }

}
