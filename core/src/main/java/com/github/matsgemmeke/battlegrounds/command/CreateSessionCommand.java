package com.github.matsgemmeke.battlegrounds.command;

import com.github.matsgemmeke.battlegrounds.api.BattleContextProvider;
import com.github.matsgemmeke.battlegrounds.api.game.Session;
import com.github.matsgemmeke.battlegrounds.api.game.SessionConfiguration;
import com.github.matsgemmeke.battlegrounds.game.DefaultSessionConfiguration;
import com.github.matsgemmeke.battlegrounds.game.SessionFactory;
import com.github.matsgemmeke.battlegrounds.locale.PlaceholderEntry;
import com.github.matsgemmeke.battlegrounds.locale.TranslationKey;
import com.github.matsgemmeke.battlegrounds.locale.Translator;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class CreateSessionCommand extends CommandSource {

    @NotNull
    private BattleContextProvider contextProvider;
    @NotNull
    private SessionFactory sessionFactory;
    @NotNull
    private Translator translator;

    public CreateSessionCommand(
            @NotNull BattleContextProvider contextProvider,
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

        if (!contextProvider.addSession(session)) {
            sender.sendMessage(translator.translate(TranslationKey.SESSION_CREATION_FAILED.getPath(), placeholder));
            return;
        }

        sender.sendMessage(translator.translate(TranslationKey.SESSION_CREATED.getPath(), placeholder));
    }
}
