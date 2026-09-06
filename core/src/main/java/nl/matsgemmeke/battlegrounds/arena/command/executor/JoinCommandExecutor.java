package nl.matsgemmeke.battlegrounds.arena.command.executor;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.GameScope;
import nl.matsgemmeke.battlegrounds.game.component.entity.PlayerRegistry;
import nl.matsgemmeke.battlegrounds.i18n.TranslationKey;
import nl.matsgemmeke.battlegrounds.i18n.Translator;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.logging.Logger;

public class JoinCommandExecutor {

    private final GameContextProvider gameContextProvider;
    private final GameScope gameScope;
    private final Logger logger;
    private final Provider<PlayerRegistry> playerRegistryProvider;
    private final Translator translator;

    @Inject
    public JoinCommandExecutor(
            GameContextProvider gameContextProvider,
            GameScope gameScope,
            @Named("Battlegrounds") Logger logger,
            Provider<PlayerRegistry> playerRegistryProvider,
            Translator translator
    ) {
        this.gameContextProvider = gameContextProvider;
        this.gameScope = gameScope;
        this.logger = logger;
        this.playerRegistryProvider = playerRegistryProvider;
        this.translator = translator;
    }

    public void execute(Player player, int arenaId) {
        GameKey gameKey = GameKey.ofArena(arenaId);
        GameContext gameContext = gameContextProvider.getGameContext(gameKey).orElse(null);

        if (gameContext == null) {
            logger.warning("Player %s attempted to join arena %s, however no game context was found for validated game key %s".formatted(player.getName(), arenaId, gameKey));
            player.sendMessage(translator.translate(TranslationKey.ARENA_NOT_AVAILABLE.getPath()).getText());
            return;
        }

        gameScope.runInScope(gameContext, () -> this.registerPlayer(player));
    }

    private void registerPlayer(Player player) {
        PlayerRegistry playerRegistry = playerRegistryProvider.get();
        UUID playerId = player.getUniqueId();

        if (playerRegistry.isRegistered(playerId)) {
            logger.warning("Player %s passed arena join validation, but was already registered in its player registry".formatted(player.getName()));
            player.sendMessage(translator.translate(TranslationKey.ALREADY_IN_ARENA_MODE.getPath()).getText());
            return;
        }

        playerRegistry.register(player);
        player.sendMessage("joined");
    }
}
