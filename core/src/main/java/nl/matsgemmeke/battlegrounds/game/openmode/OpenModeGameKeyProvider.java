package nl.matsgemmeke.battlegrounds.game.openmode;

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
import nl.matsgemmeke.battlegrounds.game.openmode.component.OpenModeTargetFinder;
import nl.matsgemmeke.battlegrounds.game.openmode.component.damage.OpenModeDamageProcessor;
import nl.matsgemmeke.battlegrounds.game.openmode.component.spawn.OpenModeSpawnPointProvider;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentBehavior;
import nl.matsgemmeke.battlegrounds.item.gun.GunBehavior;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;

public class OpenModeGameKeyProvider implements Provider<GameKey> {

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
    public OpenModeGameKeyProvider(
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
        OpenMode openMode = new OpenMode();
        openMode.addItemBehavior(new EquipmentBehavior(openMode.getEquipmentStorage()));
        openMode.addItemBehavior(new GunBehavior(openMode.getGunStorage()));

        GameKey gameKey = GameKey.ofOpenMode();

        // Registry components
        EquipmentRegistry equipmentRegistry = new DefaultEquipmentRegistry(openMode.getEquipmentStorage());
        GunRegistry gunRegistry = new DefaultGunRegistry(openMode.getGunStorage());
        PlayerRegistry playerRegistry = playerRegistryFactory.create(openMode.getPlayerStorage());

        // Info provider components
        DeploymentInfoProvider deploymentInfoProvider = new DefaultDeploymentInfoProvider(equipmentRegistry);
        GunInfoProvider gunInfoProvider = new DefaultGunInfoProvider(openMode.getGunStorage());

        // All other components
        ActionHandler actionHandler = new DefaultActionHandler(openMode, playerRegistry);
        AudioEmitter audioEmitter = new DefaultAudioEmitter();
        CollisionDetector collisionDetector = collisionDetectorProvider.get();
        SpawnPointProvider spawnPointProvider = new OpenModeSpawnPointProvider(openMode.getSpawnPointStorage());

        DamageProcessor damageProcessor = new OpenModeDamageProcessor(gameKey, deploymentInfoProvider);
        TargetFinder targetFinder = new OpenModeTargetFinder(deploymentInfoProvider, playerRegistry);

        contextProvider.assignOpenMode(openMode);

        contextProvider.registerComponent(gameKey, ActionHandler.class, actionHandler);
        contextProvider.registerComponent(gameKey, AudioEmitter.class, audioEmitter);
        contextProvider.registerComponent(gameKey, CollisionDetector.class, collisionDetector);
        contextProvider.registerComponent(gameKey, DamageProcessor.class, damageProcessor);
        contextProvider.registerComponent(gameKey, DeploymentInfoProvider.class, deploymentInfoProvider);
        contextProvider.registerComponent(gameKey, EquipmentRegistry.class, equipmentRegistry);
        contextProvider.registerComponent(gameKey, GunInfoProvider.class, gunInfoProvider);
        contextProvider.registerComponent(gameKey, GunRegistry.class, gunRegistry);
        contextProvider.registerComponent(gameKey, PlayerRegistry.class, playerRegistry);
        contextProvider.registerComponent(gameKey, SpawnPointProvider.class, spawnPointProvider);
        contextProvider.registerComponent(gameKey, TargetFinder.class, targetFinder);

        this.registerEventHandlers(gameKey);
        this.registerPlayers(gameKey);

        return gameKey;
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
