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
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.component.CollisionDetector;
import nl.matsgemmeke.battlegrounds.game.component.DefaultCollisionDetector;
import nl.matsgemmeke.battlegrounds.game.component.entity.DefaultPlayerRegistry;
import nl.matsgemmeke.battlegrounds.game.component.entity.DefaultPlayerRegistryFactory;
import nl.matsgemmeke.battlegrounds.game.component.entity.PlayerRegistry;
import nl.matsgemmeke.battlegrounds.game.training.TrainingModeGameKeyProvider;
import nl.matsgemmeke.battlegrounds.item.controls.ItemFunction;
import nl.matsgemmeke.battlegrounds.item.creator.WeaponCreator;
import nl.matsgemmeke.battlegrounds.item.creator.WeaponCreatorProvider;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffect;
import nl.matsgemmeke.battlegrounds.item.effect.activation.DelayedActivation;
import nl.matsgemmeke.battlegrounds.item.effect.activation.DelayedActivationFactory;
import nl.matsgemmeke.battlegrounds.item.effect.activation.ItemEffectActivation;
import nl.matsgemmeke.battlegrounds.item.effect.activation.trigger.Trigger;
import nl.matsgemmeke.battlegrounds.item.effect.activation.trigger.enemy.EnemyProximityTrigger;
import nl.matsgemmeke.battlegrounds.item.effect.activation.trigger.enemy.EnemyProximityTriggerFactory;
import nl.matsgemmeke.battlegrounds.item.effect.activation.trigger.floor.FloorHitTrigger;
import nl.matsgemmeke.battlegrounds.item.effect.activation.trigger.floor.FloorHitTriggerFactory;
import nl.matsgemmeke.battlegrounds.item.effect.combustion.CombustionEffect;
import nl.matsgemmeke.battlegrounds.item.effect.combustion.CombustionEffectFactory;
import nl.matsgemmeke.battlegrounds.item.effect.simulation.GunFireSimulationEffect;
import nl.matsgemmeke.battlegrounds.item.effect.simulation.GunFireSimulationEffectFactory;
import nl.matsgemmeke.battlegrounds.item.effect.smoke.SmokeScreenEffect;
import nl.matsgemmeke.battlegrounds.item.effect.smoke.SmokeScreenEffectFactory;
import nl.matsgemmeke.battlegrounds.item.equipment.controls.activate.ActivateFunction;
import nl.matsgemmeke.battlegrounds.item.equipment.controls.activate.ActivateFunctionFactory;
import nl.matsgemmeke.battlegrounds.item.equipment.controls.place.PlaceFunction;
import nl.matsgemmeke.battlegrounds.item.equipment.controls.place.PlaceFunctionFactory;
import nl.matsgemmeke.battlegrounds.item.equipment.controls.throwing.ThrowFunction;
import nl.matsgemmeke.battlegrounds.item.equipment.controls.throwing.ThrowFunctionFactory;
import nl.matsgemmeke.battlegrounds.item.reload.ReloadSystem;
import nl.matsgemmeke.battlegrounds.item.reload.magazine.MagazineReloadSystem;
import nl.matsgemmeke.battlegrounds.item.reload.magazine.MagazineReloadSystemFactory;
import nl.matsgemmeke.battlegrounds.item.reload.manual.ManualInsertionReloadSystem;
import nl.matsgemmeke.battlegrounds.item.reload.manual.ManualInsertionReloadSystemFactory;
import nl.matsgemmeke.battlegrounds.item.shoot.FireMode;
import nl.matsgemmeke.battlegrounds.item.shoot.burst.BurstMode;
import nl.matsgemmeke.battlegrounds.item.shoot.burst.BurstModeFactory;
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
        binder.bind(GameKey.class)
                .annotatedWith(Names.named("TrainingMode"))
                .toProvider(TrainingModeGameKeyProvider.class)
                .in(Singleton.class);
        binder.bind(DataConfiguration.class).toProvider(DataConfigurationProvider.class);
        binder.bind(LanguageConfiguration.class).toProvider(LanguageConfigurationProvider.class);
        binder.bind(WeaponCreator.class).toProvider(WeaponCreatorProvider.class);

        // Component bindings
        binder.bind(CollisionDetector.class).to(DefaultCollisionDetector.class);

        // Factory bindings
        binder.install(new FactoryModuleBuilder()
                .implement(FireMode.class, BurstMode.class)
                .build(BurstModeFactory.class));

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
                .implement(ItemEffectActivation.class, DelayedActivation.class)
                .build(DelayedActivationFactory.class));

        binder.install(new FactoryModuleBuilder()
                .implement(ItemFunction.class, ActivateFunction.class)
                .build(ActivateFunctionFactory.class));
        binder.install(new FactoryModuleBuilder()
                .implement(ItemFunction.class, PlaceFunction.class)
                .build(PlaceFunctionFactory.class));
        binder.install(new FactoryModuleBuilder()
                .implement(ItemFunction.class, ThrowFunction.class)
                .build(ThrowFunctionFactory.class));

        binder.install(new FactoryModuleBuilder()
                .implement(PlayerRegistry.class, DefaultPlayerRegistry.class)
                .build(DefaultPlayerRegistryFactory.class));

        binder.install(new FactoryModuleBuilder()
                .implement(ReloadSystem.class, MagazineReloadSystem.class)
                .build(MagazineReloadSystemFactory.class));
        binder.install(new FactoryModuleBuilder()
                .implement(ReloadSystem.class, ManualInsertionReloadSystem.class)
                .build(ManualInsertionReloadSystemFactory.class));

        binder.install(new FactoryModuleBuilder()
                .implement(Trigger.class, EnemyProximityTrigger.class)
                .build(EnemyProximityTriggerFactory.class));
        binder.install(new FactoryModuleBuilder()
                .implement(Trigger.class, FloorHitTrigger.class)
                .build(FloorHitTriggerFactory.class));

        // File bindings
        binder.bind(File.class).annotatedWith(Names.named("DataFolder")).toInstance(dataFolder);
        binder.bind(File.class).annotatedWith(Names.named("ItemsFolder")).toInstance(new File(dataFolder.getAbsoluteFile(), "items"));
        binder.bind(File.class).annotatedWith(Names.named("LangFolder")).toInstance(new File(dataFolder.getAbsoluteFile(), "lang"));
        binder.bind(File.class).annotatedWith(Names.named("SetupFolder")).toInstance(new File(dataFolder.getAbsoluteFile(), "setup"));
    }
}
