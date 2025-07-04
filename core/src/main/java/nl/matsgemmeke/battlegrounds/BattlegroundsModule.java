package nl.matsgemmeke.battlegrounds;

import com.google.inject.*;
import com.google.inject.Module;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.name.Names;
import nl.matsgemmeke.battlegrounds.configuration.BattlegroundsConfiguration;
import nl.matsgemmeke.battlegrounds.configuration.BattlegroundsConfigurationProvider;
import nl.matsgemmeke.battlegrounds.configuration.data.DataConfiguration;
import nl.matsgemmeke.battlegrounds.configuration.data.DataConfigurationProvider;
import nl.matsgemmeke.battlegrounds.configuration.lang.LanguageConfiguration;
import nl.matsgemmeke.battlegrounds.configuration.lang.LanguageConfigurationProvider;
import nl.matsgemmeke.battlegrounds.entity.DefaultGamePlayer;
import nl.matsgemmeke.battlegrounds.entity.DefaultGamePlayerFactory;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.event.EventDispatcher;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.component.CollisionDetector;
import nl.matsgemmeke.battlegrounds.game.component.DefaultCollisionDetector;
import nl.matsgemmeke.battlegrounds.game.component.entity.DefaultPlayerRegistry;
import nl.matsgemmeke.battlegrounds.game.component.entity.DefaultPlayerRegistryFactory;
import nl.matsgemmeke.battlegrounds.game.component.entity.PlayerRegistry;
import nl.matsgemmeke.battlegrounds.game.component.player.PlayerLifecycleHandler;
import nl.matsgemmeke.battlegrounds.game.component.storage.StatePersistenceHandler;
import nl.matsgemmeke.battlegrounds.game.openmode.component.player.OpenModePlayerLifecycleHandler;
import nl.matsgemmeke.battlegrounds.game.openmode.component.player.OpenModePlayerLifecycleHandlerFactory;
import nl.matsgemmeke.battlegrounds.game.openmode.component.storage.OpenModeStatePersistenceHandler;
import nl.matsgemmeke.battlegrounds.game.openmode.component.storage.OpenModeStatePersistenceHandlerFactory;
import nl.matsgemmeke.battlegrounds.item.creator.WeaponCreator;
import nl.matsgemmeke.battlegrounds.item.creator.WeaponCreatorProvider;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentHandlerFactory;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffect;
import nl.matsgemmeke.battlegrounds.item.effect.combustion.CombustionEffect;
import nl.matsgemmeke.battlegrounds.item.effect.combustion.CombustionEffectFactory;
import nl.matsgemmeke.battlegrounds.item.effect.simulation.GunFireSimulationEffect;
import nl.matsgemmeke.battlegrounds.item.effect.simulation.GunFireSimulationEffectFactory;
import nl.matsgemmeke.battlegrounds.item.effect.smoke.SmokeScreenEffect;
import nl.matsgemmeke.battlegrounds.item.effect.smoke.SmokeScreenEffectFactory;
import nl.matsgemmeke.battlegrounds.item.projectile.effect.ProjectileEffect;
import nl.matsgemmeke.battlegrounds.item.projectile.effect.trail.TrailEffect;
import nl.matsgemmeke.battlegrounds.item.projectile.effect.trail.TrailEffectFactory;
import nl.matsgemmeke.battlegrounds.item.reload.ReloadSystem;
import nl.matsgemmeke.battlegrounds.item.reload.magazine.MagazineReloadSystem;
import nl.matsgemmeke.battlegrounds.item.reload.magazine.MagazineReloadSystemFactory;
import nl.matsgemmeke.battlegrounds.item.reload.manual.ManualInsertionReloadSystem;
import nl.matsgemmeke.battlegrounds.item.reload.manual.ManualInsertionReloadSystemFactory;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.ProjectileLauncher;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.bullet.BulletLauncher;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.bullet.BulletLauncherFactory;
import nl.matsgemmeke.battlegrounds.scheduling.Scheduler;
import nl.matsgemmeke.battlegrounds.storage.state.equipment.EquipmentStateRepository;
import nl.matsgemmeke.battlegrounds.storage.state.equipment.sqlite.SqliteEquipmentStateRepositoryProvider;
import nl.matsgemmeke.battlegrounds.storage.state.gun.GunStateRepository;
import nl.matsgemmeke.battlegrounds.storage.state.gun.sqlite.SqliteGunStateRepositoryProvider;
import nl.matsgemmeke.battlegrounds.text.Translator;
import nl.matsgemmeke.battlegrounds.util.MetadataValueEditor;
import nl.matsgemmeke.battlegrounds.util.NamespacedKeyCreator;
import nl.matsgemmeke.battlegrounds.util.world.ParticleEffectSpawner;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.io.File;
import java.util.logging.Logger;

public class BattlegroundsModule implements Module {

