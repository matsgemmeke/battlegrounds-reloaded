package nl.matsgemmeke.battlegrounds.game.component;

import org.bukkit.Location;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class TargetQuery {

    private final Map<TargetType, Double> ranges;
    private Boolean enemiesOnly;
    private Location location;
    private UUID uniqueId;

    public TargetQuery() {
        this.ranges = new EnumMap<>(TargetType.class);
    }

    public TargetQuery enemiesOnly(Boolean enemiesOnly) {
        this.enemiesOnly = enemiesOnly;
        return this;
    }

    public TargetQuery forEntity(UUID uniqueId) {
        this.uniqueId = uniqueId;
        return this;
    }

    public TargetQuery forLocation(Location location) {
        this.location = location;
        return this;
    }

    public TargetQuery withRange(TargetType type, double value) {
        ranges.put(type, value);
        return this;
    }

    public Optional<Location> getLocation() {
        return Optional.ofNullable(location);
    }

    public Optional<Double> getRange(TargetType type) {
        return Optional.ofNullable(ranges.get(type));
    }

    public Optional<UUID> getUniqueId() {
        return Optional.ofNullable(uniqueId);
    }

    public Optional<Boolean> isEnemiesOnly() {
        return Optional.ofNullable(enemiesOnly);
    }
}
