package nl.matsgemmeke.battlegrounds.extension;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Server;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.mockito.Mockito;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class BukkitSoundRegistryExtension implements BeforeAllCallback {

    @Override
    public void beforeAll(ExtensionContext context) {
        // Runs the setup once per JVM, since Bukkit.setServer can't be called twice
        context.getRoot()
                .getStore(Namespace.create(BukkitSoundRegistryExtension.class))
                .getOrComputeIfAbsent("bukkit-server", key -> {
                    installServer();
                    return true;
                });
    }

    private void installServer() {
        Logger logger = Logger.getLogger("test");

        Server server = mock(Server.class);
        when(server.getLogger()).thenReturn(logger);

        when(server.getRegistry(any())).thenAnswer(invocation -> {
            Class<?> type = invocation.getArgument(0);
            Map<NamespacedKey, Object> cache = new ConcurrentHashMap<>();

            return mock(Registry.class, withSettings().defaultAnswer(inv -> {
                String name = inv.getMethod().getName();

                if ((name.equals("get") || name.equals("getOrThrow")) && inv.getArguments().length == 1) {
                    return cache.computeIfAbsent(inv.getArgument(0), key -> mock(type));
                }

                return Mockito.RETURNS_DEFAULTS.answer(inv);
            }));
        });

        Bukkit.setServer(server);
    }
}
