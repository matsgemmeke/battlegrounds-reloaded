package nl.matsgemmeke.battlegrounds.item.creator;

import nl.matsgemmeke.battlegrounds.configuration.ItemConfiguration;
import nl.matsgemmeke.battlegrounds.configuration.item.spec.FireModeSpecification;
import nl.matsgemmeke.battlegrounds.configuration.item.spec.GunSpecification;
import nl.matsgemmeke.battlegrounds.item.WeaponFactory;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

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
    public void existsReturnsTrueWhenSpecificationOfGivenWeaponIdExists() {
        String gunId = "TEST_GUN";

        FireModeSpecification fireModeSpecification = new FireModeSpecification("test", Collections.emptyMap());
        GunSpecification gunSpecification = new GunSpecification("test", null, 1, 1, 1, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, null, fireModeSpecification);

        WeaponCreator weaponCreator = new WeaponCreator();
        weaponCreator.addGunSpecification(gunId, gunSpecification);
        boolean exists = weaponCreator.exists(gunId);

        assertThat(exists).isTrue();
    }

    @Test
    public void existsReturnsFalseWhenSpecificationOfGivenWeaponIdDoesNotExist() {
        String gunId = "TEST_GUN";

        WeaponCreator weaponCreator = new WeaponCreator();
        boolean exists = weaponCreator.exists(gunId);

        assertThat(exists).isFalse();
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
