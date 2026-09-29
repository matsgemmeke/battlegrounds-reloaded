package nl.matsgemmeke.battlegrounds.arena.command.completion;

import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.CommandIssuer;
import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.arena.map.selection.ArenaMapSelection;
import nl.matsgemmeke.battlegrounds.arena.map.selection.ArenaMapSelector;
import nl.matsgemmeke.battlegrounds.command.CommandCompletionHandler;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class ElementIdCommandCompletionHandler implements CommandCompletionHandler {

    private static final List<String> EMPTY = Collections.emptyList();

    private final ArenaMapSelector mapSelector;

    @Inject
    public ElementIdCommandCompletionHandler(ArenaMapSelector mapSelector) {
        this.mapSelector = mapSelector;
    }

    @Override
    public Collection<String> getCompletions(BukkitCommandCompletionContext context) {
        CommandIssuer issuer = context.getIssuer();

        if (!issuer.isPlayer()) {
            return EMPTY;
        }

        UUID playerId = issuer.getUniqueId();
        ArenaMapSelection mapSelection = mapSelector.getSelection(playerId).orElse(null);

        if (mapSelection == null) {
            return EMPTY;
        }

        return mapSelection.map().getElementIds().stream().map(String::valueOf).toList();
    }
}
