package nl.matsgemmeke.battlegrounds.arena.command.condition;

import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.ConditionContext;
import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.InvalidCommandArgument;
import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.command.condition.Condition;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameContextType;
import nl.matsgemmeke.battlegrounds.i18n.TranslationKey;
import nl.matsgemmeke.battlegrounds.i18n.Translator;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ArenaModePresenceCondition implements Condition {

    private final GameContextProvider gameContextProvider;
    private final Translator translator;

    @Inject
    public ArenaModePresenceCondition(GameContextProvider gameContextProvider, Translator translator) {
        this.gameContextProvider = gameContextProvider;
        this.translator = translator;
    }

    @Override
    public void validateCondition(ConditionContext<BukkitCommandIssuer> context) throws InvalidCommandArgument {
        Player player = context.getIssuer().getPlayer();

        if (player == null) {
            throw new ConditionFailedException(translator.translate(TranslationKey.NOT_IN_ARENA_MODE.getPath()).getText());
        }

        UUID playerId = player.getUniqueId();
        GameContext gameContext = gameContextProvider.getGameContext(playerId).orElse(null);

        if (gameContext != null && gameContext.getType() == GameContextType.ARENA_MODE) {
            return;
        }

        throw new ConditionFailedException(translator.translate(TranslationKey.NOT_IN_ARENA_MODE.getPath()).getText());
    }
}
