package nl.matsgemmeke.battlegrounds.configuration.hitbox;

import nl.matsgemmeke.battlegrounds.configuration.hitbox.definition.HitboxDefinition;
import nl.matsgemmeke.battlegrounds.validation.ObjectValidator;
import nl.matsgemmeke.battlegrounds.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
class HitboxConfigurationTest {

    private File hitboxesFile;

    @TempDir
    private File tempDir;
    @Mock
    private ObjectValidator objectValidator;

    @BeforeEach
    void setUp() {
        hitboxesFile = new File(tempDir, "hitboxes.yml");
        hitboxesFile.delete();
    }

    @Test
    @DisplayName("getHitboxDefinition returns HitboxDefinitionResult not found when given section is missing")
    void getHitboxDefinition_sectionNotExists() throws FileNotFoundException {
        File resourceFile = new File("src/test/resources/hitbox-configuration/empty-hitboxes-file/hitboxes.yml");
        InputStream resource = new FileInputStream(resourceFile);

        HitboxConfiguration hitboxConfiguration = new HitboxConfiguration(objectValidator, hitboxesFile, resource);
        hitboxConfiguration.load();
        HitboxDefinitionResult hitboxDefinitionResult = hitboxConfiguration.getHitboxDefinition("player", "standing");

        assertThat(hitboxDefinitionResult.getHitboxDefinition()).isEmpty();
        assertThat(hitboxDefinitionResult.getErrorMessage()).isEmpty();
    }

    @Test
    @DisplayName("getHitboxDefinition returns HitboxDefinitionResult invalid when hitbox definition is invalid")
    void getHitboxDefinition_invalidHitboxDefinition() throws FileNotFoundException {
        File resourceFile = new File("src/main/resources/hitboxes.yml");
        InputStream resource = new FileInputStream(resourceFile);

        doThrow(new ValidationException("error")).when(objectValidator).validate(any(HitboxDefinition.class));

        HitboxConfiguration hitboxConfiguration = new HitboxConfiguration(objectValidator, hitboxesFile, resource);
        hitboxConfiguration.load();
        HitboxDefinitionResult hitboxDefinitionResult = hitboxConfiguration.getHitboxDefinition("player", "standing");

        assertThat(hitboxDefinitionResult.getHitboxDefinition()).isEmpty();
        assertThat(hitboxDefinitionResult.getErrorMessage()).hasValue("error");
    }

    @Test
    @DisplayName("getHitboxDefinition returns optional with HitboxDefinition containing values from file")
    void getHitboxDefinition_hitboxDefinitionFromFile() throws FileNotFoundException {
        File resourceFile = new File("src/main/resources/hitboxes.yml");
        InputStream resource = new FileInputStream(resourceFile);

        HitboxConfiguration hitboxConfiguration = new HitboxConfiguration(objectValidator, hitboxesFile, resource);
        hitboxConfiguration.load();
        HitboxDefinitionResult hitboxDefinitionResult = hitboxConfiguration.getHitboxDefinition("player", "standing");

        assertThat(hitboxDefinitionResult.getHitboxDefinition()).hasValueSatisfying(hitboxDefinition -> {
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
        assertThat(hitboxDefinitionResult.getErrorMessage()).isEmpty();
    }
}
