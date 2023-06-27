package com.github.matsgemmeke.battlegrounds;

import co.aikar.commands.PaperCommandManager;
import com.github.matsgemmeke.battlegrounds.api.BattleContextProvider;
import com.github.matsgemmeke.battlegrounds.api.Battlegrounds;
import com.github.matsgemmeke.battlegrounds.api.configuration.BattlegroundsConfig;
import com.github.matsgemmeke.battlegrounds.api.game.FreemodeContext;
import com.github.matsgemmeke.battlegrounds.command.*;
import com.github.matsgemmeke.battlegrounds.command.condition.ExistentGameIdCondition;
import com.github.matsgemmeke.battlegrounds.command.condition.ExistentWeaponIdCondition;
import com.github.matsgemmeke.battlegrounds.command.condition.FreemodePresenceCondition;
import com.github.matsgemmeke.battlegrounds.command.condition.NonexistentGameIdCondition;
import com.github.matsgemmeke.battlegrounds.configuration.BattleItemConfiguration;
import com.github.matsgemmeke.battlegrounds.configuration.BattlegroundsFileConfiguration;
import com.github.matsgemmeke.battlegrounds.configuration.GeneralDataConfiguration;
import com.github.matsgemmeke.battlegrounds.configuration.LanguageConfiguration;
import com.github.matsgemmeke.battlegrounds.event.EventBus;
import com.github.matsgemmeke.battlegrounds.event.EventDispatcher;
import com.github.matsgemmeke.battlegrounds.event.handler.PlayerInteractEventHandler;
import com.github.matsgemmeke.battlegrounds.event.handler.PlayerJoinEventHandler;
import com.github.matsgemmeke.battlegrounds.event.listener.EventListener;
import com.github.matsgemmeke.battlegrounds.game.DefaultFreemodeContext;
import com.github.matsgemmeke.battlegrounds.game.GameContextFactory;
import com.github.matsgemmeke.battlegrounds.item.BlockCollisionChecker;
import com.github.matsgemmeke.battlegrounds.item.WeaponProvider;
import com.github.matsgemmeke.battlegrounds.item.factory.FirearmFactory;
import com.github.matsgemmeke.battlegrounds.item.factory.FiringModeFactory;
import com.github.matsgemmeke.battlegrounds.locale.Translator;
import org.bukkit.event.player.PlayerInteractEvent;
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

    private BattleContextProvider contextProvider;
    private BattlegroundsConfig config;
    private FreemodeContext freemodeContext;
    private TaskRunner taskRunner;
    private Translator translator;
    private WeaponProvider weaponProvider;

    @NotNull
    public BattlegroundsConfig getBattlegroundsConfig() {
        return config;
    }

    @NotNull
    public BattleContextProvider getContextProvider() {
        return contextProvider;
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
        contextProvider = new DefaultBattleContextProvider();

        // Make sure the configuration folders are created
        File configFolder = this.getDataFolder();
        File configFile = new File(configFolder.getPath() + "/config.yml");
        InputStream configResource = this.getResource("config.yml");

        config = new BattlegroundsFileConfiguration(configFile, configResource);
        config.load();

        this.setUpTaskRunner();

        File gunConfigFile = new File(configFolder.getPath() + "/items/guns.yml");

        BattleItemConfiguration gunsConfiguration = new BattleItemConfiguration(gunConfigFile, this.getResource("items/guns.yml"));
        gunsConfiguration.load();

        FiringModeFactory firingModeFactory = new FiringModeFactory(taskRunner);
        FirearmFactory firearmFactory = new FirearmFactory(config, gunsConfiguration, firingModeFactory);

        weaponProvider = new WeaponProvider();
        weaponProvider.addWeaponFactory(gunsConfiguration, firearmFactory);

        this.setUpFreemode();
        this.setUpEvents();
        this.setUpTranslator();
        this.setUpCommands();
    }

    private void setUpCommands() {
        File dataFolder = new File(this.getDataFolder().getPath() + "/data");
        File generalDataFile = new File(dataFolder.getPath() + "/data/general.yml");

        GameContextFactory gameContextFactory = new GameContextFactory(taskRunner, dataFolder);

        GeneralDataConfiguration generalData = new GeneralDataConfiguration(generalDataFile);
        generalData.load();

        BattlegroundsCommand bgCommand = new BattlegroundsCommand(translator);

        // Add all subcommands to the battlegrounds command
        bgCommand.addSubcommand(new CreateGameCommand(contextProvider, gameContextFactory, translator));
        bgCommand.addSubcommand(new GiveWeaponCommand(freemodeContext, weaponProvider, translator));
        bgCommand.addSubcommand(new ReloadCommand(config, translator));
        bgCommand.addSubcommand(new RemoveGameCommand(contextProvider, taskRunner, translator));
        bgCommand.addSubcommand(new SetMainLobbyCommand(generalData, translator));

        // Register the command to ACF
        PaperCommandManager commandManager = new PaperCommandManager(this);
        commandManager.registerCommand(bgCommand);

        // Register custom conditions to ACF
        commandManager.getCommandConditions().addCondition("freemode-presence", new FreemodePresenceCondition(freemodeContext, translator));
        commandManager.getCommandConditions().addCondition(Integer.class, "existent-game-id", new ExistentGameIdCondition(contextProvider, translator));
        commandManager.getCommandConditions().addCondition(String.class, "existent-weapon-id", new ExistentWeaponIdCondition(weaponProvider, translator));
        commandManager.getCommandConditions().addCondition(Integer.class, "nonexistent-game-id", new NonexistentGameIdCondition(contextProvider, translator));
    }

    private void setUpEvents() {
        PluginManager pluginManager = this.getServer().getPluginManager();

        EventDispatcher eventDispatcher = new EventDispatcher(pluginManager);
        eventDispatcher.registerEventBus(PlayerInteractEvent.class, new EventBus<>(new PlayerInteractEventHandler(contextProvider)));
        eventDispatcher.registerEventBus(PlayerJoinEvent.class, new EventBus<>(new PlayerJoinEventHandler(freemodeContext)));

        EventListener eventListener = new EventListener(eventDispatcher);

        pluginManager.registerEvents(eventListener, this);
    }

    private void setUpFreemode() {
        BlockCollisionChecker collisionChecker = new BlockCollisionChecker();

        freemodeContext = new DefaultFreemodeContext(collisionChecker, taskRunner);

        contextProvider.addFreemodeContext(freemodeContext);
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
