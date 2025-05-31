package nl.matsgemmeke.battlegrounds;

import co.aikar.commands.PaperCommandManager;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.j256.ormlite.logger.Level;
import nl.matsgemmeke.battlegrounds.command.*;
import nl.matsgemmeke.battlegrounds.command.condition.ExistentSessionIdCondition;
import nl.matsgemmeke.battlegrounds.command.condition.ExistentWeaponIdCondition;
import nl.matsgemmeke.battlegrounds.command.condition.NonexistentSessionIdCondition;
import nl.matsgemmeke.battlegrounds.command.condition.OpenModePresenceCondition;
import nl.matsgemmeke.battlegrounds.event.EventDispatcher;
import nl.matsgemmeke.battlegrounds.event.handler.*;
import nl.matsgemmeke.battlegrounds.event.listener.EventListener;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.*;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Logger;

public class BattlegroundsPlugin extends JavaPlugin {

    private GameContextProvider contextProvider;
    private Injector injector;
    private InternalsProvider internals;
    private Logger logger;
    private PluginManager pluginManager;

    @Override
    public void onEnable() {
        logger = this.getLogger();
        pluginManager = this.getServer().getPluginManager();

        try {
            this.startPlugin();
        } catch (StartupFailedException e) {
            logger.severe("An error occurred while enabling Battlegrounds v" + this.getDescription().getVersion());
            logger.severe(e.getMessage());
            pluginManager.disablePlugin(this);
            return;
        }

        logger.info("Successfully started Battlegrounds v" + this.getDescription().getVersion());
    }

    @Override
    public void onDisable() {
        contextProvider.shutdown();
    }

    private void startPlugin() throws StartupFailedException {
        this.setUpInternalsProvider();
        this.setUpLogging();

        File dataFolder = this.getDataFolder();
        BattlegroundsModule module = new BattlegroundsModule(dataFolder, internals, this, pluginManager);

        injector = Guice.createInjector(module);
        contextProvider = injector.getInstance(GameContextProvider.class);

        this.setUpEventHandlers();
        this.setUpCommands();
    }

    private void setUpCommands() {
        BattlegroundsCommand bgCommand = injector.getInstance(BattlegroundsCommand.class);

        // Add all subcommands to the battlegrounds command
        bgCommand.addSubcommand(injector.getInstance(CreateSessionCommand.class));
        bgCommand.addSubcommand(injector.getInstance(GiveWeaponCommand.class));
        bgCommand.addSubcommand(injector.getInstance(ReloadCommand.class));
        bgCommand.addSubcommand(injector.getInstance(RemoveSessionCommand.class));
        bgCommand.addSubcommand(injector.getInstance(SetMainLobbyCommand.class));

        // Register the command to ACF
        PaperCommandManager commandManager = new PaperCommandManager(this);
        commandManager.registerCommand(bgCommand);

        // Register custom conditions to ACF
        var commandConditions = commandManager.getCommandConditions();
        commandConditions.addCondition("open-mode-presence", injector.getInstance(OpenModePresenceCondition.class));
        commandConditions.addCondition(Integer.class, "existent-session-id", injector.getInstance(ExistentSessionIdCondition.class));
        commandConditions.addCondition(String.class, "existent-weapon-id", injector.getInstance(ExistentWeaponIdCondition.class));
        commandConditions.addCondition(Integer.class, "nonexistent-session-id", injector.getInstance(NonexistentSessionIdCondition.class));
    }

    private void setUpEventHandlers() {
        EventDispatcher eventDispatcher = injector.getInstance(EventDispatcher.class);
        EventListener eventListener = injector.getInstance(EventListener.class);

        pluginManager.registerEvents(eventListener, this);

        eventDispatcher.registerEventHandler(BlockBurnEvent.class, injector.getInstance(BlockBurnEventHandler.class));
        eventDispatcher.registerEventHandler(BlockSpreadEvent.class, injector.getInstance(BlockSpreadEventHandler.class));
        eventDispatcher.registerEventHandler(EntityDamageByEntityEvent.class, injector.getInstance(EntityDamageByEntityEventHandler.class));
        eventDispatcher.registerEventHandler(EntityPickupItemEvent.class, injector.getInstance(EntityPickupItemEventHandler.class));
        eventDispatcher.registerEventHandler(PlayerDropItemEvent.class, injector.getInstance(PlayerDropItemEventHandler.class));
        eventDispatcher.registerEventHandler(PlayerInteractEvent.class, injector.getInstance(PlayerInteractEventHandler.class));
        eventDispatcher.registerEventHandler(PlayerItemHeldEvent.class, injector.getInstance(PlayerItemHeldEventHandler.class));
        eventDispatcher.registerEventHandler(PlayerJoinEvent.class, injector.getInstance(PlayerJoinEventHandler.class));
        eventDispatcher.registerEventHandler(PlayerRespawnEvent.class, injector.getInstance(PlayerRespawnEventHandler.class));
        eventDispatcher.registerEventHandler(PlayerSwapHandItemsEvent.class, injector.getInstance(PlayerSwapHandItemsEventHandler.class));
    }

    private void setUpInternalsProvider() throws StartupFailedException {
        try {
            String packageName = BattlegroundsPlugin.class.getPackage().getName();
            String internalsName = this.getServer().getClass().getPackage().getName().split("\\.")[3];
            String className = packageName + ".nms." + internalsName + "." + internalsName.toUpperCase();

            internals = (InternalsProvider) Class.forName(className).getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new StartupFailedException("Failed to find a valid implementation for this server version");
        }
    }

    private void setUpLogging() {
        // Disable ORMLite logs
        com.j256.ormlite.logger.Logger.setGlobalLogLevel(Level.ERROR);
    }
}
