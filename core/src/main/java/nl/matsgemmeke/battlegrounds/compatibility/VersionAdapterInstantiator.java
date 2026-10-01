package nl.matsgemmeke.battlegrounds.compatibility;

import nl.matsgemmeke.battlegrounds.VersionAdapter;

@FunctionalInterface
public interface VersionAdapterInstantiator {

    VersionAdapter instantiate(String className) throws ReflectiveOperationException;
}
