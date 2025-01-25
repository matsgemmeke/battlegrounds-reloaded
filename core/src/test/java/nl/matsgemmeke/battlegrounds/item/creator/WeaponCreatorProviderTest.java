package nl.matsgemmeke.battlegrounds.item.creator;

import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentFactory;
import nl.matsgemmeke.battlegrounds.item.gun.FirearmFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class WeaponCreatorProviderTest {

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
    public void getCopiesResourcesFilesIfItemsDirectoryDoesNotYetExist() {
        File itemsFolder = new File(dataFolder.getPath() + "/items");

        WeaponCreatorProvider provider = new WeaponCreatorProvider(equipmentFactory, firearmFactory, itemsFolder);
        WeaponCreator weaponCreator = provider.get();

        File createdItemFile = new File(itemsFolder + "/submachine_guns/mp5.yml");

        assertNotNull(weaponCreator);
        assertTrue(itemsFolder.exists());
        assertTrue(createdItemFile.exists());
    }

    @Test
    public void getThrowsIllegalStateExceptionWhenResourceLocationIsInvalidURI() throws URISyntaxException {
        File itemsFolder = new File(dataFolder.getPath() + "/items");

        WeaponCreatorProvider provider = spy(new WeaponCreatorProvider(equipmentFactory, firearmFactory, itemsFolder));
        when(provider.createResourceURI()).thenThrow(new URISyntaxException("fail", "test"));

        assertThrows(IllegalStateException.class, provider::get);
    }

    @Test
    public void getLoadsItemsFilesIntoItemConfigurations() throws IOException {
        File itemsFolder = new File("src/main/resources/items");

        WeaponCreatorProvider provider = new WeaponCreatorProvider(equipmentFactory, firearmFactory, itemsFolder);
        WeaponCreator weaponCreator = provider.get();

        assertNotNull(weaponCreator.getItemConfiguration("OLYMPIA"));
        assertNotNull(weaponCreator.getItemConfiguration("MP5"));
    }
}
