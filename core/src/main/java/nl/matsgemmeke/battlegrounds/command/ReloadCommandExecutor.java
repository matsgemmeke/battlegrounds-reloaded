package nl.matsgemmeke.battlegrounds.command;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.configuration.BattlegroundsConfiguration;
import nl.matsgemmeke.battlegrounds.i18n.TranslationKey;
import nl.matsgemmeke.battlegrounds.i18n.Translator;
import org.bukkit.command.CommandSender;

public class ReloadCommandExecutor {

    private final BattlegroundsConfiguration config;
    private final Translator translator;

    @Inject
    public ReloadCommandExecutor(BattlegroundsConfiguration config, Translator translator) {
        this.config = config;
        this.translator = translator;
    }

    public void execute(CommandSender sender) {
        if (config.load()) {
            translator.translateMessage(TranslationKey.RELOAD_SUCCESS.getPath()).ifPresent(message -> message.send(sender));
        } else {
            translator.translateMessage(TranslationKey.RELOAD_FAILED.getPath()).ifPresent(message -> message.send(sender));
        }
    }
}
