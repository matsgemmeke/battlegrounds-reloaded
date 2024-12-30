package nl.matsgemmeke.battlegrounds;

import co.aikar.commands.PaperCommandManager;
import nl.matsgemmeke.battlegrounds.command.*;
import nl.matsgemmeke.battlegrounds.command.condition.ExistentSessionIdCondition;
import nl.matsgemmeke.battlegrounds.command.condition.ExistentWeaponIdCondition;
import nl.matsgemmeke.battlegrounds.command.condition.NonexistentSessionIdCondition;
import nl.matsgemmeke.battlegrounds.command.condition.TrainingModePresenceCondition;
import nl.matsgemmeke.battlegrounds.configuration.*;
import nl.matsgemmeke.battlegrounds.event.EventBus;
import nl.matsgemmeke.battlegrounds.event.EventDispatcher;
import nl.matsgemmeke.battlegrounds.event.handler.*;
import nl.matsgemmeke.battlegrounds.event.listener.EventListener;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.session.SessionFactory;
import nl.matsgemmeke.battlegrounds.game.training.TrainingMode;
import nl.matsgemmeke.battlegrounds.game.training.TrainingModeFactory;
import nl.matsgemmeke.battlegrounds.item.WeaponProvider;
import nl.matsgemmeke.battlegrounds.item.WeaponProviderLoader;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectFactory;
import nl.matsgemmeke.battlegrounds.item.effect.activation.ItemEffectActivationFactory;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentFactory;
import nl.matsgemmeke.battlegrounds.item.gun.FirearmFactory;
import nl.matsgemmeke.battlegrounds.item.recoil.RecoilProducerFactory;
import nl.matsgemmeke.battlegrounds.item.reload.ReloadSystemFactory;
import nl.matsgemmeke.battlegrounds.item.shoot.FireModeFactory;
import nl.matsgemmeke.battlegrounds.item.shoot.spread.SpreadPatternFactory;
import nl.matsgemmeke.battlegrounds.text.Translator;
import nl.matsgemmeke.battlegrounds.util.MetadataValueEditor;
import nl.matsgemmeke.battlegrounds.util.NamespacedKeyCreator;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.*;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.logging.Logger;

public class BattlegroundsPlugin extends JavaPlugin {

    private BattlegroundsConfiguration config;
    private GameContext trainingModeContext;
    private GameContextProvider contextProvider;
    private InternalsProvider internals;
    private Logger logger;
    private TaskRunner taskRunner;
    private TrainingMode trainingMode;
    private Translator translator;
    private WeaponProvider weaponProvider;

    @NotNull
    public BattlegroundsConfiguration getBattlegroundsConfig() {
        return config;
    }

    @NotNull
    public GameContextProvider getContextProvider() {
        return contextProvider;
    }

    @Override
    public void onEnable() {
        logger = this.getLogger();

        try {
            this.startPlugin();
        } catch (StartupFailedException e) {
            logger.severe("An error occurred while enabling Battlegrounds v" + this.getDescription().getVersion());
            logger.severe(e.getMessage());
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }

        logger.info("Successfully started Battlegrounds v" + this.getDescription().getVersion());
    }

    private void startPlugin() throws StartupFailedException {
        contextProvider = new GameContextProvider();

        // Make sure the configuration folders are created
        File configFolder = this.getDataFolder();
        File configFile = new File(configFolder.getPath() + "/config.yml");
        InputStream configResource = this.getResource("config.yml");

        config = new BattlegroundsConfiguration(configFile, configResource);
        config.load();

        this.setUpInternalsProvider();
        this.setUpTaskRunner();
        this.setUpWeaponProvider();
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
        bgCommand.addSubcommand(new CreateSessionCommand(contextProvider, sessionFactory, translator));
        bgCommand.addSubcommand(new GiveWeaponCommand(trainingModeContext, translator, weaponProvider));
        bgCommand.addSubcommand(new ReloadCommand(config, translator));
        bgCommand.addSubcommand(new RemoveSessionCommand(contextProvider, taskRunner, translator));
        bgCommand.addSubcommand(new SetMainLobbyCommand(generalData, translator));

        // Register the command to ACF
        PaperCommandManager commandManager = new PaperCommandManager(this);
        commandManager.registerCommand(bgCommand);

        // Register custom conditions to ACF
        commandManager.getCommandConditions().addCondition("training-mode-presence", new TrainingModePresenceCondition(trainingModeContext, translator));
        commandManager.getCommandConditions().addCondition(Integer.class, "existent-session-id", new ExistentSessionIdCondition(contextProvider, translator));
        commandManager.getCommandConditions().addCondition(String.class, "existent-weapon-id", new ExistentWeaponIdCondition(weaponProvider, translator));
        commandManager.getCommandConditions().addCondition(Integer.class, "nonexistent-session-id", new NonexistentSessionIdCondition(contextProvider, translator));
    }

