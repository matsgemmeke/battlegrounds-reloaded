package nl.matsgemmeke.battlegrounds.command;

import nl.matsgemmeke.battlegrounds.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.session.DefaultSessionConfiguration;
import nl.matsgemmeke.battlegrounds.game.session.Session;
import nl.matsgemmeke.battlegrounds.game.session.SessionConfiguration;
import nl.matsgemmeke.battlegrounds.game.session.SessionFactory;
import nl.matsgemmeke.battlegrounds.locale.PlaceholderEntry;
import nl.matsgemmeke.battlegrounds.locale.TranslationKey;
import nl.matsgemmeke.battlegrounds.locale.Translator;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class CreateSessionCommand extends CommandSource {

    @NotNull
    private GameContextProvider contextProvider;
    @NotNull
    private SessionFactory sessionFactory;
    @NotNull
    private Translator translator;

    public CreateSessionCommand(
            @NotNull GameContextProvider contextProvider,
            @NotNull SessionFactory sessionFactory,
            @NotNull Translator translator
    ) {
        super("createsession", translator.translate(TranslationKey.DESCRIPTION_CREATESESSION.getPath()), "bg createsession <id>");
        this.contextProvider = contextProvider;
        this.sessionFactory = sessionFactory;
        this.translator = translator;
    }

    public void execute(@NotNull CommandSender sender, int id) {
        SessionConfiguration configuration = DefaultSessionConfiguration.getNewConfiguration();
        Session session = sessionFactory.make(id, configuration);

        PlaceholderEntry placeholder = new PlaceholderEntry("bg_session", String.valueOf(id));

        if (!contextProvider.addSessionContext(id, session.getContext())) {
            sender.sendMessage(translator.translate(TranslationKey.SESSION_CREATION_FAILED.getPath(), placeholder));
            return;
        }

        sender.sendMessage(translator.translate(TranslationKey.SESSION_CREATED.getPath(), placeholder));
    }
}
