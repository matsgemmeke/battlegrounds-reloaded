package nl.matsgemmeke.battlegrounds.game.configuration;

import nl.matsgemmeke.battlegrounds.configuration.ConfigurationFile;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public class ArenaSetupConfiguration {

    private static final String CREATED_AT_PATH = "created-at";
    private static final String CREATED_BY_PATH = "created-by";

    private final ConfigurationFile configurationFile;

    public ArenaSetupConfiguration(ConfigurationFile configurationFile) {
        this.configurationFile = configurationFile;
    }

    public Optional<Instant> getCreatedAt() {
        return configurationFile.getString(CREATED_AT_PATH).map(Instant::parse);
    }

    public void setCreatedAt(Instant instant) {
        configurationFile.set(CREATED_AT_PATH, instant.toString());
    }

    public Optional<UUID> getCreatedBy() {
        return configurationFile.getString(CREATED_BY_PATH).map(UUID::fromString);
    }

    public void setCreatedBy(UUID uuid) {
        configurationFile.set(CREATED_BY_PATH, uuid.toString());
    }

    public void save() {
        configurationFile.save();
    }
}
