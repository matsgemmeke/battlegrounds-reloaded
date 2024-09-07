package nl.matsgemmeke.battlegrounds.event.handler;

import nl.matsgemmeke.battlegrounds.configuration.BattlegroundsConfiguration;
import nl.matsgemmeke.battlegrounds.entity.GameMob;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.component.EntityRegistry;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Mob;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class CreatureSpawnEventHandlerTest {

    private BattlegroundsConfiguration config;
    private GameContext trainingModeContext;

    @Before
    public void setUp() {
        config = mock(BattlegroundsConfiguration.class);
        trainingModeContext = mock(GameContext.class);
    }

    @Test
    public void shouldNotHandleEventIfEntityIsNoMob() {
        ArmorStand armorStand = mock(ArmorStand.class);

        CreatureSpawnEvent event = new CreatureSpawnEvent(armorStand, SpawnReason.DEFAULT);

        CreatureSpawnEventHandler eventHandler = new CreatureSpawnEventHandler(trainingModeContext, config);
        eventHandler.handle(event);

        verifyNoInteractions(trainingModeContext);
    }

    @Test
    public void shouldNotRegisterEntityWhenConfiguredToBeDisabled() {
        Mob mob = mock(Mob.class);

        when(config.isEnabledRegisterEntitiesToTrainingModeUponSpawn()).thenReturn(false);

        CreatureSpawnEvent event = new CreatureSpawnEvent(mob, SpawnReason.DEFAULT);

        CreatureSpawnEventHandler eventHandler = new CreatureSpawnEventHandler(trainingModeContext, config);
        eventHandler.handle(event);

        verifyNoInteractions(trainingModeContext);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldRegisterEntityWhenConfiguredToBeEnabled() {
        EntityRegistry<GameMob, Mob> mobRegistry = (EntityRegistry<GameMob, Mob>) mock(EntityRegistry.class);
        Mob mob = mock(Mob.class);

        when(config.isEnabledRegisterEntitiesToTrainingModeUponSpawn()).thenReturn(true);
        when(trainingModeContext.getMobRegistry()).thenReturn(mobRegistry);

        CreatureSpawnEvent event = new CreatureSpawnEvent(mob, SpawnReason.DEFAULT);

        CreatureSpawnEventHandler eventHandler = new CreatureSpawnEventHandler(trainingModeContext, config);
        eventHandler.handle(event);

        verify(mobRegistry).registerEntity(mob);
    }
}
