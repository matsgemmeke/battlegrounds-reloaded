package nl.matsgemmeke.battlegrounds.configuration.polymorphic;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ObjectReaderRegistry<E, T> {

    private final Map<E, ObjectReader<T>> readers;

    public ObjectReaderRegistry() {
        this.readers = new HashMap<>();
    }

    public void register(E type, ObjectReader<T> reader) {
        readers.put(type, reader);
    }

    public Optional<ObjectReader<T>> getSectionReader(E type) {
        return Optional.ofNullable(readers.get(type));
    }
}
