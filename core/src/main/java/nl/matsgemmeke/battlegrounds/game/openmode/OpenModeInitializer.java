package nl.matsgemmeke.battlegrounds.game.openmode;

import com.google.inject.Inject;
import com.google.inject.Provider;
import nl.matsgemmeke.battlegrounds.configuration.BattlegroundsConfiguration;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.event.EventDispatcher;
import nl.matsgemmeke.battlegrounds.game.*;
import nl.matsgemmeke.battlegrounds.game.component.entity.PlayerRegistry;
import nl.matsgemmeke.battlegrounds.game.component.storage.StatePersistenceHandler;
import nl.matsgemmeke.battlegrounds.game.event.EntityDamageEventHandler;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentActionExecutor;
import nl.matsgemmeke.battlegrounds.item.gun.GunActionExecutor;
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
    private final Provider<EquipmentActionExecutor> equipmentActionExecutorProvider;
    @NotNull
    private final Provider<GunActionExecutor> gunActionExecutorProvider;
    @NotNull
    private final Provider<PlayerRegistry> playerRegistryProvider;
    @NotNull
    private final Provider<StatePersistenceHandler> statePersistenceHandlerProvider;
    @NotNull
    private final Provider<EntityDamageEventHandler> entityDamageEventHandlerProvider;

    @Inject
    public OpenModeInitializer(
            @NotNull BattlegroundsConfiguration configuration,
            @NotNull EventDispatcher eventDispatcher,
            @NotNull GameContextProvider gameContextProvider,
            @NotNull GameScope gameScope,
            @NotNull Provider<EquipmentActionExecutor> equipmentActionExecutorProvider,
            @NotNull Provider<GunActionExecutor> gunActionExecutorProvider,
            @NotNull Provider<PlayerRegistry> playerRegistryProvider,
            @NotNull Provider<StatePersistenceHandler> statePersistenceHandlerProvider,
            @NotNull Provider<EntityDamageEventHandler> entityDamageEventHandlerProvider
    ) {
        this.configuration = configuration;
        this.eventDispatcher = eventDispatcher;
        this.gameContextProvider = gameContextProvider;
        this.gameScope = gameScope;
        this.equipmentActionExecutorProvider = equipmentActionExecutorProvider;
        this.gunActionExecutorProvider = gunActionExecutorProvider;
        this.playerRegistryProvider = playerRegistryProvider;
        this.statePersistenceHandlerProvider = statePersistenceHandlerProvider;
        this.entityDamageEventHandlerProvider = entityDamageEventHandlerProvider;
    }

    public void initialize() {
        OpenMode openMode = new OpenMode();
        GameContext gameContext = new GameContext(GAME_KEY, GameContextType.OPEN_MODE);

        gameContextProvider.addGameContext(GAME_KEY, gameContext);
        gameContextProvider.assignOpenMode(openMode);

        gameScope.runInScope(gameContext, () -> this.registerComponents(openMode));
    }

    private void registerComponents(OpenMode openMode) {
        openMode.addActionExecutor(equipmentActionExecutorProvider.get());
        openMode.addActionExecutor(gunActionExecutorProvider.get());

        this.registerEventHandlers();
        this.registerPlayers();
    }

    private void registerEventHandlers() {
        eventDispatcher.registerEventHandler(EntityDamageEvent.class, entityDamageEventHandlerProvider.get());
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
