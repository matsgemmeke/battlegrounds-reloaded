package com.github.matsgemmeke.battlegrounds.command.condition;

import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.CommandConditions.Condition;
import co.aikar.commands.ConditionContext;
import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.InvalidCommandArgument;
import com.github.matsgemmeke.battlegrounds.api.game.FreemodeContext;
import com.github.matsgemmeke.battlegrounds.locale.TranslationKey;
import com.github.matsgemmeke.battlegrounds.locale.Translator;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class FreemodePresenceCondition implements Condition<BukkitCommandIssuer> {

    @NotNull
    private FreemodeContext freemodeContext;
    @NotNull
    private Translator translator;

    public FreemodePresenceCondition(@NotNull FreemodeContext freemodeContext, @NotNull Translator translator) {
        this.freemodeContext = freemodeContext;
        this.translator = translator;
    }

    public void validateCondition(ConditionContext<BukkitCommandIssuer> context) throws InvalidCommandArgument {
        Player player = context.getIssuer().getPlayer();

        if (freemodeContext.hasPlayer(player)) {
            return;
        }

        throw new ConditionFailedException(translator.translate(TranslationKey.NOT_IN_FREEMODE.getPath()));
    }
}
