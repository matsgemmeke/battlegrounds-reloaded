package com.github.matsgemmeke.battlegounds.item.factory;

import com.github.matsgemmeke.battlegrounds.TaskRunner;
import com.github.matsgemmeke.battlegrounds.api.item.Gun;
import com.github.matsgemmeke.battlegrounds.item.factory.ReloadSystemFactory;
import com.github.matsgemmeke.battlegrounds.item.factory.WeaponFactoryCreationException;
import com.github.matsgemmeke.battlegrounds.item.mechanics.ReloadSystem;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ReloadSystemFactoryTest {

    private Gun gun;
    private Section section;
    private TaskRunner taskRunner;

    @Before
    public void setUp() {
        this.gun = mock(Gun.class);
        this.section = mock(Section.class);
        this.taskRunner = mock(TaskRunner.class);
    }

    @Test
    public void makeReloadSystemInstanceForMagazineReload() {
        when(section.getString("sound")).thenReturn("ENTITY_BLAZE_HURT-3-2-0");
        when(section.getString("type")).thenReturn("MAGAZINE_RELOAD");

        ReloadSystemFactory factory = new ReloadSystemFactory(taskRunner);
        ReloadSystem reloadSystem = factory.make(gun, section);

        assertNotNull(reloadSystem);
    }

    @Test
    public void makeReloadSystemInstanceForManualReload() {
        when(section.getString("sound")).thenReturn("ENTITY_BLAZE_HURT-3-2-0");
        when(section.getString("type")).thenReturn("MANUAL_RELOAD");

        ReloadSystemFactory factory = new ReloadSystemFactory(taskRunner);
        ReloadSystem reloadSystem = factory.make(gun, section);

        assertNotNull(reloadSystem);
    }

    @Test(expected = WeaponFactoryCreationException.class)
    public void throwErrorWhenUnknownFiringModeType() {
        when(section.getString("type")).thenReturn("error");

        ReloadSystemFactory factory = new ReloadSystemFactory(taskRunner);
        factory.make(gun, section);
    }
}
