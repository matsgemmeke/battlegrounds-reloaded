package nl.matsgemmeke.battlegrounds.item.creator;

import dev.dejvokep.boostedyaml.YamlDocument;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentFactory;
import nl.matsgemmeke.battlegrounds.item.gun.FirearmFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockedStatic;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.logging.Logger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class WeaponCreatorProviderTest {

    private EquipmentFactory equipmentFactory;
    @TempDir
    private File tempDirectory;
    private FirearmFactory firearmFactory;
    private Logger logger;

    @BeforeEach
    public void setUp() {
        equipmentFactory = mock(EquipmentFactory.class);
        firearmFactory = mock(FirearmFactory.class);
        logger = mock(Logger.class);
    }

    @AfterEach
    public void tearDown() {
        // Activate garbage collector to release file lock
        System.gc();
    }

    @Test
    public void getThrowsIllegalStateExceptionWhenResourceLocationIsInvalidURI() throws URISyntaxException {
        File itemsFolder = new File(tempDirectory.getPath() + "/items");

        WeaponCreatorProvider provider = spy(new WeaponCreatorProvider(equipmentFactory, firearmFactory, itemsFolder, logger));
        when(provider.createResourceURI()).thenThrow(new URISyntaxException("fail", "test"));

        assertThrows(IllegalStateException.class, provider::get);
    }

    @Test
    public void getLogsWarningsMessageWhenDirectoryDoesNotContainAnyFiles() {
        File itemsFolder = new File("src/test/resources/weapon_creator_provider/items_empty_directory");
        itemsFolder.mkdirs();

        WeaponCreatorProvider provider = new WeaponCreatorProvider(equipmentFactory, firearmFactory, itemsFolder, logger);
        provider.get();

        verify(logger).warning("Unable to load item configuration files: directory 'src/test/resources/weapon_creator_provider/items_empty_directory' is empty. To generate the default item files, delete the directory and reload the plugin.");
    }

    @Test
    public void getLogsErrorMessageWhenSubfolderDoesNotContainAnyFiles() {
        File itemsFolder = new File("src/test/resources/weapon_creator_provider/items_empty_subfolders");

        File itemsSubfolder = new File("src/test/resources/weapon_creator_provider/items_empty_subfolders/submachine_guns");
        itemsSubfolder.mkdirs();

        WeaponCreatorProvider provider = new WeaponCreatorProvider(equipmentFactory, firearmFactory, itemsFolder, logger);
        provider.get();

        verify(logger).warning("Unable to load item configuration files in items subfolder 'src/test/resources/weapon_creator_provider/items_empty_subfolders/submachine_guns'");
    }

    @Test
    public void getLogsErrorMessageWhenItemFileFailsToLoad() {
        File itemsFolder = new File(tempDirectory.getPath() + "/items");

        MockedStatic<YamlDocument> yamlDocument = mockStatic(YamlDocument.class);
        yamlDocument.when(() -> YamlDocument.create(any(File.class))).thenThrow(new IOException());

        WeaponCreatorProvider provider = new WeaponCreatorProvider(equipmentFactory, firearmFactory, itemsFolder, logger);
        provider.get();

        verify(logger).severe("Unable to load item configuration file olympia.yml");
        verify(logger).severe("Unable to load item configuration file mp5.yml");

        yamlDocument.close();
    }

    @Test
    public void getLogsErrorMessageWhenItemFileDoesNotContainIdValue() {
        File itemsFolder = new File("src/test/resources/weapon_creator_provider/items_without_id");

        WeaponCreatorProvider provider = new WeaponCreatorProvider(equipmentFactory, firearmFactory, itemsFolder, logger);
        WeaponCreator weaponCreator = provider.get();

        assertThat(weaponCreator.exists("OLYMPIA")).isFalse();
        assertThat(weaponCreator.exists("MP5")).isFalse();

        verify(logger).severe("Cannot read item configuration file olympia.yml: Missing required 'id' value");
        verify(logger).severe("Cannot read item configuration file mp5.yml: Missing required 'id' value");
    }

    @Test
    public void getLogsErrorMessageWhenItemFileContainsErrorInItsSpecification() {
        File itemsFolder = new File("src/test/resources/weapon_creator_provider/items_invalid");

        WeaponCreatorProvider provider = new WeaponCreatorProvider(equipmentFactory, firearmFactory, itemsFolder, logger);
        WeaponCreator weaponCreator = provider.get();

        assertThat(weaponCreator.exists("OLYMPIA")).isFalse();
        assertThat(weaponCreator.exists("MP5")).isFalse();

        verify(logger).severe("Gun configuration error for OLYMPIA: Missing required 'name' value");
        verify(logger).severe("Gun configuration error for MP5: Missing required 'name' value");
    }

    @Test
    public void getCopiesResourcesFilesIfItemsDirectoryDoesNotYetExist() {
        File itemsFolder = new File(tempDirectory.getPath() + "/items");

        WeaponCreatorProvider provider = new WeaponCreatorProvider(equipmentFactory, firearmFactory, itemsFolder, logger);
        WeaponCreator weaponCreator = provider.get();

        File createdItemFile = new File(itemsFolder + "/submachine_guns/mp5.yml");

        assertThat(weaponCreator).isNotNull();
        assertThat(itemsFolder.exists()).isTrue();
        assertThat(createdItemFile.exists()).isTrue();
    }

    @Test
    public void getLoadsItemsFilesAndCreatesItemSpecifications() {
        File itemsFolder = new File("src/main/resources/items");

        WeaponCreatorProvider provider = new WeaponCreatorProvider(equipmentFactory, firearmFactory, itemsFolder, logger);
        WeaponCreator weaponCreator = provider.get();

        assertThat(weaponCreator.exists("OLYMPIA")).isTrue();
        assertThat(weaponCreator.exists("MP5")).isTrue();
    }

    @Test
    public void getLoadsItemsFilesWithoutSubfoldersAndCreatesItemSpecifications() {
        File itemsFolder = new File("src/main/resources/items/submachine_guns");

        WeaponCreatorProvider provider = new WeaponCreatorProvider(equipmentFactory, firearmFactory, itemsFolder, logger);
        WeaponCreator weaponCreator = provider.get();

        assertThat(weaponCreator.exists("MP5")).isTrue();
    }
}
