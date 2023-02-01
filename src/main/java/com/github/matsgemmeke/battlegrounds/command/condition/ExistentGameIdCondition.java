package com.github.matsgemmeke.battlegrounds.command.condition;

import co.aikar.commands.*;
import co.aikar.commands.CommandConditions.ParameterCondition;
import com.github.matsgemmeke.battlegrounds.api.BattleContextProvider;
import com.github.matsgemmeke.battlegrounds.locale.PlaceholderEntry;
import com.github.matsgemmeke.battlegrounds.locale.TranslationKey;
import com.github.matsgemmeke.battlegrounds.locale.Translator;
import org.jetbrains.annotations.NotNull;

public class ExistentGameIdCondition implements ParameterCondition<Integer, BukkitCommandExecutionContext, BukkitCommandIssuer> {

    @NotNull
    private BattleContextProvider contextProvider;
    @NotNull
    private Translator translator;

    public ExistentGameIdCondition(@NotNull BattleContextProvider contextProvider, @NotNull Translator translator) {
        this.contextProvider = contextProvider;
        this.translator = translator;
    }

    public void validateCondition(ConditionContext<BukkitCommandIssuer> context, BukkitCommandExecutionContext execContext, Integer value) throws InvalidCommandArgument {
        if (contextProvider.getGameContext(value) != null) {
            return;
        }

        PlaceholderEntry placeholder = new PlaceholderEntry("bg_game", value.toString());

        throw new ConditionFailedException(translator.translate(TranslationKey.GAME_NOT_EXISTS.getPath(), placeholder));
    }
}
