package nl.matsgemmeke.battlegrounds.command;

import co.aikar.commands.*;
import nl.matsgemmeke.battlegrounds.configuration.BattlegroundsConfiguration;
import nl.matsgemmeke.battlegrounds.i18n.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommandBootstrapperTest {

    private static final String LANGUAGE = "fr";
    private static final String INVALID_SYNTAX_MESSAGE = "invalid syntax";

    @Mock
    private BattlegroundsConfiguration battlegroundsConfiguration;
    @Mock
    private BukkitLocales bukkitLocales;
    @Mock
    private PaperCommandManager commandManager;
    @Mock
    private CommandExtension commandExtension;
    @Spy
    private Set<CommandExtension> commandExtensions = new HashSet<>();
    @Mock
    private Translator translator;
    @InjectMocks
    private CommandBootstrapper commandBootstrapper;

    @Test
    @DisplayName("initialize registers all command extensions")
    void initialize() {
        commandExtensions.add(commandExtension);

        when(battlegroundsConfiguration.getLanguage()).thenReturn(LANGUAGE);
        when(translator.translateLegacyText(eq(TranslationKey.INVALID_SYNTAX.getPath()), any(Replacement[].class))).thenReturn(Optional.of(INVALID_SYNTAX_MESSAGE));
        when(commandManager.getLocales()).thenReturn(bukkitLocales);

        commandBootstrapper.initialize();

        ArgumentCaptor<Replacement[]> replacementsCaptor = ArgumentCaptor.forClass(Replacement[].class);
        verify(translator).translateLegacyText(eq(TranslationKey.INVALID_SYNTAX.getPath()), replacementsCaptor.capture());

        assertThat(replacementsCaptor.getValue()).satisfiesExactly(replacement -> {
            assertThat(replacement.key()).isEqualTo("command");
            assertThat(replacement.value()).isEqualTo("{command}");
        }, replacement -> {
            assertThat(replacement.key()).isEqualTo("syntax");
            assertThat(replacement.value()).isEqualTo("{syntax}");
        });

        verify(commandExtension).configure(commandManager);
        verify(bukkitLocales).setDefaultLocale(Locale.FRENCH);
        verify(bukkitLocales).addMessage(Locale.FRENCH, MessageKeys.INVALID_SYNTAX, INVALID_SYNTAX_MESSAGE);
    }
}
