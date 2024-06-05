package nl.matsgemmeke.battlegrounds.command.condition;

import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.CommandConditions.Condition;
import co.aikar.commands.ConditionContext;
import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.InvalidCommandArgument;
import nl.matsgemmeke.battlegrounds.game.training.TrainingMode;
import nl.matsgemmeke.battlegrounds.locale.TranslationKey;
import nl.matsgemmeke.battlegrounds.locale.Translator;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TrainingModePresenceCondition implements Condition<BukkitCommandIssuer> {

    @NotNull
    private TrainingMode trainingMode;
    @NotNull
    private Translator translator;

    public TrainingModePresenceCondition(@NotNull TrainingMode trainingMode, @NotNull Translator translator) {
        this.trainingMode = trainingMode;
        this.translator = translator;
    }

    public void validateCondition(ConditionContext<BukkitCommandIssuer> context) throws InvalidCommandArgument {
        Player player = context.getIssuer().getPlayer();

        if (trainingMode.hasPlayer(player)) {
            return;
        }

        throw new ConditionFailedException(translator.translate(TranslationKey.NOT_IN_TRAINING_MODE.getPath()));
    }
}
