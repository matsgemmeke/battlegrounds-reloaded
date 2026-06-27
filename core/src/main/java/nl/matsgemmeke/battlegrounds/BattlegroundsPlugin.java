package nl.matsgemmeke.battlegrounds;

import co.aikar.commands.PaperCommandManager;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.j256.ormlite.logger.Level;
import nl.matsgemmeke.battlegrounds.command.CommandBootstrapper;
import nl.matsgemmeke.battlegrounds.configuration.BattlegroundsConfiguration;
import nl.matsgemmeke.battlegrounds.event.EventDispatcher;
import nl.matsgemmeke.battlegrounds.event.handler.*;
import nl.matsgemmeke.battlegrounds.event.listener.EventListener;
import nl.matsgemmeke.battlegrounds.game.GameContextShutdownManager;
import nl.matsgemmeke.battlegrounds.game.arena.loading.ArenaSetupLoader;
import nl.matsgemmeke.battlegrounds.game.freeplay.FreeplayInitializer;
import nl.matsgemmeke.battlegrounds.job.JobService;
import nl.matsgemmeke.battlegrounds.job.SaveDamageEventsJob;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.*;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.File;
import java.util.logging.Logger;

public class BattlegroundsPlugin extends JavaPlugin {

    private GameContextShutdownManager gameContextShutdownManager;
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
        } catch (StartupFailedException ex) {
            logger.severe("An error occurred while enabling Battlegrounds v%s: %s".formatted(this.getDescription().getVersion(), ex.getMessage()));
            pluginManager.disablePlugin(this);
            return;
        }

        logger.info("Successfully started Battlegrounds v" + this.getDescription().getVersion());
    }

    @Override
    public void onDisable() {
        gameContextShutdownManager.shutdown();
    }

    private void startPlugin() throws StartupFailedException {
        this.setUpInternalsProvider();
        this.setUpLogging();

        BukkitScheduler bukkitScheduler = this.getServer().getScheduler();
        File dataFolder = this.getDataFolder();
        PaperCommandManager commandManager = new PaperCommandManager(this);

        BattlegroundsModule module = new BattlegroundsModule(bukkitScheduler, dataFolder, internals, logger, commandManager, this, pluginManager);

        injector = Guice.createInjector(module);
        gameContextShutdownManager = injector.getInstance(GameContextShutdownManager.class);

        FreeplayInitializer freeplayInitializer = injector.getInstance(FreeplayInitializer.class);
        freeplayInitializer.initialize();

        this.setUpEventHandlers();
        injector.getInstance(CommandBootstrapper.class).initialize();
        injector.getInstance(ArenaSetupLoader.class).loadArenas();
        this.setUpJobs();
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
        eventDispatcher.registerEventHandler(PlayerQuitEvent.class, injector.getInstance(PlayerQuitEventHandler.class));
        eventDispatcher.registerEventHandler(PlayerRespawnEvent.class, injector.getInstance(PlayerRespawnEventHandler.class));
        eventDispatcher.registerEventHandler(PlayerSwapHandItemsEvent.class, injector.getInstance(PlayerSwapHandItemsEventHandler.class));
        eventDispatcher.registerEventHandler(ProjectileHitEvent.class, injector.getInstance(ProjectileHitEventHandler.class));
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

    private void setUpJobs() {
        BattlegroundsConfiguration configuration = injector.getInstance(BattlegroundsConfiguration.class);

        SaveDamageEventsJob saveDamageEventsJob = injector.getInstance(SaveDamageEventsJob.class);
        long saveDamageEventsJobPeriodMillis = configuration.getSaveDamageEventsJobPeriodMillis();

        JobService jobService = injector.getInstance(JobService.class);
        jobService.schedule(saveDamageEventsJob, saveDamageEventsJobPeriodMillis);
    }

    private void setUpLogging() {
        // Disable ORMLite logs
        com.j256.ormlite.logger.Logger.setGlobalLogLevel(Level.ERROR);
    }
}
