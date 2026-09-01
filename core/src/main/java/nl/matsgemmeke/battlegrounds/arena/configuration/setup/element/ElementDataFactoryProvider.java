package nl.matsgemmeke.battlegrounds.arena.configuration.setup.element;

import com.google.inject.Inject;
import com.google.inject.Provider;
import nl.matsgemmeke.battlegrounds.arena.configuration.setup.element.spawn.SpawnPointDataReader;

public class ElementDataFactoryProvider implements Provider<ElementDataFactory> {

    private final SpawnPointDataReader spawnPointDataReader;

    @Inject
    public ElementDataFactoryProvider(SpawnPointDataReader spawnPointDataReader) {
        this.spawnPointDataReader = spawnPointDataReader;
    }

    @Override
    public ElementDataFactory get() {
        ElementDataFactory elementDataFactory = new ElementDataFactory();
        elementDataFactory.addElementDataReader(ElementType.SPAWN_POINT, spawnPointDataReader);
        return elementDataFactory;
    }
}
