package nl.matsgemmeke.battlegrounds;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import nl.matsgemmeke.battlegrounds.configuration.BattlegroundsConfiguration;
import nl.matsgemmeke.battlegrounds.configuration.BattlegroundsConfigurationProvider;
import nl.matsgemmeke.battlegrounds.configuration.data.DataConfiguration;
import nl.matsgemmeke.battlegrounds.configuration.data.DataConfigurationProvider;
import nl.matsgemmeke.battlegrounds.configuration.lang.LanguageConfiguration;
import nl.matsgemmeke.battlegrounds.configuration.lang.LanguageConfigurationProvider;
import nl.matsgemmeke.battlegrounds.event.EventDispatcher;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.training.TrainingModeContextProvider;
import nl.matsgemmeke.battlegrounds.item.creator.WeaponCreator;
import nl.matsgemmeke.battlegrounds.item.creator.WeaponCreatorProvider;
import nl.matsgemmeke.battlegrounds.text.Translator;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.io.File;

public class BattlegroundsModule implements Module {

    private File dataFolder;
    private InternalsProvider internals;
    private Plugin plugin;
    private PluginManager pluginManager;

    public BattlegroundsModule(
            File dataFolder,
            InternalsProvider internals,
            Plugin plugin,
            PluginManager pluginManager
    ) {
        this.dataFolder = dataFolder;
        this.internals = internals;
        this.plugin = plugin;
        this.pluginManager = pluginManager;
    }

    public void configure(Binder binder) {
        // Instance bindings
        binder.bind(InternalsProvider.class).toInstance(internals);
        binder.bind(Plugin.class).toInstance(plugin);
        binder.bind(PluginManager.class).toInstance(pluginManager);

        // Singleton bindings
        binder.bind(EventDispatcher.class).in(Singleton.class);
        binder.bind(GameContextProvider.class).in(Singleton.class);
        binder.bind(TaskRunner.class).in(Singleton.class);
        binder.bind(Translator.class).in(Singleton.class);

        // Provider bindings
        binder.bind(BattlegroundsConfiguration.class).toProvider(BattlegroundsConfigurationProvider.class);
        binder.bind(GameContext.class)
                .annotatedWith(Names.named("TrainingMode"))
                .toProvider(TrainingModeContextProvider.class)
                .in(Singleton.class);
        binder.bind(DataConfiguration.class).toProvider(DataConfigurationProvider.class);
        binder.bind(LanguageConfiguration.class).toProvider(LanguageConfigurationProvider.class);
        binder.bind(WeaponCreator.class).toProvider(WeaponCreatorProvider.class);

        // File bindings
        binder.bind(File.class).annotatedWith(Names.named("DataFolder")).toInstance(dataFolder);
        binder.bind(File.class).annotatedWith(Names.named("ItemsFolder")).toInstance(new File(dataFolder.getAbsoluteFile(), "items"));
        binder.bind(File.class).annotatedWith(Names.named("LangFolder")).toInstance(new File(dataFolder.getAbsoluteFile(), "lang"));
        binder.bind(File.class).annotatedWith(Names.named("SetupFolder")).toInstance(new File(dataFolder.getAbsoluteFile(), "setup"));
    }
}
