package nl.matsgemmeke.battlegrounds.item;

import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentFactory;
import nl.matsgemmeke.battlegrounds.item.gun.FirearmFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class WeaponProviderLoaderTest {

    @Rule
    public TemporaryFolder dataFolder = new TemporaryFolder();

    private EquipmentFactory equipmentFactory;
    private FirearmFactory firearmFactory;

    @Before
    public void setUp() {
        equipmentFactory = mock(EquipmentFactory.class);
        firearmFactory = mock(FirearmFactory.class);
    }

    @After
    public void tearDown() {
        dataFolder.delete();
    }

    @Test
    public void shouldCopyResourcesFilesIfItemsDirectoryDoesNotYetExist() throws IOException {
        File itemsDirectory = new File(dataFolder.getRoot() + "/items");

        WeaponProviderLoader loader = new WeaponProviderLoader(equipmentFactory, firearmFactory);
        loader.loadWeaponProvider(itemsDirectory);

        File createdItemFile = new File(itemsDirectory + "/submachine_guns/mp5.yml");

        assertTrue(itemsDirectory.exists());
        assertTrue(createdItemFile.exists());
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowErrorWhenResourceLocationIsInvalidURI() throws IOException, URISyntaxException {
        File itemsDirectory = new File(dataFolder.getRoot() + "/items");

        WeaponProviderLoader loader = spy(new WeaponProviderLoader(equipmentFactory, firearmFactory));
        when(loader.createResourceURI()).thenThrow(new URISyntaxException("fail", "test"));

        loader.loadWeaponProvider(itemsDirectory);
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
