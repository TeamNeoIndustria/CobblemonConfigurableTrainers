package net.torchednova.cobblemonconfigureabletrainers.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.torchednova.cobblemonconfigureabletrainers.internalbattlehandler.BattleLadderController;

public class StartBattleLadder {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("startbattleladder").requires(source -> source.hasPermission(2))
            .then(Commands.argument("name", StringArgumentType.greedyString())
                .then(Commands.argument("player", EntityArgument.player())
                        .executes(context -> {
                            String name = StringArgumentType.getString(context, "name");
                            ServerPlayer player = EntityArgument.getPlayer(context, "player");
                            CommandSourceStack source = player.createCommandSourceStack();

                            if(BattleLadderController.playerInLadder(player.getUUID()) == false && BattleLadderController.getBattleLadder(name).setPlayer(player.getUUID()))
                            {
                                source.sendSuccess(
                                        () -> Component.literal("You have Started BattleLadder " + name),
                                        false
                                );
                                source.sendSuccess(
                                    () -> Component.literal("To leave type /leavebattleladder"),
                                    false
                                );


                                Vec3 pos = BattleLadderController.getBattleLadder(name).getStart();
                                player.connection.teleport(pos.x, pos.y, pos.z, 0, 0);
                            }
                            else
                            {
                                source.sendSuccess(
                                        () -> Component.literal("Another player is currently trying their luck in " + name + "\nPlease try again later"),
                                        false
                                );
                            }




                            return 1;
                        })))
        );
    }
}
