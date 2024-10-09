package nl.matsgemmeke.battlegrounds.command.condition;

import co.aikar.commands.*;
import co.aikar.commands.CommandConditions.ParameterCondition;
import nl.matsgemmeke.battlegrounds.GameContextProvider;
import nl.matsgemmeke.battlegrounds.text.PlaceholderEntry;
import nl.matsgemmeke.battlegrounds.text.TranslationKey;
import nl.matsgemmeke.battlegrounds.text.Translator;
import org.jetbrains.annotations.NotNull;

public class ExistentSessionIdCondition implements ParameterCondition<Integer, BukkitCommandExecutionContext, BukkitCommandIssuer> {

    @NotNull
    private GameContextProvider contextProvider;
    @NotNull
    private Translator translator;

    public ExistentSessionIdCondition(@NotNull GameContextProvider contextProvider, @NotNull Translator translator) {
        this.contextProvider = contextProvider;
        this.translator = translator;
    }

    public void validateCondition(ConditionContext<BukkitCommandIssuer> context, BukkitCommandExecutionContext execContext, Integer value) throws InvalidCommandArgument {
        if (contextProvider.getSessionContext(value) != null) {
            return;
        }

        PlaceholderEntry placeholder = new PlaceholderEntry("bg_session", value.toString());

        throw new ConditionFailedException(translator.translate(TranslationKey.SESSION_NOT_EXISTS.getPath(), placeholder));
    }
}
