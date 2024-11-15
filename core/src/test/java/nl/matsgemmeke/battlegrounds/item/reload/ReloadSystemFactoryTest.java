package nl.matsgemmeke.battlegrounds.item.reload;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.item.gun.Gun;
import nl.matsgemmeke.battlegrounds.item.WeaponFactoryCreationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ReloadSystemFactoryTest {

    private AudioEmitter audioEmitter;
    private Gun gun;
    private Section section;
    private TaskRunner taskRunner;

    @BeforeEach
    public void setUp() {
        this.audioEmitter = mock(AudioEmitter.class);
        this.gun = mock(Gun.class);
        this.section = mock(Section.class);
        this.taskRunner = mock(TaskRunner.class);
    }

    @Test
    public void makeReloadSystemInstanceForMagazineReload() {
        when(section.getString("sound")).thenReturn("ENTITY_BLAZE_HURT-3-2-0");
        when(section.getString("type")).thenReturn("MAGAZINE");

        ReloadSystemFactory factory = new ReloadSystemFactory(taskRunner);
        ReloadSystem reloadSystem = factory.make(gun, section, audioEmitter);

        assertNotNull(reloadSystem);
    }

    @Test
    public void makeReloadSystemInstanceForManualReload() {
        when(section.getString("sound")).thenReturn("ENTITY_BLAZE_HURT-3-2-0");
        when(section.getString("type")).thenReturn("MANUAL_INSERTION");

        ReloadSystemFactory factory = new ReloadSystemFactory(taskRunner);
        ReloadSystem reloadSystem = factory.make(gun, section, audioEmitter);

        assertNotNull(reloadSystem);
    }

    @Test
    public void throwErrorWhenUnknownFireModeType() {
        when(section.getString("type")).thenReturn("error");

        ReloadSystemFactory factory = new ReloadSystemFactory(taskRunner);

        assertThrows(WeaponFactoryCreationException.class, () -> factory.make(gun, section, audioEmitter));
    }
}
