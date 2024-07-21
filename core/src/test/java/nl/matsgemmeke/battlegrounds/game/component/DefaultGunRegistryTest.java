package nl.matsgemmeke.battlegrounds.game.component;

import nl.matsgemmeke.battlegrounds.entity.GunHolder;
import nl.matsgemmeke.battlegrounds.item.ItemStorage;
import nl.matsgemmeke.battlegrounds.item.gun.Gun;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class DefaultGunRegistryTest {

    private ItemStorage<Gun, GunHolder> gunStorage;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        gunStorage = (ItemStorage<Gun, GunHolder>) mock(ItemStorage.class);
    }

    @Test
    public void shouldRegisterUnassignedItemToStorage() {
        Gun gun = mock(Gun.class);

        DefaultGunRegistry gunRegistry = new DefaultGunRegistry(gunStorage);
        gunRegistry.registerItem(gun);

        verify(gunStorage).addUnassignedItem(gun);
    }

    @Test
    public void shouldRegisterAssignedItemToStorage() {
        Gun gun = mock(Gun.class);
        GunHolder holder = mock(GunHolder.class);

        DefaultGunRegistry gunRegistry = new DefaultGunRegistry(gunStorage);
        gunRegistry.registerItem(gun, holder);

        verify(gunStorage).addAssignedItem(gun, holder);
    }
}
