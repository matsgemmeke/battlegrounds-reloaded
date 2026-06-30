package nl.matsgemmeke.battlegrounds.game.configuration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ArenaSetupConfigurationFactoryTest {

    private final ArenaSetupConfigurationFactory arenaSetupConfigurationFactory = new ArenaSetupConfigurationFactory();

    @Test
    @DisplayName("create returns ArenaSetupConfiguration with yaml configuration file")
    void create() {
        File setupFile = new File("src/test/resources/arena-setups/valid/arena-1/setup.yml");

        ArenaSetupConfiguration setupConfiguration = arenaSetupConfigurationFactory.create(setupFile);

        assertThat(setupConfiguration.getCreatedAt()).hasValue(Instant.parse("2026-06-30T18:00:00Z"));
        assertThat(setupConfiguration.getCreatedBy()).hasValue(UUID.fromString("95d2f386-434b-378f-9cde-bdc30d3d4fbe"));
    }
}
