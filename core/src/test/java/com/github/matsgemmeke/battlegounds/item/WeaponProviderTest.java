package com.github.matsgemmeke.battlegounds.item;

import com.github.matsgemmeke.battlegrounds.api.item.Firearm;
import com.github.matsgemmeke.battlegrounds.configuration.ItemConfiguration;
import com.github.matsgemmeke.battlegrounds.item.WeaponProvider;
import com.github.matsgemmeke.battlegrounds.item.factory.WeaponFactory;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.HashSet;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class WeaponProviderTest {

    private ItemConfiguration configuration;
    private WeaponFactory<Firearm> weaponFactory;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        this.configuration = mock(ItemConfiguration.class);
        this.weaponFactory = (WeaponFactory<Firearm>) mock(WeaponFactory.class);
    }

    @Test
    public void canAddWeaponFactory() {
        WeaponProvider weaponProvider = new WeaponProvider();
        boolean result = weaponProvider.addWeaponFactory(configuration, weaponFactory);

        assertTrue(result);
    }

    @Test
    public void returnsTrueWhenFindingExistingWeaponId() {
        String weaponId = "real weapon";

        WeaponProvider weaponProvider = new WeaponProvider();
        weaponProvider.addWeaponFactory(configuration, weaponFactory);

        when(configuration.getIdList()).thenReturn(new HashSet<>(Collections.singletonList(weaponId)));

        boolean result = weaponProvider.exists(weaponId);

        assertTrue(result);
    }

    @Test
    public void returnsFalseWhenFindingUnknownWeaponId() {
        String weaponId = "unknown";

        WeaponProvider weaponProvider = new WeaponProvider();
        weaponProvider.addWeaponFactory(configuration, weaponFactory);

        boolean result = weaponProvider.exists(weaponId);

        assertFalse(result);
    }

    @Test
    public void returnsInstanceWhenFindingWeaponFactoryForExistingWeaponId() {
        String weaponId = "real weapon";

        WeaponProvider weaponProvider = new WeaponProvider();
        weaponProvider.addWeaponFactory(configuration, weaponFactory);

        when(configuration.getIdList()).thenReturn(new HashSet<>(Collections.singletonList(weaponId)));

        WeaponFactory<?> result = weaponProvider.getWeaponFactory(weaponId);

        assertEquals(weaponFactory, result);
    }

    @Test
    public void returnsNullWhenFindingWeaponFactoryForUnknownWeaponId() {
        String weaponId = "unknown";

        WeaponProvider weaponProvider = new WeaponProvider();
        weaponProvider.addWeaponFactory(configuration, weaponFactory);

        WeaponFactory<?> result = weaponProvider.getWeaponFactory(weaponId);

        assertNull(result);
    }
}
