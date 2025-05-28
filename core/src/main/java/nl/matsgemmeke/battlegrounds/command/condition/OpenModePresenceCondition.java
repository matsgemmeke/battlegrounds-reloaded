package nl.matsgemmeke.battlegrounds.command.condition;

import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.CommandConditions.Condition;
import co.aikar.commands.ConditionContext;
import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.InvalidCommandArgument;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.component.entity.PlayerRegistry;
import nl.matsgemmeke.battlegrounds.text.TranslationKey;
import nl.matsgemmeke.battlegrounds.text.Translator;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class OpenModePresenceCondition implements Condition<BukkitCommandIssuer> {

    @NotNull
    private final GameContextProvider contextProvider;
    @NotNull
    private final GameKey openModeGameKey;
    @NotNull
    private final Translator translator;

    @Inject
    public OpenModePresenceCondition(@NotNull GameContextProvider contextProvider, @Named("OpenMode") @NotNull GameKey openModeGameKey, @NotNull Translator translator) {
        this.contextProvider = contextProvider;
        this.openModeGameKey = openModeGameKey;
        this.translator = translator;
    }

    public void validateCondition(ConditionContext<BukkitCommandIssuer> conditionContext) throws InvalidCommandArgument {
        Player player = conditionContext.getIssuer().getPlayer();
        PlayerRegistry playerRegistry = contextProvider.getComponent(openModeGameKey, PlayerRegistry.class);

        if (playerRegistry.isRegistered(player)) {
            return;
        }

        throw new ConditionFailedException(translator.translate(TranslationKey.NOT_IN_OPEN_MODE.getPath()).getText());
    }
}
