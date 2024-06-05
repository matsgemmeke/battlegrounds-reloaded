package nl.matsgemmeke.battlegrounds.command.condition;

import co.aikar.commands.*;
import co.aikar.commands.CommandConditions.ParameterCondition;
import nl.matsgemmeke.battlegrounds.GameProvider;
import nl.matsgemmeke.battlegrounds.locale.PlaceholderEntry;
import nl.matsgemmeke.battlegrounds.locale.TranslationKey;
import nl.matsgemmeke.battlegrounds.locale.Translator;
import org.jetbrains.annotations.NotNull;

public class ExistentSessionIdCondition implements ParameterCondition<Integer, BukkitCommandExecutionContext, BukkitCommandIssuer> {

    @NotNull
    private GameProvider gameProvider;
    @NotNull
    private Translator translator;

    public ExistentSessionIdCondition(@NotNull GameProvider gameProvider, @NotNull Translator translator) {
        this.gameProvider = gameProvider;
        this.translator = translator;
    }

    public void validateCondition(ConditionContext<BukkitCommandIssuer> context, BukkitCommandExecutionContext execContext, Integer value) throws InvalidCommandArgument {
        if (gameProvider.getSession(value) != null) {
            return;
        }

        PlaceholderEntry placeholder = new PlaceholderEntry("bg_session", value.toString());

        throw new ConditionFailedException(translator.translate(TranslationKey.SESSION_NOT_EXISTS.getPath(), placeholder));
    }
}
