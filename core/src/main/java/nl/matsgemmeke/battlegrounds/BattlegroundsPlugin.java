package nl.matsgemmeke.battlegrounds;

import co.aikar.commands.PaperCommandManager;
import com.google.inject.Guice;
import com.google.inject.Injector;
import nl.matsgemmeke.battlegrounds.command.*;
import nl.matsgemmeke.battlegrounds.command.condition.ExistentSessionIdCondition;
import nl.matsgemmeke.battlegrounds.command.condition.ExistentWeaponIdCondition;
import nl.matsgemmeke.battlegrounds.command.condition.NonexistentSessionIdCondition;
import nl.matsgemmeke.battlegrounds.command.condition.TrainingModePresenceCondition;
import nl.matsgemmeke.battlegrounds.configuration.*;
import nl.matsgemmeke.battlegrounds.configuration.lang.LanguageConfiguration;
import nl.matsgemmeke.battlegrounds.event.EventDispatcher;
import nl.matsgemmeke.battlegrounds.event.handler.*;
import nl.matsgemmeke.battlegrounds.event.listener.EventListener;
import nl.matsgemmeke.battlegrounds.game.GameContext;
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
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.logging.Logger;

public class BattlegroundsPlugin extends JavaPlugin {

    private BattlegroundsConfiguration config;
    private EventDispatcher eventDispatcher;
    private GameContext trainingModeContext;
    private GameContextProvider contextProvider;
    private Injector injector;
    private InternalsProvider internals;
    private Logger logger;
    private TaskRunner taskRunner;
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
        this.setUpInternalsProvider();

        File dataFolder = this.getDataFolder();
        PluginManager pluginManager = this.getServer().getPluginManager();
        BattlegroundsModule module = new BattlegroundsModule(dataFolder, internals, this, pluginManager);

        injector = Guice.createInjector(module);

        contextProvider = new GameContextProvider();

        // Make sure the configuration folders are created
        File configFolder = this.getDataFolder();
        File configFile = new File(configFolder.getPath() + "/config.yml");
        InputStream configResource = this.getResource("config.yml");

        config = new BattlegroundsConfiguration(configFile, configResource);
        config.load();

        this.setUpTaskRunner();
        this.setUpEventSystem();
        this.setUpWeaponProvider();
        this.setUpTrainingMode();
        this.setUpEventHandlers();
        this.setUpTranslator();
        this.setUpCommands();
    }

    private void setUpCommands() {
        BattlegroundsCommand bgCommand = injector.getInstance(BattlegroundsCommand.class);

        // Add all subcommands to the battlegrounds command
        bgCommand.addSubcommand(injector.getInstance(CreateSessionCommand.class));
        bgCommand.addSubcommand(new GiveWeaponCommand(trainingModeContext, translator, weaponProvider));
        bgCommand.addSubcommand(injector.getInstance(ReloadCommand.class));
        bgCommand.addSubcommand(injector.getInstance(RemoveSessionCommand.class));
        bgCommand.addSubcommand(injector.getInstance(SetMainLobbyCommand.class));

        // Register the command to ACF
        PaperCommandManager commandManager = new PaperCommandManager(this);
        commandManager.registerCommand(bgCommand);

        // Register custom conditions to ACF
        commandManager.getCommandConditions().addCondition("training-mode-presence", new TrainingModePresenceCondition(trainingModeContext, translator));
        commandManager.getCommandConditions().addCondition(Integer.class, "existent-session-id", new ExistentSessionIdCondition(contextProvider, translator));
        commandManager.getCommandConditions().addCondition(String.class, "existent-weapon-id", new ExistentWeaponIdCondition(weaponProvider, translator));
        commandManager.getCommandConditions().addCondition(Integer.class, "nonexistent-session-id", new NonexistentSessionIdCondition(contextProvider, translator));
    }

    private void setUpEventHandlers() {
        eventDispatcher.registerEventHandler(BlockBurnEvent.class, new BlockBurnEventHandler());
        eventDispatcher.registerEventHandler(BlockSpreadEvent.class, new BlockSpreadEventHandler());
        eventDispatcher.registerEventHandler(EntityDamageByEntityEvent.class, new EntityDamageByEntityEventHandler(contextProvider));
        eventDispatcher.registerEventHandler(EntityPickupItemEvent.class, new EntityPickupItemEventHandler(contextProvider));
        eventDispatcher.registerEventHandler(PlayerDropItemEvent.class, new PlayerDropItemEventHandler(contextProvider));
        eventDispatcher.registerEventHandler(PlayerInteractEvent.class, new PlayerInteractEventHandler(contextProvider));
        eventDispatcher.registerEventHandler(PlayerItemHeldEvent.class, new PlayerItemHeldEventHandler(contextProvider));
        eventDispatcher.registerEventHandler(PlayerJoinEvent.class, new PlayerJoinEventHandler(config, trainingModeContext.getPlayerRegistry()));
        eventDispatcher.registerEventHandler(PlayerRespawnEvent.class, new PlayerRespawnEventHandler(contextProvider));
        eventDispatcher.registerEventHandler(PlayerSwapHandItemsEvent.class, new PlayerSwapHandItemsEventHandler(contextProvider));
    }

    private void setUpEventSystem() {
        PluginManager pluginManager = this.getServer().getPluginManager();

        eventDispatcher = new EventDispatcher(pluginManager, logger);

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
        this.taskRunner = new TaskRunner(this);
    }

    private void setUpTrainingMode() {
        TrainingModeFactory trainingModeFactory = new TrainingModeFactory(config, eventDispatcher, internals);
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
