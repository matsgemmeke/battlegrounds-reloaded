package nl.matsgemmeke.battlegrounds.arena.command.condition;

import co.aikar.commands.*;
import com.google.inject.Inject;
import com.google.inject.Provider;
import nl.matsgemmeke.battlegrounds.command.condition.ParameterCondition;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.GameScope;
import nl.matsgemmeke.battlegrounds.game.component.entity.PlayerRegistry;
import nl.matsgemmeke.battlegrounds.i18n.TranslationKey;
import nl.matsgemmeke.battlegrounds.i18n.Translator;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ArenaModeAbsenceCondition implements ParameterCondition<Integer> {

    private final GameContextProvider gameContextProvider;
    private final GameScope gameScope;
    private final Provider<PlayerRegistry> playerRegistryProvider;
    private final Translator translator;

    @Inject
    public ArenaModeAbsenceCondition(GameContextProvider gameContextProvider, GameScope gameScope, Provider<PlayerRegistry> playerRegistryProvider, Translator translator) {
        this.gameContextProvider = gameContextProvider;
        this.gameScope = gameScope;
        this.playerRegistryProvider = playerRegistryProvider;
        this.translator = translator;
    }

    @Override
    public void validateCondition(ConditionContext<BukkitCommandIssuer> context, BukkitCommandExecutionContext execContext, Integer value) throws InvalidCommandArgument {
        Player player = context.getIssuer().getPlayer();

        if (player == null) {
            throw new ConditionFailedException(translator.translate(TranslationKey.NOT_IN_ARENA_MODE.getPath()).getText());
        }

        GameKey gameKey = GameKey.ofArena(value);
        GameContext gameContext = gameContextProvider.getGameContext(gameKey).orElse(null);

        if (gameContext == null) {
            return;
        }

        UUID playerId = player.getUniqueId();

        gameScope.runInScope(gameContext, () -> this.verifyArenaAbsence(playerId));
    }

    private void verifyArenaAbsence(UUID playerId) {
        PlayerRegistry playerRegistry = playerRegistryProvider.get();

        if (!playerRegistry.isRegistered(playerId)) {
            return;
        }

        throw new ConditionFailedException(translator.translate(TranslationKey.NOT_IN_ARENA_MODE.getPath()).getText());
    }
}
