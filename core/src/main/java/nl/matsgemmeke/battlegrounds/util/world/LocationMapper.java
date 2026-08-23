package nl.matsgemmeke.battlegrounds.util.world;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.configuration.model.LocationData;
import org.bukkit.Location;
import org.bukkit.World;

public class LocationMapper {

    private final WorldProvider worldProvider;

    @Inject
    public LocationMapper(WorldProvider worldProvider) {
        this.worldProvider = worldProvider;
    }

    public Location toLocation(LocationData locationData) {
        World world = worldProvider.getWorld(locationData.world()).orElseThrow(() -> new InvalidLocationException("Cannot convert given LocationData because its world does not exist"));

        return new Location(world, locationData.x(), locationData.y(), locationData.z(), locationData.yaw(), locationData.pitch());
    }

    public LocationData toLocationData(Location location) {
        World world = location.getWorld();

        if (world == null) {
            throw new InvalidLocationException("Cannot convert given Location because its world is null");
        }

        return new LocationData(world.getName(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }
}
