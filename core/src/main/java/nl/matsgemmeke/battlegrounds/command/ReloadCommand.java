package nl.matsgemmeke.battlegrounds.command;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.configuration.BattlegroundsConfiguration;
import nl.matsgemmeke.battlegrounds.text.TranslationKey;
import nl.matsgemmeke.battlegrounds.text.Translator;
import org.bukkit.command.CommandSender;

public class ReloadCommand extends CommandSource {

    private final BattlegroundsConfiguration config;
    private final Translator translator;

    @Inject
    public ReloadCommand(BattlegroundsConfiguration config, Translator translator) {
        super("reload", translator.translate(TranslationKey.DESCRIPTION_RELOAD.getPath()).getText(), "bg reload");
        this.config = config;
        this.translator = translator;
    }

    public void execute(CommandSender sender) {
        if (config.load()) {
            sender.sendMessage(translator.translate(TranslationKey.RELOAD_SUCCESS.getPath()).getText());
        } else {
            sender.sendMessage(translator.translate(TranslationKey.RELOAD_FAILED.getPath()).getText());
        }
    }
}
