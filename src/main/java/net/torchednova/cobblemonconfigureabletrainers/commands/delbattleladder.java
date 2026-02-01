package net.torchednova.cobblemonconfigureabletrainers.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.torchednova.cobblemonconfigureabletrainers.internalbattlehandler.BattleLadderController;
import net.torchednova.cobblemonconfigureabletrainers.internalbattlehandler.BattleLadders;

public class delbattleladder {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("delbattleladder")
                .then(Commands.argument("name", StringArgumentType.greedyString())
                        .executes(context -> {
                                    String name = StringArgumentType.getString(context, "name");
                                    BattleLadders bl = BattleLadderController.getBattleLadder(name);
                                    if (bl == null)
                                    {
                                        return 1;
                                    }
                                    BattleLadderController.delLadder(bl.getId());
                                    bl = null;
                                    context.getSource().sendSuccess(
                                            () -> Component.literal("Removed " + name),
                                            false
                                    );
                            return 1;
                        }
                        )));
    }
}
