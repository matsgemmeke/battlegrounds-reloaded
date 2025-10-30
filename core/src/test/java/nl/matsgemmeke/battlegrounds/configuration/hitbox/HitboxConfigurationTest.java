package nl.matsgemmeke.battlegrounds.configuration.hitbox;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.*;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class HitboxConfigurationTest {

    private File hitboxesFile;

    @TempDir
    private File tempDir;

    @BeforeEach
    void setUp() {
        hitboxesFile = new File(tempDir, "hitboxes.yml");
        hitboxesFile.delete();
    }

    @Test
    void getVerticalHitboxReturnsEmptyOptionalWhenPlayerUprightSectionIsMissing() throws FileNotFoundException {
        File resourceFile = new File("src/test/resources/hitbox-configuration/hitboxes.yml");
        InputStream resource = new FileInputStream(resourceFile);

        HitboxConfiguration hitboxConfiguration = new HitboxConfiguration(hitboxesFile, resource);
        hitboxConfiguration.load();
        Optional<VerticalHitbox> playerUprightHitbox = hitboxConfiguration.getVerticalHitbox("player", "upright");

        assertThat(playerUprightHitbox).isEmpty();
    }

    @Test
    void getVerticalHitboxReturnsOptionalWithPlayerUprightHitboxWithValuesFromFile() throws FileNotFoundException {
        File resourceFile = new File("src/main/resources/hitboxes.yml");
        InputStream resource = new FileInputStream(resourceFile);

        HitboxConfiguration hitboxConfiguration = new HitboxConfiguration(hitboxesFile, resource);
        hitboxConfiguration.load();
        Optional<VerticalHitbox> playerUprightHitbox = hitboxConfiguration.getVerticalHitbox("player", "upright");

        assertThat(playerUprightHitbox).hasValueSatisfying(hitbox -> {
            assertThat(hitbox.bodyHeight()).isEqualTo(1.4);
            assertThat(hitbox.headHeight()).isEqualTo(1.8);
            assertThat(hitbox.legsHeight()).isEqualTo(0.7);
            assertThat(hitbox.width()).isEqualTo(0.6);
        });
    }
}
