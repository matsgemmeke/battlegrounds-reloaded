package nl.matsgemmeke.battlegrounds.game.openmode;

import com.google.inject.Inject;
import com.google.inject.Provider;
import nl.matsgemmeke.battlegrounds.configuration.BattlegroundsConfiguration;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.event.EventDispatcher;
import nl.matsgemmeke.battlegrounds.game.*;
import nl.matsgemmeke.battlegrounds.game.component.*;
import nl.matsgemmeke.battlegrounds.game.component.collision.CollisionDetector;
import nl.matsgemmeke.battlegrounds.game.component.damage.DamageProcessor;
import nl.matsgemmeke.battlegrounds.game.component.deploy.DefaultDeploymentInfoProvider;
import nl.matsgemmeke.battlegrounds.game.component.deploy.DeploymentInfoProvider;
import nl.matsgemmeke.battlegrounds.game.component.entity.PlayerRegistry;
import nl.matsgemmeke.battlegrounds.game.component.info.gun.DefaultGunInfoProvider;
import nl.matsgemmeke.battlegrounds.game.component.info.gun.GunInfoProvider;
import nl.matsgemmeke.battlegrounds.game.component.item.*;
import nl.matsgemmeke.battlegrounds.game.component.projectile.ProjectileHitActionRegistry;
import nl.matsgemmeke.battlegrounds.game.component.storage.StatePersistenceHandler;
import nl.matsgemmeke.battlegrounds.game.event.EntityDamageEventHandler;
import nl.matsgemmeke.battlegrounds.game.openmode.component.OpenModeTargetFinder;
import nl.matsgemmeke.battlegrounds.game.openmode.component.damage.OpenModeDamageProcessor;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentBehavior;
import nl.matsgemmeke.battlegrounds.item.gun.GunBehavior;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class OpenModeInitializer {

    private static final GameKey GAME_KEY = GameKey.ofOpenMode();

    @NotNull
    private final BattlegroundsConfiguration configuration;
    @NotNull
    private final EventDispatcher eventDispatcher;
    @NotNull
    private final GameContextProvider gameContextProvider;
    @NotNull
    private final GameScope gameScope;
    @NotNull
    private final Provider<CollisionDetector> collisionDetectorProvider;
    @NotNull
    private final Provider<PlayerRegistry> playerRegistryProvider;
    @NotNull
    private final Provider<StatePersistenceHandler> statePersistenceHandlerProvider;

    @Inject
    public OpenModeInitializer(
            @NotNull BattlegroundsConfiguration configuration,
            @NotNull EventDispatcher eventDispatcher,
            @NotNull GameContextProvider gameContextProvider,
            @NotNull GameScope gameScope,
            @NotNull Provider<CollisionDetector> collisionDetectorProvider,
            @NotNull Provider<PlayerRegistry> playerRegistryProvider,
            @NotNull Provider<StatePersistenceHandler> statePersistenceHandlerProvider
    ) {
        this.configuration = configuration;
        this.eventDispatcher = eventDispatcher;
        this.gameContextProvider = gameContextProvider;
        this.gameScope = gameScope;
        this.collisionDetectorProvider = collisionDetectorProvider;
        this.playerRegistryProvider = playerRegistryProvider;
        this.statePersistenceHandlerProvider = statePersistenceHandlerProvider;
    }

    public void initialize() {
        OpenMode openMode = new OpenMode();
        openMode.addItemBehavior(new EquipmentBehavior(openMode.getEquipmentContainer()));
        openMode.addItemBehavior(new GunBehavior(openMode.getGunContainer()));

        GameContext gameContext = new GameContext(GAME_KEY, GameContextType.OPEN_MODE);

        gameContextProvider.addGameContext(GAME_KEY, gameContext);
        gameContextProvider.assignOpenMode(openMode);

        gameScope.runInScope(gameContext, () -> this.registerComponents(openMode));
    }

    private void registerComponents(OpenMode openMode) {
        // Registry components
        EquipmentRegistry equipmentRegistry = new DefaultEquipmentRegistry(openMode.getEquipmentContainer());
        GunRegistry gunRegistry = new DefaultGunRegistry(openMode.getGunContainer());
        PlayerRegistry playerRegistry = playerRegistryProvider.get();

        // Info provider components
        DeploymentInfoProvider deploymentInfoProvider = new DefaultDeploymentInfoProvider(equipmentRegistry);
        GunInfoProvider gunInfoProvider = new DefaultGunInfoProvider(openMode.getGunContainer(), playerRegistry);

        // All other components
        ActionHandler actionHandler = new DefaultActionHandler(openMode, playerRegistry);
        AudioEmitter audioEmitter = new DefaultAudioEmitter();
        CollisionDetector collisionDetector = collisionDetectorProvider.get();
        ProjectileHitActionRegistry projectileHitActionRegistry = new ProjectileHitActionRegistry();

        DamageProcessor damageProcessor = new OpenModeDamageProcessor(GAME_KEY, deploymentInfoProvider);
        TargetFinder targetFinder = new OpenModeTargetFinder(deploymentInfoProvider, playerRegistry);

        gameContextProvider.registerComponent(GAME_KEY, ActionHandler.class, actionHandler);
        gameContextProvider.registerComponent(GAME_KEY, AudioEmitter.class, audioEmitter);
        gameContextProvider.registerComponent(GAME_KEY, CollisionDetector.class, collisionDetector);
        gameContextProvider.registerComponent(GAME_KEY, DamageProcessor.class, damageProcessor);
        gameContextProvider.registerComponent(GAME_KEY, DeploymentInfoProvider.class, deploymentInfoProvider);
        gameContextProvider.registerComponent(GAME_KEY, EquipmentRegistry.class, equipmentRegistry);
        gameContextProvider.registerComponent(GAME_KEY, GunInfoProvider.class, gunInfoProvider);
        gameContextProvider.registerComponent(GAME_KEY, GunRegistry.class, gunRegistry);
        gameContextProvider.registerComponent(GAME_KEY, PlayerRegistry.class, playerRegistry);
        gameContextProvider.registerComponent(GAME_KEY, ProjectileHitActionRegistry.class, projectileHitActionRegistry);
        gameContextProvider.registerComponent(GAME_KEY, TargetFinder.class, targetFinder);

        this.registerEventHandlers();
        this.registerPlayers();
    }

    private void registerEventHandlers() {
        DamageProcessor damageProcessor = gameContextProvider.getComponent(GAME_KEY, DamageProcessor.class);
        DeploymentInfoProvider deploymentInfoProvider = gameContextProvider.getComponent(GAME_KEY, DeploymentInfoProvider.class);

        eventDispatcher.registerEventHandler(EntityDamageEvent.class, new EntityDamageEventHandler(damageProcessor, deploymentInfoProvider));
    }

    private void registerPlayers() {
        PlayerRegistry playerRegistry = playerRegistryProvider.get();
        StatePersistenceHandler statePersistenceHandler = statePersistenceHandlerProvider.get();

        for (Player player : Bukkit.getOnlinePlayers()) {
            UUID playerId = player.getUniqueId();
            gameContextProvider.registerEntity(playerId, GAME_KEY);

            GamePlayer gamePlayer = playerRegistry.registerEntity(player);
            gamePlayer.setPassive(configuration.isEnabledRegisterPlayersAsPassive());

            statePersistenceHandler.loadPlayerState(gamePlayer);
        }
    }
}
