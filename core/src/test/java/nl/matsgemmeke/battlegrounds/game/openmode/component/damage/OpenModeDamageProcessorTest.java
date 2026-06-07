package nl.matsgemmeke.battlegrounds.game.openmode.component.damage;

import nl.matsgemmeke.battlegrounds.entity.EntityKey;
import nl.matsgemmeke.battlegrounds.entity.EntityKeyRegistry;
import nl.matsgemmeke.battlegrounds.entity.hitbox.HitboxComponentType;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.damage.*;
import nl.matsgemmeke.battlegrounds.game.damage.modifier.DamageModifier;
import nl.matsgemmeke.battlegrounds.storage.stats.damage.DamageEvent;
import org.bukkit.entity.EntityType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OpenModeDamageProcessorTest {

    private static final GameKey GAME_KEY = GameKey.ofOpenMode();
    private static final UUID DAMAGER_ID = UUID.randomUUID();
    private static final UUID VICTIM_ID = UUID.randomUUID();
    private static final String ITEM_NAME = "Test Item";
    private static final double NORMAL_DAMAGE_AMOUNT = 10.0;
    private static final Damage NORMAL_DAMAGE = new Damage(NORMAL_DAMAGE_AMOUNT, DamageType.BULLET_DAMAGE, HitboxComponentType.TORSO);
    private static final double HIGH_DAMAGE_AMOUNT = 20.0;
    private static final Damage HIGH_DAMAGE = new Damage(HIGH_DAMAGE_AMOUNT, DamageType.BULLET_DAMAGE, HitboxComponentType.TORSO);
    private static final double DISTANCE = 5.0;

    @Spy
    private Clock clock = Clock.fixed(Instant.parse("2026-05-14T13:00:00.00Z"), ZoneOffset.UTC);
    @Mock
    private DamageEventTracker damageEventTracker;
    @Mock
    private EntityKeyRegistry entityKeyRegistry;
    @Spy
    private GameKey gameKey = GAME_KEY;
    @InjectMocks
    private OpenModeDamageProcessor damageProcessor;

    static List<Arguments> isDamageAllowedArguments() {
        return List.of(
                arguments(GameKey.ofSession(1), false),
                arguments(GAME_KEY, true)
        );
    }

    @ParameterizedTest
    @MethodSource("isDamageAllowedArguments")
    @DisplayName("isDamageAllowed returns false when given game key is different")
    void isDamageAllowed(GameKey gameKey, boolean expectedAllowed) {
        boolean allowed = damageProcessor.isDamageAllowed(gameKey);

        assertThat(allowed).isEqualTo(expectedAllowed);
    }

    @Test
    @DisplayName("isDamageAllowedWithoutContext always returns true")
    void isDamageAllowedWithoutContext() {
        boolean allowed = damageProcessor.isDamageAllowedWithoutContext();

        assertThat(allowed).isTrue();
    }

    @Test
    @DisplayName("processDamage does not perform the damage pipeline when no entity key is found for damager")
    void processDamage_damagerWithoutEntityKey() {
        DamageSource source = mock(DamageSource.class);
        when(source.getUniqueId()).thenReturn(DAMAGER_ID);

        DamageTarget target = mock(DamageTarget.class);
        when(target.getUniqueId()).thenReturn(VICTIM_ID);

        DamageContext damageContext = new DamageContext(source, target, ITEM_NAME, NORMAL_DAMAGE, DISTANCE);

        when(entityKeyRegistry.getEntityKey(DAMAGER_ID)).thenReturn(Optional.empty());

        damageProcessor.processDamage(damageContext);

        verifyNoInteractions(damageEventTracker);
    }

    @Test
    @DisplayName("processDamage does not perform the damage pipeline when no entity key is found for victim")
    void processDamage_victimWithoutEntityKey() {
        EntityKey damagerEntityKey = EntityKey.fromEntityType(EntityType.PLAYER);

        DamageSource source = mock(DamageSource.class);
        when(source.getUniqueId()).thenReturn(DAMAGER_ID);

        DamageTarget target = mock(DamageTarget.class);
        when(target.getUniqueId()).thenReturn(VICTIM_ID);

        DamageContext damageContext = new DamageContext(source, target, ITEM_NAME, NORMAL_DAMAGE, DISTANCE);

        when(entityKeyRegistry.getEntityKey(DAMAGER_ID)).thenReturn(Optional.of(damagerEntityKey));
        when(entityKeyRegistry.getEntityKey(VICTIM_ID)).thenReturn(Optional.empty());

        damageProcessor.processDamage(damageContext);

        verifyNoInteractions(damageEventTracker);
    }

    @Test
    @DisplayName("processDamage performs the damage pipeline")
    void processDamage_successful() {
        EntityKey damagerEntityKey = EntityKey.fromEntityType(EntityType.PLAYER);
        EntityKey victimEntityKey = EntityKey.fromEntityType(EntityType.ZOMBIE);

        DamageSource source = mock(DamageSource.class);
        when(source.getUniqueId()).thenReturn(DAMAGER_ID);

        DamageTarget target = mock(DamageTarget.class);
        when(target.getUniqueId()).thenReturn(VICTIM_ID);
        when(target.getHealth()).thenReturn(0.0);
        when(target.damage(HIGH_DAMAGE)).thenReturn(30.0);

        DamageContext originalDamageContext = new DamageContext(source, target, ITEM_NAME, NORMAL_DAMAGE, DISTANCE);
        DamageContext modifiedDamageContext = new DamageContext(source, target, ITEM_NAME, HIGH_DAMAGE, DISTANCE);

        DamageModifier damageModifier = mock(DamageModifier.class);
        when(damageModifier.apply(originalDamageContext)).thenReturn(modifiedDamageContext);

        when(entityKeyRegistry.getEntityKey(DAMAGER_ID)).thenReturn(Optional.of(damagerEntityKey));
        when(entityKeyRegistry.getEntityKey(VICTIM_ID)).thenReturn(Optional.of(victimEntityKey));

        damageProcessor.addDamageModifier(damageModifier);
        damageProcessor.processDamage(originalDamageContext);

        ArgumentCaptor<DamageEvent> damageEventCaptor = ArgumentCaptor.forClass(DamageEvent.class);
        verify(damageEventTracker).add(damageEventCaptor.capture());

        assertThat(damageEventCaptor.getValue()).satisfies(damageEvent -> {
            assertThat(damageEvent.gameKey()).isEqualTo(gameKey);
            assertThat(damageEvent.damagerId()).isEqualTo(DAMAGER_ID);
            assertThat(damageEvent.damagerEntityKey()).isEqualTo(damagerEntityKey);
            assertThat(damageEvent.victimId()).isEqualTo(VICTIM_ID);
            assertThat(damageEvent.victimEntityType()).isEqualTo(victimEntityKey);
            assertThat(damageEvent.item()).isEqualTo(ITEM_NAME);
            assertThat(damageEvent.damageAmount()).isEqualTo(30.0);
            assertThat(damageEvent.damageType()).isEqualTo(DamageType.BULLET_DAMAGE);
            assertThat(damageEvent.hitboxComponentType()).isEqualTo(HitboxComponentType.TORSO);
            assertThat(damageEvent.distance()).isEqualTo(DISTANCE);
            assertThat(damageEvent.kill()).isTrue();
            assertThat(damageEvent.friendlyFire()).isFalse();
            assertThat(damageEvent.timestamp()).isEqualTo("2026-05-14T13:00:00.00Z");
        });

        verify(target).damage(HIGH_DAMAGE);
    }
}
