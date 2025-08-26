package nl.matsgemmeke.battlegrounds.game.component.item;

import nl.matsgemmeke.battlegrounds.item.gun.Gun;
import nl.matsgemmeke.battlegrounds.item.gun.GunHolder;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.*;

public class DefaultGunRegistryTest {

    @Test
    public void assignDoesNothingWhenGivenGunIsNotRegistered() {
        GunHolder holder = mock(GunHolder.class);

        Gun gun = mock(Gun.class);
        when(gun.getHolder()).thenReturn(holder);

        DefaultGunRegistry gunRegistry = new DefaultGunRegistry();
        gunRegistry.assign(gun, holder);

        assertThat(gunRegistry.getAssignedGuns(holder)).isEmpty();
    }

    @Test
    public void assignAddsGivenGunToAssignedListOfGivenHolder() {
        GunHolder holder = mock(GunHolder.class);

        Gun gun = mock(Gun.class);
        when(gun.getHolder()).thenReturn(holder);

        DefaultGunRegistry gunRegistry = new DefaultGunRegistry();
        gunRegistry.register(gun);
        gunRegistry.assign(gun, holder);

        assertThat(gunRegistry.getAssignedGuns(holder)).containsExactly(gun);
    }


    @Test
    public void unassignDoesNothingWhenGivenGunHasNoHolder() {
        Gun gun = mock(Gun.class);
        when(gun.getHolder()).thenReturn(null);

        DefaultGunRegistry gunRegistry = new DefaultGunRegistry();

        // Add better assertions once more methods are exposed
        assertThatCode(() -> gunRegistry.unassign(gun)).doesNotThrowAnyException();
    }

    @Test
    public void unassignDoesNothingWhenGivenGunHolderIsNotRegistered() {
        GunHolder holder = mock(GunHolder.class);

        Gun gun = mock(Gun.class);
        when(gun.getHolder()).thenReturn(holder);

        DefaultGunRegistry gunRegistry = new DefaultGunRegistry();
        gunRegistry.unassign(gun);

        assertThat(gunRegistry.getAssignedGuns(holder)).isEmpty();
    }

    @Test
    public void unassignRemovesGivenGunFromGivenHolder() {
        GunHolder holder = mock(GunHolder.class);

        Gun gun = mock(Gun.class);
        when(gun.getHolder()).thenReturn(holder);

        DefaultGunRegistry gunRegistry = new DefaultGunRegistry();
        gunRegistry.register(gun, holder);
        gunRegistry.unassign(gun);

        assertThat(gunRegistry.getAssignedGuns(holder)).isEmpty();
    }

    @Test
    public void getAssignedGunsReturnsAssignedItemsForGivenHolder() {
        Gun gun = mock(Gun.class);
        GunHolder holder = mock(GunHolder.class);

        DefaultGunRegistry gunRegistry = new DefaultGunRegistry();
        gunRegistry.register(gun, holder);
        List<Gun> assignedItems = gunRegistry.getAssignedGuns(holder);

        assertThat(assignedItems).containsExactly(gun);
    }
}
