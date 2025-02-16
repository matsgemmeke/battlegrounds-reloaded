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
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class CreateSessionCommand extends CommandSource {

    @NotNull
    private final GameContextProvider contextProvider;
    @NotNull
    private final SessionFactory sessionFactory;
    @NotNull
    private final Translator translator;

    @Inject
    public CreateSessionCommand(
            @NotNull GameContextProvider contextProvider,
            @NotNull SessionFactory sessionFactory,
            @NotNull Translator translator
    ) {
        super("createsession", translator.translate(TranslationKey.DESCRIPTION_CREATESESSION.getPath()).getText(), "bg createsession <id>");
        this.contextProvider = contextProvider;
        this.sessionFactory = sessionFactory;
        this.translator = translator;
    }

    public void execute(@NotNull CommandSender sender, int id) {
        SessionConfiguration configuration = SessionConfiguration.getNewConfiguration();
        Session session = sessionFactory.create(id, configuration);
        GameKey gameKey = GameKey.ofSession(id);

        Map<String, Object> values = Map.of("bg_session", id);
        String message;

        if (!contextProvider.addSession(gameKey, session)) {
            message = translator.translate(TranslationKey.SESSION_CREATION_FAILED.getPath()).replace(values);
        } else {
            message = translator.translate(TranslationKey.SESSION_CREATED.getPath()).replace(values);
        }

        sender.sendMessage(message);
    }
}
