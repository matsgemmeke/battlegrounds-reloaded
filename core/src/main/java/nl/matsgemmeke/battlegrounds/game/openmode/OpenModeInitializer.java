package nl.matsgemmeke.battlegrounds.game.openmode;

import com.google.inject.Inject;
import com.google.inject.Provider;
import nl.matsgemmeke.battlegrounds.configuration.BattlegroundsConfiguration;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.event.EventDispatcher;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameContextType;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.component.*;
import nl.matsgemmeke.battlegrounds.game.component.collision.CollisionDetector;
import nl.matsgemmeke.battlegrounds.game.component.damage.DamageProcessor;
import nl.matsgemmeke.battlegrounds.game.component.deploy.DefaultDeploymentInfoProvider;
import nl.matsgemmeke.battlegrounds.game.component.deploy.DeploymentInfoProvider;
import nl.matsgemmeke.battlegrounds.game.component.entity.DefaultPlayerRegistryFactory;
import nl.matsgemmeke.battlegrounds.game.component.entity.PlayerRegistry;
import nl.matsgemmeke.battlegrounds.game.component.info.gun.DefaultGunInfoProvider;
import nl.matsgemmeke.battlegrounds.game.component.info.gun.GunInfoProvider;
import nl.matsgemmeke.battlegrounds.game.component.item.*;
import nl.matsgemmeke.battlegrounds.game.component.player.PlayerLifecycleHandler;
import nl.matsgemmeke.battlegrounds.game.component.projectile.ProjectileHitActionRegistry;
import nl.matsgemmeke.battlegrounds.game.component.storage.StatePersistenceHandler;
import nl.matsgemmeke.battlegrounds.game.event.EntityDamageEventHandler;
import nl.matsgemmeke.battlegrounds.game.openmode.component.OpenModeTargetFinder;
import nl.matsgemmeke.battlegrounds.game.openmode.component.damage.OpenModeDamageProcessor;
import nl.matsgemmeke.battlegrounds.game.openmode.component.player.OpenModePlayerLifecycleHandlerFactory;
import nl.matsgemmeke.battlegrounds.game.openmode.component.storage.OpenModeStatePersistenceHandlerFactory;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentBehavior;
import nl.matsgemmeke.battlegrounds.item.gun.GunBehavior;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;

public class OpenModeInitializer {

    @NotNull
    private final BattlegroundsConfiguration configuration;
    @NotNull
    private final EventDispatcher eventDispatcher;
    @NotNull
    private final GameContextProvider gameContextProvider;
    @NotNull
    private final DefaultPlayerRegistryFactory playerRegistryFactory;
    @NotNull
    private final OpenModePlayerLifecycleHandlerFactory playerLifecycleHandlerFactory;
    @NotNull
    private final OpenModeStatePersistenceHandlerFactory statePersistenceHandlerFactory;
    @NotNull
    private final Provider<CollisionDetector> collisionDetectorProvider;
    @NotNull
    private final Provider<PlayerRegistry> playerRegistryProvider;

    @Inject
    public OpenModeInitializer(
            @NotNull BattlegroundsConfiguration configuration,
            @NotNull EventDispatcher eventDispatcher,
            @NotNull GameContextProvider gameContextProvider,
            @NotNull DefaultPlayerRegistryFactory playerRegistryFactory,
            @NotNull OpenModePlayerLifecycleHandlerFactory playerLifecycleHandlerFactory,
            @NotNull OpenModeStatePersistenceHandlerFactory statePersistenceHandlerFactory,
            @NotNull Provider<CollisionDetector> collisionDetectorProvider,
            @NotNull Provider<PlayerRegistry> playerRegistryProvider
    ) {
        this.configuration = configuration;
        this.eventDispatcher = eventDispatcher;
        this.gameContextProvider = gameContextProvider;
        this.playerRegistryFactory = playerRegistryFactory;
        this.playerLifecycleHandlerFactory = playerLifecycleHandlerFactory;
        this.statePersistenceHandlerFactory = statePersistenceHandlerFactory;
        this.collisionDetectorProvider = collisionDetectorProvider;
        this.playerRegistryProvider = playerRegistryProvider;
    }

