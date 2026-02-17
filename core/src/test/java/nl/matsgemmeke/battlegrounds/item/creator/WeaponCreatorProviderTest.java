package nl.matsgemmeke.battlegrounds.item.creator;

import com.google.inject.Provider;
import nl.matsgemmeke.battlegrounds.configuration.spec.SpecDeserializer;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentFactory;
import nl.matsgemmeke.battlegrounds.item.gun.GunFactory;
import nl.matsgemmeke.battlegrounds.item.melee.MeleeWeaponFactory;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.AfterEach;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WeaponCreatorProviderTest {

    @TempDir
    private File tempDirectory;
    @Mock
    private Logger logger;
    @Mock
    private Provider<EquipmentFactory> equipmentFactoryProvider;
    @Mock
    private Provider<GunFactory> gunFactoryProvider;
    @Mock
    private Provider<MeleeWeaponFactory> meleeWeaponFactoryProvider;
    @Spy
    private SpecDeserializer specDeserializer = new SpecDeserializer();

    @AfterEach
    void tearDown() {
        // Activate garbage collector to release file lock
        System.gc();
    }

    @Test
    void getThrowsIllegalStateExceptionWhenResourceLocationIsInvalidURI() throws URISyntaxException {
        File itemsFolder = new File(tempDirectory.getPath() + "/items");

        WeaponCreatorProvider provider = spy(new WeaponCreatorProvider(equipmentFactoryProvider, gunFactoryProvider, meleeWeaponFactoryProvider, specDeserializer, itemsFolder, logger));
        when(provider.createResourceURI()).thenThrow(new URISyntaxException("fail", "test"));

        assertThatThrownBy(provider::get).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void getLogsWarningsMessageWhenDirectoryDoesNotContainAnyFiles() {
        File itemsFolder = new File("src/test/resources/weapon_creator_provider/items_empty_directory");
        itemsFolder.mkdirs();

        WeaponCreatorProvider provider = new WeaponCreatorProvider(equipmentFactoryProvider, gunFactoryProvider, meleeWeaponFactoryProvider, specDeserializer, itemsFolder, logger);
        provider.get();

        verify(logger).warning("Unable to load item configuration files: directory 'src/test/resources/weapon_creator_provider/items_empty_directory' is empty. To generate the default item files, delete the directory and reload the plugin.");
    }

    @Test
    void getLogsErrorMessageWhenSubfolderDoesNotContainAnyFiles() {
        File itemsFolder = new File("src/test/resources/weapon_creator_provider/items_empty_subfolders");

        File itemsSubfolder = new File("src/test/resources/weapon_creator_provider/items_empty_subfolders/submachine_guns");
        itemsSubfolder.mkdirs();

        WeaponCreatorProvider provider = new WeaponCreatorProvider(equipmentFactoryProvider, gunFactoryProvider, meleeWeaponFactoryProvider, specDeserializer, itemsFolder, logger);
        provider.get();

        verify(logger).warning("Unable to load item configuration files in items subfolder 'src/test/resources/weapon_creator_provider/items_empty_subfolders/submachine_guns'");
    }

    @Test
    void getLogsErrorMessageWhenItemFileFailsToLoad() {
        File itemsFolder = new File(tempDirectory.getPath() + "/items");

        MockedStatic<YamlConfiguration> yamlConfiguration = mockStatic(YamlConfiguration.class);
        yamlConfiguration.when(() -> YamlConfiguration.loadConfiguration(any(File.class))).thenThrow(new IllegalArgumentException("An error occurred"));

        WeaponCreatorProvider provider = new WeaponCreatorProvider(equipmentFactoryProvider, gunFactoryProvider, meleeWeaponFactoryProvider, specDeserializer, itemsFolder, logger);
        provider.get();

        verify(logger).severe("Unable to load item configuration file 'olympia.yml': An error occurred");
        verify(logger).severe("Unable to load item configuration file 'mp5.yml': An error occurred");

        yamlConfiguration.close();
    }

    @Test
    void getLogsErrorMessageWhenItemFileDoesNotContainIdValue() {
        File itemsFolder = new File("src/test/resources/weapon_creator_provider/items_without_name");

        WeaponCreatorProvider provider = new WeaponCreatorProvider(equipmentFactoryProvider, gunFactoryProvider, meleeWeaponFactoryProvider, specDeserializer, itemsFolder, logger);
        WeaponCreator weaponCreator = provider.get();

        assertThat(weaponCreator.exists("Olympia")).isFalse();
        assertThat(weaponCreator.exists("MP5")).isFalse();

        verify(logger).severe("An error occurred while loading file 'olympia.yml': Identifier 'name' is missing");
        verify(logger).severe("An error occurred while loading file 'mp5.yml': Identifier 'name' is missing");
    }

    @Test
    void getLogsErrorMessageWhenItemFileContainsErrorInItsSpecification() {
        File itemsFolder = new File("src/test/resources/weapon_creator_provider/items_invalid");

        WeaponCreatorProvider provider = new WeaponCreatorProvider(equipmentFactoryProvider, gunFactoryProvider, meleeWeaponFactoryProvider, specDeserializer, itemsFolder, logger);
        WeaponCreator weaponCreator = provider.get();

        assertThat(weaponCreator.exists("Olympia")).isFalse();
        assertThat(weaponCreator.exists("MP5")).isFalse();

        verify(logger).severe("An error occurred while loading item 'Olympia': Field 'levelUnlocked' is required but no value is provided");
        verify(logger).severe("An error occurred while loading item 'MP5': Field 'levelUnlocked' is required but no value is provided");
    }

    @Test
    void getLogsErrorMessagesWhenItemFileContainsNoItemTypeSpecification() {
        File itemsFolder = new File("src/test/resources/weapon_creator_provider/items_without_type");

        WeaponCreatorProvider provider = new WeaponCreatorProvider(equipmentFactoryProvider, gunFactoryProvider, meleeWeaponFactoryProvider, specDeserializer, itemsFolder, logger);
        WeaponCreator weaponCreator = provider.get();

        assertThat(weaponCreator.exists("MP5")).isFalse();

        verify(logger).severe("An error occurred while loading item 'MP5': no item type is specified");
    }

    @Test
    void getCopiesResourcesFilesIfItemsDirectoryDoesNotYetExist() {
        File itemsFolder = new File(tempDirectory.getPath() + "/items");

        WeaponCreatorProvider provider = new WeaponCreatorProvider(equipmentFactoryProvider, gunFactoryProvider, meleeWeaponFactoryProvider, specDeserializer, itemsFolder, logger);
        WeaponCreator weaponCreator = provider.get();

        File createdItemFile = new File(itemsFolder + "/submachine_guns/mp5.yml");

        assertThat(weaponCreator).isNotNull();
        assertThat(itemsFolder.exists()).isTrue();
        assertThat(createdItemFile.exists()).isTrue();
    }

    @Test
    void getLoadsItemsFilesAndCreatesItemSpecifications() {
        File itemsFolder = new File("src/main/resources/items");

        WeaponCreatorProvider provider = new WeaponCreatorProvider(equipmentFactoryProvider, gunFactoryProvider, meleeWeaponFactoryProvider, specDeserializer, itemsFolder, logger);
        WeaponCreator weaponCreator = provider.get();

        assertThat(weaponCreator.exists("Olympia")).isTrue();
        assertThat(weaponCreator.exists("MP5")).isTrue();
    }

    @Test
    void getLoadsItemsFilesWithoutSubfoldersAndCreatesItemSpecifications() {
        File itemsFolder = new File("src/main/resources/items/submachine_guns");

        WeaponCreatorProvider provider = new WeaponCreatorProvider(equipmentFactoryProvider, gunFactoryProvider, meleeWeaponFactoryProvider, specDeserializer, itemsFolder, logger);
        WeaponCreator weaponCreator = provider.get();

        assertThat(weaponCreator.exists("MP5")).isTrue();
    }
}
