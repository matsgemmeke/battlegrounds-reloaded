package com.github.matsgemmeke.battlegrounds.command.condition;

import co.aikar.commands.*;
import co.aikar.commands.CommandConditions.ParameterCondition;
import com.github.matsgemmeke.battlegrounds.api.BattleContextProvider;
import com.github.matsgemmeke.battlegrounds.locale.PlaceholderEntry;
import com.github.matsgemmeke.battlegrounds.locale.TranslationKey;
import com.github.matsgemmeke.battlegrounds.locale.Translator;
import org.jetbrains.annotations.NotNull;

public class NonexistentSessionIdCondition implements ParameterCondition<Integer, BukkitCommandExecutionContext, BukkitCommandIssuer> {

    @NotNull
    private BattleContextProvider contextProvider;
    @NotNull
    private Translator translator;

    public NonexistentSessionIdCondition(@NotNull BattleContextProvider contextProvider, @NotNull Translator translator) {
        this.contextProvider = contextProvider;
        this.translator = translator;
    }

    public void validateCondition(ConditionContext<BukkitCommandIssuer> context, BukkitCommandExecutionContext execContext, Integer value) throws InvalidCommandArgument {
        if (contextProvider.getSession(value) == null) {
            return;
        }

        PlaceholderEntry placeholder = new PlaceholderEntry("bg_session", value.toString());

        throw new ConditionFailedException(translator.translate(TranslationKey.SESSION_ALREADY_EXISTS.getPath(), placeholder));
    }
}
