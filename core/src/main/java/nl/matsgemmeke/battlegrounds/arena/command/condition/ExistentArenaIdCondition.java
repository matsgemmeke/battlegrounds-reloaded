package nl.matsgemmeke.battlegrounds.arena.command.condition;

import co.aikar.commands.*;
import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.command.condition.ParameterCondition;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.i18n.TranslationKey;
import nl.matsgemmeke.battlegrounds.i18n.Translator;

import java.util.Map;

public class ExistentArenaIdCondition implements ParameterCondition<Integer> {

    private final GameContextProvider gameContextProvider;
    private final Translator translator;

    @Inject
    public ExistentArenaIdCondition(GameContextProvider gameContextProvider, Translator translator) {
        this.gameContextProvider = gameContextProvider;
        this.translator = translator;
    }

    @Override
    public void validateCondition(ConditionContext<BukkitCommandIssuer> context, BukkitCommandExecutionContext execContext, Integer arenaId) {
        if (gameContextProvider.arenaExists(arenaId)) {
            return;
        }

        Map<String, Object> values = Map.of("bg_arena", arenaId);
        String message = translator.translate(TranslationKey.ARENA_NOT_EXISTS.getPath()).replace(values);

        throw new ConditionFailedException(message);
    }
}
