package nl.matsgemmeke.battlegrounds.command.condition;

import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.CommandConditions.Condition;
import co.aikar.commands.ConditionContext;
import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.InvalidCommandArgument;
import com.google.inject.Inject;
import com.google.inject.Provider;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.GameScope;
import nl.matsgemmeke.battlegrounds.game.component.entity.PlayerRegistry;
import nl.matsgemmeke.battlegrounds.text.TranslationKey;
import nl.matsgemmeke.battlegrounds.text.Translator;
import org.bukkit.entity.Player;

import java.util.UUID;

public class OpenModePresenceCondition implements Condition<BukkitCommandIssuer> {

    private static final GameKey OPEN_MODE_GAME_KEY = GameKey.ofOpenMode();

    private final GameContextProvider gameContextProvider;
    private final GameScope gameScope;
    private final Provider<PlayerRegistry> playerRegistryProvider;
    private final Translator translator;

    @Inject
    public OpenModePresenceCondition(GameContextProvider gameContextProvider, GameScope gameScope, Provider<PlayerRegistry> playerRegistryProvider, Translator translator) {
        this.gameContextProvider = gameContextProvider;
        this.gameScope = gameScope;
        this.playerRegistryProvider = playerRegistryProvider;
        this.translator = translator;
    }

    public void validateCondition(ConditionContext<BukkitCommandIssuer> conditionContext) throws InvalidCommandArgument {
        Player player = conditionContext.getIssuer().getPlayer();

        if (player == null) {
            throw new ConditionFailedException(translator.translate(TranslationKey.NOT_IN_OPEN_MODE.getPath()).getText());
        }

        UUID playerId = conditionContext.getIssuer().getPlayer().getUniqueId();

        GameContext gameContext = gameContextProvider.getGameContext(OPEN_MODE_GAME_KEY).orElseThrow(() -> {
            String message = translator.translate(TranslationKey.OPEN_MODE_NOT_EXISTS.getPath()).getText();
            return new ConditionFailedException(message);
        });

        gameScope.runInScope(gameContext, () -> {
            PlayerRegistry playerRegistry = playerRegistryProvider.get();

            if (playerRegistry.isRegistered(playerId)) {
                return;
            }

            throw new ConditionFailedException(translator.translate(TranslationKey.NOT_IN_OPEN_MODE.getPath()).getText());
        });
    }
}
