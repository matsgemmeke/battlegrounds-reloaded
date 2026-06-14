package nl.matsgemmeke.battlegrounds.command;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.session.Session;
import nl.matsgemmeke.battlegrounds.game.session.SessionConfiguration;
import nl.matsgemmeke.battlegrounds.game.session.SessionFactory;
import nl.matsgemmeke.battlegrounds.text.TranslationKey;
import nl.matsgemmeke.battlegrounds.text.Translator;
import org.bukkit.command.CommandSender;

import java.util.Map;

public class CreateArenaCommand extends CommandSource {

    private final GameContextProvider gameContextProvider;
    private final SessionFactory sessionFactory;
    private final Translator translator;

    @Inject
    public CreateArenaCommand(GameContextProvider gameContextProvider, SessionFactory sessionFactory, Translator translator) {
        super("createarena", translator.translate(TranslationKey.DESCRIPTION_CREATEARENA.getPath()).getText(), "bg createarena <id>");
        this.gameContextProvider = gameContextProvider;
        this.sessionFactory = sessionFactory;
        this.translator = translator;
    }

    public void execute(CommandSender sender, int id) {
        SessionConfiguration configuration = SessionConfiguration.getNewConfiguration();
        Session session = sessionFactory.create(id, configuration);
        GameKey gameKey = GameKey.ofArena(id);

        Map<String, Object> values = Map.of("bg_arena", id);
        String message;

        if (!gameContextProvider.addArena(gameKey, session)) {
            message = translator.translate(TranslationKey.ARENA_CREATION_FAILED.getPath()).replace(values);
        } else {
            message = translator.translate(TranslationKey.ARENA_CREATED.getPath()).replace(values);
        }

        sender.sendMessage(message);
    }
}
