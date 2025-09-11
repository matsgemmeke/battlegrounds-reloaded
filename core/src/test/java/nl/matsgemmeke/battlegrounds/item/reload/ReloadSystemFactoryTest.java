package nl.matsgemmeke.battlegrounds.item.reload;

import nl.matsgemmeke.battlegrounds.configuration.item.gun.ReloadingSpec;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.item.gun.Gun;
import nl.matsgemmeke.battlegrounds.item.reload.magazine.MagazineReloadSystem;
import nl.matsgemmeke.battlegrounds.item.reload.magazine.MagazineReloadSystemFactory;
import nl.matsgemmeke.battlegrounds.item.reload.manual.ManualInsertionReloadSystem;
import nl.matsgemmeke.battlegrounds.item.reload.manual.ManualInsertionReloadSystemFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class ReloadSystemFactoryTest {

    private AmmunitionStorage ammunitionStorage;
    private AudioEmitter audioEmitter;
    private Gun gun;
    private MagazineReloadSystemFactory magazineReloadSystemFactory;
    private ManualInsertionReloadSystemFactory manualInsertionReloadSystemFactory;

    @BeforeEach
    public void setUp() {
        ammunitionStorage = new AmmunitionStorage(30, 30, 90, 300);
        audioEmitter = mock(AudioEmitter.class);
        magazineReloadSystemFactory = mock(MagazineReloadSystemFactory.class);
        manualInsertionReloadSystemFactory = mock(ManualInsertionReloadSystemFactory.class);

        gun = mock(Gun.class);
        when(gun.getAmmunitionStorage()).thenReturn(ammunitionStorage);
    }

    @Test
    public void createMakesReloadSystemInstanceForMagazineReload() {
        ReloadingSpec spec = new ReloadingSpec();
        spec.type = "MAGAZINE";
        spec.duration = 50L;

        MagazineReloadSystem reloadSystem = mock(MagazineReloadSystem.class);
        when(magazineReloadSystemFactory.create(any(ReloadProperties.class), eq(ammunitionStorage))).thenReturn(reloadSystem);

        ReloadSystemFactory factory = new ReloadSystemFactory(magazineReloadSystemFactory, manualInsertionReloadSystemFactory);
        ReloadSystem result = factory.create(spec, gun, audioEmitter);

        ArgumentCaptor<ReloadProperties> propertiesCaptor = ArgumentCaptor.forClass(ReloadProperties.class);
        verify(magazineReloadSystemFactory).create(propertiesCaptor.capture(), eq(ammunitionStorage));

        ReloadProperties properties = propertiesCaptor.getValue();
        assertThat(properties.duration()).isEqualTo(50L);

        assertThat(result).isInstanceOf(MagazineReloadSystem.class);
    }

    @Test
    public void createMakesReloadSystemInstanceForManualInsertionReload() {
        ReloadingSpec spec = new ReloadingSpec();
        spec.type = "MANUAL_INSERTION";
        spec.duration = 50L;

        ManualInsertionReloadSystem reloadSystem = mock(ManualInsertionReloadSystem.class);
        when(manualInsertionReloadSystemFactory.create(any(ReloadProperties.class), eq(ammunitionStorage), eq(audioEmitter))).thenReturn(reloadSystem);

        ReloadSystemFactory factory = new ReloadSystemFactory(magazineReloadSystemFactory, manualInsertionReloadSystemFactory);
        ReloadSystem result = factory.create(spec, gun, audioEmitter);

        ArgumentCaptor<ReloadProperties> propertiesCaptor = ArgumentCaptor.forClass(ReloadProperties.class);
        verify(manualInsertionReloadSystemFactory).create(propertiesCaptor.capture(), eq(ammunitionStorage), eq(audioEmitter));

        ReloadProperties properties = propertiesCaptor.getValue();
        assertThat(properties.duration()).isEqualTo(50L);

        assertThat(result).isInstanceOf(ManualInsertionReloadSystem.class);
    }
}
