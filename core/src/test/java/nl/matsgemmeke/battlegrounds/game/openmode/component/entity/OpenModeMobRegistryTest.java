package nl.matsgemmeke.battlegrounds.game.openmode.component.entity;

import nl.matsgemmeke.battlegrounds.entity.EntityKeyRegistry;
import nl.matsgemmeke.battlegrounds.entity.GameMob;
import nl.matsgemmeke.battlegrounds.entity.hitbox.HitboxResolver;
import nl.matsgemmeke.battlegrounds.entity.hitbox.provider.HitboxProvider;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.junit.jupiter.api.DisplayName;
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
class OpenModeMobRegistryTest {

    private static final UUID UNIQUE_ID = UUID.randomUUID();

    @Mock
    private EntityKeyRegistry entityKeyRegistry;
    @Mock
    private HitboxResolver hitboxResolver;
    @InjectMocks
    private OpenModeMobRegistry mobRegistry;

    @Test
    @DisplayName("findByUniqueId returns empty optional when given unique id is not registered")
    void findByUniqueId_notRegistered() {
        Optional<GameMob> gameMobOptional = mobRegistry.findByUniqueId(UNIQUE_ID);

        assertThat(gameMobOptional).isEmpty();
    }

    @Test
    @DisplayName("findByUniqueId returns optional with matching mob")
    void findByUniqueId_registered() {
        HitboxProvider<LivingEntity> hitboxProvider = mock();

        LivingEntity livingEntity = mock(LivingEntity.class);
        when(livingEntity.getUniqueId()).thenReturn(UNIQUE_ID);
        when(livingEntity.getType()).thenReturn(EntityType.ZOMBIE);

        when(hitboxResolver.resolveHitboxProvider(livingEntity)).thenReturn(hitboxProvider);

        mobRegistry.register(livingEntity);
        Optional<GameMob> gameMobOptional = mobRegistry.findByUniqueId(UNIQUE_ID);

        assertThat(gameMobOptional).hasValueSatisfying(gameEntity -> {
            assertThat(gameEntity.getUniqueId()).isEqualTo(UNIQUE_ID);
        });
    }

    @Test
    @DisplayName("register returns matching mob instance for given entity when already registered")
    void register_alreadyRegistered() {
        HitboxProvider<LivingEntity> hitboxProvider = mock();

        LivingEntity livingEntity = mock(LivingEntity.class);
        when(livingEntity.getUniqueId()).thenReturn(UNIQUE_ID);
        when(livingEntity.getType()).thenReturn(EntityType.ZOMBIE);

        when(hitboxResolver.resolveHitboxProvider(livingEntity)).thenReturn(hitboxProvider);

        GameMob gameMob1 = mobRegistry.register(livingEntity);
        GameMob gameMob2 = mobRegistry.register(livingEntity);

        assertThat(gameMob2.getUniqueId()).isEqualTo(UNIQUE_ID);
        assertThat(gameMob2).isEqualTo(gameMob1);

        verify(entityKeyRegistry).register(eq(UNIQUE_ID), argThat(entityKey -> entityKey.getValue().equals("minecraft:zombie")));
    }

    @Test
    @DisplayName("register returns new GameMob instance for given entity")
    void register_successful() {
        HitboxProvider<LivingEntity> hitboxProvider = mock();

        LivingEntity livingEntity = mock(LivingEntity.class);
        when(livingEntity.getUniqueId()).thenReturn(UNIQUE_ID);
        when(livingEntity.getType()).thenReturn(EntityType.ZOMBIE);

        when(hitboxResolver.resolveHitboxProvider(livingEntity)).thenReturn(hitboxProvider);

        GameMob gameMob = mobRegistry.register(livingEntity);

        assertThat(gameMob.getUniqueId()).isEqualTo(UNIQUE_ID);

        verify(entityKeyRegistry).register(eq(UNIQUE_ID), argThat(entityKey -> entityKey.getValue().equals("minecraft:zombie")));
    }
}
