package nl.matsgemmeke.battlegrounds.arena.command.executor;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.arena.Arena;
import nl.matsgemmeke.battlegrounds.arena.ArenaFactory;
import nl.matsgemmeke.battlegrounds.arena.ArenaRegistry;
import nl.matsgemmeke.battlegrounds.arena.settings.ArenaSettings;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.text.TranslationKey;
import nl.matsgemmeke.battlegrounds.text.Translator;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public class CreateArenaCommandExecutor {

    private final ArenaFactory arenaFactory;
    private final ArenaRegistry arenaRegistry;
    private final Translator translator;

    @Inject
    public CreateArenaCommandExecutor(ArenaFactory arenaFactory, ArenaRegistry arenaRegistry, Translator translator) {
        this.arenaFactory = arenaFactory;
        this.arenaRegistry = arenaRegistry;
        this.translator = translator;
    }

    public void execute(Player player, int id) {
        ArenaSettings settings = ArenaSettings.getDefaultSettings();
        UUID uniqueId = player.getUniqueId();

        Arena arena = arenaFactory.create(id, settings, uniqueId);
        GameKey gameKey = GameKey.ofArena(id);

        Map<String, Object> values = Map.of("bg_arena", id);
        String message;

        if (!arenaRegistry.addArena(gameKey, arena)) {
            message = translator.translate(TranslationKey.ARENA_CREATION_FAILED.getPath()).replace(values);
        } else {
            message = translator.translate(TranslationKey.ARENA_CREATED.getPath()).replace(values);
        }

        player.sendMessage(message);
    }
}
