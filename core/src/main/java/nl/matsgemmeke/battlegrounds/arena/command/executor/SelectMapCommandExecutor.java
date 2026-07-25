package nl.matsgemmeke.battlegrounds.arena.command.executor;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import nl.matsgemmeke.battlegrounds.arena.Arena;
import nl.matsgemmeke.battlegrounds.arena.ArenaRegistry;
import nl.matsgemmeke.battlegrounds.arena.map.ArenaMap;
import nl.matsgemmeke.battlegrounds.arena.map.selection.ArenaMapSelection;
import nl.matsgemmeke.battlegrounds.arena.map.selection.ArenaMapSelector;
import nl.matsgemmeke.battlegrounds.i18n.TranslationKey;
import nl.matsgemmeke.battlegrounds.i18n.Translator;
import org.bukkit.entity.Player;

import java.util.Map;
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
            player.sendMessage(translator.translate(TranslationKey.GENERIC_ERROR.getPath()).getText());
            logger.severe("Arena %s is null in remove map command despite prior validation".formatted(arenaId));
            return;
        }

        ArenaMap map = arena.getMap(mapName).orElse(null);

        // Extra check, the command should have already validated the map name
        if (map == null) {
            player.sendMessage(translator.translate(TranslationKey.GENERIC_ERROR.getPath()).getText());
            logger.severe("Map %s in arena %s is null in remove map command despite prior validation".formatted(mapName, arenaId));
            return;
        }

        UUID playerId = player.getUniqueId();
        ArenaMapSelection selection = new ArenaMapSelection(arena, map);

        mapSelector.select(playerId, selection);

        Map<String, Object> textTemplateValues = Map.of("bg_arena", arenaId, "bg_map", mapName);

        player.sendMessage(translator.translate(TranslationKey.MAP_SELECTED.getPath()).replace(textTemplateValues));
    }
}
