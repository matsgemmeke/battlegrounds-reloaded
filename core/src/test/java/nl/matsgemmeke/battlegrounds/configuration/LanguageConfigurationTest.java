package nl.matsgemmeke.battlegrounds.configuration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class LanguageConfigurationTest {

    private File langFile;

    @BeforeEach
    public void setUp(@TempDir File tempDir) throws IOException {
        File langFolder = new File(tempDir.getPath() + "/Battlegrounds/lang");

        this.langFile = new File(langFolder, "lang_en.yml");

        langFile.delete();
    }

    @Test
    public void shouldBeAbleToGetLocale() {
        Locale locale = Locale.ENGLISH;
        LanguageConfiguration languageConfiguration = new LanguageConfiguration(langFile, null, locale);

        assertEquals(Locale.ENGLISH, languageConfiguration.getLocale());
    }

    @Test
    public void shouldCreateNewFileWithResourceContentsUponFirstLoad() throws IOException {
        File resourceFile = new File("src/main/resources/lang/lang_en.yml");
        InputStream resource = new FileInputStream(resourceFile);
        Locale locale = Locale.ENGLISH;

        LanguageConfiguration languageConfiguration = new LanguageConfiguration(langFile, resource, locale);
        languageConfiguration.load();

        assertNotNull(languageConfiguration.getString("admin.reload-failed"));
    }
}
