package nl.matsgemmeke.battlegrounds.arena.command.condition;

import co.aikar.commands.*;
import co.aikar.commands.CommandConditions.ParameterCondition;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import nl.matsgemmeke.battlegrounds.arena.Arena;
import nl.matsgemmeke.battlegrounds.arena.ArenaRegistry;
import nl.matsgemmeke.battlegrounds.arena.map.ArenaMap;
import nl.matsgemmeke.battlegrounds.text.TranslationKey;
import nl.matsgemmeke.battlegrounds.text.Translator;

import java.util.Map;
import java.util.logging.Logger;

public class NonexistentMapNameCondition implements ParameterCondition<String, BukkitCommandExecutionContext, BukkitCommandIssuer> {

    private final ArenaRegistry arenaRegistry;
    private final Logger logger;
    private final Translator translator;

    @Inject
    public NonexistentMapNameCondition(ArenaRegistry arenaRegistry, @Named("Battlegrounds") Logger logger, Translator translator) {
        this.arenaRegistry = arenaRegistry;
        this.logger = logger;
        this.translator = translator;
    }

    @Override
    public void validateCondition(ConditionContext<BukkitCommandIssuer> context, BukkitCommandExecutionContext execContext, String mapName) {
        Integer arenaId = execContext.getResolvedArg("arena-id", Integer.class);

        if (arenaId == null) {
            logger.warning("NonexistentMapNameCondition: argument \"arena-id\" was not resolved; check parameter order/name");

            String message = translator.translate(TranslationKey.GENERIC_ERROR.getPath()).getText();

            throw new ConditionFailedException(message);
        }

        Arena arena = arenaRegistry.getArena(arenaId).orElse(null);

        if (arena == null) {
            Map<String, Object> values = Map.of("bg_arena", arenaId);
            String message = translator.translate(TranslationKey.ARENA_NOT_EXISTS.getPath()).replace(values);

            throw new ConditionFailedException(message);
        }

        ArenaMap map = arena.getMap(mapName).orElse(null);

        if (map == null) {
            return;
        }

        Map<String, Object> values = Map.of("bg_arena", arenaId, "bg_map", mapName);
        String message = translator.translate(TranslationKey.MAP_ALREADY_EXISTS.getPath()).replace(values);

        throw new ConditionFailedException(message);
    }
}
