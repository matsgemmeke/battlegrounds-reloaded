package com.github.matsgemmeke.battlegounds.item.recoil;

import com.github.matsgemmeke.battlegrounds.InternalsProvider;
import com.github.matsgemmeke.battlegrounds.item.factory.WeaponFactoryCreationException;
import com.github.matsgemmeke.battlegrounds.item.recoil.CameraMovementRecoil;
import com.github.matsgemmeke.battlegrounds.item.recoil.RandomSpreadRecoil;
import com.github.matsgemmeke.battlegrounds.item.recoil.RecoilSystem;
import com.github.matsgemmeke.battlegrounds.item.recoil.RecoilSystemFactory;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class RecoilSystemFactoryTest {

    private InternalsProvider internals;

    @Before
    public void setUp() {
        this.internals = mock(InternalsProvider.class);
    }

    @Test
    public void isAbleToCreateRandomSpreadInstance() {
        Section section = mock(Section.class);
        when(section.getFloatList("horizontal")).thenReturn(Arrays.asList(10.0f, 20.0f));
        when(section.getFloatList("vertical")).thenReturn(Arrays.asList(10.0f, 20.0f));
        when(section.getString("type")).thenReturn("RANDOM_SPREAD");

        RecoilSystemFactory factory = new RecoilSystemFactory(internals);
        RecoilSystem recoilSystem = factory.make(section);

        assertNotNull(recoilSystem);
        assertTrue(recoilSystem instanceof RandomSpreadRecoil);
    }

    @Test
    public void isAbleToCreateCameraMovementInstance() {
        Section section = mock(Section.class);
        when(section.getFloatList("horizontal")).thenReturn(Arrays.asList(10.0f, 20.0f));
        when(section.getFloatList("vertical")).thenReturn(Arrays.asList(10.0f, 20.0f));
        when(section.getString("type")).thenReturn("CAMERA_MOVEMENT");

        RecoilSystemFactory factory = new RecoilSystemFactory(internals);
        RecoilSystem recoilSystem = factory.make(section);

        assertNotNull(recoilSystem);
        assertTrue(recoilSystem instanceof CameraMovementRecoil);
    }

    @Test(expected = WeaponFactoryCreationException.class)
    public void throwsExceptionWhenCreatingUnknownRecoilSystemType() {
        Section section = mock(Section.class);
        when(section.getString("type")).thenReturn("error");

        RecoilSystemFactory factory = new RecoilSystemFactory(internals);
        factory.make(section);
    }
}
