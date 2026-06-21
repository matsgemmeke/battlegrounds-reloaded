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

public class RemoveArenaCommand extends CommandSource {

    public static final String NAME = "removearena";
    public static final String USAGE = "/bg removearena <id>";
    public static final String SUGGESTION = "/bg removearena ";
    public static final String[] PERMISSIONS = new String[] { "battlegrounds.removearena" };
    private static final long CONFIRM_LIST_COOLDOWN = 200L;

    private final GameContextProvider gameContextProvider;
    private final List<CommandSender> confirmList;
    private final Scheduler scheduler;
    private final Translator translator;

    @Inject
    public RemoveArenaCommand(GameContextProvider gameContextProvider, Scheduler scheduler, Translator translator) {
        super("removearena", translator.translate(TranslationKey.DESCRIPTION_REMOVEARENA.getPath()).getText(), "bg removearena <id>");
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

        String message;

        if (!gameContextProvider.removeArena(id)) {
            message = translator.translate(TranslationKey.ARENA_REMOVAL_FAILED.getPath()).replace(values);
        } else {
            message = translator.translate(TranslationKey.ARENA_REMOVED.getPath()).replace(values);
        }

        confirmList.remove(sender);

        sender.sendMessage(message);
    }
}
