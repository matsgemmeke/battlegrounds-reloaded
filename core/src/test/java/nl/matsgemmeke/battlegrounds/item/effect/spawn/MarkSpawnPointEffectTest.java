package nl.matsgemmeke.battlegrounds.item.effect.spawn;

import nl.matsgemmeke.battlegrounds.game.component.spawn.SpawnPointProvider;
import nl.matsgemmeke.battlegrounds.game.spawn.SpawnPoint;
import nl.matsgemmeke.battlegrounds.item.deploy.Deployer;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectSource;
import nl.matsgemmeke.battlegrounds.item.trigger.Trigger;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerObserver;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.UUID;

import static org.mockito.Mockito.*;

public class MarkSpawnPointEffectTest {

    private Deployer deployer;
    private Entity entity;
    private ItemEffectSource source;
    private SpawnPointProvider spawnPointProvider;
    private Trigger trigger;

    @BeforeEach
    public void setUp() {
        deployer = mock(Deployer.class);
        entity = mock(Entity.class);
        source = mock(ItemEffectSource.class);
        spawnPointProvider = mock(SpawnPointProvider.class);
        trigger = mock(Trigger.class);
    }

    @Test
    public void primeCreatesNewCustomSpawnPointAndAssignsToDeployer() {
        UUID entityId = UUID.randomUUID();
        Location initiationLocation = new Location(null, 1, 1, 1, 1.0f, 1.0f);
        ItemEffectContext context = new ItemEffectContext(deployer, entity, initiationLocation, source);

        when(entity.getUniqueId()).thenReturn(entityId);

        MarkSpawnPointEffect effect = new MarkSpawnPointEffect(spawnPointProvider);
        effect.addTrigger(trigger);
        effect.prime(context);

        ArgumentCaptor<TriggerObserver> triggerObserverCaptor = ArgumentCaptor.forClass(TriggerObserver.class);
        verify(trigger).addObserver(triggerObserverCaptor.capture());

        triggerObserverCaptor.getValue().onActivate();

        ArgumentCaptor<MarkedSpawnPoint> spawnPointCaptor = ArgumentCaptor.forClass(MarkedSpawnPoint.class);
        verify(spawnPointProvider).setCustomSpawnPoint(eq(entityId), spawnPointCaptor.capture());
    }

    @Test
    public void resetDoesNotResetSpawnPointIfEffectIsNotPerformed() {
        MarkSpawnPointEffect effect = new MarkSpawnPointEffect(spawnPointProvider);
        effect.reset();

        verify(spawnPointProvider, never()).setCustomSpawnPoint(any(UUID.class), any(SpawnPoint.class));
    }

    @Test
    public void resetResetsSpawnPointIfEffectIsPerformed() {
        UUID entityId = UUID.randomUUID();
        Location initationLocation = new Location(null, 1, 1, 1, 1.0f, 1.0f);
        ItemEffectContext context = new ItemEffectContext(deployer, entity, initationLocation, source);

        when(entity.getUniqueId()).thenReturn(entityId);

        MarkSpawnPointEffect effect = new MarkSpawnPointEffect(spawnPointProvider);
        effect.prime(context);
        effect.reset();

        verify(spawnPointProvider).setCustomSpawnPoint(entityId, null);
    }
}
