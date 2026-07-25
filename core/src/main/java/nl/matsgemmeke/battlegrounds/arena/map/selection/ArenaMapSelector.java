package nl.matsgemmeke.battlegrounds.arena.map.selection;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class ArenaMapSelector {

    private final Map<UUID, ArenaMapSelection> selections;

    public ArenaMapSelector() {
        this.selections = new HashMap<>();
    }

    public void select(UUID playerId, ArenaMapSelection selection) {
        selections.put(playerId, selection);
    }

    public void deselect(UUID playerId) {
        selections.remove(playerId);
    }

    public Optional<ArenaMapSelection> getSelection(UUID playerId) {
        return Optional.ofNullable(selections.get(playerId));
    }
}
