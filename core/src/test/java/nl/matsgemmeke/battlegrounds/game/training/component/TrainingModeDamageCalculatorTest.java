package nl.matsgemmeke.battlegrounds.game.training.component;

import nl.matsgemmeke.battlegrounds.entity.GameItem;
import nl.matsgemmeke.battlegrounds.game.component.EntityRegistry;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.entity.EntityDamageEvent;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TrainingModeDamageCalculatorTest {

    private EntityRegistry<GameItem, Item> itemRegistry;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        itemRegistry = (EntityRegistry<GameItem, Item>) mock(EntityRegistry.class);
    }

    @Test
    public void shouldNegateDamageFromItemEntities() {
        UUID damagerUUID = UUID.randomUUID();

        Entity entity = mock(Entity.class);

        Entity damager = mock(Entity.class);
        when(damager.getUniqueId()).thenReturn(damagerUUID);

        when(itemRegistry.isRegistered(damagerUUID)).thenReturn(true);

        TrainingModeDamageCalculator damageCalculator = new TrainingModeDamageCalculator(itemRegistry);

        double damage = damageCalculator.calculateDamage(damager, entity, EntityDamageEvent.DamageCause.ENTITY_EXPLOSION, 10.0);

        assertEquals(0.0, damage, 0.0);
    }

    @Test
    public void shouldReturnOriginalDamageForUnhandledCases() {
        UUID damagerUUID = UUID.randomUUID();

        Entity entity = mock(Entity.class);

        Entity damager = mock(Entity.class);
        when(damager.getUniqueId()).thenReturn(damagerUUID);

        TrainingModeDamageCalculator damageCalculator = new TrainingModeDamageCalculator(itemRegistry);

        double damage = damageCalculator.calculateDamage(damager, entity, EntityDamageEvent.DamageCause.ENTITY_EXPLOSION, 10.0);

        assertEquals(10.0, damage, 0.0);
    }
}
