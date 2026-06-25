package nl.matsgemmeke.battlegrounds.command.arena;

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

public class RemoveArenaCommandExecutor {

    private static final long CONFIRM_LIST_COOLDOWN = 200L;

    private final GameContextProvider gameContextProvider;
    private final List<CommandSender> confirmList;
    private final Scheduler scheduler;
    private final Translator translator;

    @Inject
    public RemoveArenaCommandExecutor(GameContextProvider gameContextProvider, Scheduler scheduler, Translator translator) {
        this.gameContextProvider = gameContextProvider;
        this.scheduler = scheduler;
        this.translator = translator;
        this.confirmList = new ArrayList<>();
    }

    public void execute(CommandSender sender, int id) {
        Map<String, Object> values = Map.of("bg_arena", id);

        if (!confirmList.contains(sender)) {
            confirmList.add(sender);

            String confirmMessage = translator.translate(TranslationKey.ARENA_CONFIRM_REMOVAL.getPath()).replace(values);
            sender.sendMessage(confirmMessage);

            Schedule schedule = scheduler.createSingleRunSchedule(CONFIRM_LIST_COOLDOWN);
            schedule.addTask(() -> confirmList.remove(sender));
            schedule.start();
            return;
        }

        if (!gameContextProvider.removeArena(id)) {
            String removalFailedMessage = translator.translate(TranslationKey.ARENA_REMOVAL_FAILED.getPath()).replace(values);
            sender.sendMessage(removalFailedMessage);
            return;
        }

        confirmList.remove(sender);

        String removedMessage = translator.translate(TranslationKey.ARENA_REMOVED.getPath()).replace(values);
        sender.sendMessage(removedMessage);
    }
}
