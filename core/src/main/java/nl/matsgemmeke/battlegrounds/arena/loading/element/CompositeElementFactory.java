package nl.matsgemmeke.battlegrounds.arena.loading.element;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.arena.configuration.setup.element.ElementData;
import nl.matsgemmeke.battlegrounds.arena.configuration.setup.element.spawn.SpawnPointData;
import nl.matsgemmeke.battlegrounds.arena.map.element.Element;

public class CompositeElementFactory {

    private final SpawnPointFactory spawnPointFactory;

    @Inject
    public CompositeElementFactory(SpawnPointFactory spawnPointFactory) {
        this.spawnPointFactory = spawnPointFactory;
    }

    public Element create(ElementData elementData) {
        // Candidate for refactor when upgrading to Java 21
        if (elementData instanceof SpawnPointData spawnPointData) {
            return spawnPointFactory.create(spawnPointData);
        }

        throw new IllegalArgumentException("Cannot convert element data into element because type '%s' is not supported".formatted(elementData.getClass().getSimpleName()));
    }
}
