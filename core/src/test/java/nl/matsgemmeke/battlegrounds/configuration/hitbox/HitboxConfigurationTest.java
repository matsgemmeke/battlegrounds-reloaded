package nl.matsgemmeke.battlegrounds.configuration.hitbox;

import nl.matsgemmeke.battlegrounds.configuration.hitbox.definition.HitboxDefinition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
    void getHitboxDefinitionReturnsEmptyOptionalWhenGivenSectionIsMissing() throws FileNotFoundException {
        File resourceFile = new File("src/test/resources/hitbox-configuration/empty-hitboxes-file/hitboxes.yml");
        InputStream resource = new FileInputStream(resourceFile);

        HitboxConfiguration hitboxConfiguration = new HitboxConfiguration(hitboxesFile, resource);
        hitboxConfiguration.load();
        Optional<HitboxDefinition> hitboxDefinitionOptional = hitboxConfiguration.getHitboxDefinition("player", "standing");

        assertThat(hitboxDefinitionOptional).isEmpty();
    }

    @Test
    void getHitboxDefinitionThrowsInvalidHitboxDefinitionExceptionWhenHitboxDefinitionForGivenEntityTypeAndPositionFailsValidation() throws FileNotFoundException {
        File resourceFile = new File("src/test/resources/hitbox-configuration/invalid-hitboxes-file/hitboxes.yml");
        InputStream resource = new FileInputStream(resourceFile);

        HitboxConfiguration hitboxConfiguration = new HitboxConfiguration(hitboxesFile, resource);
        hitboxConfiguration.load();

        assertThatThrownBy(() -> hitboxConfiguration.getHitboxDefinition("player", "standing"))
                .isInstanceOf(InvalidHitboxDefinitionException.class)
                .hasMessage("Validation failed for the hitbox definition for player for the position standing: Invalid HitboxComponentType value 'fail' for field 'type'");
    }

    @Test
    void getHitboxDefinitionReturnsOptionalWithHitboxDefinitionContaingValuesFromFile() throws FileNotFoundException {
        File resourceFile = new File("src/main/resources/hitboxes.yml");
        InputStream resource = new FileInputStream(resourceFile);

        HitboxConfiguration hitboxConfiguration = new HitboxConfiguration(hitboxesFile, resource);
        hitboxConfiguration.load();
        Optional<HitboxDefinition> hitboxDefinitionOptional = hitboxConfiguration.getHitboxDefinition("player", "standing");

        assertThat(hitboxDefinitionOptional).hasValueSatisfying(hitboxDefinition -> {
            assertThat(hitboxDefinition.components).hasSize(5);

            assertThat(hitboxDefinition.components.get(0).type).isEqualTo("HEAD");
            assertThat(hitboxDefinition.components.get(0).size).containsExactly(0.5, 0.5, 0.5);
            assertThat(hitboxDefinition.components.get(0).offset).containsExactly(0.0, 1.4, 0.0);

            assertThat(hitboxDefinition.components.get(1).type).isEqualTo("TORSO");
            assertThat(hitboxDefinition.components.get(1).size).containsExactly(0.5, 0.7, 0.3);
            assertThat(hitboxDefinition.components.get(1).offset).containsExactly(0.0, 0.7, 0.0);

            assertThat(hitboxDefinition.components.get(2).type).isEqualTo("LIMBS");
            assertThat(hitboxDefinition.components.get(2).size).containsExactly(0.2, 0.7, 0.3);
            assertThat(hitboxDefinition.components.get(2).offset).containsExactly(0.35, 0.7, 0.0);

            assertThat(hitboxDefinition.components.get(3).type).isEqualTo("LIMBS");
            assertThat(hitboxDefinition.components.get(3).size).containsExactly(0.2, 0.7, 0.3);
            assertThat(hitboxDefinition.components.get(3).offset).containsExactly(-0.35, 0.7, 0.0);

            assertThat(hitboxDefinition.components.get(4).type).isEqualTo("LIMBS");
            assertThat(hitboxDefinition.components.get(4).size).containsExactly(0.5, 0.7, 0.3);
            assertThat(hitboxDefinition.components.get(4).offset).containsExactly(0.0, 0.0, 0.0);
        });
    }
}
