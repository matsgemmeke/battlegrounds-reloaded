package nl.matsgemmeke.battlegrounds.command.condition;

import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.CommandConditions.Condition;
import co.aikar.commands.ConditionContext;
import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.InvalidCommandArgument;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.text.TranslationKey;
import nl.matsgemmeke.battlegrounds.text.Translator;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TrainingModePresenceCondition implements Condition<BukkitCommandIssuer> {

    @NotNull
    private GameContext gameContext;
    @NotNull
    private Translator translator;

    public TrainingModePresenceCondition(@NotNull GameContext gameContext, @NotNull Translator translator) {
        this.gameContext = gameContext;
        this.translator = translator;
    }

    public void validateCondition(ConditionContext<BukkitCommandIssuer> conditionContext) throws InvalidCommandArgument {
        Player player = conditionContext.getIssuer().getPlayer();

        if (gameContext.getPlayerRegistry().isRegistered(player)) {
            return;
        }

        throw new ConditionFailedException(translator.translate(TranslationKey.NOT_IN_TRAINING_MODE.getPath()).getText());
    }
}
