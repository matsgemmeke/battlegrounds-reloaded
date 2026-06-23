package nl.matsgemmeke.battlegrounds.command;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.configuration.BattlegroundsConfiguration;
import nl.matsgemmeke.battlegrounds.text.TranslationKey;
import nl.matsgemmeke.battlegrounds.text.Translator;
import org.bukkit.command.CommandSender;

public class ReloadCommandExecutor {

    public static final String USAGE = "/bg reload";
    public static final String SUGGESTION = "/bg reload";
    public static final String[] PERMISSIONS = new String[] { "battlegrounds.reload" };

    private final BattlegroundsConfiguration config;
    private final Translator translator;

    @Inject
    public ReloadCommandExecutor(BattlegroundsConfiguration config, Translator translator) {
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
