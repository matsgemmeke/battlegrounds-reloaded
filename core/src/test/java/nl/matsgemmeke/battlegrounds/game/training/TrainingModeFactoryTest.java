package nl.matsgemmeke.battlegrounds.game.training;

import nl.matsgemmeke.battlegrounds.InternalsProvider;
import nl.matsgemmeke.battlegrounds.configuration.BattlegroundsConfiguration;
import nl.matsgemmeke.battlegrounds.event.EventDispatcher;
import nl.matsgemmeke.battlegrounds.game.event.EntityCombustEventHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityCombustEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TrainingModeFactoryTest {

    private BattlegroundsConfiguration config;
    private EventDispatcher eventDispatcher;
    private InternalsProvider internals;
    private MockedStatic<Bukkit> bukkit;

    @BeforeEach
    public void setUp() {
        config = mock(BattlegroundsConfiguration.class);
        eventDispatcher = mock(EventDispatcher.class);
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

        TrainingModeFactory factory = new TrainingModeFactory(config, eventDispatcher, internals);
        TrainingMode trainingMode = factory.make();

        ArgumentCaptor<EntityCombustEventHandler> entityCombustEventHandlerCaptor = ArgumentCaptor.forClass(EntityCombustEventHandler.class);
        verify(eventDispatcher).registerEventHandler(eq(EntityCombustEvent.class), entityCombustEventHandlerCaptor.capture());

        assertInstanceOf(DefaultTrainingMode.class, trainingMode);
        assertNotNull(trainingMode.getPlayerStorage().getEntity(player));
    }
}
