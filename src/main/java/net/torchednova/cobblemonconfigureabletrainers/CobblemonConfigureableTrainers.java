package net.torchednova.cobblemonconfigureabletrainers;

import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import com.cobblemon.mod.common.api.battles.model.actor.BattleActor;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.api.events.battles.BattleVictoryEvent;
import com.cobblemon.mod.common.battles.actor.PlayerBattleActor;
import com.cobblemon.mod.common.battles.actor.TrainerBattleActor;
import com.gitlab.srcmc.rctapi.api.battle.BattleManager;
import com.google.common.eventbus.Subscribe;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.torchednova.cobblemonconfigureabletrainers.commands.*;
import net.torchednova.cobblemonconfigureabletrainers.datastorage.TargetDataStorage;
import net.torchednova.cobblemonconfigureabletrainers.internalbattlehandler.BattleLadderController;
import net.torchednova.cobblemonconfigureabletrainers.internalbattlehandler.BattleLadders;
import net.torchednova.cobblemonconfigureabletrainers.internalbattlehandler.Battles;
import net.torchednova.cobblemonconfigureabletrainers.internalbattlehandler.EastNPCTrainer;
import net.torchednova.cobblemonconfigureabletrainers.trainer.TrainerHandler;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.Objects;
import java.util.UUID;

import static com.mojang.blaze3d.Blaze3D.getTime;
import static net.minecraft.commands.execution.tasks.ContinuationTask.schedule;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(CobblemonConfigureableTrainers.MODID)
public class CobblemonConfigureableTrainers {
    public static final String MODID = "cobconfigtrainer";
    public static final Logger LOGGER = LogUtils.getLogger();

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public CobblemonConfigureableTrainers(IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (ExampleMod) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
        NeoForge.EVENT_BUS.register(this);

        CobblemonEvents.BATTLE_VICTORY.subscribe(this::battleVictory);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void battleVictory(BattleVictoryEvent battleVictoryEvent) {
        BattleActor playerAct = null;
        ServerPlayer player = null;
        BattleManager.TrainerEntityBattleActor trainerActor = null;

        boolean playerWin = false;


        for (BattleActor actor : battleVictoryEvent.getWinners()) {
            //LOGGER.info(actor.getType().toString());
            if (Objects.equals(actor.getType().toString(), "PLAYER")){
                playerAct = actor;
                playerWin = true;
                //LOGGER.info("won");
            }
            if (Objects.equals(actor.getType().toString(), "NPC")) {
                trainerActor = (BattleManager.TrainerEntityBattleActor) actor;
            }
        }

        for (BattleActor actor : battleVictoryEvent.getLosers()) {
            //LOGGER.info(actor.getType().toString());
            if (Objects.equals(actor.getType().toString(), "PLAYER")){
                playerAct = actor;
                //LOGGER.info("lost");
            }
            if (Objects.equals(actor.getType().toString(), "NPC")) {
                trainerActor = (BattleManager.TrainerEntityBattleActor) actor;
            }
        }

        // Ignore non player-vs-trainer battles
        //LOGGER.info(trainerActor.getUuid().toString());
        //LOGGER.info(playerAct.getUuid().toString());
        if (trainerActor == null || playerAct == null || Battles.haveBattle(trainerActor.getUuid()) == false) {
            //LOGGER.info("failed bad");
            return;
        }

        player = trainerActor.getEntity().getServer().getPlayerList().getPlayer(playerAct.getUuid());
        if (player == null)
        {
            //LOGGER.info("panick");
            return;
        }




        Battles.delBattle(trainerActor.getUuid());
        ServerPlayer sPlayer = (ServerPlayer) player;
        Vec3 nextPos = BattleLadderController.getEndPos(player.getUUID());

        if (playerWin == false)
        {
            
            sPlayer.connection.teleport(nextPos.x, nextPos.y, nextPos.z, 0, 0);
            return;
        }
        nextPos = BattleLadderController.getNextPos(player.getUUID());
        if (nextPos == null)
        {
            return;
        }

        sPlayer.connection.teleport(nextPos.x, nextPos.y, nextPos.z, 0, 0);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");
        TrainerHandler.init();
        Battles.init();
        BattleLadderController.init();


    }




    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        LOGGER.info("HELLO from CobblemonConfigurableTrainers starting");

        BattleLadderController.bl = TargetDataStorage.load(event.getServer());
    }

    @SubscribeEvent
    public void onServerStarted(ServerStartedEvent event) {
        EastNPCTrainer.init(event.getServer());

    }

    @SubscribeEvent
    public void onServerStopping(ServerStoppingEvent event)
    {
        TargetDataStorage.save(event.getServer());
        TargetDataStorage.saveTrainers(event.getServer());
    }


    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event)
    {
        GetTrainers.register(event.getDispatcher());
        NewTrainer.register(event.getDispatcher());
        GetAllTrainers.register(event.getDispatcher());
        StartBattle.register(event.getDispatcher());
        NewBattleLadder.register(event.getDispatcher());
        StartBattleLadder.register(event.getDispatcher());
        LeaveBattleLadder.register(event.getDispatcher());
        AttachTrainerToNPC.register(event.getDispatcher());
        ShowAllBattleLadders.register(event.getDispatcher());
        ShowBattleLadderInfo.register(event.getDispatcher());
        delbattleladder.register(event.getDispatcher());
        delbattle.register(event.getDispatcher());
    }

}
