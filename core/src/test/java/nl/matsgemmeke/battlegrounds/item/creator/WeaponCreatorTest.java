package nl.matsgemmeke.battlegrounds.item.creator;

import nl.matsgemmeke.battlegrounds.configuration.ItemConfiguration;
import nl.matsgemmeke.battlegrounds.item.WeaponFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class WeaponCreatorTest {

    @Test
    public void shouldBeAbleToAddConfigurationAndFactory() {
        ItemConfiguration configuration = mock(ItemConfiguration.class);
        WeaponFactory factory = mock(WeaponFactory.class);

        WeaponCreator weaponCreator = new WeaponCreator();
        boolean result = weaponCreator.addConfigurationFactory(configuration, factory);

        assertTrue(result);
    }

    @Test
    public void shouldReturnThatItemConfigurationHasWeaponId() {
        String weaponId = "TEST_WEAPON";

        ItemConfiguration configuration = mock(ItemConfiguration.class);
        when(configuration.getItemId()).thenReturn(weaponId);

        WeaponFactory factory = mock(WeaponFactory.class);

        WeaponCreator weaponCreator = new WeaponCreator();
        weaponCreator.addConfigurationFactory(configuration, factory);

        boolean result = weaponCreator.exists(weaponId);

        assertTrue(result);
    }

    @Test
    public void shouldReturnThatItemConfigurationDoesNotHaveWeaponId() {
        String weaponId = "TEST_WEAPON";

        ItemConfiguration configuration = mock(ItemConfiguration.class);
        WeaponFactory factory = mock(WeaponFactory.class);

        WeaponCreator weaponCreator = new WeaponCreator();
        weaponCreator.addConfigurationFactory(configuration, factory);

        boolean result = weaponCreator.exists(weaponId);

        assertFalse(result);
    }

    @Test
    public void shouldReturnFactoryWhichCorrespondsToGivenItemConfiguration() {
        ItemConfiguration configuration = mock(ItemConfiguration.class);
        WeaponFactory factory = mock(WeaponFactory.class);

        WeaponCreator weaponCreator = new WeaponCreator();
        weaponCreator.addConfigurationFactory(configuration, factory);

        WeaponFactory result = weaponCreator.getFactory(configuration);

        assertEquals(factory, result);
    }

    @Test
    public void shouldReturnNullIfNoFactoryMatchesWithGivenWeaponId() {
        ItemConfiguration configuration = mock(ItemConfiguration.class);
        WeaponFactory factory = mock(WeaponFactory.class);

        WeaponCreator weaponCreator = new WeaponCreator();
        weaponCreator.addConfigurationFactory(mock(ItemConfiguration.class), factory);

        WeaponFactory result = weaponCreator.getFactory(configuration);

        assertNull(result);
    }
}
