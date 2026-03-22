package nl.matsgemmeke.battlegrounds.entity.hitbox;

import nl.matsgemmeke.battlegrounds.configuration.hitbox.HitboxConfiguration;
import nl.matsgemmeke.battlegrounds.configuration.hitbox.HitboxDefinitionResult;
import nl.matsgemmeke.battlegrounds.configuration.hitbox.definition.HitboxComponentDefinition;
import nl.matsgemmeke.battlegrounds.configuration.hitbox.definition.HitboxDefinition;
import nl.matsgemmeke.battlegrounds.entity.hitbox.mapper.HitboxMapper;
import nl.matsgemmeke.battlegrounds.entity.hitbox.provider.DefaultEntityHitboxProvider;
import nl.matsgemmeke.battlegrounds.entity.hitbox.provider.PlayerHitboxProvider;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.logging.Logger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HitboxResolverProviderTest {

    @Mock
    private HitboxConfiguration hitboxConfiguration;
    @Spy
    private HitboxMapper hitboxMapper;
    @Mock
    private Logger logger;
    @InjectMocks
    private HitboxResolverProvider provider;

    @Test
    @DisplayName("get returns HitboxResolver with fallback hitbox providers when hitbox definitions are not found")
    void get_hitboxDefinitionsNotFound() {
        Player player = mock(Player.class);

        when(hitboxConfiguration.getHitboxDefinition(anyString(), anyString())).thenReturn(HitboxDefinitionResult.notFound());

        HitboxResolver hitboxResolver = provider.get();

        assertThat(hitboxResolver.resolveHitboxProvider(player)).isInstanceOf(DefaultEntityHitboxProvider.class);

        verify(logger).severe("Missing hitbox for player.standing; falling back to default");
    }

    @Test
    @DisplayName("get returns HitboxResolver with fallback hitbox providers when hitbox definitions are invalid")
    void get_hitboxDefinitionsInvalid() {
        Player player = mock(Player.class);

        when(hitboxConfiguration.getHitboxDefinition(anyString(), anyString())).thenReturn(HitboxDefinitionResult.invalid("error"));

        HitboxResolver hitboxResolver = provider.get();

        assertThat(hitboxResolver.resolveHitboxProvider(player)).isInstanceOf(DefaultEntityHitboxProvider.class);

        verify(logger).severe("Invalid hitbox for player.standing: error");
    }

    @Test
    @DisplayName("get returns HitboxResolver with preset hitbox providers")
    void get_returnsPresetHitboxResolver() {
        HitboxDefinition hitboxDefinition = createHitboxDefinition();

        Player player = mock(Player.class);
        when(player.getType()).thenReturn(EntityType.PLAYER);

        when(hitboxConfiguration.getHitboxDefinition(anyString(), anyString())).thenReturn(HitboxDefinitionResult.notFound());
        when(hitboxConfiguration.getHitboxDefinition("player", "standing")).thenReturn(HitboxDefinitionResult.success(hitboxDefinition));

        HitboxResolver hitboxResolver = provider.get();

        assertThat(hitboxResolver.resolveHitboxProvider(player)).isInstanceOf(PlayerHitboxProvider.class);
    }

    private static HitboxDefinition createHitboxDefinition() {
        HitboxComponentDefinition hitboxComponentDefinition = new HitboxComponentDefinition();
        hitboxComponentDefinition.type = "TORSO";
        hitboxComponentDefinition.size = new Double[] { 0.7, 0.4, 0.2 };
        hitboxComponentDefinition.offset = new Double[] { 0.0, 0.7, 0.0 };

        HitboxDefinition hitboxDefinition = new HitboxDefinition();
        hitboxDefinition.components = List.of(hitboxComponentDefinition);
        return hitboxDefinition;
    }
}
