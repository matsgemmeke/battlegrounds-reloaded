package nl.matsgemmeke.battlegrounds.arena.command.condition;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.ConditionContext;
import co.aikar.commands.ConditionFailedException;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import nl.matsgemmeke.battlegrounds.arena.Arena;
import nl.matsgemmeke.battlegrounds.arena.ArenaRegistry;
import nl.matsgemmeke.battlegrounds.arena.map.ArenaMap;
import nl.matsgemmeke.battlegrounds.command.condition.ParameterCondition;
import nl.matsgemmeke.battlegrounds.i18n.Replacement;
import nl.matsgemmeke.battlegrounds.i18n.TranslationKey;
import nl.matsgemmeke.battlegrounds.i18n.Translator;

import java.util.logging.Logger;

public class ExistentMapNameCondition implements ParameterCondition<String> {

    private final ArenaRegistry arenaRegistry;
    private final Logger logger;
    private final Translator translator;

    @Inject
    public ExistentMapNameCondition(ArenaRegistry arenaRegistry, @Named("Battlegrounds") Logger logger, Translator translator) {
        this.arenaRegistry = arenaRegistry;
        this.logger = logger;
        this.translator = translator;
    }

    @Override
    public void validateCondition(ConditionContext<BukkitCommandIssuer> context, BukkitCommandExecutionContext execContext, String mapName) {
        Integer arenaId = execContext.getResolvedArg("arena-id", Integer.class);

        if (arenaId == null) {
            logger.warning("ExistentMapNameCondition: argument \"arena-id\" was not resolved; check parameter order/name");

            throw translator.translateLegacyText(TranslationKey.GENERIC_ERROR.getPath())
                    .map(ConditionFailedException::new)
                    .orElseGet(ConditionFailedException::new);
        }

        Arena arena = arenaRegistry.getArena(arenaId).orElse(null);

        if (arena == null) {
            Replacement arenaIdReplacement = Replacement.parsed("arena-id", String.valueOf(arenaId));

            throw translator.translateLegacyText(TranslationKey.ARENA_NOT_EXISTS.getPath(), arenaIdReplacement)
                    .map(ConditionFailedException::new)
                    .orElseGet(ConditionFailedException::new);
        }

        ArenaMap map = arena.getMap(mapName).orElse(null);

        if (map != null) {
            return;
        }

        Replacement[] replacements = {
                Replacement.parsed("arena-id", String.valueOf(arenaId)),
                Replacement.parsed("map-name", mapName)
        };

        throw translator.translateLegacyText(TranslationKey.MAP_NOT_EXISTS.getPath(), replacements)
                .map(ConditionFailedException::new)
                .orElseGet(ConditionFailedException::new);
    }
}
