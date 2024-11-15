package nl.matsgemmeke.battlegrounds.item;

import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentFactory;
import nl.matsgemmeke.battlegrounds.item.gun.FirearmFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class WeaponProviderLoaderTest {

    private EquipmentFactory equipmentFactory;
    @TempDir
    private File dataFolder;
    private FirearmFactory firearmFactory;

    @BeforeEach
    public void setUp() {
        equipmentFactory = mock(EquipmentFactory.class);
        firearmFactory = mock(FirearmFactory.class);
    }

    @AfterEach
    public void tearDown() {
        // Activate garbage collector to release file lock
        System.gc();
    }

    @Test
    public void shouldCopyResourcesFilesIfItemsDirectoryDoesNotYetExist() throws IOException {
        File itemsDirectory = new File(dataFolder.getPath() + "/items");

        WeaponProviderLoader loader = new WeaponProviderLoader(equipmentFactory, firearmFactory);
        loader.loadWeaponProvider(itemsDirectory);

        File createdItemFile = new File(itemsDirectory + "/submachine_guns/mp5.yml");

        assertTrue(itemsDirectory.exists());
        assertTrue(createdItemFile.exists());
    }

    @Test
    public void shouldThrowErrorWhenResourceLocationIsInvalidURI() throws URISyntaxException {
        File itemsDirectory = new File(dataFolder.getPath() + "/items");

        WeaponProviderLoader loader = spy(new WeaponProviderLoader(equipmentFactory, firearmFactory));
        when(loader.createResourceURI()).thenThrow(new URISyntaxException("fail", "test"));

        assertThrows(IllegalStateException.class, () -> loader.loadWeaponProvider(itemsDirectory));
    }

    @Test
    public void shouldLoadItemsFilesIntoItemConfigurations() throws IOException {
        File itemsDirectory = new File("src/main/resources/items");

        WeaponProviderLoader loader = new WeaponProviderLoader(equipmentFactory, firearmFactory);
        WeaponProvider provider = loader.loadWeaponProvider(itemsDirectory);

        assertNotNull(provider.getItemConfiguration("OLYMPIA"));
        assertNotNull(provider.getItemConfiguration("MP5"));
    }
}
