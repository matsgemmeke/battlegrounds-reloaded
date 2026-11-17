package nl.matsgemmeke.battlegrounds.command;

import com.google.inject.Inject;
import net.kyori.adventure.audience.Audience;
import nl.matsgemmeke.battlegrounds.i18n.TranslationKey;
import nl.matsgemmeke.battlegrounds.i18n.Translator;
import nl.matsgemmeke.battlegrounds.i18n.Replacement;
import org.bukkit.permissions.Permissible;

import java.util.Arrays;
import java.util.List;

public class HelpMenu {

    private final Translator translator;

    @Inject
    public HelpMenu(Translator translator) {
        this.translator = translator;
    }

    public void send(Audience audience, List<CommandInfo> commandInfoList) {
        audience.forEachAudience(member -> commandInfoList.stream()
                .filter(commandInfo -> this.hasPermission(member, commandInfo))
                .forEach(commandInfo -> this.send(member, commandInfo)));
    }

    private boolean hasPermission(Audience audience, CommandInfo commandInfo) {
        String[] permissions = commandInfo.permissions();

        if (permissions.length == 0) {
            return true;
        }

        return audience instanceof Permissible permissible
                && Arrays.stream(permissions).anyMatch(permissible::hasPermission);
    }

    private void send(Audience audience, CommandInfo commandInfo) {
        Replacement[] replacements = {
                Replacement.unparsed("bg-description", commandInfo.description()),
                Replacement.unparsed("bg-usage", commandInfo.usage()),
                Replacement.parsed("bg-suggestion", commandInfo.suggestion())
        };

        translator.translateMessage(TranslationKey.HELP_MENU_COMMAND.getPath(), replacements).ifPresent(message -> message.send(audience));
    }
}
