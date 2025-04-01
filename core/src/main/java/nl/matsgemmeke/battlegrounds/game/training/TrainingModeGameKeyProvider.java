package nl.matsgemmeke.battlegrounds.game.training;

import com.google.inject.Inject;
import com.google.inject.Provider;
import nl.matsgemmeke.battlegrounds.configuration.BattlegroundsConfiguration;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.event.EventDispatcher;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.component.*;
import nl.matsgemmeke.battlegrounds.game.component.damage.DamageProcessor;
import nl.matsgemmeke.battlegrounds.game.component.deploy.DefaultDeploymentInfoProvider;
import nl.matsgemmeke.battlegrounds.game.component.deploy.DeploymentInfoProvider;
import nl.matsgemmeke.battlegrounds.game.component.entity.DefaultPlayerRegistryFactory;
import nl.matsgemmeke.battlegrounds.game.component.entity.PlayerRegistry;
import nl.matsgemmeke.battlegrounds.game.component.info.gun.DefaultGunInfoProvider;
import nl.matsgemmeke.battlegrounds.game.component.info.gun.GunInfoProvider;
import nl.matsgemmeke.battlegrounds.game.component.item.DefaultEquipmentRegistry;
import nl.matsgemmeke.battlegrounds.game.component.item.DefaultGunRegistry;
import nl.matsgemmeke.battlegrounds.game.component.item.EquipmentRegistry;
import nl.matsgemmeke.battlegrounds.game.component.item.GunRegistry;
import nl.matsgemmeke.battlegrounds.game.component.spawn.SpawnPointProvider;
import nl.matsgemmeke.battlegrounds.game.event.EntityDamageEventHandler;
import nl.matsgemmeke.battlegrounds.game.training.component.TrainingModeTargetFinder;
import nl.matsgemmeke.battlegrounds.game.training.component.damage.TrainingModeDamageProcessor;
import nl.matsgemmeke.battlegrounds.game.training.component.spawn.TrainingModeSpawnPointProvider;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentBehavior;
import nl.matsgemmeke.battlegrounds.item.gun.GunBehavior;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;

public class TrainingModeGameKeyProvider implements Provider<GameKey> {

    @NotNull
    private final BattlegroundsConfiguration configuration;
    @NotNull
    private final EventDispatcher eventDispatcher;
    @NotNull
    private final GameContextProvider contextProvider;
    @NotNull
    private final DefaultPlayerRegistryFactory playerRegistryFactory;
    @NotNull
    private final Provider<CollisionDetector> collisionDetectorProvider;

    @Inject
    public TrainingModeGameKeyProvider(
            @NotNull BattlegroundsConfiguration configuration,
            @NotNull EventDispatcher eventDispatcher,
            @NotNull GameContextProvider contextProvider,
            @NotNull DefaultPlayerRegistryFactory playerRegistryFactory,
            @NotNull Provider<CollisionDetector> collisionDetectorProvider
    ) {
        this.configuration = configuration;
        this.eventDispatcher = eventDispatcher;
        this.contextProvider = contextProvider;
        this.playerRegistryFactory = playerRegistryFactory;
        this.collisionDetectorProvider = collisionDetectorProvider;
    }

    public GameKey get() {
        TrainingMode trainingMode = new TrainingMode();
        trainingMode.addItemBehavior(new EquipmentBehavior(trainingMode.getEquipmentStorage()));
        trainingMode.addItemBehavior(new GunBehavior(trainingMode.getGunStorage()));

        GameKey trainingModeKey = GameKey.ofTrainingMode();

        // Registry components
        EquipmentRegistry equipmentRegistry = new DefaultEquipmentRegistry(trainingMode.getEquipmentStorage());
        GunRegistry gunRegistry = new DefaultGunRegistry(trainingMode.getGunStorage());
        PlayerRegistry playerRegistry = playerRegistryFactory.create(trainingMode.getPlayerStorage());

        // Info provider components
        DeploymentInfoProvider deploymentInfoProvider = new DefaultDeploymentInfoProvider(equipmentRegistry);
        GunInfoProvider gunInfoProvider = new DefaultGunInfoProvider(trainingMode.getGunStorage());

        // All other components
        ActionHandler actionHandler = new DefaultActionHandler(trainingMode, playerRegistry);
        AudioEmitter audioEmitter = new DefaultAudioEmitter();
        CollisionDetector collisionDetector = collisionDetectorProvider.get();
        SpawnPointProvider spawnPointProvider = new TrainingModeSpawnPointProvider(trainingMode.getSpawnPointStorage());

        DamageProcessor damageProcessor = new TrainingModeDamageProcessor(trainingModeKey, deploymentInfoProvider);
        TargetFinder targetFinder = new TrainingModeTargetFinder(deploymentInfoProvider, playerRegistry);

        contextProvider.assignTrainingMode(trainingMode);

        contextProvider.registerComponent(trainingModeKey, ActionHandler.class, actionHandler);
        contextProvider.registerComponent(trainingModeKey, AudioEmitter.class, audioEmitter);
        contextProvider.registerComponent(trainingModeKey, CollisionDetector.class, collisionDetector);
        contextProvider.registerComponent(trainingModeKey, DamageProcessor.class, damageProcessor);
        contextProvider.registerComponent(trainingModeKey, DeploymentInfoProvider.class, deploymentInfoProvider);
        contextProvider.registerComponent(trainingModeKey, EquipmentRegistry.class, equipmentRegistry);
        contextProvider.registerComponent(trainingModeKey, GunInfoProvider.class, gunInfoProvider);
        contextProvider.registerComponent(trainingModeKey, GunRegistry.class, gunRegistry);
        contextProvider.registerComponent(trainingModeKey, PlayerRegistry.class, playerRegistry);
        contextProvider.registerComponent(trainingModeKey, SpawnPointProvider.class, spawnPointProvider);
        contextProvider.registerComponent(trainingModeKey, TargetFinder.class, targetFinder);

        this.registerEventHandlers(trainingModeKey);
        this.registerPlayers(trainingModeKey);

        return trainingModeKey;
    }

    private void registerEventHandlers(@NotNull GameKey gameKey) {
        DamageProcessor damageProcessor = contextProvider.getComponent(gameKey, DamageProcessor.class);
        DeploymentInfoProvider deploymentInfoProvider = contextProvider.getComponent(gameKey, DeploymentInfoProvider.class);

        eventDispatcher.registerEventHandler(EntityDamageEvent.class, new EntityDamageEventHandler(damageProcessor, deploymentInfoProvider));
    }

    private void registerPlayers(@NotNull GameKey gameKey) {
        PlayerRegistry playerRegistry = contextProvider.getComponent(gameKey, PlayerRegistry.class);

        for (Player player : Bukkit.getOnlinePlayers()) {
            GamePlayer gamePlayer = playerRegistry.registerEntity(player);
            gamePlayer.setPassive(configuration.isEnabledRegisterPlayersAsPassive());
        }
    }
}
