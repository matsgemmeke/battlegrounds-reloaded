package nl.matsgemmeke.battlegrounds.arena.command.condition;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.ConditionContext;
import co.aikar.commands.ConditionFailedException;
import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.arena.Arena;
import nl.matsgemmeke.battlegrounds.arena.ArenaRegistry;
import nl.matsgemmeke.battlegrounds.command.condition.ParameterCondition;
import nl.matsgemmeke.battlegrounds.i18n.TranslationKey;
import nl.matsgemmeke.battlegrounds.i18n.Translator;

import java.util.Map;

public class ExistentLobbyCondition implements ParameterCondition<Integer> {

    private final ArenaRegistry arenaRegistry;
    private final Translator translator;

    @Inject
    public ExistentLobbyCondition(ArenaRegistry arenaRegistry, Translator translator) {
        this.arenaRegistry = arenaRegistry;
        this.translator = translator;
    }

    @Override
    public void validateCondition(ConditionContext<BukkitCommandIssuer> context, BukkitCommandExecutionContext execContext, Integer arenaId) {
        Arena arena = arenaRegistry.getArena(arenaId).orElse(null);

        if (arena == null || arena.getLobbyLocation().isPresent()) {
            return;
        }

        Map<String, Object> values = Map.of("bg_arena_id", arenaId);
        String message = translator.translate(TranslationKey.LOBBY_NOT_EXISTS.getPath()).replace(values);

        throw new ConditionFailedException(message);
    }
}
