package nl.matsgemmeke.battlegrounds.arena;

import nl.matsgemmeke.battlegrounds.arena.map.ArenaMap;
import nl.matsgemmeke.battlegrounds.arena.settings.ArenaSettings;
import nl.matsgemmeke.battlegrounds.game.BaseGame;
import org.bukkit.Location;

import java.util.*;

/**
 * Represents an arena which groups multiple players to play various kinds of game modes.
 */
public class Arena extends BaseGame {

    private final ArenaSettings settings;
    private final int id;
    private final Set<ArenaMap> maps;
    private Location lobbyLocation;

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

    public Optional<Location> getLobbyLocation() {
        return Optional.ofNullable(lobbyLocation);
    }

    public void setLobbyLocation(Location lobbyLocation) {
        this.lobbyLocation = lobbyLocation;
    }

    public void addMap(ArenaMap map) {
        maps.add(map);
    }

    public void removeMap(ArenaMap map) {
        maps.remove(map);
    }

    public List<ArenaMap> getMaps() {
        return new ArrayList<>(maps);
    }

    public Optional<ArenaMap> getMap(String name) {
        return maps.stream().filter(map -> map.getName().equals(name)).findFirst();
    }

    public List<String> getMapNames() {
        return maps.stream().map(ArenaMap::getName).toList();
    }
}
