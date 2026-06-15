package nl.matsgemmeke.battlegrounds.command;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.arena.Arena;
import nl.matsgemmeke.battlegrounds.game.arena.ArenaConfiguration;
import nl.matsgemmeke.battlegrounds.game.arena.ArenaFactory;
import nl.matsgemmeke.battlegrounds.text.TranslationKey;
import nl.matsgemmeke.battlegrounds.text.Translator;
import org.bukkit.command.CommandSender;

import java.util.Map;

public class CreateArenaCommand extends CommandSource {

    private final ArenaFactory arenaFactory;
    private final GameContextProvider gameContextProvider;
    private final Translator translator;

    @Inject
    public CreateArenaCommand(ArenaFactory arenaFactory, GameContextProvider gameContextProvider, Translator translator) {
        super("createarena", translator.translate(TranslationKey.DESCRIPTION_CREATEARENA.getPath()).getText(), "bg createarena <id>");
        this.arenaFactory = arenaFactory;
        this.gameContextProvider = gameContextProvider;
        this.translator = translator;
    }

    public void execute(CommandSender sender, int id) {
        ArenaConfiguration configuration = ArenaConfiguration.getNewConfiguration();
        Arena arena = arenaFactory.create(id, configuration);
        GameKey gameKey = GameKey.ofArena(id);

        Map<String, Object> values = Map.of("bg_arena", id);
        String message;

        if (!gameContextProvider.addArena(gameKey, arena)) {
            message = translator.translate(TranslationKey.ARENA_CREATION_FAILED.getPath()).replace(values);
        } else {
            message = translator.translate(TranslationKey.ARENA_CREATED.getPath()).replace(values);
        }

        sender.sendMessage(message);
    }
}
