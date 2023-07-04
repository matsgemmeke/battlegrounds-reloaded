package com.github.matsgemmeke.battlegrounds.command;

import com.github.matsgemmeke.battlegrounds.api.configuration.BattlegroundsConfig;
import com.github.matsgemmeke.battlegrounds.locale.TranslationKey;
import com.github.matsgemmeke.battlegrounds.locale.Translator;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ReloadCommand extends CommandSource {

    @NotNull
    private BattlegroundsConfig config;
    @NotNull
    private Translator translator;

    public ReloadCommand(@NotNull BattlegroundsConfig config, @NotNull Translator translator) {
        super("reload", translator.translate(TranslationKey.DESCRIPTION_RELOAD.getPath()), "bg reload");
        this.config = config;
        this.translator = translator;
    }

    public void execute(@NotNull CommandSender sender) {
        if (config.load()) {
            sender.sendMessage(translator.translate(TranslationKey.RELOAD_SUCCESS.getPath()));
        } else {
            sender.sendMessage(translator.translate(TranslationKey.RELOAD_FAILED.getPath()));
        }
    }
}
