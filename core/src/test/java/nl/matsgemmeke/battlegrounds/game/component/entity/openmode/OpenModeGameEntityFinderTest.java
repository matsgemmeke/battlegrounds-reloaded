package nl.matsgemmeke.battlegrounds.game.component.entity.openmode;

import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import nl.matsgemmeke.battlegrounds.entity.GameMob;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.component.entity.MobRegistry;
import nl.matsgemmeke.battlegrounds.game.component.entity.PlayerRegistry;
import nl.matsgemmeke.battlegrounds.util.BukkitEntityFinder;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OpenModeGameEntityFinderTest {

    private static final UUID UNIQUE_ID = UUID.randomUUID();

    @Mock
    private BukkitEntityFinder bukkitEntityFinder;
    @Mock
    private MobRegistry mobRegistry;
    @Mock
    private PlayerRegistry playerRegistry;
    @InjectMocks
    private OpenModeGameEntityFinder gameEntityFinder;

    @Test
    void findGameEntityByUniqueIdReturnsOptionalWithGamePlayerWhenPlayerRegistryContainsGivenUniqueId() {
        GamePlayer gamePlayer = mock(GamePlayer.class);

        when(playerRegistry.findByUniqueId(UNIQUE_ID)).thenReturn(Optional.of(gamePlayer));

        Optional<GameEntity> gameEntityOptional = gameEntityFinder.findGameEntityByUniqueId(UNIQUE_ID);

        assertThat(gameEntityOptional).hasValue(gamePlayer);
    }

    @Test
    void findGameEntityByUniqueIdReturnsOptionalWithGameMobWhenMobRegistryContainsGivenUniqueId() {
        GameMob gameMob = mock(GameMob.class);

        when(playerRegistry.findByUniqueId(UNIQUE_ID)).thenReturn(Optional.empty());
        when(mobRegistry.findByUniqueId(UNIQUE_ID)).thenReturn(Optional.of(gameMob));

        Optional<GameEntity> gameEntityOptional = gameEntityFinder.findGameEntityByUniqueId(UNIQUE_ID);

        assertThat(gameEntityOptional).hasValue(gameMob);
    }

    @Test
    void findGameEntityByUniqueIdReturnsOptionalWithNewlyCreatedGameMobWhenMobIsNotRegistered() {
        Mob mob = mock(Mob.class);
        GameMob gameMob = mock(GameMob.class);

        when(playerRegistry.findByUniqueId(UNIQUE_ID)).thenReturn(Optional.empty());
        when(mobRegistry.findByUniqueId(UNIQUE_ID)).thenReturn(Optional.empty());
        when(mobRegistry.register(mob)).thenReturn(gameMob);
        when(bukkitEntityFinder.getEntityByUniqueId(UNIQUE_ID)).thenReturn(Optional.of(mob));

        Optional<GameEntity> gameEntityOptional = gameEntityFinder.findGameEntityByUniqueId(UNIQUE_ID);

        assertThat(gameEntityOptional).hasValue(gameMob);
    }

    @Test
    void findGameEntityByUniqueIdReturnsEmptyOptionalWhenNoRegistryContainsUniqueIdAndEntityIsNoMob() {
        Entity entity = mock(Entity.class);

        when(playerRegistry.findByUniqueId(UNIQUE_ID)).thenReturn(Optional.empty());
        when(mobRegistry.findByUniqueId(UNIQUE_ID)).thenReturn(Optional.empty());
        when(bukkitEntityFinder.getEntityByUniqueId(UNIQUE_ID)).thenReturn(Optional.of(entity));

        Optional<GameEntity> gameEntityOptional = gameEntityFinder.findGameEntityByUniqueId(UNIQUE_ID);

        assertThat(gameEntityOptional).isEmpty();
    }
}
