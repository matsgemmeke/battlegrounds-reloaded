package nl.matsgemmeke.battlegrounds.arena.command.executor;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameContextType;
import nl.matsgemmeke.battlegrounds.game.GameScope;
import nl.matsgemmeke.battlegrounds.game.component.membership.MembershipService;
import nl.matsgemmeke.battlegrounds.i18n.TranslationKey;
import nl.matsgemmeke.battlegrounds.i18n.Translator;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.logging.Logger;

public class LeaveCommandExecutor {

    private final GameContextProvider gameContextProvider;
    private final GameScope gameScope;
    private final Logger logger;
    private final Provider<MembershipService> membershipServiceProvider;
    private final Translator translator;

    @Inject
    public LeaveCommandExecutor(
            GameContextProvider gameContextProvider,
            GameScope gameScope,
            @Named("Battlegrounds") Logger logger,
            Provider<MembershipService> membershipServiceProvider,
            Translator translator
    ) {
        this.gameContextProvider = gameContextProvider;
        this.gameScope = gameScope;
        this.logger = logger;
        this.membershipServiceProvider = membershipServiceProvider;
        this.translator = translator;
    }

    public void execute(Player player) {
        UUID playerId = player.getUniqueId();
        GameContext gameContext = gameContextProvider.getGameContext(playerId).orElse(null);

        if (gameContext == null || gameContext.getType() != GameContextType.ARENA_MODE) {
            logger.warning("Player %s attempts to leave its current arena, however they are not registered in a game context despite prior validation".formatted(player.getName()));
            player.sendMessage(translator.translate(TranslationKey.NOT_IN_ARENA_MODE.getPath()).getText());
            return;
        }

        gameScope.runInScope(gameContext, () -> membershipServiceProvider.get().leave(player));
    }
}
