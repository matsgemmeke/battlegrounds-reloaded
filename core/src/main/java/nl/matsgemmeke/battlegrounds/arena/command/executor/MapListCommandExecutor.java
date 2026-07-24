package nl.matsgemmeke.battlegrounds.arena.command.executor;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.arena.Arena;
import nl.matsgemmeke.battlegrounds.arena.ArenaRegistry;
import nl.matsgemmeke.battlegrounds.arena.exception.ArenaNotFoundException;
import nl.matsgemmeke.battlegrounds.arena.map.ArenaMap;
import nl.matsgemmeke.battlegrounds.text.TranslationKey;
import nl.matsgemmeke.battlegrounds.text.Translator;
import org.bukkit.entity.Player;

import java.util.Map;

public class MapListCommandExecutor {

    private final ArenaRegistry arenaRegistry;
    private final Translator translator;

    @Inject
    public MapListCommandExecutor(ArenaRegistry arenaRegistry, Translator translator) {
        this.arenaRegistry = arenaRegistry;
        this.translator = translator;
    }

    public void execute(Player player, int arenaId) {
        Arena arena = arenaRegistry.getArena(arenaId).orElse(null);

        if (arena == null) {
            throw new ArenaNotFoundException("Received a supposedly validated arena id %s, but the arena instance is not present".formatted(arenaId));
        }

        Map<String, Object> headerTemplateValues = Map.of("bg_arena", arenaId);
        String header = translator.translate(TranslationKey.MAP_LIST_HEADER.getPath()).replace(headerTemplateValues);

        player.sendMessage(header);

        for (ArenaMap map : arena.getMaps()) {
            Map<String, Object> entryTemplateValues = Map.of(
                    "bg_arena", arenaId,
                    "bg_map", map.getName(),
                    "bg_map_created_by", "me",
                    "bg_map_creation_date", "yesterday"
            );
            String entry = translator.translate(TranslationKey.MAP_LIST_ENTRY.getPath()).replace(entryTemplateValues);

            player.sendMessage(entry);
        }

        player.sendMessage(translator.translate(TranslationKey.MAP_LIST_FOOTER.getPath()).getText());
    }
}
