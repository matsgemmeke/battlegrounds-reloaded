package nl.matsgemmeke.battlegrounds.arena.command.condition;

import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.ConditionContext;
import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.InvalidCommandArgument;
import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.arena.map.selection.ArenaMapSelector;
import nl.matsgemmeke.battlegrounds.command.condition.Condition;
import nl.matsgemmeke.battlegrounds.i18n.TranslationKey;
import nl.matsgemmeke.battlegrounds.i18n.Translator;
import org.bukkit.entity.Player;

import java.util.UUID;

public class MapSelectedCondition implements Condition {

    private final ArenaMapSelector mapSelector;
    private final Translator translator;

    @Inject
    public MapSelectedCondition(ArenaMapSelector mapSelector, Translator translator) {
        this.mapSelector = mapSelector;
        this.translator = translator;
    }

    @Override
    public void validateCondition(ConditionContext<BukkitCommandIssuer> context) throws InvalidCommandArgument {
        Player player = context.getIssuer().getPlayer();

        if (player == null) {
            throw new ConditionFailedException(translator.translate(TranslationKey.NO_MAP_SELECTED.getPath()).getText());
        }

        UUID playerId = player.getUniqueId();

        if (mapSelector.getSelection(playerId).isPresent()) {
            return;
        }

        throw new ConditionFailedException(translator.translate(TranslationKey.NO_MAP_SELECTED.getPath()).getText());
    }
}
