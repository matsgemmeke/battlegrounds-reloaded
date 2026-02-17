package nl.matsgemmeke.battlegrounds.item.trigger.impl;

import nl.matsgemmeke.battlegrounds.item.actor.Actor;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerContext;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HitEntityFilterTest {

    private static final UUID SOURCE_ID = UUID.randomUUID();

    @Mock
    private Actor actor;
    @Mock
    private Entity entity;

    private HitEntityFilter filter;

    @BeforeEach
    void setUp() {
        TriggerContext triggerContext = new TriggerContext(SOURCE_ID, actor);

        filter = new HitEntityFilter(triggerContext);
    }

    @Test
    void testReturnsFalseWhenEntityUniqueIdEqualsSourceId() {
        when(entity.getUniqueId()).thenReturn(SOURCE_ID);

        boolean result = filter.test(entity);

        assertThat(result).isFalse();
    }

    static List<Arguments> entityAndActorLocationCases() {
        return List.of(
                arguments(new Location(null, 1, 1, 1), new Location(null, 1, 1, 1), false),
                arguments(new Location(null, 2, 2, 2), new Location(null, 1, 1, 1), true)
        );
    }

    @ParameterizedTest
    @MethodSource("entityAndActorLocationCases")
    void testReturnsWhetherEntityLocationDoesNotEqualActorLocation(Location entityLocation, Location actorLocation, boolean expectedResult) {
        when(actor.getLocation()).thenReturn(actorLocation);
        when(entity.getUniqueId()).thenReturn(UUID.randomUUID());
        when(entity.getLocation()).thenReturn(entityLocation);

        boolean result = filter.test(entity);

        assertThat(result).isEqualTo(expectedResult);
    }
}
