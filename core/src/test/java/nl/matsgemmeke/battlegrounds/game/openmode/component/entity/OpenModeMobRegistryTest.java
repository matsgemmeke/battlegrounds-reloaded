package nl.matsgemmeke.battlegrounds.game.openmode.component.entity;

import nl.matsgemmeke.battlegrounds.entity.GameMob;
import nl.matsgemmeke.battlegrounds.entity.hitbox.HitboxResolver;
import nl.matsgemmeke.battlegrounds.entity.hitbox.provider.HitboxProvider;
import org.bukkit.entity.LivingEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OpenModeMobRegistryTest {

    private static final UUID UNIQUE_ID = UUID.randomUUID();

    @Mock
    private HitboxResolver hitboxResolver;
    @InjectMocks
    private OpenModeMobRegistry mobRegistry;

    @Test
    void findByUniqueIdReturnsEmptyOptionalWhenGivenUniqueIdIsNotRegistered() {
        Optional<GameMob> gameMobOptional = mobRegistry.findByUniqueId(UNIQUE_ID);

        assertThat(gameMobOptional).isEmpty();
    }

    @Test
    void findByUniqueIdReturnsOptionalWithMatchingGameEntity() {
        HitboxProvider<LivingEntity> hitboxProvider = mock();

        LivingEntity livingEntity = mock(LivingEntity.class);
        when(livingEntity.getUniqueId()).thenReturn(UNIQUE_ID);

        when(hitboxResolver.resolveHitboxProvider(livingEntity)).thenReturn(hitboxProvider);

        mobRegistry.register(livingEntity);
        Optional<GameMob> gameMobOptional = mobRegistry.findByUniqueId(UNIQUE_ID);

        assertThat(gameMobOptional).hasValueSatisfying(gameEntity -> {
            assertThat(gameEntity.getUniqueId()).isEqualTo(UNIQUE_ID);
        });
    }

    @Test
    void registerReturnsNewGameEntityInstanceOfGivenLivingEntity() {
        HitboxProvider<LivingEntity> hitboxProvider = mock();

        LivingEntity livingEntity = mock(LivingEntity.class);
        when(livingEntity.getUniqueId()).thenReturn(UNIQUE_ID);

        when(hitboxResolver.resolveHitboxProvider(livingEntity)).thenReturn(hitboxProvider);

        GameMob gameMob = mobRegistry.register(livingEntity);

        assertThat(gameMob.getUniqueId()).isEqualTo(UNIQUE_ID);
    }

    @Test
    void registerReturnsSameGameEntityInstanceOfGivenLivingEntityWhenAlreadyRegistered() {
        HitboxProvider<LivingEntity> hitboxProvider = mock();

        LivingEntity livingEntity = mock(LivingEntity.class);
        when(livingEntity.getUniqueId()).thenReturn(UNIQUE_ID);

        when(hitboxResolver.resolveHitboxProvider(livingEntity)).thenReturn(hitboxProvider);

        GameMob gameMob1 = mobRegistry.register(livingEntity);
        GameMob gameMob2 = mobRegistry.register(livingEntity);

        assertThat(gameMob2.getUniqueId()).isEqualTo(UNIQUE_ID);
        assertThat(gameMob2).isEqualTo(gameMob1);
    }
}
