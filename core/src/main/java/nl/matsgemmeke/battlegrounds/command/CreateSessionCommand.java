package nl.matsgemmeke.battlegrounds.command;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.session.SessionConfiguration;
import nl.matsgemmeke.battlegrounds.game.session.SessionContext;
import nl.matsgemmeke.battlegrounds.game.session.SessionContextFactory;
import nl.matsgemmeke.battlegrounds.text.TranslationKey;
import nl.matsgemmeke.battlegrounds.text.Translator;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class CreateSessionCommand extends CommandSource {

    @NotNull
    private GameContextProvider contextProvider;
    @NotNull
    private SessionContextFactory sessionContextFactory;
    @NotNull
    private Translator translator;

    @Inject
    public CreateSessionCommand(
            @NotNull GameContextProvider contextProvider,
            @NotNull SessionContextFactory sessionContextFactory,
            @NotNull Translator translator
    ) {
        super("createsession", translator.translate(TranslationKey.DESCRIPTION_CREATESESSION.getPath()).getText(), "bg createsession <id>");
        this.contextProvider = contextProvider;
        this.sessionContextFactory = sessionContextFactory;
        this.translator = translator;
    }

    public void execute(@NotNull CommandSender sender, int id) {
        SessionConfiguration configuration = SessionConfiguration.getNewConfiguration();
        SessionContext sessionContext = sessionContextFactory.make(id, configuration);

        Map<String, Object> values = Map.of("bg_session", id);
        String message;

        if (!contextProvider.addSessionContext(id, sessionContext)) {
            message = translator.translate(TranslationKey.SESSION_CREATION_FAILED.getPath()).replace(values);
        } else {
            message = translator.translate(TranslationKey.SESSION_CREATED.getPath()).replace(values);
        }

        sender.sendMessage(message);
    }
}
