package nl.matsgemmeke.battlegrounds.arena.command.executor;

import com.google.inject.Inject;
import jakarta.inject.Named;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.i18n.Replacement;
import nl.matsgemmeke.battlegrounds.i18n.TranslationKey;
import nl.matsgemmeke.battlegrounds.i18n.Translator;
import nl.matsgemmeke.battlegrounds.scheduling.Schedule;
import nl.matsgemmeke.battlegrounds.scheduling.Scheduler;
import nl.matsgemmeke.battlegrounds.util.FileUtil;
import org.bukkit.command.CommandSender;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RemoveArenaCommandExecutor {

    private static final long CONFIRM_LIST_COOLDOWN = 200L;

    private final File arenasFolder;
    private final GameContextProvider gameContextProvider;
    private final List<CommandSender> confirmList;
    private final Scheduler scheduler;
    private final Translator translator;

    @Inject
    public RemoveArenaCommandExecutor(@Named("ArenasFolder") File arenasFolder, GameContextProvider gameContextProvider, Scheduler scheduler, Translator translator) {
        this.arenasFolder = arenasFolder;
        this.gameContextProvider = gameContextProvider;
        this.scheduler = scheduler;
        this.translator = translator;
        this.confirmList = new ArrayList<>();
    }

    public void execute(CommandSender sender, int arenaId) {
        Replacement arenaIdReplacement = Replacement.parsed("arena-id", String.valueOf(arenaId));

        if (!confirmList.contains(sender)) {
            confirmList.add(sender);

            translator.translateMessage(TranslationKey.ARENA_CONFIRM_REMOVAL.getPath(), arenaIdReplacement).ifPresent(message -> message.send(sender));

            Schedule schedule = scheduler.createSingleRunSchedule(CONFIRM_LIST_COOLDOWN);
            schedule.addTask(() -> confirmList.remove(sender));
            schedule.start();
            return;
        }

        GameKey gameKey = GameKey.ofArena(arenaId);
        boolean removed = gameContextProvider.removeGameContext(gameKey);

        if (!removed) {
            translator.translateMessage(TranslationKey.ARENA_REMOVAL_FAILED.getPath(), arenaIdReplacement).ifPresent(message -> message.send(sender));
            return;
        }

        confirmList.remove(sender);

        File arenaFolder = new File(arenasFolder, "arena-" + arenaId);
        FileUtil.deleteFolder(arenaFolder);

        translator.translateMessage(TranslationKey.ARENA_REMOVED.getPath(), arenaIdReplacement).ifPresent(message -> message.send(sender));
    }
}
