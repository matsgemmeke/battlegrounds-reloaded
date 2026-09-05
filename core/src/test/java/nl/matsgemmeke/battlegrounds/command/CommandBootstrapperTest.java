package nl.matsgemmeke.battlegrounds.command;

import co.aikar.commands.*;
import nl.matsgemmeke.battlegrounds.configuration.BattlegroundsConfiguration;
import nl.matsgemmeke.battlegrounds.i18n.TextTemplate;
import nl.matsgemmeke.battlegrounds.i18n.TranslationKey;
import nl.matsgemmeke.battlegrounds.i18n.Translator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

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
        when(translator.translate(TranslationKey.INVALID_SYNTAX.getPath())).thenReturn(new TextTemplate(INVALID_SYNTAX_MESSAGE));
        when(commandManager.getLocales()).thenReturn(bukkitLocales);

        commandBootstrapper.initialize();

        verify(commandExtension).configure(commandManager);
        verify(bukkitLocales).setDefaultLocale(Locale.FRENCH);
        verify(bukkitLocales).addMessage(Locale.FRENCH, MessageKeys.INVALID_SYNTAX, INVALID_SYNTAX_MESSAGE);
    }
}
