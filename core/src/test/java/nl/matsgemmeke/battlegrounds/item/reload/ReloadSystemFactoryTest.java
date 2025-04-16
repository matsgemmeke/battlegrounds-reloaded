package nl.matsgemmeke.battlegrounds.item.reload;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.item.gun.Gun;
import nl.matsgemmeke.battlegrounds.item.WeaponFactoryCreationException;
import nl.matsgemmeke.battlegrounds.item.reload.magazine.MagazineReloadSystem;
import nl.matsgemmeke.battlegrounds.item.reload.magazine.MagazineReloadSystemFactory;
import nl.matsgemmeke.battlegrounds.item.reload.manual.ManualInsertionReloadSystem;
import nl.matsgemmeke.battlegrounds.item.reload.manual.ManualInsertionReloadSystemFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class ReloadSystemFactoryTest {

    private AmmunitionStorage ammunitionStorage;
    private AudioEmitter audioEmitter;
    private Gun gun;
    private MagazineReloadSystemFactory magazineReloadSystemFactory;
    private ManualInsertionReloadSystemFactory manualInsertionReloadSystemFactory;
    private Section section;

    @BeforeEach
    public void setUp() {
        ammunitionStorage = new AmmunitionStorage(30, 30, 90, 300);
        audioEmitter = mock(AudioEmitter.class);
        magazineReloadSystemFactory = mock(MagazineReloadSystemFactory.class);
        manualInsertionReloadSystemFactory = mock(ManualInsertionReloadSystemFactory.class);
        section = mock(Section.class);

        gun = mock(Gun.class);
        when(gun.getAmmunitionStorage()).thenReturn(ammunitionStorage);
    }

    @Test
    public void createMakesReloadSystemInstanceForMagazineReload() {
        int duration = 50;

        when(section.getInt("duration")).thenReturn(duration);
        when(section.getString("sounds")).thenReturn("ENTITY_BLAZE_HURT-3-2-0");
        when(section.getString("type")).thenReturn("MAGAZINE");

        MagazineReloadSystem reloadSystem = mock(MagazineReloadSystem.class);
        when(magazineReloadSystemFactory.create(any(ReloadProperties.class), eq(ammunitionStorage), eq(audioEmitter))).thenReturn(reloadSystem);

        ReloadSystemFactory factory = new ReloadSystemFactory(magazineReloadSystemFactory, manualInsertionReloadSystemFactory);
        ReloadSystem result = factory.create(gun, section, audioEmitter);

        ArgumentCaptor<ReloadProperties> propertiesCaptor = ArgumentCaptor.forClass(ReloadProperties.class);
        verify(magazineReloadSystemFactory).create(propertiesCaptor.capture(), eq(ammunitionStorage), eq(audioEmitter));

        ReloadProperties properties = propertiesCaptor.getValue();
        assertEquals(duration, properties.duration());

        assertInstanceOf(MagazineReloadSystem.class, result);
    }

    @Test
    public void createMakesReloadSystemInstanceForManualReload() {
        int duration = 50;

        when(section.getInt("duration")).thenReturn(duration);
        when(section.getString("sounds")).thenReturn("ENTITY_BLAZE_HURT-3-2-0");
        when(section.getString("type")).thenReturn("MANUAL_INSERTION");

        ManualInsertionReloadSystem reloadSystem = mock(ManualInsertionReloadSystem.class);
        when(manualInsertionReloadSystemFactory.create(any(ReloadProperties.class), eq(ammunitionStorage), eq(audioEmitter))).thenReturn(reloadSystem);

        ReloadSystemFactory factory = new ReloadSystemFactory(magazineReloadSystemFactory, manualInsertionReloadSystemFactory);
        ReloadSystem result = factory.create(gun, section, audioEmitter);

        ArgumentCaptor<ReloadProperties> propertiesCaptor = ArgumentCaptor.forClass(ReloadProperties.class);
        verify(manualInsertionReloadSystemFactory).create(propertiesCaptor.capture(), eq(ammunitionStorage), eq(audioEmitter));

        ReloadProperties properties = propertiesCaptor.getValue();
        assertEquals(duration, properties.duration());

        assertInstanceOf(ManualInsertionReloadSystem.class, result);
    }

    @Test
    public void throwErrorWhenUnknownFireModeType() {
        when(section.getString("type")).thenReturn("error");

        ReloadSystemFactory factory = new ReloadSystemFactory(magazineReloadSystemFactory, manualInsertionReloadSystemFactory);

        assertThrows(WeaponFactoryCreationException.class, () -> factory.create(gun, section, audioEmitter));
    }
}
