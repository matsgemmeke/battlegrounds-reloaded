package nl.matsgemmeke.battlegrounds.command;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
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
    private final GameContextProvider contextProvider;
    @NotNull
    private final List<CommandSender> confirmList;
    @NotNull
    private final TaskRunner taskRunner;
    @NotNull
    private final Translator translator;

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
        Map<String, Object> values = Map.of("bg_session", id);

        if (!confirmList.contains(sender)) {
            confirmList.add(sender);

            String confirmMessage = translator.translate(TranslationKey.SESSION_CONFIRM_REMOVAL.getPath()).replace(values);
            sender.sendMessage(confirmMessage);

            taskRunner.runTaskLater(() -> confirmList.remove(sender), CONFIRM_LIST_COOLDOWN);
            return;
        }

        String message;

        if (!contextProvider.removeSession(id)) {
            message = translator.translate(TranslationKey.SESSION_REMOVAL_FAILED.getPath()).replace(values);
        } else {
            message = translator.translate(TranslationKey.SESSION_REMOVED.getPath()).replace(values);
        }

        sender.sendMessage(message);
    }
}
