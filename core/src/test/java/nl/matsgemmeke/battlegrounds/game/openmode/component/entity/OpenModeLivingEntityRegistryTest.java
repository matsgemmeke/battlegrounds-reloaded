package nl.matsgemmeke.battlegrounds.game.openmode.component.entity;

import nl.matsgemmeke.battlegrounds.entity.GameEntity;
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
class OpenModeLivingEntityRegistryTest {

    private static final UUID UNIQUE_ID = UUID.randomUUID();

    @Mock
    private HitboxResolver hitboxResolver;
    @InjectMocks
    private OpenModeLivingEntityRegistry livingEntityRegistry;

    @Test
    void findByUniqueIdReturnsEmptyOptionalWhenGivenUniqueIdIsNotRegistered() {
        Optional<GameEntity> gameEntityOptional = livingEntityRegistry.findByUniqueId(UNIQUE_ID);

        assertThat(gameEntityOptional).isEmpty();
    }

    @Test
    void findByUniqueIdReturnsOptionalWithMatchingGameEntity() {
        HitboxProvider hitboxProvider = mock(HitboxProvider.class);

        LivingEntity livingEntity = mock(LivingEntity.class);
        when(livingEntity.getUniqueId()).thenReturn(UNIQUE_ID);

        when(hitboxResolver.resolveHitboxProvider(livingEntity)).thenReturn(hitboxProvider);

        livingEntityRegistry.register(livingEntity);
        Optional<GameEntity> gameEntityOptional = livingEntityRegistry.findByUniqueId(UNIQUE_ID);

        assertThat(gameEntityOptional).hasValueSatisfying(gameEntity -> {
            assertThat(gameEntity.getEntity()).isEqualTo(livingEntity);
        });
    }

    @Test
    void registerReturnsNewGameEntityInstanceOfGivenLivingEntity() {
        HitboxProvider hitboxProvider = mock(HitboxProvider.class);
        LivingEntity livingEntity = mock(LivingEntity.class);

        when(hitboxResolver.resolveHitboxProvider(livingEntity)).thenReturn(hitboxProvider);

        GameEntity gameEntity = livingEntityRegistry.register(livingEntity);

        assertThat(gameEntity.getEntity()).isEqualTo(livingEntity);
    }

    @Test
    void registerReturnsSameGameEntityInstanceOfGivenLivingEntityWhenAlreadyRegistered() {
        HitboxProvider hitboxProvider = mock(HitboxProvider.class);
        LivingEntity livingEntity = mock(LivingEntity.class);

        when(hitboxResolver.resolveHitboxProvider(livingEntity)).thenReturn(hitboxProvider);

        GameEntity gameEntity1 = livingEntityRegistry.register(livingEntity);
        GameEntity gameEntity2 = livingEntityRegistry.register(livingEntity);

        assertThat(gameEntity2.getEntity()).isEqualTo(livingEntity);
        assertThat(gameEntity2).isEqualTo(gameEntity1);
    }
}
