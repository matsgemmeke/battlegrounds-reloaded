package nl.matsgemmeke.battlegrounds.item.effect.spawn;

import nl.matsgemmeke.battlegrounds.game.component.spawn.SpawnPointProvider;
import nl.matsgemmeke.battlegrounds.game.spawn.SpawnPoint;
import nl.matsgemmeke.battlegrounds.item.deploy.Deployer;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectSource;
import nl.matsgemmeke.battlegrounds.item.effect.activation.ItemEffectActivation;
import nl.matsgemmeke.battlegrounds.util.Procedure;
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
    private ItemEffectActivation effectActivation;
    private ItemEffectContext context;
    private ItemEffectSource source;
    private SpawnPointProvider spawnPointProvider;

    @BeforeEach
    public void setUp() {
        effectActivation = mock(ItemEffectActivation.class);
        spawnPointProvider = mock(SpawnPointProvider.class);

        deployer = mock(Deployer.class);
        entity = mock(Entity.class);
        source = mock(ItemEffectSource.class);
        context = new ItemEffectContext(deployer, entity, source);
    }

    @Test
    public void primeCreatesNewCustomSpawnPointAndAssignsToDeployer() {
        UUID entityId = UUID.randomUUID();
        Location deployDirection = new Location(null, 1, 1, 1, 1.0f, 1.0f);

        when(deployer.getDeployDirection()).thenReturn(deployDirection);
        when(entity.getUniqueId()).thenReturn(entityId);

        MarkSpawnPointEffect effect = new MarkSpawnPointEffect(effectActivation, spawnPointProvider);
        effect.prime(context);

        ArgumentCaptor<Procedure> procedureCaptor = ArgumentCaptor.forClass(Procedure.class);
        verify(effectActivation).prime(eq(context), procedureCaptor.capture());

        procedureCaptor.getValue().apply();

        ArgumentCaptor<MarkedSpawnPoint> spawnPointCaptor = ArgumentCaptor.forClass(MarkedSpawnPoint.class);
        verify(spawnPointProvider).setCustomSpawnPoint(eq(entityId), spawnPointCaptor.capture());
    }

    @Test
    public void resetDoesNotResetSpawnPointIfEffectIsNotPerformed() {
        MarkSpawnPointEffect effect = new MarkSpawnPointEffect(effectActivation, spawnPointProvider);
        effect.reset();

        verify(spawnPointProvider, never()).setCustomSpawnPoint(any(UUID.class), any(SpawnPoint.class));
    }

    @Test
    public void resetResetsSpawnPointIfEffectIsPerformed() {
        UUID entityId = UUID.randomUUID();
        Location deployDirection = new Location(null, 1, 1, 1, 1.0f, 1.0f);

        when(deployer.getDeployDirection()).thenReturn(deployDirection);
        when(entity.getUniqueId()).thenReturn(entityId);

        MarkSpawnPointEffect effect = new MarkSpawnPointEffect(effectActivation, spawnPointProvider);
        effect.prime(context);
        effect.reset();

        verify(spawnPointProvider).setCustomSpawnPoint(entityId, null);
    }
}
