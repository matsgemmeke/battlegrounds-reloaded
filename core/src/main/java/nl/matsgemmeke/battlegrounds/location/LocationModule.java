package nl.matsgemmeke.battlegrounds.location;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Singleton;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Names;
import nl.matsgemmeke.battlegrounds.command.CommandExtension;
import nl.matsgemmeke.battlegrounds.configuration.ConfigurationFile;
import nl.matsgemmeke.battlegrounds.location.command.LocationCommandExtension;
import nl.matsgemmeke.battlegrounds.location.configuration.LocationConfigurationFileProvider;

public class LocationModule implements Module {

    @Override
    public void configure(Binder binder) {
        binder.bind(ConfigurationFile.class).annotatedWith(Names.named("data")).toProvider(LocationConfigurationFileProvider.class).in(Singleton.class);

        Multibinder<CommandExtension> commandExtensionBinder = Multibinder.newSetBinder(binder, CommandExtension.class);
        commandExtensionBinder.addBinding().to(LocationCommandExtension.class).in(Singleton.class);
    }
}
