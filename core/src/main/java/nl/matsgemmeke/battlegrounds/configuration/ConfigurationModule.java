package nl.matsgemmeke.battlegrounds.configuration;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import nl.matsgemmeke.battlegrounds.configuration.data.DataConfigurationFileProvider;

public class ConfigurationModule implements Module {

    @Override
    public void configure(Binder binder) {
        binder.bind(ConfigurationFile.class).annotatedWith(Names.named("data")).toProvider(DataConfigurationFileProvider.class).in(Singleton.class);
    }
}
