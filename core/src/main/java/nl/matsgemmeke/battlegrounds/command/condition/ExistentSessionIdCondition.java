package nl.matsgemmeke.battlegrounds.command.condition;

import co.aikar.commands.*;
import co.aikar.commands.CommandConditions.ParameterCondition;
import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.text.TranslationKey;
import nl.matsgemmeke.battlegrounds.text.Translator;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class ExistentSessionIdCondition implements ParameterCondition<Integer, BukkitCommandExecutionContext, BukkitCommandIssuer> {

    @NotNull
    private final GameContextProvider contextProvider;
    @NotNull
    private final Translator translator;

    @Inject
    public ExistentSessionIdCondition(@NotNull GameContextProvider contextProvider, @NotNull Translator translator) {
        this.contextProvider = contextProvider;
        this.translator = translator;
    }

    public void validateCondition(ConditionContext<BukkitCommandIssuer> context, BukkitCommandExecutionContext execContext, Integer value) throws InvalidCommandArgument {
        if (contextProvider.sessionExists(value)) {
            return;
        }

        Map<String, Object> values = Map.of("bg_session", value);
        String message = translator.translate(TranslationKey.SESSION_NOT_EXISTS.getPath()).replace(values);

        throw new ConditionFailedException(message);
    }
}
