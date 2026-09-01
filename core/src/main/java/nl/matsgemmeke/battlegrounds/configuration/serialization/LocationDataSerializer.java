package nl.matsgemmeke.battlegrounds.configuration.serialization;

import nl.matsgemmeke.battlegrounds.configuration.Section;
import nl.matsgemmeke.battlegrounds.configuration.model.LocationData;

import java.util.Optional;
import java.util.function.Function;

public class LocationDataSerializer implements DataSerializer<LocationData> {
    
    private static final String WORLD_KEY = "world";
    private static final String X_KEY = "x";
    private static final String Y_KEY = "y";
    private static final String Z_KEY = "z";
    private static final String YAW_KEY = "yaw";
    private static final String PITCH_KEY = "pitch";

    @Override
    public void serialize(LocationData data, Section section) {
        section.set(WORLD_KEY, data.world());
        section.set(X_KEY, data.x());
        section.set(Y_KEY, data.y());
        section.set(Z_KEY, data.z());
        section.set(YAW_KEY, data.yaw());
        section.set(PITCH_KEY, data.pitch());
    }

    @Override
    public LocationData deserialize(Section section) {
        String world = this.require(section, WORLD_KEY, section::getString);
        double x = this.require(section, X_KEY, section::getDouble);
        double y = this.require(section, Y_KEY, section::getDouble);
        double z = this.require(section, Z_KEY, section::getDouble);
        float yaw = this.require(section, YAW_KEY, section::getDouble).floatValue();
        float pitch = this.require(section, PITCH_KEY, section::getDouble).floatValue();

        return new LocationData(world, x, y, z, yaw, pitch);
    }

    private <T> T require(Section section, String key, Function<String, Optional<T>> getter) {
        return getter.apply(key).orElseThrow(() -> new SerializationException("Missing value '%s' at %s".formatted(key, section.getAbsolutePath())));
    }
}
