package nl.matsgemmeke.battlegrounds.arena.map;

public class ArenaMap {

    private final ArenaMapMetadata metadata;
    private final String name;

    public ArenaMap(String name, ArenaMapMetadata metadata) {
        this.name = name;
        this.metadata = metadata;
    }

    public ArenaMapMetadata getMetadata() {
        return metadata;
    }

    public String getName() {
        return name;
    }
}