    private void setUpEvents() {
        PluginManager pluginManager = this.getServer().getPluginManager();

        EventDispatcher eventDispatcher = new EventDispatcher(pluginManager);
        eventDispatcher.registerEventBus(BlockBurnEvent.class, new EventBus<>(new BlockBurnEventHandler()));
        eventDispatcher.registerEventBus(BlockSpreadEvent.class, new EventBus<>(new BlockSpreadEventHandler()));
        eventDispatcher.registerEventBus(EntityDamageByEntityEvent.class, new EventBus<>(new EntityDamageByEntityEventHandler(contextProvider)));
        eventDispatcher.registerEventBus(EntityPickupItemEvent.class, new EventBus<>(new EntityPickupItemEventHandler(contextProvider)));
        eventDispatcher.registerEventBus(PlayerDropItemEvent.class, new EventBus<>(new PlayerDropItemEventHandler(contextProvider)));
        eventDispatcher.registerEventBus(PlayerInteractEvent.class, new EventBus<>(new PlayerInteractEventHandler(contextProvider)));
        eventDispatcher.registerEventBus(PlayerItemHeldEvent.class, new EventBus<>(new PlayerItemHeldEventHandler(contextProvider)));
        eventDispatcher.registerEventBus(PlayerJoinEvent.class, new EventBus<>(new PlayerJoinEventHandler(config, trainingModeContext.getPlayerRegistry())));
        eventDispatcher.registerEventBus(PlayerRespawnEvent.class, new EventBus<>(new PlayerRespawnEventHandler(contextProvider)));
        eventDispatcher.registerEventBus(PlayerSwapHandItemsEvent.class, new EventBus<>(new PlayerSwapHandItemsEventHandler(contextProvider)));

        EventListener eventListener = new EventListener(eventDispatcher);

        pluginManager.registerEvents(eventListener, this);
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

    private void setUpTrainingMode() {
        TrainingModeFactory trainingModeFactory = new TrainingModeFactory(config, internals);
        TrainingMode trainingMode = trainingModeFactory.make();

        trainingModeContext = trainingMode.getContext();

        contextProvider.assignTrainingModeContext(trainingModeContext);
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

    private void setUpWeaponProvider() throws StartupFailedException {
        FireModeFactory fireModeFactory = new FireModeFactory(taskRunner);
        RecoilProducerFactory recoilProducerFactory = new RecoilProducerFactory(config);
        NamespacedKeyCreator keyCreator = new NamespacedKeyCreator(this);
        ReloadSystemFactory reloadSystemFactory = new ReloadSystemFactory(taskRunner);
        SpreadPatternFactory spreadPatternFactory = new SpreadPatternFactory();
        FirearmFactory firearmFactory = new FirearmFactory(config, fireModeFactory, keyCreator, recoilProducerFactory, reloadSystemFactory, spreadPatternFactory);

        MetadataValueEditor metadataValueEditor = new MetadataValueEditor(this);
        ItemEffectFactory effectFactory = new ItemEffectFactory(metadataValueEditor, taskRunner);
        ItemEffectActivationFactory effectActivationFactory = new ItemEffectActivationFactory(taskRunner);
        EquipmentFactory equipmentFactory = new EquipmentFactory(effectFactory, effectActivationFactory, keyCreator, taskRunner);

        File itemsDirectory = new File(this.getDataFolder() + "/items");
        WeaponProviderLoader loader = new WeaponProviderLoader(equipmentFactory, firearmFactory);

        try {
            weaponProvider = loader.loadWeaponProvider(itemsDirectory);
        } catch (IOException e) {
            throw new StartupFailedException("Unable to create copies of the items resources");
        } catch (IllegalArgumentException e) {
            throw new StartupFailedException("The jar file is missing the required resource files");
        }
    }
}
