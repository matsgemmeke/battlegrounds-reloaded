package nl.matsgemmeke.battlegrounds.game.training;

import nl.matsgemmeke.battlegrounds.InternalsProvider;
import nl.matsgemmeke.battlegrounds.configuration.BattlegroundsConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Bukkit.class)
public class TrainingModeFactoryTest {

    private BattlegroundsConfiguration config;
    private InternalsProvider internals;

    @Before
    public void setUp() {
        config = mock(BattlegroundsConfiguration.class);
        internals = mock(InternalsProvider.class);

        PowerMockito.mockStatic(Bukkit.class);
    }

    @Test
    public void createNewInstanceOfTrainingModeAndRegisterOnlinePlayers() {
        Player player = mock(Player.class);

        when(Bukkit.getOnlinePlayers()).then((Answer<?>) invocation -> List.of(player));

        TrainingModeFactory factory = new TrainingModeFactory(config, internals);
        TrainingMode trainingMode = factory.make();

        assertTrue(trainingMode instanceof DefaultTrainingMode);
        assertNotNull(trainingMode.getPlayerStorage().getEntity(player));
    }
}
