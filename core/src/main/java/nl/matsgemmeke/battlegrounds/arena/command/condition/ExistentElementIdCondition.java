package nl.matsgemmeke.battlegrounds.arena.command.condition;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.ConditionContext;
import co.aikar.commands.ConditionFailedException;
import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.arena.map.ArenaMap;
import nl.matsgemmeke.battlegrounds.arena.map.selection.ArenaMapSelection;
import nl.matsgemmeke.battlegrounds.arena.map.selection.ArenaMapSelector;
import nl.matsgemmeke.battlegrounds.command.condition.ParameterCondition;
import nl.matsgemmeke.battlegrounds.i18n.Replacement;
import nl.matsgemmeke.battlegrounds.i18n.TranslationKey;
import nl.matsgemmeke.battlegrounds.i18n.Translator;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ExistentElementIdCondition implements ParameterCondition<Integer> {

    private final ArenaMapSelector mapSelector;
    private final Translator translator;

    @Inject
    public ExistentElementIdCondition(ArenaMapSelector mapSelector, Translator translator) {
        this.mapSelector = mapSelector;
        this.translator = translator;
    }

    @Override
    public void validateCondition(ConditionContext<BukkitCommandIssuer> context, BukkitCommandExecutionContext execContext, Integer elementId) {
        Player player = context.getIssuer().getPlayer();

        if (player == null) {
            throw translator.translateLegacyText(TranslationKey.PLAYER_ONLY_COMMAND.getPath())
                    .map(ConditionFailedException::new)
                    .orElseGet(ConditionFailedException::new);
        }

        UUID playerId = player.getUniqueId();
        ArenaMapSelection mapSelection = mapSelector.getSelection(playerId).orElse(null);

        if (mapSelection == null) {
            throw translator.translateLegacyText(TranslationKey.NO_MAP_SELECTED.getPath())
                    .map(ConditionFailedException::new)
                    .orElseGet(ConditionFailedException::new);
        }

        ArenaMap map = mapSelection.map();

        if (map.elementExists(elementId)) {
            return;
        }

        Replacement[] replacements = {
                Replacement.parsed("bg-map-name", map.getName()),
                Replacement.parsed("bg-element-id", String.valueOf(elementId))
        };

        throw translator.translateLegacyText(TranslationKey.ELEMENT_NOT_EXISTS.getPath(), replacements)
                .map(ConditionFailedException::new)
                .orElseGet(ConditionFailedException::new);
    }
}
