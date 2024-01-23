package com.github.matsgemmeke.battlegrounds;

import co.aikar.commands.PaperCommandManager;
import com.github.matsgemmeke.battlegrounds.api.Battlegrounds;
import com.github.matsgemmeke.battlegrounds.api.GameProvider;
import com.github.matsgemmeke.battlegrounds.api.configuration.BattlegroundsConfig;
import com.github.matsgemmeke.battlegrounds.api.game.GameContext;
import com.github.matsgemmeke.battlegrounds.api.game.TrainingMode;
import com.github.matsgemmeke.battlegrounds.command.*;
import com.github.matsgemmeke.battlegrounds.command.condition.ExistentSessionIdCondition;
import com.github.matsgemmeke.battlegrounds.command.condition.ExistentWeaponIdCondition;
import com.github.matsgemmeke.battlegrounds.command.condition.NonexistentSessionIdCondition;
import com.github.matsgemmeke.battlegrounds.command.condition.TrainingModePresenceCondition;
import com.github.matsgemmeke.battlegrounds.configuration.BattlegroundsFileConfiguration;
import com.github.matsgemmeke.battlegrounds.configuration.GeneralDataConfiguration;
import com.github.matsgemmeke.battlegrounds.configuration.ItemConfiguration;
import com.github.matsgemmeke.battlegrounds.configuration.LanguageConfiguration;
import com.github.matsgemmeke.battlegrounds.event.EventBus;
import com.github.matsgemmeke.battlegrounds.event.EventDispatcher;
import com.github.matsgemmeke.battlegrounds.event.handler.*;
import com.github.matsgemmeke.battlegrounds.event.listener.EventListener;
import com.github.matsgemmeke.battlegrounds.game.BlockCollisionChecker;
import com.github.matsgemmeke.battlegrounds.game.DefaultTrainingMode;
import com.github.matsgemmeke.battlegrounds.game.DefaultTrainingModeContext;
import com.github.matsgemmeke.battlegrounds.game.SessionFactory;
import com.github.matsgemmeke.battlegrounds.item.WeaponProvider;
import com.github.matsgemmeke.battlegrounds.item.factory.FireModeFactory;
import com.github.matsgemmeke.battlegrounds.item.factory.FirearmFactory;
import com.github.matsgemmeke.battlegrounds.item.factory.ReloadSystemFactory;
import com.github.matsgemmeke.battlegrounds.item.recoil.RecoilSystemFactory;
import com.github.matsgemmeke.battlegrounds.locale.Translator;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.InputStream;
import java.util.Locale;

public class BattlegroundsPlugin extends JavaPlugin implements Battlegrounds {

    private BattlegroundsConfig config;
    private GameContext trainingContext;
    private GameProvider gameProvider;
    private InternalsProvider internals;
    private TaskRunner taskRunner;
    private TrainingMode trainingMode;
    private Translator translator;
    private WeaponProvider weaponProvider;

    @NotNull
    public BattlegroundsConfig getBattlegroundsConfig() {
        return config;
    }

    @NotNull
    public GameProvider getGameProvider() {
        return gameProvider;
    }

    @Override
    public void onEnable() {
        try {
            this.startPlugin();
        } catch (StartupFailedException e) {
            this.getLogger().severe("An error occurred while enabling Battlegrounds v" + this.getDescription().getVersion());
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }

        this.getLogger().info("Successfully started Battlegrounds v" + this.getDescription().getVersion());
    }

    private void startPlugin() throws StartupFailedException {
        gameProvider = new DefaultGameProvider();

        // Make sure the configuration folders are created
        File configFolder = this.getDataFolder();
        File configFile = new File(configFolder.getPath() + "/config.yml");
        InputStream configResource = this.getResource("config.yml");

        config = new BattlegroundsFileConfiguration(configFile, configResource);
        config.load();

        this.setUpInternalsProvider();
        this.setUpTaskRunner();

        File gunConfigFile = new File(configFolder.getPath() + "/items/guns.yml");

        ItemConfiguration gunsConfiguration = new ItemConfiguration(gunConfigFile, this.getResource("items/guns.yml"));
        gunsConfiguration.load();

        FireModeFactory fireModeFactory = new FireModeFactory(taskRunner);
        RecoilSystemFactory recoilSystemFactory = new RecoilSystemFactory(internals);
        ReloadSystemFactory reloadSystemFactory = new ReloadSystemFactory(taskRunner);
        FirearmFactory firearmFactory = new FirearmFactory(config, fireModeFactory, internals, gunsConfiguration, recoilSystemFactory, reloadSystemFactory);

        weaponProvider = new WeaponProvider();
        weaponProvider.addWeaponFactory(gunsConfiguration, firearmFactory);

        this.setUpTrainingMode();
        this.setUpEvents();
        this.setUpTranslator();
        this.setUpCommands();
    }

