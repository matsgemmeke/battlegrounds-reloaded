package nl.matsgemmeke.battlegrounds.command;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.GameContextProvider;
import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.text.TranslationKey;
import nl.matsgemmeke.battlegrounds.text.Translator;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RemoveSessionCommand extends CommandSource {

    private static final long CONFIRM_LIST_COOLDOWN = 200;

    @NotNull
    private GameContextProvider contextProvider;
    @NotNull
    private List<CommandSender> confirmList;
    @NotNull
    private TaskRunner taskRunner;
    @NotNull
    private Translator translator;

    @Inject
    public RemoveSessionCommand(
            @NotNull GameContextProvider contextProvider,
            @NotNull TaskRunner taskRunner,
            @NotNull Translator translator
    ) {
        super("removesession", translator.translate(TranslationKey.DESCRIPTION_REMOVESESSION.getPath()).getText(), "bg removesession <id>");
        this.contextProvider = contextProvider;
        this.taskRunner = taskRunner;
        this.translator = translator;
        this.confirmList = new ArrayList<>();
    }

    public void execute(@NotNull CommandSender sender, int id) {
        if (!confirmList.contains(sender)) {
            confirmList.add(sender);

            String confirmMessage = translator.translate(TranslationKey.SESSION_CONFIRM_REMOVAL.getPath()).getText();
            sender.sendMessage(confirmMessage);

            taskRunner.runTaskLater(() -> confirmList.remove(sender), CONFIRM_LIST_COOLDOWN);
            return;
        }

        GameContext sessionContext = contextProvider.getSessionContext(id);
        Map<String, Object> values = Map.of("bg_session", id);
        String message;

        if (sessionContext == null || !contextProvider.removeSessionContext(id)) {
            message = translator.translate(TranslationKey.SESSION_REMOVAL_FAILED.getPath()).replace(values);
        } else {
            message = translator.translate(TranslationKey.SESSION_REMOVED.getPath()).replace(values);
        }

        sender.sendMessage(message);
    }
}
