package nl.matsgemmeke.battlegrounds.item;

import nl.matsgemmeke.battlegrounds.configuration.ItemConfiguration;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class WeaponProviderTest {

    @Test
    public void shouldBeAbleToAddConfigurationAndFactory() {
        ItemConfiguration configuration = mock(ItemConfiguration.class);
        WeaponFactory factory = mock(WeaponFactory.class);

        WeaponProvider provider = new WeaponProvider();
        boolean result = provider.addConfigurationFactory(configuration, factory);

        assertTrue(result);
    }

    @Test
    public void shouldReturnThatItemConfigurationHasWeaponId() {
        String weaponId = "TEST_WEAPON";

        ItemConfiguration configuration = mock(ItemConfiguration.class);
        when(configuration.getItemId()).thenReturn(weaponId);

        WeaponFactory factory = mock(WeaponFactory.class);

        WeaponProvider provider = new WeaponProvider();
        provider.addConfigurationFactory(configuration, factory);

        boolean result = provider.exists(weaponId);

        assertTrue(result);
    }

    @Test
    public void shouldReturnThatItemConfigurationDoesNotHaveWeaponId() {
        String weaponId = "TEST_WEAPON";

        ItemConfiguration configuration = mock(ItemConfiguration.class);
        WeaponFactory factory = mock(WeaponFactory.class);

        WeaponProvider provider = new WeaponProvider();
        provider.addConfigurationFactory(configuration, factory);

        boolean result = provider.exists(weaponId);

        assertFalse(result);
    }

    @Test
    public void shouldReturnFactoryWhichCorrespondsToGivenItemConfiguration() {
        ItemConfiguration configuration = mock(ItemConfiguration.class);
        WeaponFactory factory = mock(WeaponFactory.class);

        WeaponProvider provider = new WeaponProvider();
        provider.addConfigurationFactory(configuration, factory);

        WeaponFactory result = provider.getFactory(configuration);

        assertEquals(factory, result);
    }

    @Test
    public void shouldReturnNullIfNoFactoryMatchesWithGivenWeaponId() {
        ItemConfiguration configuration = mock(ItemConfiguration.class);
        WeaponFactory factory = mock(WeaponFactory.class);

        WeaponProvider provider = new WeaponProvider();
        provider.addConfigurationFactory(mock(ItemConfiguration.class), factory);

        WeaponFactory result = provider.getFactory(configuration);

        assertNull(result);
    }
}
