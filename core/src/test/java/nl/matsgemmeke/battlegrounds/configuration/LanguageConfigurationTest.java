package nl.matsgemmeke.battlegrounds.configuration;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class LanguageConfigurationTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private File langFile;

    @Before
    public void setUp() throws IOException {
        File langFolder = folder.newFolder("Battlegrounds", "lang");

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
