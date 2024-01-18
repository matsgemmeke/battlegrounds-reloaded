package com.github.matsgemmeke.battlegrounds.command;

import com.github.matsgemmeke.battlegrounds.*;
import com.github.matsgemmeke.battlegrounds.api.BattleContextProvider;
import com.github.matsgemmeke.battlegrounds.api.game.Session;
import com.github.matsgemmeke.battlegrounds.locale.PlaceholderEntry;
import com.github.matsgemmeke.battlegrounds.locale.TranslationKey;
import com.github.matsgemmeke.battlegrounds.locale.Translator;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class RemoveSessionCommand extends CommandSource {

    private static final long CONFIRM_LIST_COOLDOWN = 200;

    @NotNull
    private BattleContextProvider contextProvider;
    @NotNull
    private List<CommandSender> confirmList;
    @NotNull
    private TaskRunner taskRunner;
    @NotNull
    private Translator translator;

    public RemoveSessionCommand(
            @NotNull BattleContextProvider contextProvider,
            @NotNull TaskRunner taskRunner,
            @NotNull Translator translator
    ) {
        super("removesession", translator.translate(TranslationKey.DESCRIPTION_REMOVESESSION.getPath()), "bg removesession <id>");
        this.contextProvider = contextProvider;
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

        Session session = contextProvider.getSession(id);
        PlaceholderEntry placeholder = new PlaceholderEntry("bg_session", String.valueOf(id));

        if (!contextProvider.removeSession(session)) {
            sender.sendMessage(translator.translate(TranslationKey.SESSION_REMOVAL_FAILED.getPath(), placeholder));
            return;
        }

        sender.sendMessage(translator.translate(TranslationKey.SESSION_REMOVED.getPath(), placeholder));
    }
}
