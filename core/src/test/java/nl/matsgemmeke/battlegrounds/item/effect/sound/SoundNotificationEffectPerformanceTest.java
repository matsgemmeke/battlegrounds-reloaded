package nl.matsgemmeke.battlegrounds.item.effect.sound;

import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.entity.PlayerRegistry;
import nl.matsgemmeke.battlegrounds.game.damage.DamageSource;
import nl.matsgemmeke.battlegrounds.item.effect.CollisionResult;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.item.effect.source.ItemEffectSource;
import org.bukkit.Location;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SoundNotificationEffectPerformanceTest {

    private static final CollisionResult COLLISION_RESULT = new CollisionResult(null, null, null);
    private static final Location INITIATION_LOCATION = new Location(null, 0, 0, 0);
    private static final UUID DAMAGE_SOURCE_ID = UUID.randomUUID();

    @Mock
    private DamageSource damageSource;
    @Mock
    private GameSound sound;
    @Mock
    private ItemEffectSource effectSource;
    @Mock
    private PlayerRegistry playerRegistry;

    private SoundNotificationEffectPerformance performance;

    @BeforeEach
    void setUp() {
        SoundNotificationProperties properties = new SoundNotificationProperties(List.of(sound));

        performance = new SoundNotificationEffectPerformance(playerRegistry, properties);
    }

    @Test
    void isPerformingReturnsFalseEvenAfterStartingPerformance() {
        ItemEffectContext context = this.createItemEffectContext();

        performance.perform(context);
        boolean performing = performance.isPerforming();

        assertThat(performing).isFalse();
    }

    @Test
    void performDoesNothingWhenDamageSourceIsNoPlayer() {
        ItemEffectContext context = this.createItemEffectContext();

        when(damageSource.getUniqueId()).thenReturn(DAMAGE_SOURCE_ID);
        when(playerRegistry.findByUniqueId(DAMAGE_SOURCE_ID)).thenReturn(Optional.empty());

        assertThatCode(() -> performance.perform(context)).doesNotThrowAnyException();
    }

    @Test
    void performPlaysNotificationSoundsWhenDamageSourceIsPlayer() {
        Location playerLocation = new Location(null, 1, 1, 1);
        ItemEffectContext context = this.createItemEffectContext();

        GamePlayer gamePlayer = mock(GamePlayer.class);
        when(gamePlayer.getLocation()).thenReturn(playerLocation);

        when(damageSource.getUniqueId()).thenReturn(DAMAGE_SOURCE_ID);
        when(playerRegistry.findByUniqueId(DAMAGE_SOURCE_ID)).thenReturn(Optional.of(gamePlayer));

        performance.perform(context);

        verify(gamePlayer).playSound(playerLocation, sound);
    }

    private ItemEffectContext createItemEffectContext() {
        return new ItemEffectContext(COLLISION_RESULT, damageSource, effectSource, INITIATION_LOCATION);
    }
}
