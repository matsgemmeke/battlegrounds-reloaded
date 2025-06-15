package nl.matsgemmeke.battlegrounds.game.component.info.gun;

import nl.matsgemmeke.battlegrounds.game.ItemContainer;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.item.gun.Gun;
import nl.matsgemmeke.battlegrounds.item.gun.GunHolder;
import nl.matsgemmeke.battlegrounds.item.shoot.firemode.FireMode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DefaultGunInfoProviderTest {

    private ItemContainer<Gun, GunHolder> gunContainer;

    @BeforeEach
    public void setUp() {
        gunContainer = new ItemContainer<>();
    }

    @Test
    public void getGunFireSimulationInfoReturnsNullIfGivenHolderDoesNotHaveGuns() {
        GunHolder holder = mock(GunHolder.class);

        DefaultGunInfoProvider gunInfoProvider = new DefaultGunInfoProvider(gunContainer);
        GunFireSimulationInfo gunFireSimulationInfo = gunInfoProvider.getGunFireSimulationInfo(holder);

        assertNull(gunFireSimulationInfo);
    }

    @Test
    public void getGunFireSimulationInfoReturnsNewObjectBasedOnFirstHeldGun() {
        List<GameSound> shotSounds = Collections.emptyList();
        int rateOfFire = 600;

        FireMode fireMode = mock(FireMode.class);
        when(fireMode.getRateOfFire()).thenReturn(rateOfFire);

        Gun gun = mock(Gun.class);
        when(gun.getFireMode()).thenReturn(fireMode);
        when(gun.getShotSounds()).thenReturn(shotSounds);

        GunHolder holder = mock(GunHolder.class);

        gunContainer.addAssignedItem(gun, holder);

        DefaultGunInfoProvider gunInfoProvider = new DefaultGunInfoProvider(gunContainer);
        GunFireSimulationInfo gunFireSimulationInfo = gunInfoProvider.getGunFireSimulationInfo(holder);

        assertNotNull(gunFireSimulationInfo);
        assertEquals(shotSounds, gunFireSimulationInfo.shotSounds());
        assertEquals(rateOfFire, gunFireSimulationInfo.rateOfFire());
    }
}