    private final File dataFolder;
    private final InternalsProvider internals;
    private final Logger logger;
    private final Plugin plugin;
    private final PluginManager pluginManager;

    public BattlegroundsModule(File dataFolder, InternalsProvider internals, Logger logger, Plugin plugin, PluginManager pluginManager) {
        this.dataFolder = dataFolder;
        this.internals = internals;
        this.logger = logger;
        this.plugin = plugin;
        this.pluginManager = pluginManager;
    }

    public void configure(Binder binder) {
        // Instance bindings
        binder.bind(InternalsProvider.class).toInstance(internals);
        binder.bind(Logger.class).annotatedWith(Names.named("Battlegrounds")).toInstance(logger);
        binder.bind(Plugin.class).toInstance(plugin);
        binder.bind(PluginManager.class).toInstance(pluginManager);

        // Singleton bindings
        binder.bind(EventDispatcher.class).in(Singleton.class);
        binder.bind(GameContextProvider.class).in(Singleton.class);
        binder.bind(MetadataValueEditor.class).in(Singleton.class);
        binder.bind(NamespacedKeyCreator.class).in(Singleton.class);
        binder.bind(ParticleEffectSpawner.class).in(Singleton.class);
        binder.bind(Scheduler.class).in(Singleton.class);
        binder.bind(TaskRunner.class).in(Singleton.class);
        binder.bind(Translator.class).in(Singleton.class);

        // Provider bindings
        binder.bind(BattlegroundsConfiguration.class).toProvider(BattlegroundsConfigurationProvider.class);
        binder.bind(DataConfiguration.class).toProvider(DataConfigurationProvider.class);
        binder.bind(EquipmentStateRepository.class).toProvider(SqliteEquipmentStateRepositoryProvider.class).in(Singleton.class);
        binder.bind(GunStateRepository.class).toProvider(SqliteGunStateRepositoryProvider.class).in(Singleton.class);
        binder.bind(LanguageConfiguration.class).toProvider(LanguageConfigurationProvider.class);
        binder.bind(WeaponCreator.class).toProvider(WeaponCreatorProvider.class).in(Singleton.class);

        // Component bindings
        binder.bind(CollisionDetector.class).to(DefaultCollisionDetector.class);

        binder.install(new FactoryModuleBuilder()
                .implement(PlayerLifecycleHandler.class, OpenModePlayerLifecycleHandler.class)
                .build(OpenModePlayerLifecycleHandlerFactory.class));

        // Factory bindings
        binder.install(new FactoryModuleBuilder()
                .build(DeploymentHandlerFactory.class));

        binder.install(new FactoryModuleBuilder()
                .implement(GamePlayer.class, DefaultGamePlayer.class)
                .build(DefaultGamePlayerFactory.class));

        binder.install(new FactoryModuleBuilder()
                .implement(ItemEffect.class, CombustionEffect.class)
                .build(CombustionEffectFactory.class));
        binder.install(new FactoryModuleBuilder()
                .implement(ItemEffect.class, GunFireSimulationEffect.class)
                .build(GunFireSimulationEffectFactory.class));
        binder.install(new FactoryModuleBuilder()
                .implement(ItemEffect.class, SmokeScreenEffect.class)
                .build(SmokeScreenEffectFactory.class));

        binder.install(new FactoryModuleBuilder()
                .implement(PlayerRegistry.class, DefaultPlayerRegistry.class)
                .build(DefaultPlayerRegistryFactory.class));
        binder.install(new FactoryModuleBuilder()
                .implement(StatePersistenceHandler.class, OpenModeStatePersistenceHandler.class)
                .build(OpenModeStatePersistenceHandlerFactory.class));

        binder.install(new FactoryModuleBuilder()
                .implement(ProjectileEffect.class, TrailEffect.class)
                .build(TrailEffectFactory.class));

        binder.install(new FactoryModuleBuilder()
                .implement(ProjectileLauncher.class, BulletLauncher.class)
                .build(BulletLauncherFactory.class));

        binder.install(new FactoryModuleBuilder()
                .implement(ReloadSystem.class, MagazineReloadSystem.class)
                .build(MagazineReloadSystemFactory.class));
        binder.install(new FactoryModuleBuilder()
                .implement(ReloadSystem.class, ManualInsertionReloadSystem.class)
                .build(ManualInsertionReloadSystemFactory.class));

        // File bindings
        binder.bind(File.class).annotatedWith(Names.named("DataFolder")).toInstance(dataFolder);
        binder.bind(File.class).annotatedWith(Names.named("ItemsFolder")).toInstance(new File(dataFolder.getAbsoluteFile(), "items"));
        binder.bind(File.class).annotatedWith(Names.named("LangFolder")).toInstance(new File(dataFolder.getAbsoluteFile(), "lang"));
        binder.bind(File.class).annotatedWith(Names.named("SetupFolder")).toInstance(new File(dataFolder.getAbsoluteFile(), "setup"));
    }
}
