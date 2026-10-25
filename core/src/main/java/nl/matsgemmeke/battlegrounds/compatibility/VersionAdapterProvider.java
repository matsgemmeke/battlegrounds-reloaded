package nl.matsgemmeke.battlegrounds.compatibility;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import nl.matsgemmeke.battlegrounds.BattlegroundsPlugin;
import nl.matsgemmeke.battlegrounds.StartupFailedException;

import java.util.function.Supplier;

public class VersionAdapterProvider implements Provider<VersionAdapter> {

    private final Supplier<String> bukkitVersionSupplier;
    private final VersionAdapterInstantiator versionAdapterInstantiator;

    @Inject
    public VersionAdapterProvider(@Named("bukkitVersion") Supplier<String> bukkitVersionSupplier, VersionAdapterInstantiator versionAdapterInstantiator) {
        this.bukkitVersionSupplier = bukkitVersionSupplier;
        this.versionAdapterInstantiator = versionAdapterInstantiator;
    }

    @Override
    public VersionAdapter get() {
        String versionString = bukkitVersionSupplier.get();
        int version = Integer.parseInt(versionString.split("\\.")[0]);

        if (version >= 26) {
            return this.instantiateVersionAdapter("v26");
        }

        throw new StartupFailedException("Unsupported Minecraft version: " + versionString);
    }

    private VersionAdapter instantiateVersionAdapter(String version) {
        try {
            String packageName = BattlegroundsPlugin.class.getPackage().getName();
            String className = packageName + ".nms." + version + "." + version.toUpperCase() + "VersionAdapter";

            return versionAdapterInstantiator.instantiate(className);
        } catch (ReflectiveOperationException ex) {
            throw new StartupFailedException("Failed to instantiate version adapter for Minecraft version " + version, ex);
        }
    }
}
