package nl.matsgemmeke.battlegrounds.arena.component.membership;

import nl.matsgemmeke.battlegrounds.arena.Arena;
import nl.matsgemmeke.battlegrounds.game.component.entity.PlayerRegistry;
import nl.matsgemmeke.battlegrounds.game.component.membership.JoinResult;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
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
class ArenaMembershipServiceTest {

    private static final UUID PLAYER_ID = UUID.randomUUID();

    @Mock
    private Arena arena;
    @Mock
    private Player player;
    @Mock
    private PlayerRegistry playerRegistry;
    @InjectMocks
    private ArenaMembershipService arenaMembershipService;

    @BeforeEach
    void setUp() {
        when(player.getUniqueId()).thenReturn(PLAYER_ID);
    }

    @Test
    @DisplayName("join returns ALREADY_IN_GAME when player registry already contains the player id")
    void join_alreadyInGame() {
        when(playerRegistry.isRegistered(PLAYER_ID)).thenReturn(true);

        JoinResult joinResult = arenaMembershipService.join(player);

        assertThat(joinResult).isEqualTo(JoinResult.ALREADY_IN_GAME);

        verify(playerRegistry, never()).register(any(Player.class));
    }

    @Test
    @DisplayName("join returns SUCCESS and does not teleport player to arena lobby when absent")
    void join_success_withoutArenaLobby() {
        when(playerRegistry.isRegistered(PLAYER_ID)).thenReturn(false);
        when(arena.getLobbyLocation()).thenReturn(Optional.empty());

        JoinResult joinResult = arenaMembershipService.join(player);

        assertThat(joinResult).isEqualTo(JoinResult.SUCCESS);

        verify(playerRegistry).register(player);
        verify(player, never()).teleport(any(Location.class));
    }

    @Test
    @DisplayName("join returns SUCCESS and teleports player to arena lobby when present")
    void join_success_withArenaLobby() {
        Location lobbyLocation = new Location(null, 1, 2, 3);

        when(playerRegistry.isRegistered(PLAYER_ID)).thenReturn(false);
        when(arena.getLobbyLocation()).thenReturn(Optional.of(lobbyLocation));

        JoinResult joinResult = arenaMembershipService.join(player);

        assertThat(joinResult).isEqualTo(JoinResult.SUCCESS);

        verify(playerRegistry).register(player);
        verify(player).teleport(lobbyLocation);
    }
}
