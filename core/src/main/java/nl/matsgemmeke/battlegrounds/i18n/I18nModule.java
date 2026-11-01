package nl.matsgemmeke.battlegrounds.i18n;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Singleton;
import net.kyori.adventure.text.minimessage.MiniMessage;
import nl.matsgemmeke.battlegrounds.i18n.configuration.LanguageConfiguration;
import nl.matsgemmeke.battlegrounds.i18n.configuration.LanguageConfigurationProvider;

public class I18nModule implements Module {

    @Override
    public void configure(Binder binder) {
        binder.bind(LanguageConfiguration.class).toProvider(LanguageConfigurationProvider.class);
        binder.bind(MiniMessage.class).toProvider(MiniMessage::miniMessage);
        binder.bind(Translator.class).in(Singleton.class);
    }
}
