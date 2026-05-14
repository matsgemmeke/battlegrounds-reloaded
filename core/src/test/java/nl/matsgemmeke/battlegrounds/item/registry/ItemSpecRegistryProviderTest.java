package nl.matsgemmeke.battlegrounds.item.registry;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import nl.matsgemmeke.battlegrounds.configuration.spec.SpecDeserializer;
import nl.matsgemmeke.battlegrounds.validation.ObjectValidator;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.net.URISyntaxException;
import java.util.logging.Logger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.spy;

@ExtendWith(MockitoExtension.class)
class ItemSpecRegistryProviderTest {

    @TempDir
    private File tempDirectory;
    @Mock
    private Logger logger;
    @Spy
    private SpecDeserializer specDeserializer = new SpecDeserializer();

    private ObjectValidator objectValidator;

    @BeforeEach
    void setUp() {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

        objectValidator = new ObjectValidator(validator);
    }

    @AfterEach
    void tearDown() {
        // Activate garbage collector to release file lock
        System.gc();
    }

    @Test
    @DisplayName("get throws IllegalStateException when resources location is an invalid URI")
    void get_invalidResourcesLocation() throws URISyntaxException {
        File itemsFolder = new File(tempDirectory.getPath() + "/items");

        ItemSpecRegistryProvider provider = spy(new ItemSpecRegistryProvider(objectValidator, specDeserializer, itemsFolder, logger));
        when(provider.createResourceURI()).thenThrow(new URISyntaxException("fail", "test"));

        assertThatThrownBy(provider::get).isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("get logs warning message when resources directory does not contain any files")
    void get_emptyResourcesDirectory() {
        File itemsFolder = new File("src/test/resources/weapon_creator_provider/items_empty_directory");
        itemsFolder.mkdirs();

        ItemSpecRegistryProvider provider = spy(new ItemSpecRegistryProvider(objectValidator, specDeserializer, itemsFolder, logger));
        provider.get();

        verify(logger).warning("Unable to load item configuration files: directory 'src/test/resources/weapon_creator_provider/items_empty_directory' is empty. To generate the default item files, delete the directory and reload the plugin.");
    }

    @Test
    @DisplayName("get logs warning messages when subfolder does not contain any files")
    void get_emptySubfolders() {
        File itemsFolder = new File("src/test/resources/weapon_creator_provider/items_empty_subfolders");

        File itemsSubfolder = new File("src/test/resources/weapon_creator_provider/items_empty_subfolders/submachine_guns");
        itemsSubfolder.mkdirs();

        ItemSpecRegistryProvider provider = spy(new ItemSpecRegistryProvider(objectValidator, specDeserializer, itemsFolder, logger));
        provider.get();

        verify(logger).warning("Unable to load item configuration files in items subfolder 'src/test/resources/weapon_creator_provider/items_empty_subfolders/submachine_guns'");
    }

    @Test
    @DisplayName("get logs error message when item file fails to load")
    void get_itemFileLoadFailure() {
        File itemsFolder = new File(tempDirectory.getPath() + "/items");

        MockedStatic<YamlConfiguration> yamlConfiguration = mockStatic(YamlConfiguration.class);
        yamlConfiguration.when(() -> YamlConfiguration.loadConfiguration(any(File.class))).thenThrow(new IllegalArgumentException("An error occurred"));

        ItemSpecRegistryProvider provider = spy(new ItemSpecRegistryProvider(objectValidator, specDeserializer, itemsFolder, logger));
        provider.get();

        verify(logger).severe("Unable to load item configuration file 'olympia.yml': An error occurred");
        verify(logger).severe("Unable to load item configuration file 'mp5.yml': An error occurred");

        yamlConfiguration.close();
    }

    @Test
    @DisplayName("get logs error message when item file does not contain a name value")
    void get_itemFileWithoutName() {
        File itemsFolder = new File("src/test/resources/weapon_creator_provider/items_without_name");

        ItemSpecRegistryProvider provider = spy(new ItemSpecRegistryProvider(objectValidator, specDeserializer, itemsFolder, logger));
        ItemSpecRegistry registry = provider.get();

        assertThat(registry.exists("Olympia")).isFalse();
        assertThat(registry.exists("MP5")).isFalse();

        verify(logger).severe("An error occurred while loading file 'olympia.yml': Identifier 'name' is missing");
        verify(logger).severe("An error occurred while loading file 'mp5.yml': Identifier 'name' is missing");
    }

    @Test
    @DisplayName("get logs error message when item file contains error in its specification")
    void get_itemSpecificationContainsError() {
        File itemsFolder = new File("src/test/resources/weapon_creator_provider/items_invalid");

        ItemSpecRegistryProvider provider = spy(new ItemSpecRegistryProvider(objectValidator, specDeserializer, itemsFolder, logger));
        ItemSpecRegistry registry = provider.get();

        assertThat(registry.exists("Olympia")).isFalse();
        assertThat(registry.exists("MP5")).isFalse();

        verify(logger).severe("""
                An error occurred while loading item 'Olympia': Validation failed for GunSpec (1 constraint violations):
                 - level-unlocked: value is required""");
        verify(logger).severe("""
                An error occurred while loading item 'MP5': Validation failed for GunSpec (1 constraint violations):
                 - level-unlocked: value is required""");
    }

    @Test
    @DisplayName("get logs error message when item file contains no item type specification")
    void getLogsErrorMessagesWhenItemFileContainsNoItemTypeSpecification() {
        File itemsFolder = new File("src/test/resources/weapon_creator_provider/items_without_type");

        ItemSpecRegistryProvider provider = spy(new ItemSpecRegistryProvider(objectValidator, specDeserializer, itemsFolder, logger));
        ItemSpecRegistry registry = provider.get();

        assertThat(registry.exists("MP5")).isFalse();

        verify(logger).severe("An error occurred while loading item 'MP5': no item type is specified");
    }

    @Test
    @DisplayName("get copies resources files when items directory does not exist yet")
    void get_itemsDirectoryDoesNotExist() {
        File itemsFolder = new File(tempDirectory.getPath() + "/items");

        ItemSpecRegistryProvider provider = spy(new ItemSpecRegistryProvider(objectValidator, specDeserializer, itemsFolder, logger));
        ItemSpecRegistry registry = provider.get();

        File createdItemFile = new File(itemsFolder + "/submachine_guns/mp5.yml");

        assertThat(registry).isNotNull();
        assertThat(itemsFolder.exists()).isTrue();
        assertThat(createdItemFile.exists()).isTrue();
    }

    @Test
    @DisplayName("get loads item files and creates item specifications")
    void get_loadsFromItemsDirectory() {
        File itemsFolder = new File("src/main/resources/items");

        ItemSpecRegistryProvider provider = spy(new ItemSpecRegistryProvider(objectValidator, specDeserializer, itemsFolder, logger));
        ItemSpecRegistry registry = provider.get();

        assertThat(registry.exists("Olympia")).isTrue();
        assertThat(registry.exists("MP5")).isTrue();
    }

    @Test
    @DisplayName("get loads item files without subfolders and creates item specifications")
    void get_loadsFromItemsDirectoryWithoutSubfolders() {
        File itemsFolder = new File("src/main/resources/items/submachine_guns");

        ItemSpecRegistryProvider provider = spy(new ItemSpecRegistryProvider(objectValidator, specDeserializer, itemsFolder, logger));
        ItemSpecRegistry registry = provider.get();

        assertThat(registry.exists("MP5")).isTrue();
    }
}
