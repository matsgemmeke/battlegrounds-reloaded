package nl.matsgemmeke.battlegrounds.command;

import nl.matsgemmeke.battlegrounds.GameProvider;
import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.game.session.Session;
import nl.matsgemmeke.battlegrounds.locale.PlaceholderEntry;
import nl.matsgemmeke.battlegrounds.locale.TranslationKey;
import nl.matsgemmeke.battlegrounds.locale.Translator;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class RemoveSessionCommand extends CommandSource {

    private static final long CONFIRM_LIST_COOLDOWN = 200;

    @NotNull
    private GameProvider gameProvider;
    @NotNull
    private List<CommandSender> confirmList;
    @NotNull
    private TaskRunner taskRunner;
    @NotNull
    private Translator translator;

    public RemoveSessionCommand(
            @NotNull GameProvider gameProvider,
            @NotNull TaskRunner taskRunner,
            @NotNull Translator translator
    ) {
        super("removesession", translator.translate(TranslationKey.DESCRIPTION_REMOVESESSION.getPath()), "bg removesession <id>");
        this.gameProvider = gameProvider;
        this.taskRunner = taskRunner;
        this.translator = translator;
        this.confirmList = new ArrayList<>();
    }

    public void execute(@NotNull CommandSender sender, int id) {
        if (!confirmList.contains(sender)) {
            confirmList.add(sender);

            sender.sendMessage(translator.translate(TranslationKey.SESSION_CONFIRM_REMOVAL.getPath()));

            taskRunner.runTaskLater(() -> confirmList.remove(sender), CONFIRM_LIST_COOLDOWN);
            return;
        }

        Session session = gameProvider.getSession(id);
        PlaceholderEntry placeholder = new PlaceholderEntry("bg_session", String.valueOf(id));

        if (!gameProvider.removeSession(session)) {
            sender.sendMessage(translator.translate(TranslationKey.SESSION_REMOVAL_FAILED.getPath(), placeholder));
            return;
        }

        sender.sendMessage(translator.translate(TranslationKey.SESSION_REMOVED.getPath(), placeholder));
    }
}
