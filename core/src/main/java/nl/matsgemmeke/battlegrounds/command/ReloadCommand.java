package nl.matsgemmeke.battlegrounds.command;

import nl.matsgemmeke.battlegrounds.configuration.BattlegroundsConfiguration;
import nl.matsgemmeke.battlegrounds.text.TranslationKey;
import nl.matsgemmeke.battlegrounds.text.Translator;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ReloadCommand extends CommandSource {

    @NotNull
    private BattlegroundsConfiguration config;
    @NotNull
    private Translator translator;

    public ReloadCommand(@NotNull BattlegroundsConfiguration config, @NotNull Translator translator) {
        super("reload", translator.translate(TranslationKey.DESCRIPTION_RELOAD.getPath()).getText(), "bg reload");
        this.config = config;
        this.translator = translator;
    }

    public void execute(@NotNull CommandSender sender) {
        if (config.load()) {
            sender.sendMessage(translator.translate(TranslationKey.RELOAD_SUCCESS.getPath()).getText());
        } else {
            sender.sendMessage(translator.translate(TranslationKey.RELOAD_FAILED.getPath()).getText());
        }
    }
}
