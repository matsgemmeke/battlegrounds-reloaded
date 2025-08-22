package nl.matsgemmeke.battlegrounds;

import com.google.inject.*;
import com.google.inject.Module;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.multibindings.MapBinder;
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
import nl.matsgemmeke.battlegrounds.game.*;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.game.component.DefaultAudioEmitter;
import nl.matsgemmeke.battlegrounds.game.component.TargetFinder;
import nl.matsgemmeke.battlegrounds.game.component.TargetFinderProvider;
import nl.matsgemmeke.battlegrounds.game.component.collision.CollisionDetector;
import nl.matsgemmeke.battlegrounds.game.component.collision.DefaultCollisionDetector;
import nl.matsgemmeke.battlegrounds.game.component.damage.DamageProcessor;
import nl.matsgemmeke.battlegrounds.game.component.damage.DamageProcessorProvider;
import nl.matsgemmeke.battlegrounds.game.component.deploy.DefaultDeploymentInfoProvider;
import nl.matsgemmeke.battlegrounds.game.component.deploy.DeploymentInfoProvider;
import nl.matsgemmeke.battlegrounds.game.component.entity.DefaultPlayerRegistry;
import nl.matsgemmeke.battlegrounds.game.component.entity.PlayerRegistry;
import nl.matsgemmeke.battlegrounds.game.component.item.*;
import nl.matsgemmeke.battlegrounds.game.component.player.PlayerLifecycleHandler;
import nl.matsgemmeke.battlegrounds.game.component.player.PlayerLifecycleHandlerProvider;
import nl.matsgemmeke.battlegrounds.game.component.spawn.RespawnHandler;
import nl.matsgemmeke.battlegrounds.game.component.spawn.RespawnHandlerProvider;
import nl.matsgemmeke.battlegrounds.game.component.spawn.SpawnPointRegistry;
import nl.matsgemmeke.battlegrounds.game.component.spawn.SpawnPointRegistryProvider;
import nl.matsgemmeke.battlegrounds.game.component.storage.StatePersistenceHandler;
import nl.matsgemmeke.battlegrounds.game.component.storage.StatePersistenceHandlerProvider;
import nl.matsgemmeke.battlegrounds.game.openmode.component.OpenModeTargetFinder;
import nl.matsgemmeke.battlegrounds.game.openmode.component.damage.OpenModeDamageProcessor;
import nl.matsgemmeke.battlegrounds.game.openmode.component.storage.OpenModeStatePersistenceHandler;
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
import nl.matsgemmeke.battlegrounds.item.gun.DefaultFirearmFactory;
import nl.matsgemmeke.battlegrounds.item.projectile.effect.ProjectileEffect;
import nl.matsgemmeke.battlegrounds.item.projectile.effect.trail.TrailEffect;
import nl.matsgemmeke.battlegrounds.item.projectile.effect.trail.TrailEffectFactory;
import nl.matsgemmeke.battlegrounds.item.reload.ReloadSystem;
import nl.matsgemmeke.battlegrounds.item.reload.magazine.MagazineReloadSystem;
import nl.matsgemmeke.battlegrounds.item.reload.magazine.MagazineReloadSystemFactory;
import nl.matsgemmeke.battlegrounds.item.reload.manual.ManualInsertionReloadSystem;
import nl.matsgemmeke.battlegrounds.item.reload.manual.ManualInsertionReloadSystemFactory;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.ProjectileLauncher;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.fireball.FireballLauncher;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.fireball.FireballLauncherFactory;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.hitscan.HitscanLauncher;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.hitscan.HitscanLauncherFactory;
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

        // Game scope bindings
        GameScope gameScope = new GameScope();

        binder.bind(GameScope.class).toInstance(gameScope);
        binder.bindScope(GameScoped.class, gameScope);

        MapBinder<GameContextType, DamageProcessor> damageProcessorMapBinder = MapBinder.newMapBinder(binder, GameContextType.class, DamageProcessor.class);
        damageProcessorMapBinder.addBinding(GameContextType.OPEN_MODE).to(OpenModeDamageProcessor.class);

        MapBinder<GameContextType, StatePersistenceHandler> statePersistenceHandlerMapBinder = MapBinder.newMapBinder(binder, GameContextType.class, StatePersistenceHandler.class);
        statePersistenceHandlerMapBinder.addBinding(GameContextType.OPEN_MODE).to(OpenModeStatePersistenceHandler.class);

        MapBinder<GameContextType, TargetFinder> targetFinderMapBinder = MapBinder.newMapBinder(binder, GameContextType.class, TargetFinder.class);
        targetFinderMapBinder.addBinding(GameContextType.OPEN_MODE).to(OpenModeTargetFinder.class);

        binder.bind(AudioEmitter.class).to(DefaultAudioEmitter.class).in(GameScoped.class);
        binder.bind(CollisionDetector.class).to(DefaultCollisionDetector.class).in(GameScoped.class);
        binder.bind(DamageProcessor.class).toProvider(DamageProcessorProvider.class).in(GameScoped.class);
        binder.bind(DeploymentInfoProvider.class).to(DefaultDeploymentInfoProvider.class).in(GameScoped.class);
        binder.bind(EquipmentRegistry.class).to(DefaultEquipmentRegistry.class).in(GameScoped.class);
        binder.bind(GameKey.class).toProvider(GameKeyProvider.class).in(GameScoped.class);
        binder.bind(GunRegistry.class).to(DefaultGunRegistry.class).in(GameScoped.class);
        binder.bind(ItemLifecycleHandler.class).to(DefaultItemLifecycleHandler.class);
        binder.bind(PlayerLifecycleHandler.class).toProvider(PlayerLifecycleHandlerProvider.class).in(GameScoped.class);
        binder.bind(PlayerRegistry.class).to(DefaultPlayerRegistry.class).in(GameScoped.class);
        binder.bind(RespawnHandler.class).toProvider(RespawnHandlerProvider.class).in(GameScoped.class);
        binder.bind(SpawnPointRegistry.class).toProvider(SpawnPointRegistryProvider.class).in(GameScoped.class);
        binder.bind(StatePersistenceHandler.class).toProvider(StatePersistenceHandlerProvider.class).in(GameScoped.class);
        binder.bind(TargetFinder.class).toProvider(TargetFinderProvider.class).in(GameScoped.class);

        // Factory bindings
        binder.install(new FactoryModuleBuilder()
                .build(DeploymentHandlerFactory.class));

        binder.install(new FactoryModuleBuilder()
                .build(DefaultFirearmFactory.class));

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
                .implement(ProjectileEffect.class, TrailEffect.class)
                .build(TrailEffectFactory.class));

        binder.install(new FactoryModuleBuilder()
                .implement(ProjectileLauncher.class, FireballLauncher.class)
                .build(FireballLauncherFactory.class));
        binder.install(new FactoryModuleBuilder()
                .implement(ProjectileLauncher.class, HitscanLauncher.class)
                .build(HitscanLauncherFactory.class));

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
