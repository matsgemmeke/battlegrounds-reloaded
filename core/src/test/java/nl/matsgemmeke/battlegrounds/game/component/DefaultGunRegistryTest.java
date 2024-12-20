package nl.matsgemmeke.battlegrounds.game.component;

import nl.matsgemmeke.battlegrounds.game.ItemStorage;
import nl.matsgemmeke.battlegrounds.item.gun.Gun;
import nl.matsgemmeke.battlegrounds.item.gun.GunHolder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class DefaultGunRegistryTest {

    private ItemStorage<Gun, GunHolder> gunStorage;

    @BeforeEach
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
