package nl.matsgemmeke.battlegrounds.compatibility;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import nl.matsgemmeke.battlegrounds.VersionAdapter;
import org.bukkit.Bukkit;

import java.util.function.Supplier;

public class CompatibilityModule implements Module {

    @Override
    public void configure(Binder binder) {
        binder.bind(VersionAdapter.class).toProvider(VersionAdapterProvider.class).in(Singleton.class);
    }

    @Provides
    @Named("bukkitVersion")
    @Singleton
    Supplier<String> provideBukkitVersionSupplier() {
        return Bukkit::getBukkitVersion;
    }

    @Provides
    @Singleton
    VersionAdapterInstantiator provideVersionAdapterInstantiator() {
        return className -> (VersionAdapter) Class.forName(className).getDeclaredConstructor().newInstance();
    }
}
