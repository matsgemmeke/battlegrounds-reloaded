package nl.matsgemmeke.battlegrounds.game.training;

import nl.matsgemmeke.battlegrounds.InternalsProvider;
import nl.matsgemmeke.battlegrounds.configuration.BattlegroundsConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TrainingModeFactoryTest {

    private BattlegroundsConfiguration config;
    private InternalsProvider internals;
    private MockedStatic<Bukkit> bukkit;

    @BeforeEach
    public void setUp() {
        config = mock(BattlegroundsConfiguration.class);
        internals = mock(InternalsProvider.class);
        bukkit = mockStatic(Bukkit.class);
    }

    @AfterEach
    public void tearDown() {
        bukkit.close();
    }

    @Test
    public void createNewInstanceOfTrainingModeAndRegisterOnlinePlayers() {
        Player player = mock(Player.class);

        bukkit.when(Bukkit::getOnlinePlayers).thenReturn(List.of(player));

        TrainingModeFactory factory = new TrainingModeFactory(config, internals);
        TrainingMode trainingMode = factory.make();

        assertInstanceOf(DefaultTrainingMode.class, trainingMode);
        assertNotNull(trainingMode.getPlayerStorage().getEntity(player));
    }
}
