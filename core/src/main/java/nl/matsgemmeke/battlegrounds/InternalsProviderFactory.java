package nl.matsgemmeke.battlegrounds;

import org.bukkit.Bukkit;

import java.lang.reflect.InvocationTargetException;

public class InternalsProviderFactory {

    private InternalsProviderFactory() {
    }

    public static VersionAdapter create() {
        String version = Bukkit.getBukkitVersion();
        int[] v = parse(version);

        if (v[0] >= 26) {
            return instantiateInternalsProvider("v26");
        }

        throw new StartupFailedException("Unsupported Minecraft version: " + version);
    }

    private static int[] parse(String version) {
        String[] parts = version.split("\\.");
        int[] out = new int[3];

        for (int i = 0; i < Math.min(parts.length, 3); i++) {
            out[i] = Integer.parseInt(parts[i].replaceAll("\\D.*", ""));
        }

        return out;
    }

    private static VersionAdapter instantiateInternalsProvider(String version) {
        try {
            String packageName = BattlegroundsPlugin.class.getPackage().getName();
            String className = packageName + ".nms." + version + "." + version.toUpperCase() + "InternalsProvider";

            return (VersionAdapter) Class.forName(className).getDeclaredConstructor().newInstance();
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException ex) {
            throw new StartupFailedException("Failed to instantiate internals provider for Minecraft version " + version, ex);
        }
    }
}
