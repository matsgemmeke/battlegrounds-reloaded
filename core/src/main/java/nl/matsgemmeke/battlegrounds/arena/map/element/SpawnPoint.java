package nl.matsgemmeke.battlegrounds.arena.map.element;

import org.bukkit.Location;

public class SpawnPoint implements Element {

    private final int id;
    private final int teamId;
    private final Location location;

    public SpawnPoint(int id, int teamId, Location location) {
        this.id = id;
        this.teamId = teamId;
        this.location = location;
    }

    @Override
    public int getId() {
        return id;
    }

    public Location getLocation() {
        return location;
    }

    public int getTeamId() {
        return teamId;
    }
}
