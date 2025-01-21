package nl.matsgemmeke.battlegrounds.configuration.lang;

import nl.matsgemmeke.battlegrounds.configuration.BasePluginConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.InputStream;
import java.util.Locale;

public class LanguageConfiguration extends BasePluginConfiguration {

    private static final boolean READ_ONLY = true;

    @NotNull
    private Locale locale;

    public LanguageConfiguration(@NotNull File file, @Nullable InputStream resource, @NotNull Locale locale) {
        super(file, resource, READ_ONLY);
        this.locale = locale;
    }

    @NotNull
    public Locale getLocale() {
        return locale;
    }
}
