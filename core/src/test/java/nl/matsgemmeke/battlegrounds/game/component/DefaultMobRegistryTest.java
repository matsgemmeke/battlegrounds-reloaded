package nl.matsgemmeke.battlegrounds.game.component;

import nl.matsgemmeke.battlegrounds.entity.DefaultGameMob;
import nl.matsgemmeke.battlegrounds.entity.GameMob;
import nl.matsgemmeke.battlegrounds.game.EntityStorage;
import org.bukkit.entity.Mob;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DefaultMobRegistryTest {

    private EntityStorage<GameMob> mobStorage;

    @Before
    public void setUp() {
        mobStorage = new EntityStorage<>();
    }

    @Test
    public void shouldFindByEntityAndReturnMatchingEntity() {
        Mob mob = mock(Mob.class);

        DefaultMobRegistry mobRegistry = new DefaultMobRegistry(mobStorage);
        GameMob gameMob = mobRegistry.registerEntity(mob);

        GameMob result = mobRegistry.findByEntity(mob);

        assertEquals(gameMob, result);
    }

    @Test
    public void shouldFindByEntityAndReturnNullIfNoMatch() {
        Mob mob = mock(Mob.class);
        Mob otherMob = mock(Mob.class);

        DefaultMobRegistry mobRegistry = new DefaultMobRegistry(mobStorage);
        mobRegistry.registerEntity(mob);

        GameMob result = mobRegistry.findByEntity(otherMob);

        assertNull(result);
    }

    @Test
    public void shouldFindByUUIDAndReturnMatchingEntity() {
        UUID uuid = UUID.randomUUID();

        Mob mob = mock(Mob.class);
        when(mob.getUniqueId()).thenReturn(uuid);

        DefaultMobRegistry mobRegistry = new DefaultMobRegistry(mobStorage);
        GameMob gameMob = mobRegistry.registerEntity(mob);

        GameMob result = mobRegistry.findByUUID(uuid);

        assertEquals(gameMob, result);
    }

    @Test
    public void shouldFindByUUIDAndReturnNullIfNoMatch() {
        UUID uuid = UUID.randomUUID();
        UUID otherUuid = UUID.randomUUID();

        Mob mob = mock(Mob.class);
        when(mob.getUniqueId()).thenReturn(uuid);

        DefaultMobRegistry mobRegistry = new DefaultMobRegistry(mobStorage);
        mobRegistry.registerEntity(mob);

        GameMob result = mobRegistry.findByUUID(otherUuid);

        assertNull(result);
    }

    @Test
    public void shouldReturnTrueWhenCheckingIfRegisteredAndStorageContainsGivenMob() {
        Mob mob = mock(Mob.class);

        DefaultMobRegistry mobRegistry = new DefaultMobRegistry(mobStorage);
        mobRegistry.registerEntity(mob);

        boolean registered = mobRegistry.isRegistered(mob);

        assertTrue(registered);
    }

    @Test
    public void shouldReturnFalseWhenCheckingIfRegisteredAndStorageDoesNotContainGivenMob() {
        Mob mob = mock(Mob.class);

        DefaultMobRegistry mobRegistry = new DefaultMobRegistry(mobStorage);
        boolean registered = mobRegistry.isRegistered(mob);

        assertFalse(registered);
    }

    @Test
    public void shouldReturnTrueWhenCheckingIfRegisteredAndStorageContainsGivenMobUUID() {
        UUID uuid = UUID.randomUUID();

        Mob mob = mock(Mob.class);
        when(mob.getUniqueId()).thenReturn(uuid);

        DefaultMobRegistry mobRegistry = new DefaultMobRegistry(mobStorage);
        mobRegistry.registerEntity(mob);

        boolean registered = mobRegistry.isRegistered(uuid);

        assertTrue(registered);
    }

    @Test
    public void shouldReturnFalseWhenCheckingIfRegisteredAndStorageDoesNotContainGivenMobUUID() {
        UUID uuid = UUID.randomUUID();
        UUID otherUuid = UUID.randomUUID();

        Mob mob = mock(Mob.class);
        when(mob.getUniqueId()).thenReturn(uuid);

        DefaultMobRegistry mobRegistry = new DefaultMobRegistry(mobStorage);
        mobRegistry.registerEntity(mob);

        boolean registered = mobRegistry.isRegistered(otherUuid);

        assertFalse(registered);
    }

    @Test
    public void shouldCreateNewInstanceOfGameItemAndRegisterToGameStorage() {
        Mob mob = mock(Mob.class);

        DefaultMobRegistry mobRegistry = new DefaultMobRegistry(mobStorage);
        GameMob gameMob = mobRegistry.registerEntity(mob);

        assertTrue(gameMob instanceof DefaultGameMob);
    }
}
