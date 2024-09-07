package nl.matsgemmeke.battlegrounds.game.component;

import nl.matsgemmeke.battlegrounds.InternalsProvider;
import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import nl.matsgemmeke.battlegrounds.entity.GameMob;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.EntityStorage;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class EntityFinderTest {

    private EntityRegistry<GameMob, Mob> mobRegistry;
    private EntityRegistry<GamePlayer, Player> playerRegistry;

    @Before
    public void setUp() {
        mobRegistry = new DefaultMobRegistry(new EntityStorage<>());
        playerRegistry = new DefaultPlayerRegistry(new EntityStorage<>(), mock(InternalsProvider.class));
    }

    @Test
    public void shouldReturnMatchingEntity() {
        Player player = mock(Player.class);
        GamePlayer gamePlayer = playerRegistry.registerEntity(player);

        EntityFinder entityFinder = new EntityFinder(mobRegistry, playerRegistry);
        GameEntity result = entityFinder.findEntity(player);

        assertNotNull(result);
        assertEquals(gamePlayer, result);
    }

    @Test
    public void shouldReturnNullIfNoMatchingEntityWasFound() {
        Player player = mock(Player.class);
        Player otherPlayer = mock(Player.class);

        playerRegistry.registerEntity(player);

        EntityFinder entityFinder = new EntityFinder(mobRegistry, playerRegistry);
        GameEntity result = entityFinder.findEntity(otherPlayer);

        assertNull(result);
    }
}
