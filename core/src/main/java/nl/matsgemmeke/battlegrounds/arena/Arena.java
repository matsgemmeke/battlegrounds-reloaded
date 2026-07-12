package nl.matsgemmeke.battlegrounds.arena;

import nl.matsgemmeke.battlegrounds.arena.map.ArenaMap;
import nl.matsgemmeke.battlegrounds.arena.settings.ArenaSettings;
import nl.matsgemmeke.battlegrounds.game.BaseGame;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Represents an arena which groups multiple players to play various kinds of game modes.
 */
public class Arena extends BaseGame {

    private final ArenaSettings settings;
    private final int id;
    private final Set<ArenaMap> maps;

    public Arena(int id, ArenaSettings settings) {
        this.id = id;
        this.settings = settings;
        this.maps = new HashSet<>();
    }

    public int getId() {
        return id;
    }

    public ArenaSettings getSettings() {
        return settings;
    }

    public void addMap(ArenaMap map) {
        maps.add(map);
    }

    public Optional<ArenaMap> getMap(String name) {
        return maps.stream().filter(map -> map.getName().equals(name)).findFirst();
    }
}
