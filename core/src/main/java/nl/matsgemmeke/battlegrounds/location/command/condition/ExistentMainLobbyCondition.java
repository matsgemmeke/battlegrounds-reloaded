package nl.matsgemmeke.battlegrounds.location.command.condition;

import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.ConditionContext;
import co.aikar.commands.ConditionFailedException;
import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.command.condition.Condition;
import nl.matsgemmeke.battlegrounds.i18n.TranslationKey;
import nl.matsgemmeke.battlegrounds.i18n.Translator;
import nl.matsgemmeke.battlegrounds.location.configuration.LocationConfiguration;

public class ExistentMainLobbyCondition implements Condition {

    private final LocationConfiguration locationConfiguration;
    private final Translator translator;

    @Inject
    public ExistentMainLobbyCondition(LocationConfiguration locationConfiguration, Translator translator) {
        this.locationConfiguration = locationConfiguration;
        this.translator = translator;
    }

    @Override
    public void validateCondition(ConditionContext<BukkitCommandIssuer> context) {
        if (locationConfiguration.getMainLobbyLocation().isPresent()) {
            return;
        }

        throw new ConditionFailedException(translator.translate(TranslationKey.MAIN_LOBBY_NOT_EXISTS.getPath()).getText());
    }
}
