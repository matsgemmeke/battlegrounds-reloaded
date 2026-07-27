package nl.matsgemmeke.battlegrounds.arena.map;

import nl.matsgemmeke.battlegrounds.arena.map.element.ElementRegistry;

public class ArenaMap {

    private final ArenaMapMetadata metadata;
    private final ElementRegistry elementRegistry;
    private final String name;

    public ArenaMap(String name, ArenaMapMetadata metadata) {
        this.name = name;
        this.metadata = metadata;
        this.elementRegistry = new ElementRegistry();
    }

    public ElementRegistry getElementRegistry() {
        return elementRegistry;
    }

    public ArenaMapMetadata getMetadata() {
        return metadata;
    }

    public String getName() {
        return name;
    }
}
