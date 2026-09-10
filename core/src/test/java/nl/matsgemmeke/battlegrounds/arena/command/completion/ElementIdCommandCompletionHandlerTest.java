package nl.matsgemmeke.battlegrounds.arena.command.completion;

import co.aikar.commands.BukkitCommandCompletionContext;
import nl.matsgemmeke.battlegrounds.arena.map.ArenaMap;
import nl.matsgemmeke.battlegrounds.arena.map.selection.ArenaMapSelection;
import nl.matsgemmeke.battlegrounds.arena.map.selection.ArenaMapSelector;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ElementIdCommandCompletionHandlerTest {

    private static final UUID PLAYER_ID = UUID.randomUUID();
    private static final int ELEMENT_ID = 5;

    @Mock
    private ArenaMapSelector mapSelector;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private BukkitCommandCompletionContext context;
    @InjectMocks
    private ElementIdCommandCompletionHandler commandCompletionHandler;

    @Test
    @DisplayName("getCompletions returns empty list when issuer is not a player")
    void getCompletions_nonPlayerIssuer() {
        when(context.getIssuer().isPlayer()).thenReturn(false);

        Collection<String> completions = commandCompletionHandler.getCompletions(context);

        assertThat(completions).isEmpty();
    }

    @Test
    @DisplayName("getCompletions returns empty list when player has not selected any map")
    void getCompletions_noSelectedMap() {
        when(context.getIssuer().isPlayer()).thenReturn(true);
        when(context.getIssuer().getUniqueId()).thenReturn(PLAYER_ID);
        when(mapSelector.getSelection(PLAYER_ID)).thenReturn(Optional.empty());

        Collection<String> completions = commandCompletionHandler.getCompletions(context);

        assertThat(completions).isEmpty();
    }

    @Test
    @DisplayName("getCompletions returns list of element id when player has selected a map")
    void getCompletions_successful() {
        ArenaMap map = mock(ArenaMap.class);
        when(map.getElementIds()).thenReturn(List.of(ELEMENT_ID));

        ArenaMapSelection mapSelection = new ArenaMapSelection(null, map);

        when(context.getIssuer().isPlayer()).thenReturn(true);
        when(context.getIssuer().getUniqueId()).thenReturn(PLAYER_ID);
        when(mapSelector.getSelection(PLAYER_ID)).thenReturn(Optional.of(mapSelection));

        Collection<String> completions = commandCompletionHandler.getCompletions(context);

        assertThat(completions).containsExactly(String.valueOf(ELEMENT_ID));
    }
}
