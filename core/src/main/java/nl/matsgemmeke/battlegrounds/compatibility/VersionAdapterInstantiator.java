package nl.matsgemmeke.battlegrounds.compatibility;

@FunctionalInterface
public interface VersionAdapterInstantiator {

    VersionAdapter instantiate(String className) throws ReflectiveOperationException;
}
