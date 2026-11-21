package nl.matsgemmeke.battlegrounds.arena.command.executor;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.arena.Arena;
import nl.matsgemmeke.battlegrounds.arena.ArenaFactory;
import nl.matsgemmeke.battlegrounds.arena.ArenaRegistry;
import nl.matsgemmeke.battlegrounds.arena.settings.ArenaSettings;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.i18n.Replacement;
import nl.matsgemmeke.battlegrounds.i18n.TranslationKey;
import nl.matsgemmeke.battlegrounds.i18n.Translator;
import org.bukkit.entity.Player;

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

    public void execute(Player player, int arenaId) {
        ArenaSettings settings = ArenaSettings.getDefaultSettings();
        UUID uniqueId = player.getUniqueId();

        Arena arena = arenaFactory.create(arenaId, settings, uniqueId);
        GameKey gameKey = GameKey.ofArena(arenaId);

        arenaRegistry.addArena(gameKey, arena);

        Replacement arenaIdReplacement = Replacement.parsed("arena-id", String.valueOf(arenaId));

        translator.translateMessage(TranslationKey.ARENA_CREATED.getPath(), arenaIdReplacement).ifPresent(message -> message.send(player));
    }
}
