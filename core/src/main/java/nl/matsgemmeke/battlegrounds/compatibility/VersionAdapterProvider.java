package nl.matsgemmeke.battlegrounds.compatibility;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import nl.matsgemmeke.battlegrounds.BattlegroundsPlugin;
import nl.matsgemmeke.battlegrounds.StartupFailedException;
import nl.matsgemmeke.battlegrounds.VersionAdapter;

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
        String version = bukkitVersionSupplier.get();
        int[] v = this.parse(version);

        if (v[0] >= 26) {
            return this.instantiateVersionAdapter("v26");
        }

        throw new StartupFailedException("Unsupported Minecraft version: " + version);
    }

    private int[] parse(String version) {
        String[] parts = version.split("\\.");
        int[] out = new int[3];

        for (int i = 0; i < Math.min(parts.length, 3); i++) {
            out[i] = Integer.parseInt(parts[i].replaceAll("\\D.*", ""));
        }

        return out;
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
