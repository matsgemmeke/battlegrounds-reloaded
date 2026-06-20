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
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.UUID;

public class OpenModeInitializer {

    private static final GameKey GAME_KEY = GameKey.ofFreeplay();

    private final BattlegroundsConfiguration configuration;
    private final EventDispatcher eventDispatcher;
    private final GameContextProvider gameContextProvider;
    private final GameScope gameScope;
    private final Provider<PlayerRegistry> playerRegistryProvider;
    private final Provider<StatePersistenceHandler> statePersistenceHandlerProvider;
    private final Provider<EntityDamageEventHandler> entityDamageEventHandlerProvider;

    @Inject
    public OpenModeInitializer(
            BattlegroundsConfiguration configuration,
            EventDispatcher eventDispatcher,
            GameContextProvider gameContextProvider,
            GameScope gameScope,
            Provider<PlayerRegistry> playerRegistryProvider,
            Provider<StatePersistenceHandler> statePersistenceHandlerProvider,
            Provider<EntityDamageEventHandler> entityDamageEventHandlerProvider
    ) {
        this.configuration = configuration;
        this.eventDispatcher = eventDispatcher;
        this.gameContextProvider = gameContextProvider;
        this.gameScope = gameScope;
        this.playerRegistryProvider = playerRegistryProvider;
        this.statePersistenceHandlerProvider = statePersistenceHandlerProvider;
        this.entityDamageEventHandlerProvider = entityDamageEventHandlerProvider;
    }

    public void initialize() {
        Freeplay freeplay = new Freeplay();
        GameContext gameContext = new GameContext(GAME_KEY, GameContextType.FREEPLAY_MODE);

        gameContextProvider.addGameContext(GAME_KEY, gameContext);
        gameContextProvider.assignFreeplay(freeplay);

        gameScope.runInScope(gameContext, this::registerComponents);
    }

    private void registerComponents() {
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

            GamePlayer gamePlayer = playerRegistry.register(player);
            gamePlayer.setPassive(configuration.isEnabledRegisterPlayersAsPassive());

            statePersistenceHandler.loadPlayerState(gamePlayer);
        }
    }
}
