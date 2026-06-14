package nl.matsgemmeke.battlegrounds.command.condition;

import co.aikar.commands.*;
import co.aikar.commands.CommandConditions.ParameterCondition;
import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.text.TranslationKey;
import nl.matsgemmeke.battlegrounds.text.Translator;

import java.util.Map;

public class NonexistentArenaIdCondition implements ParameterCondition<Integer, BukkitCommandExecutionContext, BukkitCommandIssuer> {

    private final GameContextProvider gameContextProvider;
    private final Translator translator;

    @Inject
    public NonexistentArenaIdCondition(GameContextProvider gameContextProvider, Translator translator) {
        this.gameContextProvider = gameContextProvider;
        this.translator = translator;
    }

    @Override
    public void validateCondition(ConditionContext<BukkitCommandIssuer> context, BukkitCommandExecutionContext execContext, Integer value) throws InvalidCommandArgument {
        if (!gameContextProvider.arenaExists(value)) {
            return;
        }

        Map<String, Object> values = Map.of("bg_arena", value);
        String message = translator.translate(TranslationKey.ARENA_ALREADY_EXISTS.getPath()).replace(values);

        throw new ConditionFailedException(message);
    }
}
