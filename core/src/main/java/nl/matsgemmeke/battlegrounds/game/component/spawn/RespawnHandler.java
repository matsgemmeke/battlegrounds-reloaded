package nl.matsgemmeke.battlegrounds.game.component.spawn;

import org.bukkit.Location;

import java.util.Optional;
import java.util.UUID;

public interface RespawnHandler {

    /**
     * Retrieves and removes the custom respawn location assigned to the given entity, if one exists.
     * <p>
     * This method is intended for one-time retrieval of a stored respawn location. If a custom location is found for
     * the specified entity id, it is returned and immediately unassigned from the entity. Subsequent calls for the
     * same entity will return an empty {@link Optional} unless a new customlocation is set in the meantime.
     *
     * @param entityId the unique id of the entity whose custom respawn location is to be retrieved
     * @return         an optional containing the custom respawn location if present, or an empty optional if no custom
     *                 location is assigned
     */
    Optional<Location> consumeRespawnLocation(UUID entityId);
}
