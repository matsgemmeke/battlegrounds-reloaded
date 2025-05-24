package nl.matsgemmeke.battlegrounds.configuration;

import dev.dejvokep.boostedyaml.YamlDocument;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockedStatic;

import java.io.*;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mockStatic;

public class YamlReaderTest {

    @TempDir
    private Path tempDir;

    @Test
    public void loadThrowsYamlReadExceptionWhenGivenFileDoesNotExist() {
        File file = new File("src/test/resources/yaml_reader/nonexistent_folder/test.yml");

        YamlReader yamlReader = new YamlReader(file);

        assertThatThrownBy(yamlReader::load)
                .isInstanceOf(YamlReadException.class)
                .hasMessage("Cannot read file 'src/test/resources/yaml_reader/nonexistent_folder/test.yml': it does not exist");
    }

    @Test
    public void loadThrowsYamlReadExceptionWhenGivenFileHasNoYmlExtension() {
        File file = new File("src/test/resources/yaml_reader/invalid_file_extension/test.txt");

        YamlReader yamlReader = new YamlReader(file);

        assertThatThrownBy(yamlReader::load)
                .isInstanceOf(YamlReadException.class)
                .hasMessage("Cannot read file 'src/test/resources/yaml_reader/invalid_file_extension/test.txt': it does not have a .yml extension");
    }

    @Test
    public void loadThrowsYamlReadExceptionWhenGivenFileCannotBeLoaded() {
        File file = new File("src/test/resources/yaml_reader/valid_file/test.yml");

        MockedStatic<YamlDocument> yamlDocument = mockStatic(YamlDocument.class);
        yamlDocument.when(() -> YamlDocument.create(file)).thenThrow(new IOException("An IO error ocurred"));

        YamlReader yamlReader = new YamlReader(file);

        assertThatThrownBy(yamlReader::load)
                .isInstanceOf(YamlReadException.class)
                .hasMessage("An error ocurred while loading file 'src/test/resources/yaml_reader/valid_file/test.yml': An IO error ocurred");

        yamlDocument.close();
    }

    @Test
    public void loadCreatesCopyOfGivenResource() throws FileNotFoundException {
        File file = tempDir.resolve("test.yml").toFile();
        File resourceFile = new File("src/test/resources/yaml_reader/valid_file/test.yml");
        FileInputStream resource = new FileInputStream(resourceFile);

        YamlReader yamlReader = new YamlReader(file, resource);
        yamlReader.load();

        assertThat(file.exists()).isTrue();
        assertThat(yamlReader.getOptionalDouble("value.double")).hasValue(2.5);
        assertThat(yamlReader.getOptionalFloat("value.float")).hasValue(0.5F);
        assertThat(yamlReader.getOptionalFloatList("value.float-list")).hasValue(List.of(0.25F, 0.75F));
        assertThat(yamlReader.getOptionalInt("value.integer")).hasValue(1);
        assertThat(yamlReader.getString("string")).isEqualTo("success");
    }
}
