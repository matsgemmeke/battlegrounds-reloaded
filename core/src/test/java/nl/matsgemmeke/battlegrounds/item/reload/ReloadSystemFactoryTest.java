package nl.matsgemmeke.battlegrounds.item.reload;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.item.gun.Gun;
import nl.matsgemmeke.battlegrounds.item.WeaponFactoryCreationException;
import nl.matsgemmeke.battlegrounds.item.reload.magazine.MagazineReloadSystem;
import nl.matsgemmeke.battlegrounds.item.reload.magazine.MagazineReloadSystemFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class ReloadSystemFactoryTest {

    private AudioEmitter audioEmitter;
    private Gun gun;
    private MagazineReloadSystemFactory magazineReloadSystemFactory;
    private Section section;
    private TaskRunner taskRunner;

    @BeforeEach
    public void setUp() {
        audioEmitter = mock(AudioEmitter.class);
        gun = mock(Gun.class);
        magazineReloadSystemFactory = mock(MagazineReloadSystemFactory.class);
        section = mock(Section.class);
        taskRunner = mock(TaskRunner.class);
    }

    @Test
    public void createMakesReloadSystemInstanceForMagazineReload() {
        int duration = 50;

        when(section.getInt("duration")).thenReturn(duration);
        when(section.getString("sound")).thenReturn("ENTITY_BLAZE_HURT-3-2-0");
        when(section.getString("type")).thenReturn("MAGAZINE");

        MagazineReloadSystem reloadSystem = mock(MagazineReloadSystem.class);
        when(magazineReloadSystemFactory.create(any(ReloadProperties.class), eq(gun), eq(audioEmitter))).thenReturn(reloadSystem);

        ReloadSystemFactory factory = new ReloadSystemFactory(magazineReloadSystemFactory, taskRunner);
        ReloadSystem result = factory.create(gun, section, audioEmitter);

        ArgumentCaptor<ReloadProperties> propertiesCaptor = ArgumentCaptor.forClass(ReloadProperties.class);
        verify(magazineReloadSystemFactory).create(propertiesCaptor.capture(), eq(gun), eq(audioEmitter));

        ReloadProperties properties = propertiesCaptor.getValue();
        assertEquals(duration, properties.duration());

        assertInstanceOf(MagazineReloadSystem.class, result);
    }

    @Test
    public void createMakesReloadSystemInstanceForManualReload() {
        when(section.getString("sound")).thenReturn("ENTITY_BLAZE_HURT-3-2-0");
        when(section.getString("type")).thenReturn("MANUAL_INSERTION");

        ReloadSystemFactory factory = new ReloadSystemFactory(magazineReloadSystemFactory, taskRunner);
        ReloadSystem reloadSystem = factory.create(gun, section, audioEmitter);

        assertNotNull(reloadSystem);
    }

    @Test
    public void throwErrorWhenUnknownFireModeType() {
        when(section.getString("type")).thenReturn("error");

        ReloadSystemFactory factory = new ReloadSystemFactory(magazineReloadSystemFactory, taskRunner);

        assertThrows(WeaponFactoryCreationException.class, () -> factory.create(gun, section, audioEmitter));
    }
}