    private void setUpCommands() {
        File dataFolder = new File(this.getDataFolder().getPath() + "/data");
        File generalDataFile = new File(dataFolder.getPath() + "/general.yml");

        SessionFactory sessionFactory = new SessionFactory(dataFolder, internals);

        GeneralDataConfiguration generalData = new GeneralDataConfiguration(generalDataFile);
        generalData.load();

        BattlegroundsCommand bgCommand = new BattlegroundsCommand(translator);

        // Add all subcommands to the battlegrounds command
        bgCommand.addSubcommand(new CreateSessionCommand(gameProvider, sessionFactory, translator));
        bgCommand.addSubcommand(new GiveWeaponCommand(trainingMode, trainingContext, translator, weaponProvider));
        bgCommand.addSubcommand(new ReloadCommand(config, translator));
        bgCommand.addSubcommand(new RemoveSessionCommand(gameProvider, taskRunner, translator));
        bgCommand.addSubcommand(new SetMainLobbyCommand(generalData, translator));

        // Register the command to ACF
        PaperCommandManager commandManager = new PaperCommandManager(this);
        commandManager.registerCommand(bgCommand);

        // Register custom conditions to ACF
        commandManager.getCommandConditions().addCondition("training-mode-presence", new TrainingModePresenceCondition(trainingMode, translator));
        commandManager.getCommandConditions().addCondition(Integer.class, "existent-session-id", new ExistentSessionIdCondition(gameProvider, translator));
        commandManager.getCommandConditions().addCondition(String.class, "existent-weapon-id", new ExistentWeaponIdCondition(weaponProvider, translator));
        commandManager.getCommandConditions().addCondition(Integer.class, "nonexistent-session-id", new NonexistentSessionIdCondition(gameProvider, translator));
    }

    private void setUpEvents() {
        PluginManager pluginManager = this.getServer().getPluginManager();

        EventDispatcher eventDispatcher = new EventDispatcher(pluginManager);
        eventDispatcher.registerEventBus(EntityPickupItemEvent.class, new EventBus<>(new EntityPickupItemEventHandler(gameProvider)));
        eventDispatcher.registerEventBus(PlayerDropItemEvent.class, new EventBus<>(new PlayerDropItemEventHandler(gameProvider)));
        eventDispatcher.registerEventBus(PlayerInteractEvent.class, new EventBus<>(new PlayerInteractEventHandler(gameProvider)));
        eventDispatcher.registerEventBus(PlayerItemHeldEvent.class, new EventBus<>(new PlayerItemHeldEventHandler(gameProvider)));
        eventDispatcher.registerEventBus(PlayerJoinEvent.class, new EventBus<>(new PlayerJoinEventHandler(trainingMode)));

        EventListener eventListener = new EventListener(eventDispatcher);

        pluginManager.registerEvents(eventListener, this);
    }

    private void setUpTrainingMode() {
        BlockCollisionChecker collisionChecker = new BlockCollisionChecker();

        trainingMode = new DefaultTrainingMode(internals);
        trainingContext = new DefaultTrainingModeContext(collisionChecker);

        gameProvider.assignTrainingMode(trainingMode);
    }

    private void setUpInternalsProvider() throws StartupFailedException {
        try {
            String packageName = BattlegroundsPlugin.class.getPackage().getName();
            String internalsName = getServer().getClass().getPackage().getName().split("\\.")[3];
            internals = (InternalsProvider) Class.forName(packageName + ".nms." + internalsName + "." + internalsName.toUpperCase()).newInstance();
        } catch (Exception e) {
            throw new StartupFailedException("Failed to find a valid implementation for this server version");
        }
    }

    private void setUpTaskRunner() {
        this.taskRunner = new TaskRunner() {
            @NotNull
            public BukkitTask runTaskLater(@NotNull BukkitRunnable runnable, long delay) {
                return runnable.runTaskLater(BattlegroundsPlugin.this, delay);
            }

            @NotNull
            public BukkitTask runTaskLater(@NotNull Runnable runnable, long delay) {
                return getServer().getScheduler().runTaskLater(BattlegroundsPlugin.this, runnable, delay);
            }

            @NotNull
            public BukkitTask runTaskTimer(@NotNull BukkitRunnable runnable, long delay, long period) {
                return runnable.runTaskTimer(BattlegroundsPlugin.this, delay, period);
            }

            @NotNull
            public BukkitTask runTaskTimer(@NotNull Runnable runnable, long delay, long period) {
                return getServer().getScheduler().runTaskTimer(BattlegroundsPlugin.this, runnable, delay, period);
            }
        };
    }

    private void setUpTranslator() {
        String language = config.getLanguage();
        String fileName = "lang_" + language + ".yml";

        File langFile = getDataFolder().toPath().resolve("lang").resolve(fileName).toFile();
        InputStream resource = getResource("lang/" + fileName);
        Locale locale = Locale.forLanguageTag(language);

        LanguageConfiguration languageConfiguration = new LanguageConfiguration(langFile, resource, locale);
        languageConfiguration.load();

        translator = new Translator(languageConfiguration);
    }
}
