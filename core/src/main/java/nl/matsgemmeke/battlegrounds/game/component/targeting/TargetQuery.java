package nl.matsgemmeke.battlegrounds.game.component.targeting;

import nl.matsgemmeke.battlegrounds.game.component.targeting.condition.TargetCondition;
import org.bukkit.Location;

import java.util.*;

public class TargetQuery {

    private boolean enemiesOnly;
    private Collection<TargetCondition> conditions;
    private Location location;
    private UUID uniqueId;

    public TargetQuery() {
        this.conditions = Collections.emptySet();
        this.enemiesOnly = false;
    }

    public TargetQuery conditions(TargetCondition... conditions) {
        this.conditions = Set.of(conditions);
        return this;
    }

    public TargetQuery conditions(Collection<TargetCondition> conditions) {
        this.conditions = conditions;
        return this;
    }

    public TargetQuery enemiesOnly(boolean enemiesOnly) {
        this.enemiesOnly = enemiesOnly;
        return this;
    }

    public TargetQuery location(Location location) {
        this.location = location;
        return this;
    }

    public TargetQuery uniqueId(UUID uniqueId) {
        this.uniqueId = uniqueId;
        return this;
    }

    public Collection<TargetCondition> getConditions() {
        return conditions;
    }

    public Optional<Location> getLocation() {
        return Optional.ofNullable(location);
    }

    public Optional<UUID> getUniqueId() {
        return Optional.ofNullable(uniqueId);
    }

    public boolean isEnemiesOnly() {
        return enemiesOnly;
    }
}
