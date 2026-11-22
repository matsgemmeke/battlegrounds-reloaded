package nl.matsgemmeke.battlegrounds.arena.command.executor;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import nl.matsgemmeke.battlegrounds.arena.Arena;
import nl.matsgemmeke.battlegrounds.arena.ArenaRegistry;
import nl.matsgemmeke.battlegrounds.arena.map.ArenaMap;
import nl.matsgemmeke.battlegrounds.arena.map.selection.ArenaMapSelection;
import nl.matsgemmeke.battlegrounds.arena.map.selection.ArenaMapSelector;
import nl.matsgemmeke.battlegrounds.i18n.Replacement;
import nl.matsgemmeke.battlegrounds.i18n.TranslationKey;
import nl.matsgemmeke.battlegrounds.i18n.Translator;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.logging.Logger;

public class SelectMapCommandExecutor {

    private final ArenaMapSelector mapSelector;
    private final ArenaRegistry arenaRegistry;
    private final Logger logger;
    private final Translator translator;

    @Inject
    public SelectMapCommandExecutor(ArenaMapSelector mapSelector, ArenaRegistry arenaRegistry, @Named("Battlegrounds") Logger logger, Translator translator) {
        this.mapSelector = mapSelector;
        this.arenaRegistry = arenaRegistry;
        this.logger = logger;
        this.translator = translator;
    }

    public void execute(Player player, int arenaId, String mapName) {
        Arena arena = arenaRegistry.getArena(arenaId).orElse(null);

        // Extra check, the command should have already validated the arena id
        if (arena == null) {
            logger.severe("Arena %s is null in remove map command despite prior validation".formatted(arenaId));
            translator.translateMessage(TranslationKey.GENERIC_ERROR.getPath()).ifPresent(message -> message.send(player));
            return;
        }

        ArenaMap map = arena.getMap(mapName).orElse(null);

        // Extra check, the command should have already validated the map name
        if (map == null) {
            logger.severe("Map %s in arena %s is null in remove map command despite prior validation".formatted(mapName, arenaId));
            translator.translateMessage(TranslationKey.GENERIC_ERROR.getPath()).ifPresent(message -> message.send(player));
            return;
        }

        UUID playerId = player.getUniqueId();
        ArenaMapSelection selection = new ArenaMapSelection(arena, map);

        mapSelector.select(playerId, selection);

        Replacement[] replacements = {
                Replacement.parsed("arena-id", String.valueOf(arenaId)),
                Replacement.parsed("map-name", mapName)
        };

        translator.translateMessage(TranslationKey.MAP_SELECTED.getPath(), replacements).ifPresent(message -> message.send(player));
    }
}