    public void initialize() {
        OpenMode openMode = new OpenMode();
        openMode.addItemBehavior(new EquipmentBehavior(openMode.getEquipmentContainer()));
        openMode.addItemBehavior(new GunBehavior(openMode.getGunContainer()));

        GameKey gameKey = GameKey.ofOpenMode();
        GameContext gameContext = new GameContext(GameContextType.OPEN_MODE);

        gameContextProvider.addGameContext(gameKey, gameContext);

        // Registry components
        EquipmentRegistry equipmentRegistry = new DefaultEquipmentRegistry(openMode.getEquipmentContainer());
        GunRegistry gunRegistry = new DefaultGunRegistry(openMode.getGunContainer());
        PlayerRegistry playerRegistry = playerRegistryFactory.create(openMode.getPlayerContainer());

        // Info provider components
        DeploymentInfoProvider deploymentInfoProvider = new DefaultDeploymentInfoProvider(equipmentRegistry);
        GunInfoProvider gunInfoProvider = new DefaultGunInfoProvider(openMode.getGunContainer(), playerRegistry);

        // All other components
        ActionHandler actionHandler = new DefaultActionHandler(openMode, playerRegistry);
        AudioEmitter audioEmitter = new DefaultAudioEmitter();
        CollisionDetector collisionDetector = collisionDetectorProvider.get();
        ProjectileHitActionRegistry projectileHitActionRegistry = new ProjectileHitActionRegistry();
        StatePersistenceHandler statePersistanceHandler = statePersistenceHandlerFactory.create(equipmentRegistry, gunRegistry, playerRegistry);

        ItemLifecycleHandler itemLifecycleHandler = new DefaultItemLifecycleHandler(equipmentRegistry);
        PlayerLifecycleHandler playerLifecycleHandler = playerLifecycleHandlerFactory.create(itemLifecycleHandler, playerRegistry, statePersistanceHandler);

        DamageProcessor damageProcessor = new OpenModeDamageProcessor(gameKey, deploymentInfoProvider);
        TargetFinder targetFinder = new OpenModeTargetFinder(deploymentInfoProvider, playerRegistry);

        gameContextProvider.assignOpenMode(openMode);

        gameContextProvider.registerComponent(gameKey, ActionHandler.class, actionHandler);
        gameContextProvider.registerComponent(gameKey, AudioEmitter.class, audioEmitter);
        gameContextProvider.registerComponent(gameKey, CollisionDetector.class, collisionDetector);
        gameContextProvider.registerComponent(gameKey, DamageProcessor.class, damageProcessor);
        gameContextProvider.registerComponent(gameKey, DeploymentInfoProvider.class, deploymentInfoProvider);
        gameContextProvider.registerComponent(gameKey, EquipmentRegistry.class, equipmentRegistry);
        gameContextProvider.registerComponent(gameKey, GunInfoProvider.class, gunInfoProvider);
        gameContextProvider.registerComponent(gameKey, GunRegistry.class, gunRegistry);
        gameContextProvider.registerComponent(gameKey, PlayerLifecycleHandler.class, playerLifecycleHandler);
        gameContextProvider.registerComponent(gameKey, PlayerRegistry.class, playerRegistry);
        gameContextProvider.registerComponent(gameKey, ProjectileHitActionRegistry.class, projectileHitActionRegistry);
        gameContextProvider.registerComponent(gameKey, StatePersistenceHandler.class, statePersistanceHandler);
        gameContextProvider.registerComponent(gameKey, TargetFinder.class, targetFinder);

        this.registerEventHandlers(gameKey);
        this.registerPlayers(gameKey);
    }

    private void registerEventHandlers(@NotNull GameKey gameKey) {
        DamageProcessor damageProcessor = gameContextProvider.getComponent(gameKey, DamageProcessor.class);
        DeploymentInfoProvider deploymentInfoProvider = gameContextProvider.getComponent(gameKey, DeploymentInfoProvider.class);

        eventDispatcher.registerEventHandler(EntityDamageEvent.class, new EntityDamageEventHandler(damageProcessor, deploymentInfoProvider));
    }

    private void registerPlayers(@NotNull GameKey gameKey) {
        PlayerRegistry playerRegistry = playerRegistryProvider.get();
        StatePersistenceHandler statePersistenceHandler = gameContextProvider.getComponent(gameKey, StatePersistenceHandler.class);

        for (Player player : Bukkit.getOnlinePlayers()) {
            GamePlayer gamePlayer = playerRegistry.registerEntity(player);
            gamePlayer.setPassive(configuration.isEnabledRegisterPlayersAsPassive());

            statePersistenceHandler.loadPlayerState(gamePlayer);
        }
    }
}
