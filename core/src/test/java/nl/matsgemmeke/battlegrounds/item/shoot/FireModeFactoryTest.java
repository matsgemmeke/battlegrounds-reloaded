package nl.matsgemmeke.battlegrounds.item.shoot;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.item.WeaponFactoryCreationException;
import nl.matsgemmeke.battlegrounds.item.shoot.burst.BurstMode;
import nl.matsgemmeke.battlegrounds.item.shoot.burst.BurstModeFactory;
import nl.matsgemmeke.battlegrounds.item.shoot.fullauto.FullyAutomaticMode;
import nl.matsgemmeke.battlegrounds.item.shoot.fullauto.FullyAutomaticModeFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FireModeFactoryTest {

    private BurstModeFactory burstModeFactory;
    private FullyAutomaticModeFactory fullyAutomaticModeFactory;
    private Section section;
    private Shootable item;
    private TaskRunner taskRunner;

    @BeforeEach
    public void setUp() {
        burstModeFactory = mock(BurstModeFactory.class);
        fullyAutomaticModeFactory = mock(FullyAutomaticModeFactory.class);
        section = mock(Section.class);
        item = mock(Shootable.class);
        taskRunner = mock(TaskRunner.class);
    }

    @Test
    public void makeFireModeInstanceForBurstMode() {
        int amountOfShots = 3;
        int rateOfFire = 600;

        when(section.getInt("amount-of-shots")).thenReturn(amountOfShots);
        when(section.getInt("rate-of-fire")).thenReturn(rateOfFire);
        when(section.getString("type")).thenReturn("BURST_MODE");

        BurstMode fireMode = mock(BurstMode.class);
        when(burstModeFactory.create(item, amountOfShots, rateOfFire)).thenReturn(fireMode);

        FireModeFactory factory = new FireModeFactory(burstModeFactory, fullyAutomaticModeFactory, taskRunner);
        FireMode result = factory.create(item, section);

        assertEquals(fireMode, result);
    }

    @Test
    public void makeFireModeInstanceForFullyAutomatic() {
        int rateOfFire = 600;

        when(section.getInt("rate-of-fire")).thenReturn(rateOfFire);
        when(section.getString("type")).thenReturn("FULLY_AUTOMATIC");

        FullyAutomaticMode fireMode = mock(FullyAutomaticMode.class);
        when(fullyAutomaticModeFactory.create(item, rateOfFire)).thenReturn(fireMode);

        FireModeFactory factory = new FireModeFactory(burstModeFactory, fullyAutomaticModeFactory, taskRunner);
        FireMode result = factory.create(item, section);

        assertEquals(fireMode, result);
    }

    @Test
    public void makeFireModeInstanceForSemiAutomatic() {
        when(section.getString("type")).thenReturn("SEMI_AUTOMATIC");

        FireModeFactory factory = new FireModeFactory(burstModeFactory, fullyAutomaticModeFactory, taskRunner);
        FireMode fireMode = factory.create(item, section);

        assertInstanceOf(SemiAutomaticMode.class, fireMode);
    }

    @Test
    public void throwErrorWhenUnknownFireModeType() {
        when(section.getString("type")).thenReturn("error");

        FireModeFactory factory = new FireModeFactory(burstModeFactory, fullyAutomaticModeFactory, taskRunner);

        assertThrows(WeaponFactoryCreationException.class, () -> factory.create(item, section));
    }
}
