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
import nl.matsgemmeke.battlegrounds.i18n.TranslationKey;
import nl.matsgemmeke.battlegrounds.i18n.Translator;
import org.bukkit.entity.Player;

import java.util.Map;
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
            String message = translator.translate(TranslationKey.PLAYER_ONLY_COMMAND.getPath()).getText();

            throw new ConditionFailedException(message);
        }

        UUID playerId = player.getUniqueId();
        ArenaMapSelection mapSelection = mapSelector.getSelection(playerId).orElse(null);

        if (mapSelection == null) {
            String message = translator.translate(TranslationKey.NO_MAP_SELECTED.getPath()).getText();

            throw new ConditionFailedException(message);
        }

        ArenaMap map = mapSelection.map();

        if (map.elementExists(elementId)) {
            return;
        }

        Map<String, Object> values = Map.of(
                "bg_map_name", map.getName(),
                "bg_element_id", elementId
        );
        String message = translator.translate(TranslationKey.ELEMENT_NOT_EXISTS.getPath()).replace(values);

        throw new ConditionFailedException(message);
    }
}
