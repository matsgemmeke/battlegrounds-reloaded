package nl.matsgemmeke.battlegrounds.game.configuration;

import nl.matsgemmeke.battlegrounds.configuration.ConfigurationFile;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ArenaSetupConfigurationTest {

    private static final String CREATED_AT_TEXT = "2026-06-30T18:00:00Z";
    private static final Instant CREATED_AT = Instant.parse(CREATED_AT_TEXT);
    private static final String CREATED_BY_TEXT = "2c11afe2-48f0-4399-9a04-195bb8ac640e";
    private static final UUID CREATED_BY = UUID.fromString(CREATED_BY_TEXT);

    @Mock
    private ConfigurationFile configurationFile;
    @InjectMocks
    private ArenaSetupConfiguration setupConfiguration;

    @Test
    @DisplayName("getCreatedAt returns empty optional when configuration file does not have a value")
    void getCreatedAt_valueNotFound() {
        when(configurationFile.getString("created-at")).thenReturn(Optional.empty());

        Optional<Instant> createdAtOptional = setupConfiguration.getCreatedAt();

        assertThat(createdAtOptional).isEmpty();
    }

    @Test
    @DisplayName("getCreatedAt returns optional with configuration file value as instant")
    void getCreatedAt_successful() {
        when(configurationFile.getString("created-at")).thenReturn(Optional.of(CREATED_AT_TEXT));

        Optional<Instant> createdAtOptional = setupConfiguration.getCreatedAt();

        assertThat(createdAtOptional).hasValue(CREATED_AT);
    }

    @Test
    @DisplayName("setCreatedAt sets given instant as string in configuration file")
    void setCreatedAt() {
        setupConfiguration.setCreatedAt(CREATED_AT);

        verify(configurationFile).set("created-at", CREATED_AT_TEXT);
    }

    @Test
    @DisplayName("getCreatedBy returns empty optional when configuration file does not have a value")
    void getCreatedBy_valueNotFound() {
        when(configurationFile.getString("created-by")).thenReturn(Optional.empty());

        Optional<UUID> createdByOptional = setupConfiguration.getCreatedBy();

        assertThat(createdByOptional).isEmpty();
    }

    @Test
    @DisplayName("getCreatedBy returns optional with configuration file value as uuid")
    void getCreatedBy_successful() {
        when(configurationFile.getString("created-by")).thenReturn(Optional.of(CREATED_BY_TEXT));

        Optional<UUID> createdByOptional = setupConfiguration.getCreatedBy();

        assertThat(createdByOptional).hasValue(CREATED_BY);
    }

    @Test
    @DisplayName("setCreatedBy sets given uuid as string in configuration file")
    void setCreatedBy() {
        setupConfiguration.setCreatedBy(CREATED_BY);

        verify(configurationFile).set("created-by", CREATED_BY_TEXT);
    }

    @Test
    @DisplayName("save saves the configuration file")
    void save() {
        setupConfiguration.save();

        verify(configurationFile).save();
    }
}
