package nl.matsgemmeke.battlegrounds.command;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.scheduling.Schedule;
import nl.matsgemmeke.battlegrounds.scheduling.Scheduler;
import nl.matsgemmeke.battlegrounds.text.TranslationKey;
import nl.matsgemmeke.battlegrounds.text.Translator;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RemoveSessionCommand extends CommandSource {

    private static final long CONFIRM_LIST_COOLDOWN = 200L;

    private final GameContextProvider gameContextProvider;
    private final List<CommandSender> confirmList;
    private final Scheduler scheduler;
    private final Translator translator;

    @Inject
    public RemoveSessionCommand(GameContextProvider gameContextProvider, Scheduler scheduler, Translator translator) {
        super("removesession", translator.translate(TranslationKey.DESCRIPTION_REMOVESESSION.getPath()).getText(), "bg removesession <id>");
        this.gameContextProvider = gameContextProvider;
        this.scheduler = scheduler;
        this.translator = translator;
        this.confirmList = new ArrayList<>();
    }

    public void execute(CommandSender sender, int id) {
        Map<String, Object> values = Map.of("bg_session", id);

        if (!confirmList.contains(sender)) {
            confirmList.add(sender);

            String confirmMessage = translator.translate(TranslationKey.SESSION_CONFIRM_REMOVAL.getPath()).replace(values);
            sender.sendMessage(confirmMessage);

            Schedule schedule = scheduler.createSingleRunSchedule(CONFIRM_LIST_COOLDOWN);
            schedule.addTask(() -> confirmList.remove(sender));
            schedule.start();
            return;
        }

        String message;

        if (!gameContextProvider.removeSession(id)) {
            message = translator.translate(TranslationKey.SESSION_REMOVAL_FAILED.getPath()).replace(values);
        } else {
            message = translator.translate(TranslationKey.SESSION_REMOVED.getPath()).replace(values);
        }

        confirmList.remove(sender);

        sender.sendMessage(message);
    }
}
