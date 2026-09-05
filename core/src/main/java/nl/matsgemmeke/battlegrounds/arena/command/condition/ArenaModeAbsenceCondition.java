package nl.matsgemmeke.battlegrounds.arena.command.condition;

import co.aikar.commands.*;
import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.command.condition.Condition;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameContextType;
import nl.matsgemmeke.battlegrounds.i18n.TranslationKey;
import nl.matsgemmeke.battlegrounds.i18n.Translator;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ArenaModeAbsenceCondition implements Condition {

    private final GameContextProvider gameContextProvider;
    private final Translator translator;

    @Inject
    public ArenaModeAbsenceCondition(GameContextProvider gameContextProvider, Translator translator) {
        this.gameContextProvider = gameContextProvider;
        this.translator = translator;
    }

    @Override
    public void validateCondition(ConditionContext<BukkitCommandIssuer> context) {
        Player player = context.getIssuer().getPlayer();

        if (player == null) {
            // Issuer is not a player, and therefore cannot be in an arena
            return;
        }

        UUID playerId = player.getUniqueId();
        GameContext gameContext = gameContextProvider.getGameContext(playerId).orElse(null);

        if (gameContext == null || gameContext.getType() == GameContextType.FREEPLAY_MODE) {
            return;
        }

        throw new ConditionFailedException(translator.translate(TranslationKey.ALREADY_IN_ARENA_MODE.getPath()).getText());
    }
}
