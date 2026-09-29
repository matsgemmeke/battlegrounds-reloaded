package nl.matsgemmeke.battlegrounds.command;

import co.aikar.commands.BukkitLocales;
import co.aikar.commands.MessageKeys;
import co.aikar.commands.PaperCommandManager;
import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.configuration.BattlegroundsConfiguration;
import nl.matsgemmeke.battlegrounds.i18n.TranslationKey;
import nl.matsgemmeke.battlegrounds.i18n.Translator;

import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class CommandBootstrapper {

    private static final String ACF_COMMAND_PLACEHOLDER = "{command}";
    private static final String ACF_SYNTAX_PLACEHOLDER = "{syntax}";

    private final BattlegroundsConfiguration battlegroundsConfiguration;
    private final PaperCommandManager commandManager;
    private final Set<CommandExtension> commandExtensions;
    private final Translator translator;

    @Inject
    public CommandBootstrapper(
            BattlegroundsConfiguration battlegroundsConfiguration,
            PaperCommandManager commandManager,
            Set<CommandExtension> commandExtensions,
            Translator translator
    ) {
        this.battlegroundsConfiguration = battlegroundsConfiguration;
        this.commandManager = commandManager;
        this.commandExtensions = commandExtensions;
        this.translator = translator;
    }

    public void initialize() {
        commandExtensions.forEach(extension -> extension.configure(commandManager));

        String language = battlegroundsConfiguration.getLanguage();
        Locale locale = Locale.forLanguageTag(language);

        Map<String, Object> invalidSyntaxMessageValues = Map.of(
                "bg_command", ACF_COMMAND_PLACEHOLDER,
                "bg_syntax", ACF_SYNTAX_PLACEHOLDER
        );
        String invalidSyntaxMessage = translator.translate(TranslationKey.INVALID_SYNTAX.getPath()).replace(invalidSyntaxMessageValues);

        BukkitLocales locales = commandManager.getLocales();
        locales.setDefaultLocale(locale);
        locales.addMessage(locale, MessageKeys.INVALID_SYNTAX, invalidSyntaxMessage);
    }
}
