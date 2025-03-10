package nl.matsgemmeke.battlegrounds.item.effect.spawn;

import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import nl.matsgemmeke.battlegrounds.game.component.spawn.SpawnPointProvider;
import nl.matsgemmeke.battlegrounds.game.spawn.SpawnPoint;
import nl.matsgemmeke.battlegrounds.item.ItemHolder;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectSource;
import nl.matsgemmeke.battlegrounds.item.effect.activation.ItemEffectActivation;
import nl.matsgemmeke.battlegrounds.util.Procedure;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.mockito.Mockito.*;

public class MarkSpawnPointEffectTest {

    private ItemEffectActivation effectActivation;
    private SpawnPointProvider spawnPointProvider;

    @BeforeEach
    public void setUp() {
        effectActivation = mock(ItemEffectActivation.class);
        spawnPointProvider = mock(SpawnPointProvider.class);
    }

    @Test
    public void primeCreatesNewCustomSpawnPointAndAssignsToHolder() {
        Location eyeLocation = new Location(null, 1, 1, 1, 1.0f, 1.0f);

        Player player = mock(Player.class);
        when(player.getEyeLocation()).thenReturn(eyeLocation);

        ItemHolder holder = mock(ItemHolder.class);
        when(holder.getEntity()).thenReturn(player);

        ItemEffectSource source = mock(ItemEffectSource.class);
        ItemEffectContext context = new ItemEffectContext(holder, source);

        MarkSpawnPointEffect effect = new MarkSpawnPointEffect(effectActivation, spawnPointProvider);
        effect.prime(context);

        ArgumentCaptor<Procedure> procedureCaptor = ArgumentCaptor.forClass(Procedure.class);
        verify(effectActivation).prime(eq(context), procedureCaptor.capture());

        procedureCaptor.getValue().apply();

        ArgumentCaptor<MarkedSpawnPoint> spawnPointCaptor = ArgumentCaptor.forClass(MarkedSpawnPoint.class);
        verify(spawnPointProvider).setCustomSpawnPoint(eq(holder), spawnPointCaptor.capture());
    }

    @Test
    public void resetDoesNotResetSpawnPointIfEffectIsNotPerformed() {
        MarkSpawnPointEffect effect = new MarkSpawnPointEffect(effectActivation, spawnPointProvider);
        effect.reset();

        verify(spawnPointProvider, never()).setCustomSpawnPoint(any(GameEntity.class), any(SpawnPoint.class));
    }

    @Test
    public void resetResetsSpawnPointIfEffectIsPerformed() {
        Location eyeLocation = new Location(null, 1, 1, 1, 1.0f, 1.0f);

        Player player = mock(Player.class);
        when(player.getEyeLocation()).thenReturn(eyeLocation);

        ItemHolder holder = mock(ItemHolder.class);
        when(holder.getEntity()).thenReturn(player);

        ItemEffectSource source = mock(ItemEffectSource.class);
        ItemEffectContext context = new ItemEffectContext(holder, source);

        MarkSpawnPointEffect effect = new MarkSpawnPointEffect(effectActivation, spawnPointProvider);
        effect.prime(context);
        effect.reset();

        verify(spawnPointProvider).setCustomSpawnPoint(holder, null);
    }
}
